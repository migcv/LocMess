package pt.ulisboa.tecnico.cmov.locmess.utils;

/**
 * Created by dharuqueshil on 31/03/2017.
 */

import android.util.Log;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.SocketImpl;
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
        String str = "";
        try {
            DataInputStream dis = new DataInputStream(SocketHandler.getSocket().getInputStream());
            str = dis.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("POPULATE", str);
    }

    public static void setData(String title, String content, String contact, String date, String time, String deliveryMode ){
        List<String> aux = new ArrayList<String>();
        aux.add(title);
        aux.add(content);
        aux.add(contact);
        aux.add(date);
        aux.add(time);
        aux.add(deliveryMode);
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