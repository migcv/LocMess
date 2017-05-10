package pt.ulisboa.tecnico.cmov.locmess.utils;

import android.app.Application;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Messenger;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import pt.inesc.termite.wifidirect.SimWifiP2pManager;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketServer;

/**
 * Created by Miguel on 20/04/2017.
 */

public class GlobalLocMess extends Application {

    private double latitude = 38.7378954;

    private double longitude = -9.1378972;

    private ConcurrentHashMap<String, Post> postsMap = new ConcurrentHashMap<>();

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

    public void logout() {
        postsMap = new ConcurrentHashMap<>();
        ExpandableListDataPump.clean();
    }

}
