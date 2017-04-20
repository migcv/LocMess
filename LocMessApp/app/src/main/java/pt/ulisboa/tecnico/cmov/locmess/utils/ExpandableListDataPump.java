package pt.ulisboa.tecnico.cmov.locmess.utils;

/**
 * Created by dharuqueshil on 31/03/2017.
 */

import android.util.Log;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpandableListDataPump {

    private static  ArrayList<List<String>> expandableListDetail = new ArrayList<List<String>>();
    private static ArrayList<List<String>> myPosts = new ArrayList<List<String>>();
    private static int count = 0;

    public static ArrayList<List<String>> getData() {
        if(expandableListDetail.size() == 0) {
            populate();
            return expandableListDetail;
        }
        else
            return expandableListDetail;
    }

    public static void populate() {
        setData("Macaco", "Eu gosto de macacos", "211234561a", "", "", "");
        setData("Macaco1", "Eu gosto de macacos1", "211234561b", "", "", "");
        setData("Macaco2", "Eu gosto de macacos2", "211234561c", "", "", "");
        setData("Macaco3", "Eu gosto de macacos3", "211234561d", "", "", "");
    }

    public static ArrayList<List<String>> getMyPosts() {
        try {
            myPosts = new ArrayList<List<String>>();

            DataInputStream dis = new DataInputStream(SocketHandler.getSocket().getInputStream());
            String response = dis.readUTF();
            Log.d("GET_MY_POSTS", response);

            String[] responseSplitted = response.split(";:;");

            while(!responseSplitted[0].equals("END")) {
                String[] postArguments = responseSplitted[1].split(",");
                setDataMyPost(postArguments[0], postArguments[1], postArguments[2], postArguments[3], postArguments[4], postArguments[5], postArguments[6]);

                response = dis.readUTF();
                Log.d("GET_MY_POSTS", response);
                Log.d("POSTS_ARGUMENTS", postArguments[0]);
                Log.d("POSTS_ARGUMENTS", postArguments[1]);
                Log.d("POSTS_ARGUMENTS", postArguments[2]);
                Log.d("POSTS_ARGUMENTS", postArguments[3]);
                Log.d("POSTS_ARGUMENTS", postArguments[4]);
                Log.d("POSTS_ARGUMENTS", postArguments[5]);

                responseSplitted = response.split(";:;");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return myPosts;
    }

    public static void setData(String title, String content, String contact, String date, String time, String deliveryMode ){
        List<String> aux = new ArrayList<String>();
        aux.add(title);
        aux.add(content);
        aux.add(contact);
        aux.add(date);
        aux.add(time);
        aux.add(deliveryMode);
        expandableListDetail.add(aux);
        setCount();

    }

    public static void setDataMyPost(String id, String title, String content, String contact, String date, String time, String deliveryMode ){
        List<String> aux = new ArrayList<String>();
        aux.add(id);
        aux.add(title);
        aux.add(content);
        aux.add(contact);
        aux.add(date);
        aux.add(time);
        aux.add(deliveryMode);
        myPosts.add(aux);
    }

    public static int getCount() {
        return count;
    }

    public static void setCount() {
        count = count + 1;
    }

}