package pt.ulisboa.tecnico.cmov.locmess.utils;

import java.util.ArrayList;

import com.mapbox.mapboxsdk.geometry.LatLng;

/**
 * Created by Miguel on 08/04/2017.
 */

public class NewPost {

    public static final String GPS = "GPS";
    public static final String WIFI = "WIFI";
    public static final String WIFI_DIRECT = "WIFI_DIRECT";

    public static final String EVERYONE = "EVERYONE";
    public static final String WHITE = "WHITE";
    public static final String BLACK = "BLACK";

    public static String tittle;
    public static String content;
    public static String contact;

    public static long lifetime;

    public static String deliveryMode;
    public static String location_name;

    public static LatLng location;
    public static int radius;

    public static String restrictionPolicy;
    public static ArrayList<String> restrictionList = new ArrayList<>();

}
