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
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.os.Messenger;
import android.os.StrictMode;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.inesc.termite.wifidirect.SimWifiP2pDevice;
import pt.inesc.termite.wifidirect.SimWifiP2pDeviceList;
import pt.inesc.termite.wifidirect.SimWifiP2pInfo;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;
import pt.inesc.termite.wifidirect.service.SimWifiP2pService;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketManager;
import pt.ulisboa.tecnico.cmov.locmess.activities.PostsActivity;
import pt.ulisboa.tecnico.cmov.locmess.utils.GlobalLocMess;
import pt.ulisboa.tecnico.cmov.locmess.utils.Post;
import pt.ulisboa.tecnico.cmov.locmess.utils.SimWifiP2pBroadcastReceiver;
import pt.ulisboa.tecnico.cmov.locmess.utils.SocketHandler;

/**
 * Created by Miguel on 18/04/2017.
 */

public class LocationService extends Service implements LocationListener, SimWifiP2pManager.PeerListListener, SimWifiP2pManager.GroupInfoListener{

    Location location;
    LocationManager locationManager;
    double latitude;
    double longitude;

    //private WifiManager mainWifi;
    //private WifiReceiver receiverWifi;

    private int notificationCounter = 0;

    SimWifiP2pBroadcastReceiver receiver;
    private SimWifiP2pManager mManager = null;
    private SimWifiP2pManager.Channel mChannel = null;
    private Messenger mService = null;
    private boolean mBound = false;

    private boolean stop = false;

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

        Thread thread = new Thread(new UserLocation());
        thread.start();
    }

    public class UserLocation implements Runnable {

        public void run(){
            Looper.prepare();

            Log.d("LOCATION_ SERVICE", "Thread started!");
            while(!stop) {
                try {
                    getLocation();
                    Log.d("LOCATION_ SERVICE", location == null ? "Location Null" : "Location " + latitude + ", " + longitude);
                    if(location != null) {
                        ((GlobalLocMess) getApplicationContext()).setLatitude(latitude);
                        ((GlobalLocMess) getApplicationContext()).setLongitude(longitude);

                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                        StrictMode.setThreadPolicy(policy);
                        String toSend = "CurrentGPS;:;" + SocketHandler.getToken() + ";:;" + latitude + ", " + longitude;
                        Socket s = SocketHandler.getSocket();
                        Log.d("CONNECTION", "Connection successful!");
                        // Sending to Server
                        DataOutputStream dout = new DataOutputStream(s.getOutputStream());
                        dout.writeUTF(toSend);
                        dout.flush();
                        Log.d("CURRENT_GPS", toSend);
                        // Receiving from Server
                        DataInputStream dis = new DataInputStream(s.getInputStream());
                        String str = dis.readUTF();
                        Log.d("CURRENT_GPS", str);
                        while (!str.equals("END")) {
                            addPost(str);
                            dis = new DataInputStream(s.getInputStream());
                            str = dis.readUTF();
                            Log.d("CURRENT_GPS", str);
                        }

                    }
                    if (mManager == null){
                        Log.d("CURRENT_WIFI", "OFF Wifi");
                        Intent intent = new Intent(getApplicationContext(), SimWifiP2pService.class);
                        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
                    } else {
                        mManager.requestPeers(mChannel, LocationService.this);
                        Log.d("CURRENT_WIFI", "Enable Wifi");
                    }


                    /*mainWifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

                    receiverWifi = new WifiReceiver();
                    getApplicationContext().registerReceiver(receiverWifi, new IntentFilter(
                            WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
                    if (mainWifi.isWifiEnabled() == false) {
                        mainWifi.setWifiEnabled(true);
                    }
                    mainWifi.startScan();*/


                    if(!((GlobalLocMess)getApplicationContext()).getCurrentWifis().isEmpty()) {
                        String toSend = "CurrentWIFI;:;" + SocketHandler.getToken() + ";:;";
                        for(String ssid : ((GlobalLocMess)getApplicationContext()).getCurrentWifis()) {
                            toSend = toSend + "" + ssid + ",";
                        }
                        Socket s = SocketHandler.getSocket();
                        Log.d("CONNECTION", "Connection successful!");
                        // Sending to Server
                        DataOutputStream dout = new DataOutputStream(s.getOutputStream());
                        dout.writeUTF(toSend);
                        dout.flush();
                        Log.d("CURRENT_GPS", toSend);
                        // Receiving from Server
                        DataInputStream dis = new DataInputStream(s.getInputStream());
                        String str = dis.readUTF();
                        Log.d("CURRENT_GPS", str);
                        while (!str.equals("END")) {
                            addPost(str);
                            dis = new DataInputStream(s.getInputStream());
                            str = dis.readUTF();
                            Log.d("CURRENT_GPS", str);
                        }
                    }
                    // Removing Expired Posts
                    Log.d("SERVICE", "Removing expired posts");
                    ((GlobalLocMess) getApplicationContext()).removeExpiredPosts();
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
        } else {
            Log.d("ADD_POST", "Post already EXISTS with id: " + postArguments[0] + "" + postArguments[1]);
        }
    }
    /*
     *  TERMITE STUFF
     */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.d("SERVICE CONNECTED" , "LIGUEI");
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
    }

    @Override
    public void onPeersAvailable(SimWifiP2pDeviceList peers) {
        ((GlobalLocMess)getApplicationContext()).cleanCurrentWifis();
        for (SimWifiP2pDevice device : peers.getDeviceList()) {
            ((GlobalLocMess)getApplicationContext()).addCurrentWifi(device.deviceName);
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
            // getting network status
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            5000,
                            10, this);
                    Log.d("LOCATION_ SERVICE", "Network Enabled");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                5000,
                                10, this);
                        Log.d("LOCATION_ SERVICE", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
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
