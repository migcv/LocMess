package pt.ulisboa.tecnico.cmov.locmess.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import pt.ulisboa.tecnico.cmov.locmess.R;
import pt.ulisboa.tecnico.cmov.locmess.utils.SocketHandler;

/**
 * Created by Rafael Barreira on 05/04/2017.
 */

public class ProfileInterestsFragment extends Fragment {

    View view;

    HashMap<String, ArrayList<String>> restrictionsApp = new HashMap<>();

    HashMap<String, LinearLayout> layoutTopicMap = new HashMap<>();

    LinearLayout interestLayout;

    AutoCompleteTextView topicView;
    AutoCompleteTextView valueView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.profile_fragment_interests, container, false);

        populateRestrictions();

        interestLayout = (LinearLayout) view.findViewById(R.id.layout_interests);

        topicView = (AutoCompleteTextView) view.findViewById(R.id.autocomplete_topic);
        valueView = (AutoCompleteTextView) view.findViewById(R.id.autocomplete_value);

        view.findViewById(R.id.button_add_interest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean error = false;

                String topic = topicView.getText().toString();
                String value = valueView.getText().toString();

                if(topic.isEmpty()) {
                    topicView.setError("Topic can't be empty!");
                }
                if(value.isEmpty()) {
                    valueView.setError("Value can't be empty!");
                }
                if(error) {
                    return;
                }
                if(restrictionsApp.containsKey(topic)) {
                    ArrayList<String> list = restrictionsApp.get(topic);
                    if(list.contains(value)) {
                        valueView.setError("You already have that interest!");
                        return;
                    } else {
                        // Adds to Map
                        list.add(value);
                        restrictionsApp.put(topic, list);

                        // Adds to layout
                        LinearLayout topicLayout = layoutTopicMap.get(topic);
                        topicLayout.addView(addNewInterestValue(topic, value));
                    }
                } else {
                    // Adds to Map
                    ArrayList<String> list = new ArrayList<String>();
                    list.add(value);
                    restrictionsApp.put(topic, list);

                    // Adds to layout
                    LinearLayout topicLayout = new LinearLayout(view.getContext());
                    topicLayout.setOrientation(LinearLayout.VERTICAL);
                    topicLayout.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT));
                    topicLayout.setId(View.generateViewId());

                    TextView tv = new TextView(view.getContext());
                    tv.setText(topic);
                    tv.setTextSize(18);
                    tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
                    topicLayout.addView(tv);

                    layoutTopicMap.put(topic, topicLayout);

                    LinearLayout newLayout = addNewInterestValue(topic, value);
                    topicLayout.addView(newLayout);

                    interestLayout.addView(topicLayout);
                }
                addRestriction(SocketHandler.getToken(), topic, value);
                topicView.setText("");
                valueView.setText("");
            }
        });

        populateView();

        return view;
    }

    public void addRestriction(String token, String topic, String value){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        String toSend = "AddRestrictions;:;" + token + ";:;" + topic + ":" + value;
        try {
            Socket s = SocketHandler.getSocket();
            Log.d("CONNECTION", "Connection successful!");
            DataOutputStream dout = new DataOutputStream(s.getOutputStream());
            dout.writeUTF(toSend);
            dout.flush();
            Log.d("RESTRICTIONS", toSend);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void populateRestrictions() {
        String str;
        try {
            Socket s = SocketHandler.getSocket();
            DataOutputStream dout = new DataOutputStream(s.getOutputStream());
            dout.writeUTF("MYRestrictions;:;" + SocketHandler.getToken());
            dout.flush();
            DataInputStream dis = new DataInputStream(s.getInputStream());
            str = dis.readUTF();
            if(str.equals("WRONG")){
                return;
            }
            String[] aux1 = str.split(";:;");

            for(int i = 0; i< aux1.length;i++) {
                String[] aux2 = aux1[i].split(",");
                if(!(restrictionsApp.containsKey(aux2[0]))){
                    restrictionsApp.put(aux2[0], new ArrayList<String>());
                    restrictionsApp.get(aux2[0]).add(aux2[1]);
                }else if(restrictionsApp.containsKey(aux2[0]) &&!(restrictionsApp.get(aux2[0]).contains(aux2[1]))){
                    restrictionsApp.get(aux2[0]).add(aux2[1]);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void populateView() {

        for (Map.Entry<String, ArrayList<String>> entry : restrictionsApp.entrySet()) {
            String key = entry.getKey();
            ArrayList<String> list = entry.getValue();

            LinearLayout topicLayout = new LinearLayout(this.getContext());
            topicLayout.setOrientation(LinearLayout.VERTICAL);
            topicLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT));
            topicLayout.setId(View.generateViewId());

            TextView tv = new TextView(this.getContext());
            tv.setText(key);
            tv.setTextSize(18);
            tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
            topicLayout.addView(tv);

            layoutTopicMap.put(key, topicLayout);

            for (String s : list) {
                LinearLayout newLayout = addNewInterestValue(key, s);
                topicLayout.addView(newLayout);
            }

            interestLayout.addView(topicLayout);
        }
    }

    private LinearLayout addNewInterestValue(final String topic, final String value) {
        final LinearLayout newLayout = new LinearLayout(this.getContext());
        newLayout.setOrientation(LinearLayout.HORIZONTAL);
        newLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));

        TextView text = new TextView(this.getContext());
        text.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f));
        text.setText("" + value);

        newLayout.addView(text);

        Button deleteButton = new Button(this.getContext());
        deleteButton.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        deleteButton.setText("X");
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                restrictionsApp.get(topic).remove(value);
                layoutTopicMap.get(topic).removeView(newLayout);

                Log.d("CHILD", "" + layoutTopicMap.get(topic).getChildCount());

                if(layoutTopicMap.get(topic).getChildCount() == 1) {
                    restrictionsApp.remove(topic);
                    layoutTopicMap.get(topic).setVisibility(View.GONE);
                }

                // TODO Connection to Server
                removeRestriction(SocketHandler.getToken(), topic, value);
            }
        });

        newLayout.addView(deleteButton);

        return newLayout;
    }

    public void removeRestriction(String token, String topic, String value){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        String toSend = "RemoveRestrictions;:;" + token + ";:;" + topic + ":" + value;
        try {
            Socket s = SocketHandler.getSocket();
            Log.d("CONNECTION", "Connection successful!");
            DataOutputStream dout = new DataOutputStream(s.getOutputStream());
            dout.writeUTF(toSend);
            dout.flush();
            Log.d("RESTRICTIONS", toSend);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
