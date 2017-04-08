package pt.ulisboa.tecnico.cmov.locmess.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import pt.ulisboa.tecnico.cmov.locmess.R;
import pt.ulisboa.tecnico.cmov.locmess.activities.PostsActivity;

public class RestritionOptionActivity extends AppCompatActivity {

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
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/

                final Dialog postDialog = new Dialog(view.getContext());

                postDialog.setContentView(R.layout.dialog_new_post);

                postDialog.findViewById(R.id.button_post).setOnClickListener( new View.OnClickListener() {
                        public void onClick(View v) {
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

        findViewById(R.id.radioButton_everyone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLayoutsGone();
                findViewById(R.id.layout_everyone).setVisibility(View.VISIBLE);
            }
        });
        findViewById(R.id.radioButton_white).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLayoutsGone();
                findViewById(R.id.layout_white).setVisibility(View.VISIBLE);
            }
        });
        findViewById(R.id.radioButton_black).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLayoutsGone();
                findViewById(R.id.layout_black).setVisibility(View.VISIBLE);
            }
        });

        // Get the string array
        String[] suggestions = {"students", "Termite is Love", "Termite is Life", "ist", "macaco", "mamas", "bananas", "CMU", "Nuninho Fan Club"};

        // Get a reference to the AutoCompleteTextView in the layout
        final AutoCompleteTextView autoComplete_white = (AutoCompleteTextView) findViewById(R.id.autocomplete_white);
        // Create the adapter and set it to the AutoCompleteTextView
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, suggestions);
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
                addContentToLayout((LinearLayout) findViewById(R.id.layout_white_content), content);
                autoComplete_white.setText("");
            }
        });

        // Get a reference to the AutoCompleteTextView in the layout
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
                addContentToLayout((LinearLayout) findViewById(R.id.layout_black_content), content);
                autoComplete_black.setText("");
            }
        });

    }

    private void addContentToLayout(LinearLayout layout, String content) {

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

        Button deleteButton = new Button(this);
        deleteButton.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        deleteButton.setText("X");
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ll.setVisibility(View.GONE);
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
