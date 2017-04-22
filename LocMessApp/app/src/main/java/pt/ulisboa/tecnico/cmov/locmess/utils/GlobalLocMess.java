package pt.ulisboa.tecnico.cmov.locmess.utils;

import android.app.Application;

import java.util.HashMap;

/**
 * Created by Miguel on 20/04/2017.
 */

public class GlobalLocMess extends Application {

    private double latitude = 38.7378954;

    private double longitude = -9.1378972;

    private HashMap<String, Post> postsMap = new HashMap<>();

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

    public void addPost() {

    }

}
