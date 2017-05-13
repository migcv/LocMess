package pt.ulisboa.tecnico.cmov.locmess.utils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Miguel on 22/04/2017.
 */

public class Post {

    private String tittle;
    private String user;
    private String content;
    private String contact;
    private String post_lifetime;
    private String post_time;
    private String type;
    private String location_name;

    private boolean seen = false;

    // FOR DECENTRALIZED POSTS

    private double latitude;
    private double longitude;
    private Integer radius;

    private ArrayList<String> ssids = new ArrayList<>();

    private String restrictionPolicy;
    private HashMap<String, ArrayList<String>> restrictions = new HashMap<>();

    public Post(String user, String tittle, String content, String contact, String post_time, String post_lifetime, String type, String location_name, Object... decentralizedArguments) {
        this.user = user;
        this.tittle = tittle;
        this.content = content;
        this.contact = contact;
        this.post_lifetime = post_lifetime;
        this.post_time = post_time;
        this.type = type;
        this.location_name = location_name;
        if(decentralizedArguments.length > 0) {
            if(type.equals("GPS")) {
                radius = (Integer) decentralizedArguments[0];
            } else if(type.equals("WIFI")) {
                ssids = (ArrayList) decentralizedArguments[0];
            }
            restrictionPolicy = (String) decentralizedArguments[1];
            if(!restrictionPolicy.equals(NewPost.EVERYONE)) {
                restrictions = (HashMap) decentralizedArguments[2];
            }
        }
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public Integer getRadius() {
        return radius;
    }

    public ArrayList<String> getSsids() {
        return ssids;
    }

    public String getRestrictionPolicy() {
        return restrictionPolicy;
    }

    public HashMap<String, ArrayList<String>> getRestrictions() {
        return this.restrictions;
    }

    public String getTittle() {
        return tittle;
    }

    public String getUser() {
        return user;
    }

    public String getContent() {
        return content;
    }

    public String getContact() {
        return contact;
    }

    public String getPostLifetime() {
        return post_lifetime;
    }

    public String getPostTime() {
        return post_time;
    }

    public String getLocationName() {
        return location_name;
    }

    public String getType() {
        return type;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }
}
