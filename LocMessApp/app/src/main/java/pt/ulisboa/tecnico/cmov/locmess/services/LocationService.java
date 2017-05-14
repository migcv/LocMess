package pt.ulisboa.tecnico.cmov.locmess.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.os.Messenger;
import android.os.StrictMode;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.security.Provider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.inesc.termite.wifidirect.SimWifiP2pDevice;
import pt.inesc.termite.wifidirect.SimWifiP2pDeviceList;
import pt.inesc.termite.wifidirect.SimWifiP2pInfo;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;
import pt.inesc.termite.wifidirect.service.SimWifiP2pService;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketManager;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketServer;
import pt.ulisboa.tecnico.cmov.locmess.R;
import pt.ulisboa.tecnico.cmov.locmess.activities.PostsActivity;
import pt.ulisboa.tecnico.cmov.locmess.utils.GlobalLocMess;
import pt.ulisboa.tecnico.cmov.locmess.utils.NewPost;
import pt.ulisboa.tecnico.cmov.locmess.utils.Post;
import pt.ulisboa.tecnico.cmov.locmess.utils.SimWifiP2pBroadcastReceiver;
import pt.ulisboa.tecnico.cmov.locmess.utils.SocketHandler;

/**
 * Created by Miguel on 18/04/2017.
 */

public class LocationService extends Service implements LocationListener, SimWifiP2pManager.PeerListListener, SimWifiP2pManager.GroupInfoListener {

    private static final int PORT = 10001;

    Location location;
    LocationManager locationManager;

    //private WifiManager mainWifi;
    //private WifiReceiver receiverWifi;

    private int notificationCounter = 0;

    private SimWifiP2pBroadcastReceiver receiver;
    private SimWifiP2pManager mManager = null;
    private SimWifiP2pManager.Channel mChannel = null;
    private Messenger mService = null;
    private boolean mBound = false;


    private SimWifiP2pSocket mCliSocket = null;
    private SimWifiP2pSocketServer mSrvSocket = null;

    private boolean stop = false;

    private ArrayList<String> postReceivedServer = new ArrayList<>();

