package pt.ulisboa.tecnico.cmov.locmess.utils;

import java.util.ArrayList;

/**
 * Created by dharuqueshil on 21/04/2017.
 */

public class Location {

    private String type;
    private String gps;
    private double latitude;
    private double longitude;
    private ArrayList<String> wifi;

    public Location(String type, String gps) {
        this.type = type;
        this.gps = gps;
        String[] splt = gps.split(", ");
        this.latitude = Double.parseDouble(splt[0]);
        this.longitude = Double.parseDouble(splt[1]);
    }

    public Location(String type, ArrayList<String> wifi) {
        this.type = type;
        this.wifi = wifi;
    }

    public String getType() {
        return type;
    }

    public ArrayList<String> getWifi() {
        return wifi;
    }

    public String getGps() {
        return gps;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
