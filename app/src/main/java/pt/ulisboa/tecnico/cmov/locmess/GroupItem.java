package pt.ulisboa.tecnico.cmov.locmess;

/**
 * Created by dharuqueshil on 27/03/2017.
 */

public class GroupItem extends BaseItem {

    private int mLevel;

    public GroupItem(String title) {
        super(title);
        mLevel = 0;
    }

    public void setLevel(int level){
        mLevel = level;
    }

    public int getLevel(){
        return mLevel;
    }
}