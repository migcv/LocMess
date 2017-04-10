package pt.ulisboa.tecnico.cmov.locmess.utils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by dharuqueshil on 10/04/2017.
 */

public class Restrictions {

    public HashMap<String, ArrayList<String>> restrictionList = new HashMap<>();


    public void addRestriction(String topic, String interest){
        if(restrictionList.isEmpty()){
            ArrayList<String> aux = new ArrayList<>();
            aux.add(interest);
            restrictionList.put(topic, aux);
        }
        if(restrictionList.get(topic) != null){
            restrictionList.get(topic).add(interest);
        }
    }
}
