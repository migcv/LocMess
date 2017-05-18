package pt.ulisboa.tecnico.cmov.locmess.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.locmess.utils.ExpandableListDataPump;
import pt.ulisboa.tecnico.cmov.locmess.utils.GlobalLocMess;
import pt.ulisboa.tecnico.cmov.locmess.utils.MyPostsListAdapter;
import pt.ulisboa.tecnico.cmov.locmess.R;
import pt.ulisboa.tecnico.cmov.locmess.utils.SocketHandler;

/**
 * Created by Rafael Barreira on 06/04/2017.
 */

public class MyPostsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int REQUEST_CREATE_POST = 0;
    ArrayList<List<String>> expandableMap;
    ArrayList<String> expandableListTitle;
    ExpandableListAdapter expandableListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_posts_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                Intent intent = new Intent(getApplicationContext(), CreatePostActivity.class);
                startActivityForResult(intent, REQUEST_CREATE_POST);
            }
        });

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            String toSend = "MYPosts;:;" + SocketHandler.getToken();
            Socket s = SocketHandler.getSocket();
            Log.d("CONNECTION", "Connection successful!");
            DataOutputStream dout = new DataOutputStream(s.getOutputStream());
            dout.writeUTF(toSend);
            dout.flush();
            //dout.close();
            Log.d("MYPosts", toSend);
        } catch (IOException e) {
            e.printStackTrace();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(MyPostsActivity.this);

        ExpandableListView expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
        expandableMap = ExpandableListDataPump.getMyPosts();

        expandableListTitle = new ArrayList<String>();
        for(int i = 0; i < expandableMap.size(); i++) {
            expandableListTitle.add(expandableMap.get(i).get(1));
        }
        expandableListAdapter = new MyPostsListAdapter(this, expandableListTitle, expandableMap);
        expandableListView.setAdapter(expandableListAdapter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            Intent intent = new Intent(MyPostsActivity.this, ProfileActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_posts) {
            Intent intent = new Intent(MyPostsActivity.this, PostsActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_myposts) {

        } else if (id == R.id.nav_logout) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            try {
                Socket s = SocketHandler.getSocket();
                Log.d("CONNECTION", "Connection successful!");
                DataOutputStream dout = new DataOutputStream(s.getOutputStream());
                dout.writeUTF("SignOut;:;" + SocketHandler.getToken());
                dout.flush();
                s.close();
                ((GlobalLocMess) getApplicationContext()).logout();
                Intent intent = new Intent(MyPostsActivity.this, MainActivity.class);
                startActivity(intent);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }
}

