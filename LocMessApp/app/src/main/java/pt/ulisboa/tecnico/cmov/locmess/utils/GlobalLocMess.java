package pt.ulisboa.tecnico.cmov.locmess.utils;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Messenger;
import android.util.Log;

import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
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

    private double latitude;
    private double longitude;

    private HashMap<String, ArrayList<String>> userInterests = new HashMap<>();

    private ConcurrentHashMap<String, Post> postsMap = new ConcurrentHashMap<>();

    private ArrayList<SimWifiP2pDevice> currentWifis = new ArrayList<>();

    private ArrayList<SimWifiP2pDevice> devicesToDelivery = new ArrayList<>();
    private ConcurrentHashMap<String, Post> postsToDelivery = new ConcurrentHashMap<>();

    public void removeExpiredPosts() {
        // POSTS RECEIVED
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
        // POSTS TO DELIVERY
        for(int i = 0; postsToDelivery.size() < i; i++) {
            Post post = postsToDelivery.get(i);
            Long postLimitTime = Long.valueOf(post.getPostLifetime());
            Long currentTime = System.currentTimeMillis();
            if (postLimitTime - currentTime < 0) {
                removePostToDelivery(i);
            }
        }
    }

    public void checkPostToSend() {
        GlobalLocMess globalLocMess = ((GlobalLocMess) getApplicationContext());
        for(String id : globalLocMess.getPostsToDelivery().keySet()) {
            if(globalLocMess.getPost(id) == null && !postsToDelivery.get(id).getUser().equals(SocketHandler.getUsername())) {
                Post post = postsToDelivery.get(id);
                if (post.getType().equals("GPS")) {
                    if(verifyPostRange(getLatitude(), getLongitude(), post.getLatitude(), post.getLongitude(), post.getRadius())) {
                        if(verifyRestrictions(post)) {
                            addPost(id, post);
                        }
                    }
                } else if (post.getType().equals("WIFI")) {
                    if(verifyPostWIFI(getCurrentWifis(),post.getSsids())) {
                        if(verifyRestrictions(post)) {
                            addPost(id, post);
                        }
                    }
                }
            }
        }
    }

    public boolean verifyRestrictions(Post p) {
        if (p.getRestrictionPolicy().equals("EVERYONE")) {
            return true;
        } else if (p.getRestrictionPolicy().equals("WHITE")) {
            Set<String> ures = userInterests.keySet();
            HashMap<String, ArrayList<String>> postRestrictions = p.getRestrictions();
            Set<String> pres = postRestrictions.keySet();
            for (String res : pres) {
                if (ures.contains(res)) {
                    for (int a = 0; a < postRestrictions.get(res).size(); a++) {
                        if (userInterests.get(res).contains(postRestrictions.get(res).get(a))) {
                            return true;
                        }
                    }
                }
            }
        } else if (p.getRestrictionPolicy().equals("BLACK")) {
            Set<String> ures = userInterests.keySet();
            HashMap<String, ArrayList<String>> postRestrictions = p.getRestrictions();
            Set<String> pres = postRestrictions.keySet();
            int counter = 0;
            for (String res : pres) {
                if (ures.contains(res)) {
                    for (int a = 0; a < postRestrictions.get(res).size(); a++) {
                        if (userInterests.get(res).contains(postRestrictions.get(res).get(a))) {
                            return false;
                        } else {
                            counter++;
                        }
                    }
                } else if (!ures.contains(res)) {
                    counter++;
                }
            }
            if (counter == pres.size()) {
                return true;
            }
        }
        return false;
    }

    public boolean verifyPostRange(double currentLat, double currentLong, double lat, double longi, double radius) {
        double earthRadius = 6371000; // meters
        double dLat = Math.toRadians(lat - currentLat);
        double dLng = Math.toRadians(longi - currentLong);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat))
                * Math.cos(Math.toRadians(currentLat)) * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        float dist = (float) (earthRadius * c);
        if (dist <= radius) {
            return true;
        }
        return false;
    }

    public boolean verifyPostWIFI(ArrayList<SimWifiP2pDevice> currentWIFI, ArrayList<String> postWIFI) {
        for (int k = 0; k < currentWIFI.size(); k++) {
            for (int j = 0; j < postWIFI.size(); j++) {
                if (currentWIFI.get(k).deviceName.equals(postWIFI.get(j))) {
                    return true;
                }
            }
        }
        return false;
    }

    public void logout() {
        getApplicationContext().stopService(new Intent(getApplicationContext(), LocationService.class));
        postsMap = new ConcurrentHashMap<>();
        currentWifis = new ArrayList<>();
        postsToDelivery = new ConcurrentHashMap<>();
        userInterests = new HashMap<>();
        ExpandableListDataPump.clean();
    }

    public HashMap<String, ArrayList<String>> getUserInterests() {
        return userInterests;
    }

    public void addPostToDelivary(String id, Post post) {
        postsToDelivery.put(id, post);
    }

    public Post getPostToDelivery(String id) {
        return this.postsToDelivery.get(id);
    }

    public ConcurrentHashMap<String,Post> getPostsToDelivery() {
        return this.postsToDelivery;
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
