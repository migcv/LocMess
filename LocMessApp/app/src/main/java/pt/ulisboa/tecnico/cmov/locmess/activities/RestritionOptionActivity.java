package pt.ulisboa.tecnico.cmov.locmess.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import pt.ulisboa.tecnico.cmov.locmess.R;
import pt.ulisboa.tecnico.cmov.locmess.activities.PostsActivity;
import pt.ulisboa.tecnico.cmov.locmess.utils.NewPost;
import pt.ulisboa.tecnico.cmov.locmess.utils.SocketHandler;

public class RestritionOptionActivity extends AppCompatActivity {

    private RadioButton radioButtonEveryone;
    private RadioButton radioButtonWhite;
    private RadioButton radioButtonBlack;

    private ArrayList<String> whiteRestrictionList = new ArrayList<String>();
    private ArrayList<String> blackRestrictionList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restrition_option);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        /* Set back arrow button on toolbar */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(radioButtonEveryone.isChecked()) {
                    NewPost.restrictionPolicy = NewPost.EVERYONE;
                }
                else if(radioButtonWhite.isChecked()) {
                    NewPost.restrictionPolicy = NewPost.WHITE;
                    NewPost.restrictionList = whiteRestrictionList;
                }
                else if(radioButtonBlack.isChecked()) {
                    NewPost.restrictionPolicy = NewPost.BLACK;
                    NewPost.restrictionList = blackRestrictionList;
                }

                final Dialog postDialog = new Dialog(view.getContext());
                postDialog.setContentView(R.layout.dialog_new_post);
                ((TextView)postDialog.findViewById(R.id.text_tittle)).setText(NewPost.tittle);
                ((TextView)postDialog.findViewById(R.id.text_content)).setText(NewPost.content);
                ((TextView)postDialog.findViewById(R.id.text_contact)).setText(NewPost.contact);
                ((TextView)postDialog.findViewById(R.id.text_date)).setText(String.format(" %02d:%02d", NewPost.hour, NewPost.minute) + " "
                                    + NewPost.day + "/" + NewPost.month + "/" + NewPost.year);
                ((TextView)postDialog.findViewById(R.id.text_delivery_mode)).setText(NewPost.deliveryMode);

                if(NewPost.deliveryMode.equals("GPS")) {
                    ((TextView) postDialog.findViewById(R.id.text_location)).setText(String.format(" %.4f, %.4f %d", NewPost.location.getLatitude(), NewPost.location.getLongitude(), NewPost.radius));
                }
                else{
                    postDialog.findViewById(R.id.location).setVisibility(View.GONE);
                    postDialog.findViewById(R.id.text_location).setVisibility(View.GONE);
                }

                ((TextView)postDialog.findViewById(R.id.text_restriction_policy)).setText(NewPost.restrictionPolicy);
                if(NewPost.restrictionList.isEmpty()) {
                    postDialog.findViewById(R.id.layout_restrictions).setVisibility(View.GONE);
                } else {
                    TextView restrictions = (TextView) postDialog.findViewById(R.id.text_restriction_list);
                    restrictions.setText(NewPost.restrictionList.get(0));
                    for (int i = 1; i < NewPost.restrictionList.size(); i++) {
                        restrictions.setText(restrictions.getText() + ", ");
                        restrictions.setText(restrictions.getText() + NewPost.restrictionList.get(i));
                    }
                }

                final String toSend;
                if(NewPost.deliveryMode.equals("GPS")){
                    toSend = "NewPosts;:;" + SocketHandler.getUsername() + ";:;"+ NewPost.tittle + ";:;" + NewPost.content + ";:;" + NewPost.contact + ";:;" + NewPost.day + "/" + NewPost.month + "/" + NewPost.year + ";:;" + String.format("%02d:%02d", NewPost.hour, NewPost.minute) + ";:;" + NewPost.deliveryMode + ";:;" + String.format("%.4f, %.4f", NewPost.location.getLatitude(), NewPost.location.getLongitude()) + ";:;" + NewPost.radius;
                }else {
                    toSend = "NewPosts;:;" + SocketHandler.getUsername() + ";:;"+ NewPost.tittle + ";:;" + NewPost.content + ";:;" + NewPost.contact + ";:;" + NewPost.day + "/" + NewPost.month + "/" + NewPost.year + ";:;" + String.format("%02d:%02d", NewPost.hour, NewPost.minute) + ";:;" + NewPost.deliveryMode;
                }

                postDialog.findViewById(R.id.button_post).setOnClickListener( new View.OnClickListener() {
                        public void onClick(View v) {
                            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                            StrictMode.setThreadPolicy(policy);
                            try {
                                Socket s = SocketHandler.getSocket();
                                Log.d("CONNECTION", "Connection successful!");
                                DataOutputStream dout = new DataOutputStream(s.getOutputStream());
                                dout.writeUTF(toSend);
                                dout.flush();
                                //dout.close();
                                Log.d("NEW POST", toSend);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Intent activity = new Intent(getApplicationContext(), PostsActivity.class);
                            startActivity(activity);
                        }
                    }
                );
                postDialog.findViewById(R.id.button_cancel).setOnClickListener( new View.OnClickListener() {
                       public void onClick(View v) {
                           postDialog.dismiss();
                       }
                   }
                );
                postDialog.show();
            }
        });

        radioButtonEveryone = (RadioButton) findViewById(R.id.radioButton_everyone);
        radioButtonEveryone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLayoutsGone();
                findViewById(R.id.layout_everyone).setVisibility(View.VISIBLE);
            }
        });
        radioButtonWhite = (RadioButton) findViewById(R.id.radioButton_white);
        radioButtonWhite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLayoutsGone();
                findViewById(R.id.layout_white).setVisibility(View.VISIBLE);
            }
        });
        radioButtonBlack = (RadioButton) findViewById(R.id.radioButton_black);
        radioButtonBlack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLayoutsGone();
                findViewById(R.id.layout_black).setVisibility(View.VISIBLE);
            }
        });

        // Get the string array
        String[] suggestions = {"students", "Termite is Love", "Termite is Life", "ist", "macaco", "mamas", "bananas", "CMU", "Nuninho Fan Club"};
        /*
         *      WHITE RESTRICTION
         */
        final AutoCompleteTextView autoComplete_white = (AutoCompleteTextView) findViewById(R.id.autocomplete_white);
        // Create the adapter and set it to the AutoCompleteTextView
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, suggestions);
        autoComplete_white.setAdapter(adapter);
        autoComplete_white.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event){
                autoComplete_white.showDropDown();
                return false;
            }
        });
        findViewById(R.id.button_add_white).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = autoComplete_white.getText().toString();
                findViewById(R.id.white_placeholder).setVisibility(View.GONE);
                if(!whiteRestrictionList.contains(content)) {
                    addContentToLayout((LinearLayout) findViewById(R.id.layout_white_content), content);
                    autoComplete_white.setText("");
                } else {
                    Snackbar.make(view, "Restriction already added", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });
        /*
         *      BLACK RESTRICTION
         */
        final AutoCompleteTextView autoComplete_black = (AutoCompleteTextView) findViewById(R.id.autocomplete_black);
        // Create the adapter and set it to the AutoCompleteTextView
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, suggestions);
        autoComplete_black.setAdapter(adapter);
        autoComplete_black.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event){
                autoComplete_black.showDropDown();
                return false;
            }
        });
        findViewById(R.id.button_add_black).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = autoComplete_black.getText().toString();
                findViewById(R.id.black_placeholder).setVisibility(View.GONE);
                if(!blackRestrictionList.contains(content)) {
                    addContentToLayout((LinearLayout) findViewById(R.id.layout_black_content), content);
                    autoComplete_black.setText("");
                } else {
                    Snackbar.make(view, "Restriction already added", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                }
            }
        });

    }

    private void addContentToLayout(LinearLayout layout, final String content) {

        String restrictionPolicy = null;

        final LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));

        TextView text = new TextView(this);
        text.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f));
        text.setText("" + content);

        ll.addView(text);

        if(layout.getId() == R.id.layout_black_content) {
            restrictionPolicy = NewPost.BLACK;
            blackRestrictionList.add(content);
        }
        else if(layout.getId() == R.id.layout_white_content) {
            restrictionPolicy = NewPost.WHITE;
            whiteRestrictionList.add(content);
        }

        final String restriciotnFinal = restrictionPolicy;
        Button deleteButton = new Button(this);
        deleteButton.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        deleteButton.setText("X");
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String restriction = restriciotnFinal;
                if(restriction.equals(NewPost.BLACK)) {
                    removeContent(blackRestrictionList, content);
                } else {
                    removeContent(whiteRestrictionList, content);
                }
                ll.setVisibility(View.GONE);
            }

            private void removeContent(ArrayList<String> list, String content) {
                for(int i = 0; i < list.size(); i++) {
                    if(list.get(i).equals(content)) {
                        list.remove(i);
                        break;
                    }
                }
            }
        });

        ll.addView(deleteButton);

        layout.addView(ll);
    }

    private void setLayoutsGone(){
        findViewById(R.id.layout_everyone).setVisibility(View.GONE);
        findViewById(R.id.layout_white).setVisibility(View.GONE);
        findViewById(R.id.layout_black).setVisibility(View.GONE);
    }

}
