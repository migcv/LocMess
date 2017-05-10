package pt.ulisboa.tecnico.cmov.locmess.utils;

import java.util.ArrayList;

/**
 * Created by dharuqueshil on 21/04/2017.
 */

public class Location {

    private String type;
    private String location;
    private double latitude;
    private double longitude;
    private String[] wifi;

    public Location(String type, String location) {
        this.type = type;
        this.location = location;
        if(type.equals("GPS")) {
            String[] splt = location.split(", ");
            this.latitude = Double.parseDouble(splt[0]);
            this.longitude = Double.parseDouble(splt[1]);
        }
        else if(type.equals("WIFI")) {
            this.location = location;
        }
    }

    public Location(String type, String[] wifi) {
        this.type = type;
        this.wifi = wifi;
    }

    public String getType() {
        return type;
    }

    public String[] getWifi() {
        return wifi;
    }

    public String getLocation() {
        return location;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
