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

    private SimWifiP2pManager mManager;
    private SimWifiP2pManager.Channel mChannel;
    private Messenger mService;
    private boolean mBound = false;
    private SimWifiP2pSocketServer mSrvSocket;

    public SimWifiP2pSocketServer getmSrvSocket() {
        return mSrvSocket;
    }

    public void setmSrvSocket(SimWifiP2pSocketServer mSrvSocket) {
        this.mSrvSocket = mSrvSocket;
    }

    public SimWifiP2pManager getSimWifiP2pManager() {
        return mManager;
    }

    public void setSimWifiP2pManager(SimWifiP2pManager mManager){
        this.mManager = mManager;
    }

    public SimWifiP2pManager.Channel getmChannel() {
        return mChannel;
    }

    public void setmChannel(SimWifiP2pManager.Channel mChannel) {
        this.mChannel = mChannel;
    }

    public Messenger getmService() {
        return mService;
    }

    public void setmService(Messenger mService) {
        this.mService = mService;
    }

    public boolean ismBound() {
        return mBound;
    }

    public void setmBound(boolean mBound) {
        this.mBound = mBound;
    }

}
