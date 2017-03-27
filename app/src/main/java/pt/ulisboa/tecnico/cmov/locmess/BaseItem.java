package pt.ulisboa.tecnico.cmov.locmess;

/**
 * Created by dharuqueshil on 26/03/2017.
 */

public class BaseItem {

    private String title;
    private String content;
    private String contact;
    private String tags;

    public BaseItem(String title) {
        this.title = title;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

}
