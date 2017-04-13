package pt.ulisboa.tecnico.cmov.locmess.fragments;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
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

public class ProfileIntererstsFragment extends Fragment {

    HashMap<String, ArrayList<String>> restrictionsApp = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.profile_fragment2_view, container, false);

        populateRestrictions();

        v = populateView(v);
        return v;
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

            for(int i = 0; i< aux1.length;i++){
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

    public View populateView(View v) {
        // Find the LinearLayout in ScrollView
        LinearLayout ll = (LinearLayout) v.findViewById(R.id.linearLayout1);

        for (Map.Entry<String, ArrayList<String>> entry : restrictionsApp.entrySet()) {
            String key = entry.getKey();
            ArrayList<String> list = entry.getValue();
            // Add text
            TextView tv = new TextView(this.getContext());
            tv.setText(key);
            tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
            ll.addView(tv);

            for (String s : list) {
                LinearLayout ll1 = new LinearLayout(this.getContext());
                ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                params.height = params.MATCH_PARENT;
                params.width = params.MATCH_PARENT;

                if (params instanceof ViewGroup.MarginLayoutParams) {
                    ((ViewGroup.MarginLayoutParams) params).topMargin = 5;
                    ((ViewGroup.MarginLayoutParams) params).leftMargin = 20;
                } else {
                    Log.e("MyApp", "Attempted to set the margins on a class that doesn't support margins");
                }

                ll1.setLayoutParams(params);
                ll1.setOrientation(LinearLayout.HORIZONTAL);
                CheckBox cb = new CheckBox(this.getContext());
                cb.setText(s);

                ll1.addView(cb);
                ll.addView(ll1);
            }
        }

        return v;
    }


}
