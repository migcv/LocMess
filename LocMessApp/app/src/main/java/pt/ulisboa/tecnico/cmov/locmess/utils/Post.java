package pt.ulisboa.tecnico.cmov.locmess.utils;

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

    public Post(String user, String tittle, String content, String contact, String post_time, String post_lifetime, String type, String location_name) {
        this.tittle = tittle;
        this.user = user;
        this.content = content;
        this.contact = contact;
        this.post_lifetime = post_lifetime;
        this.post_time = post_time;
        this.type = type;
        this.location_name = location_name;
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