    @Override
    public IBinder onBind(Intent intent) {

        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);
        unbindService(mConnection);
        stop = true;
        Log.i("LOCATION_SERVICE", "Service stopped!");
    }

    @Override
    public void onCreate() {
        Log.d("LOCATION_SERVICE", "Service created!");

        // initialize the Termite API
        SimWifiP2pSocketManager.Init(getApplicationContext());
        IntentFilter filter = new IntentFilter();
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_STATE_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_PEERS_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_NETWORK_MEMBERSHIP_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_GROUP_OWNERSHIP_CHANGED_ACTION);
        receiver = new SimWifiP2pBroadcastReceiver();
        registerReceiver(receiver, filter);

        new IncommingCommTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        Thread locationThread = new Thread(new UserLocation());
        locationThread.start();
    }

    public class UserLocation implements Runnable {

        public void run() {
            Looper.prepare();
            Log.d("LOCATION_ SERVICE", "Thread started!");
            while(!stop) {
                try {
                    /*
                     * SEND_CURRENT_GPS
                     */
                    getLocationNew();
                    //getLocation();
                    Log.d("LOCATION_ SERVICE", location == null ? "Location Null" : "Location " + location.getLatitude() + ", " + location.getLongitude());
                    if(location != null) {
                        ((GlobalLocMess) getApplicationContext()).setLatitude(location.getLatitude());
                        ((GlobalLocMess) getApplicationContext()).setLongitude(location.getLongitude());

                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                        StrictMode.setThreadPolicy(policy);
                        String toSend = "CurrentGPS;:;" + SocketHandler.getToken() + ";:;" + location.getLatitude() + ", " + location.getLongitude();
                        Socket s = SocketHandler.getSocket();
                        // Sending to Server
                        DataOutputStream dout = new DataOutputStream(s.getOutputStream());
                        dout.writeUTF(toSend);
                        dout.flush();
                        // Receiving from Server
                        DataInputStream dis = new DataInputStream(s.getInputStream());
                        String str = dis.readUTF();
                        while (!str.equals("END")) {
                            addPost(str);
                            dis = new DataInputStream(s.getInputStream());
                            str = dis.readUTF();
                        }
                    }
                    /*
                     * TERMITE STUFF
                     */
                    if (mManager == null){
                        Intent intent = new Intent(getApplicationContext(), SimWifiP2pService.class);
                        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
                    } else {
                        mManager.requestPeers(mChannel, LocationService.this);
                        mManager.requestGroupInfo(mChannel, LocationService.this);
                    }
                    /*
                     * SEND_CURRENT_WIFI
                     */
                    if(!((GlobalLocMess)getApplicationContext()).getCurrentWifis().isEmpty()) {
                        String toSend = "CurrentWIFI;:;" + SocketHandler.getToken() + ";:;";
                        String wifis = "";
                        for(SimWifiP2pDevice device : ((GlobalLocMess)getApplicationContext()).getCurrentWifis()) {
                            wifis = wifis + device.deviceName + ",";
                        }
                        Log.d("CURRENT_WIFI", wifis);
                        toSend = toSend + wifis;
                        Socket s = SocketHandler.getSocket();
                        // Sending to Server
                        DataOutputStream dout = new DataOutputStream(s.getOutputStream());
                        dout.writeUTF(toSend);
                        dout.flush();
                        // Receiving from Server
                        DataInputStream dis = new DataInputStream(s.getInputStream());
                        String str = dis.readUTF();
                        while (!str.equals("END")) {
                            addPost(str);
                            dis = new DataInputStream(s.getInputStream());
                            str = dis.readUTF();
                        }
                    }
                    // Removing Expired Posts
                    ((GlobalLocMess) getApplicationContext()).removeExpiredPosts();
                    // GET DECENTRALIZED POSTS (FROM SERVER)
                    getDecentralizedPosts();
                    /*
                     * SEND_POSTS_WIFI_P2P
                     */
                    if(!((GlobalLocMess) getApplicationContext()).getPostsToDelivery().isEmpty() ) {
                        for (SimWifiP2pDevice device : ((GlobalLocMess) getApplicationContext()).getDevicesToDelivery()) {
                            for(String id : ((GlobalLocMess) getApplicationContext()).getPostsToDelivery().keySet()) {
                                Log.d("SOCKET_CLIENT", "Preparing to send Post > " + id);
                                Post post = ((GlobalLocMess) getApplicationContext()).getPostsToDelivery().get(id);
                                String toSend = "DecentralizedPost;:;" + id + ";:;" + post.getUser() + ";:;" + post.getTittle() + ";:;" +
                                        post.getContent() + ";:;" + post.getContact() + ";:;" + post.getPostTime() + ";:;"
                                        + post.getPostLifetime() + ";:;" + post.getType() + ";:;" + post.getLocationName();
                                if(post.getType().equals("GPS")) {
                                    toSend = toSend + ";:;" + post.getLatitude() + "," + post.getLongitude() + "," + post.getRadius();
                                } else if(post.getType().equals("WIFI")) {
                                    toSend = toSend + ";:;";
                                    for(String ssid : post.getSsids()) {
                                        toSend = toSend + ssid + ",";
                                    }
                                }
                                toSend = toSend + ";:;" + post.getRestrictionPolicy();
                                if(!post.getRestrictionPolicy().equals(NewPost.EVERYONE)) {
                                    toSend = toSend + ";:;";
                                    for(String key : post.getRestrictions().keySet()) {
                                        for(String restriction : post.getRestrictions().get(key))
                                        toSend = toSend + restriction + "(" + key + "),";
                                    }
                                }
                                new SendCommTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, device.virtDeviceAddress.split(":")[0], toSend);
                            }
                        }
                    }
                    /*
                     * ADD_POST_DECENTRALIZED
                     */
                    ((GlobalLocMess) getApplicationContext()).checkPostToSend();
                    // Sleeps for 10 seconds
                    Thread.sleep(10000);
                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Log.d("LOCATION_ SERVICE", "Thread ended!");
        }
    }

    public void addPost(String str) {
        String[] postArguments = str.split(";:;")[1].split(",");
        GlobalLocMess global = (GlobalLocMess) getApplicationContext();
        if(global.getPost(postArguments[0] + "" + postArguments[1]) == null) {
            Post newPost = new Post(postArguments[1], postArguments[2], postArguments[3], postArguments[4], postArguments[5], postArguments[6], postArguments[7], postArguments[8]);
            global.addPost(postArguments[0] + "" + postArguments[1], newPost);

            notificationCounter = notificationCounter + 1;
            NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                    .setSmallIcon(android.R.drawable.ic_menu_gallery)
                    .setContentTitle("New Post: " + postArguments[2]);

            Intent notificationIntent = new Intent(this, PostsActivity.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            // Adds the back stack
            stackBuilder.addParentStack(PostsActivity.class);
            // Adds the Intent to the top of the stack
            stackBuilder.addNextIntent(notificationIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(pendingIntent);
            mBuilder.setAutoCancel(true);

            //startForeground(notificationCounter, mBuilder.build());
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(PostsActivity.NOTIFICATION_SERVICE);
            mNotificationManager.notify(notificationCounter, mBuilder.build());
            Log.d("ADD_POST", "Post added with id: " + postArguments[0] + "" + postArguments[1]);
        }
    }

    public void addPostToSend(String str) {
        String[] postArguments = str.split(";:;");
        GlobalLocMess global = (GlobalLocMess) getApplicationContext();
        if(global.getPostsToDelivery().get(postArguments[1] + "" + postArguments[2]) == null) {
            String user = postArguments[2];
            String tittle = postArguments[3];
            String content = postArguments[4];
            String contact = postArguments[5];
            String post_time =  postArguments[6];
            String post_lifetime =  postArguments[7];
            String location_type =  postArguments[8];
            String location_name =  postArguments[9];
            double latitude = 0, longitude = 0, radius = 0;
            ArrayList<String> ssids = new ArrayList<>();
            if(location_type.equals("GPS")) {
                latitude = Double.parseDouble(postArguments[10].split(",")[0]);
                longitude = Double.parseDouble(postArguments[10].split(",")[1]);
                radius = Double.parseDouble(postArguments[10].split(",")[2]);
            } else if(location_type.equals("WIFI")) {
                for(String ssid : postArguments[10].split(",")) {
                    ssids.add(ssid);
                }
            }
            String restrictions_policy = postArguments[11];
            HashMap<String, ArrayList<String>> restrictions = new HashMap<>();
            if(!restrictions_policy.equals(NewPost.EVERYONE)) {
                for(String restriction : postArguments[12].split(",")) {
                    String value = restriction.split(" \\(")[0];
                    String key = restriction.split("\\(")[1].split("\\)")[0];
                    if(restrictions.get(key) == null) {
                        ArrayList<String> list = new ArrayList<String>();
                        list.add(value);
                        restrictions.put(key, list);
                    } else {
                        restrictions.get(key).add(value);
                    }
                }
            }
            Post newPost = null;
            if(location_type.equals(NewPost.GPS)) {
                newPost = new Post(user, tittle, content, contact, post_time, post_lifetime, location_type, location_name,
                        latitude, longitude, radius, restrictions_policy, restrictions);
            } else {
                newPost = new Post(user, tittle, content, contact, post_time, post_lifetime, location_type, location_name,
                        ssids, restrictions_policy, restrictions);
            }
            global.addPostToDelivary(postArguments[1] + "" + postArguments[2], newPost);

            Log.d("ADD_POST_TO_SEND", "Post added with id: " + postArguments[1] + "" + postArguments[2]);
        }
    }

    public void addPostToSendP2P(String str) {
        String[] postArguments = str.split(";:;");
        GlobalLocMess global = (GlobalLocMess) getApplicationContext();
        if(global.getPostsToDelivery().get(postArguments[1]) == null) {
            String user = postArguments[2];
            String tittle = postArguments[3];
            String content = postArguments[4];
            String contact = postArguments[5];
            String post_time =  postArguments[6];
            String post_lifetime =  postArguments[7];
            String location_type =  postArguments[8];
            String location_name =  postArguments[9];
            double latitude = 0, longitude = 0, radius = 0;
            ArrayList<String> ssids = new ArrayList<>();
            if(location_type.equals("GPS")) {
                latitude = Double.parseDouble(postArguments[10].split(",")[0]);
                longitude = Double.parseDouble(postArguments[10].split(",")[1]);
                radius = Double.parseDouble(postArguments[10].split(",")[2]);
            } else if(location_type.equals("WIFI")) {
                for(String ssid : postArguments[10].split(",")) {
                    ssids.add(ssid);
                }
            }
            String restrictions_policy = postArguments[11];
            HashMap<String, ArrayList<String>> restrictions = new HashMap<>();
            if(!restrictions_policy.equals(NewPost.EVERYONE)) {
                for(String restriction : postArguments[12].split(",")) {
                    String value = restriction.split("\\(")[0];
                    String key = restriction.split("\\(")[1].split("\\)")[0];
                    if(restrictions.get(key) == null) {
                        ArrayList<String> list = new ArrayList<String>();
                        list.add(value);
                        restrictions.put(key, list);
                    } else {
                        restrictions.get(key).add(value);
                    }
                }
            }
            Post newPost = null;
            if(location_type.equals(NewPost.GPS)) {
                newPost = new Post(user, tittle, content, contact, post_time, post_lifetime, location_type, location_name,
                        latitude, longitude, radius, restrictions_policy, restrictions);
            } else {
                newPost = new Post(user, tittle, content, contact, post_time, post_lifetime, location_type, location_name,
                        ssids, restrictions_policy, restrictions);
            }
            global.addPostToDelivary(postArguments[1], newPost);

            Log.d("ADD_POST_TO_SEND", "Post added with id: " + postArguments[1]);
        }
    }

    public void getDecentralizedPosts() {
        String toSend = "GetDecentralizedPosts;:;" + SocketHandler.getToken() + ";:;";
        Socket s =SocketHandler.getSocket();
        try {
            // Sending request to Server
            DataOutputStream dout = new DataOutputStream(s.getOutputStream());
            Log.d("GET_DECENTRALIZED_POSTS", "Resquest > " + toSend);
            dout.writeUTF(toSend);
            dout.flush();
            // Receiving response from Server
            DataInputStream dis = new DataInputStream(s.getInputStream());
            String str = dis.readUTF();
            while (!str.equals("END")) {
                Log.d("GET_DECENTRALIZED_POSTS", "Response > " + str);
                addPostToSend(str);
                dis = new DataInputStream(s.getInputStream());
                str = dis.readUTF();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /*
     *  TERMITE STUFF
     */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);
            mManager = new SimWifiP2pManager(mService);
            mChannel = mManager.initialize(getApplication().getApplicationContext(), getMainLooper(), null);
            mBound = true;
        }
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mService = null;
            mManager = null;
            mChannel = null;
            mBound = false;
        }
    };

    @Override
    public void onGroupInfoAvailable(SimWifiP2pDeviceList simWifiP2pDeviceList, SimWifiP2pInfo simWifiP2pInfo) {
        ((GlobalLocMess)getApplicationContext()).cleanDevicesToDelivery();
        for (SimWifiP2pDevice device : simWifiP2pDeviceList.getDeviceList()) {
            if(!device.deviceName.equals(simWifiP2pInfo.getDeviceName()) && !device.virtDeviceAddress.startsWith("0.0.0.0")) {
                ((GlobalLocMess) getApplicationContext()).addDeviceToDelivery(device);
                Log.d("GROUP_INFO_AVAILABLE", "Device Name: " + device.deviceName + " Device Address: " + device.virtDeviceAddress);
            }
        }
    }

    @Override
    public void onPeersAvailable(SimWifiP2pDeviceList peers) {
        ((GlobalLocMess)getApplicationContext()).cleanCurrentWifis();
        for (SimWifiP2pDevice device : peers.getDeviceList()) {
            ((GlobalLocMess)getApplicationContext()).addCurrentWifi(device);
            Log.d("PEERS_AVAILABLE", "Device Name: " + device.deviceName + " Device Address: " + device.realDeviceAddress);
        }
    }
    /*
     * Asynctasks implementing Client
     */
    public class SendCommTask extends AsyncTask<String, String, Void> {

        @Override
        protected Void doInBackground(String... params) {
            try {
                Log.d("SOCKET_CLIENT", "SimWifiP2pSocket > Connecting to " + params[0] + ":" + PORT);
                mCliSocket = new SimWifiP2pSocket(params[0], PORT);
                Log.d("SOCKET_CLIENT", "SimWifiP2pSocket > Sending " + params[1]);
                mCliSocket.getOutputStream().write((params[1] + "\n").getBytes());
                BufferedReader sockIn = new BufferedReader(new InputStreamReader(mCliSocket.getInputStream()));
                sockIn.readLine();
                Log.d("SOCKET_CLIENT", "SimWifiP2pSocket > Sent, closing socket");
                //mCliSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //mCliSocket = null;
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

        }
    }
    /*
	 * Asynctasks implementing Server
	 */
    public class IncommingCommTask extends AsyncTask<Void, String, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Log.d("SOCKET_SERVER", "SimWifiP2pSocketServer > Started AsyncTask");
            try {
                mSrvSocket = new SimWifiP2pSocketServer(PORT);
                Log.d("SOCKET_SERVER", "SimWifiP2pSocketServer > Listenning " + PORT);
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    SimWifiP2pSocket sock = mSrvSocket.accept();
                    Log.d("SOCKET_SERVER", "SimWifiP2pSocketServer > Accepted ");
                    try {
                        BufferedReader sockIn = new BufferedReader(
                                new InputStreamReader(sock.getInputStream()));
                        String st = sockIn.readLine();
                        publishProgress(st);
                        sock.getOutputStream().write(("\n").getBytes());
                    } catch (IOException e) {
                        Log.d("Error reading socket:", e.getMessage());
                    } finally {
                        sock.close();
                    }
                } catch (IOException e) {
                    Log.d("Error socket:", e.getMessage());
                    break;
                    //e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            Log.d("SOCKET_SERVER", "Received Message > " + values[0] + "\n");
            addPostToSendP2P(values[0]);
            Log.d("SOCKET_SERVER", "Post Added");
        }
    }
    /*
     * GPS Location STUFF
     */
    public Location getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            // getting GPS status
            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (!isGPSEnabled) {
                Log.d("LOCATION_ SERVICE", "GPS Disabled");
            } else {
                if (location == null) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, this);
                    Log.d("LOCATION_ SERVICE", "GPS Enabled");
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    }
                }
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }

    public Location getLocationNew() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            String provider = locationManager.getBestProvider(criteria, false);
            if(provider != null) {
                locationManager.requestLocationUpdates(provider, 1000, 10, this);
                location = locationManager.getLastKnownLocation(provider);
                if (location != null) {
                    Log.d("NEW_LOCATION", "lat: " + location.getLatitude() + " : log: " + location.getLongitude());

                    ((GlobalLocMess) getApplicationContext()).setLatitude(location.getLatitude());
                    ((GlobalLocMess) getApplicationContext()).setLongitude(location.getLongitude());
                    return location;
                }
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    @Override
    public void onProviderEnabled(String s) {
    }

    @Override
    public void onProviderDisabled(String s) {
    }
    /*
     *  WIFI STUFF
     */
    /*private class WifiReceiver extends BroadcastReceiver {
        public void onReceive(Context c, Intent intent) {
            ArrayList<String> connections=new ArrayList<String>();
            ArrayList<Float> Signal_Strenth= new ArrayList<Float>();

            wifiSSIDList = new ArrayList<>();

            List<ScanResult> wifiList;
            wifiList = mainWifi.getScanResults();
            for(int i = 0; i < wifiList.size(); i++) {
                connections.add(wifiList.get(i).SSID);
                Log.d("CURRENT_WIFI", i + " " + wifiList.get(i).SSID);
                if(!wifiSSIDList.contains(wifiList.get(i).SSID)) {
                    wifiSSIDList.add(wifiList.get(i).SSID);
                }
            }
        }
    }*/

}
