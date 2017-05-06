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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ExpandableListDataPump {

    private static  ArrayList<List<String>> posts = new ArrayList<List<String>>();
    private static ArrayList<List<String>> myPosts = new ArrayList<List<String>>();
    private static int count = 0;

    public static ArrayList<List<String>> setPost(Map<String, Post> postsMap) {
        posts = new ArrayList<List<String>>();

        for(String id : postsMap.keySet()) {
            Post post = postsMap.get(id);
            List<String> aux = new ArrayList<String>();
            aux.add(post.getTittle());
            aux.add(post.getContent());
            aux.add(post.getContact());
            aux.add(post.getUser());
            aux.add(post.getPostTime());
            aux.add(post.getPostLifetime());
            aux.add(post.getType());
            aux.add(post.getLocationName());
            aux.add(id);
            posts.add(aux);
        }
        return posts;
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
        posts.add(aux);
        setCount();
    }

    public static void setDataMyPost(String id, String title, String content, String contact, String post_time, String post_lifetime, String deliveryMode ){
        List<String> aux = new ArrayList<String>();
        aux.add(id);
        aux.add(title);
        aux.add(content);
        aux.add(contact);
        aux.add(post_time);
        aux.add(post_lifetime);
        aux.add(deliveryMode);
        myPosts.add(aux);
    }

    public static int getCount() {
        return count;
    }

    public static void setCount() {
        count = count + 1;
    }

    public static void clean() {
        posts = new ArrayList<List<String>>();
        myPosts = new ArrayList<List<String>>();
    }

}