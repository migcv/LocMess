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



}
