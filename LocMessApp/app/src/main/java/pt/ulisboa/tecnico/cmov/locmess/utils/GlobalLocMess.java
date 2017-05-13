package pt.ulisboa.tecnico.cmov.locmess.utils;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Messenger;

import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import pt.inesc.termite.wifidirect.SimWifiP2pDevice;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketServer;
import pt.ulisboa.tecnico.cmov.locmess.services.LocationService;

/**
 * Created by Miguel on 20/04/2017.
 */

public class GlobalLocMess extends Application {

    private double latitude = 38.7378954;
    private double longitude = -9.1378972;

    private ConcurrentHashMap<String, Post> postsMap = new ConcurrentHashMap<>();

    private ArrayList<SimWifiP2pDevice> currentWifis = new ArrayList<>();

    private ArrayList<SimWifiP2pDevice> devicesToDelivery = new ArrayList<>();
    private ArrayList<Post> postsToDelivery = new ArrayList<>();

    public void removeExpiredPosts() {
        for(String post_id : postsMap.keySet()) {
            Post post = postsMap.get(post_id);
            if(post.isSeen()) {
                Long postLimitTime = Long.valueOf(post.getPostLifetime());
                Long currentTime = System.currentTimeMillis();
                if (postLimitTime - currentTime < 0) {
                    postsMap.remove(post_id);
                }
            }
        }
    }

    public void postsToSend(SimWifiP2pSocket mCliSocket) {
        for(Post p : postsToDelivery){
        DataOutputStream dataOutputStream;
        if (p.getRestrictionPolicy().equals("EVERYONE")) {
            String response = "Posts;:;" + p.getId() + "," + p.getUser() + "," + p.getTitle() + "," + p.getContent()
                        + "," + p.getContact() + "," + p.getCreationDateTime() + "," + p.getLimitDateTime() + ","
                        + p.getDeliveryMode() + "," + p.getLocationName();

            try {
                dataOutputStream = new DataOutputStream(mCliSocket.getOutputStream());
                dataOutputStream.writeUTF(response);
                dataOutputStream.flush();
                System.out.println("EVERYONE: " + response);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (p.getRestrictionPolicy().equals("WHITE")) {
            HashMap<String, ArrayList<String>> userRestrictions = LocMess.getUserRestrictions().get(this);
            Set<String> ures = userRestrictions.keySet();
            String restrictions = p.getRestrictions();
            HashMap<String, ArrayList<String>> postRestrictions = getRestrictionsFromPost(restrictions);
            Set<String> pres = postRestrictions.keySet();
            boolean flag = false;
            for (String res : pres) {
                if (ures.contains(res)) {
                    for (int a = 0; a < postRestrictions.get(res).size(); a++) {
                        if (userRestrictions.get(res).contains(postRestrictions.get(res).get(a))) {

                            String response = "Posts;:;" + p.getId() + "," + key.getUsername() + "," + p.getTitle() + ","
                                        + p.getContent() + "," + p.getContact() + "," + p.getCreationDateTime() + ","
                                        + p.getLimitDateTime() + "," + p.getDeliveryMode() + ","
                                        + p.getLoc().getLocationName();

                            try {
                                dataOutputStream = new DataOutputStream(mCliSocket.getOutputStream());
                                dataOutputStream.writeUTF(response);
                                dataOutputStream.flush();
                                System.out.println("WHITE: " + response);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            flag = true;
                            break;
                        }
                    }
                    if (flag) {
                        break;
                    }
                }
            }
        } else if (p.getRestrictionPolicy().equals("BLACK")) {
            HashMap<String, ArrayList<String>> userRestrictions = LocMess.getUserRestrictions().get(this);
            Set<String> ures = userRestrictions.keySet();
            String restrictions = p.getRestrictions();
            HashMap<String, ArrayList<String>> postRestrictions = getRestrictionsFromPost(restrictions);
            Set<String> pres = postRestrictions.keySet();
            int counter = 0;
            for (String res : pres) {
                if (ures.contains(res)) {
                    for (int a = 0; a < postRestrictions.get(res).size(); a++) {
                        if (userRestrictions.get(res).contains(postRestrictions.get(res).get(a))) {
                            break;
                        } else {
                            String response = "Posts;:;" + p.getId() + "," + key.getUsername() + "," + p.getTitle() + ","
                                        + p.getContent() + "," + p.getContact() + "," + p.getCreationDateTime() + ","
                                        + p.getLimitDateTime() + "," + p.getDeliveryMode() + ","
                                        + p.getLoc().getLocationName();

                            try {
                                dataOutputStream = new DataOutputStream(mCliSocket.getOutputStream());
                                dataOutputStream.writeUTF(response);
                                dataOutputStream.flush();
                                System.out.println("BLACK: " + response);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else {
                    counter++;
                    if (counter == pres.size()) {
                        String response  = "Posts;:;" + p.getId() + "," + key.getUsername() + "," + p.getTitle() + ","
                                    + p.getContent() + "," + p.getContact() + "," + p.getCreationDateTime() + ","
                                    + p.getLimitDateTime() + "," + p.getDeliveryMode() + ","
                                    + p.getLoc().getLocationName();

                        try {
                            dataOutputStream = new DataOutputStream(s.getOutputStream());
                            dataOutputStream.writeUTF(response);
                            dataOutputStream.flush();
                            System.out.println("BLACK: " + response);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }}

    public void logout() {
        getApplicationContext().stopService(new Intent(getApplicationContext(), LocationService.class));
        postsMap = new ConcurrentHashMap<>();
        currentWifis = new ArrayList<>();
        postsToDelivery = new ArrayList<>();
        ExpandableListDataPump.clean();
    }

    public void addNewPostToDelivery(Post post) {
        this.postsToDelivery.add(post);
    }

    public Post getPostToDelivery(int index) {
        return this.postsToDelivery.get(index);
    }

    public void removePostToDelivery(int index) {
        this.postsToDelivery.remove(index);
    }

    public void addDeviceToDelivery(SimWifiP2pDevice device) {
        this.devicesToDelivery.add(device);
    }

    public ArrayList<SimWifiP2pDevice> getDevicesToDelivery() {
        return this.devicesToDelivery;
    }

    public void cleanDevicesToDelivery() {
        this.devicesToDelivery = new ArrayList<>();
    }

    public void addCurrentWifi(SimWifiP2pDevice ssid) {
        this.currentWifis.add(ssid);
    }

    public ArrayList<SimWifiP2pDevice> getCurrentWifis() {
        return this.currentWifis;
    }

    public void cleanCurrentWifis() {
        this.currentWifis = new ArrayList<>();
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public ConcurrentHashMap<String, Post> getPostsMap() {
        return this.postsMap;
    }

    public void addPost(String id, Post post) {
        postsMap.put(id, post);
    }

    public Post getPost(String id) {
        return postsMap.get(id);
    }

}
