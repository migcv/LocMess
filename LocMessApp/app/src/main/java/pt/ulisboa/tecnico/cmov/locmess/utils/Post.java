package pt.ulisboa.tecnico.cmov.locmess.utils;

/**
 * Created by Miguel on 22/04/2017.
 */

public class Post {

    private String tittle;
    private String user;
    private String content;
    private String contact;
    private String date;
    private String type;

    private boolean seen = false;

    public Post(String user, String tittle, String content, String contact, String date, String type) {
        this.tittle = tittle;
        this.user = user;
        this.content = content;
        this.contact = contact;
        this.date = date;
        this.type = type;
    }
}
