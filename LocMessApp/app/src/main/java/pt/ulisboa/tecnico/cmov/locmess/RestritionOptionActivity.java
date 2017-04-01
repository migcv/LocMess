package pt.ulisboa.tecnico.cmov.locmess;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

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
    }

}
