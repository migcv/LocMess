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
    HashMap<String, ArrayList<String>> restrinctionsUser = new HashMap<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.profile_fragment2_view, container, false);


        restrictionsApp.put("Profession", new ArrayList<String>());
        restrictionsApp.get("Profession").add("Estudante");
        restrictionsApp.get("Profession").add("Pedreiro");
        restrictionsApp.put("Music", new ArrayList<String>());
        restrictionsApp.get("Music").add("Rock");
        restrictionsApp.get("Music").add("Pop");
        restrictionsApp.put("Monkeys", new ArrayList<String>());
        restrictionsApp.get("Monkeys").add("Adriano");
        restrictionsApp.get("Monkeys").add("Kong");

        v = populateView(v);

        populateRestrictions();

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
            String[] aux1 = str.split(":");
            restrictionsApp.put(aux1[0], new ArrayList<String>());
            for(int i = 0; i< aux1.length;i++){
                Log.d("MYRESTRICTIONS", aux1[i]);
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
            Log.d("DEBUG", "Add TV");

            for (String s : list) {
                LinearLayout ll1 = new LinearLayout(this.getContext());
                ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                params.height = params.MATCH_PARENT;
                params.width = params.MATCH_PARENT;
                Log.d("DEBUG", "match parent");

                if (params instanceof ViewGroup.MarginLayoutParams) {
                    ((ViewGroup.MarginLayoutParams) params).topMargin = 5;
                    ((ViewGroup.MarginLayoutParams) params).leftMargin = 20;
                } else {
                    Log.e("MyApp", "Attempted to set the margins on a class that doesn't support margins");
                }

                ll1.setLayoutParams(params);
                ll1.setOrientation(LinearLayout.HORIZONTAL);
                Log.d("DEBUG", "params");
                CheckBox cb = new CheckBox(this.getContext());
                cb.setText(s);

                ll1.addView(cb);
                ll.addView(ll1);
                Log.d("DEBUG", "add views");
            }
        }

        return v;
    }


}
