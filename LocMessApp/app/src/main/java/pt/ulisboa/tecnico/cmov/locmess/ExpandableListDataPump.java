package pt.ulisboa.tecnico.cmov.locmess;

/**
 * Created by dharuqueshil on 31/03/2017.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpandableListDataPump {

    private static HashMap<Integer, List<String>> expandableListDetail = new HashMap<Integer, List<String>>();
    private static int count = 0;

    public static HashMap<Integer, List<String>> getData() {
        if(expandableListDetail.size() == 0) {
            populate();
            return expandableListDetail;
        }
        else
            return expandableListDetail;
    }

    public static void populate(){
        setData("Macaco", "Eu gosto de macacos", "211234561a");
        setData("Macaco1", "Eu gosto de macacos1", "211234561b");
        setData("Macaco2", "Eu gosto de macacos2", "211234561c");
        setData("Macaco3", "Eu gosto de macacos3", "211234561d");
        setData("Macaco", "Eu gosto de macacos", "211234561a");
        setData("Macaco1", "Eu gosto de macacos1", "211234561b");
        setData("Macaco2", "Eu gosto de macacos2", "211234561c");
        setData("Macaco3", "Eu gosto de macacos3", "211234561d");
        setData("Macaco", "Eu gosto de macacos", "211234561a");
        setData("Macaco1", "Eu gosto de macacos1", "211234561b");
        setData("Macaco2", "Eu gosto de macacos2", "211234561c");
        setData("Macaco3", "Eu gosto de macacos3", "211234561d");
    }

    public static void setData(String title, String content, String contact){
        List<String> aux = new ArrayList<String>();
        aux.add(title);
        aux.add(content);
        aux.add(contact);
        expandableListDetail.put(getCount(), aux);
        setCount();

    }

    public static int getCount() {
        return count;
    }

    public static void setCount() {
        count = count + 1;
    }

}