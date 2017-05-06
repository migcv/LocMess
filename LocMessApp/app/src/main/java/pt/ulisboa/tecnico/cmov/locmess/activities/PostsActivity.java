package pt.ulisboa.tecnico.cmov.locmess.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.locmess.utils.GlobalLocMess;
import pt.ulisboa.tecnico.cmov.locmess.utils.PostsListAdapter;
import pt.ulisboa.tecnico.cmov.locmess.utils.ExpandableListDataPump;
import pt.ulisboa.tecnico.cmov.locmess.R;
import pt.ulisboa.tecnico.cmov.locmess.utils.SocketHandler;


public class PostsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int REQUEST_CREATE_POST = 0;
    Handler mHandler;
    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts);

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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(PostsActivity.this);

        expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
        ArrayList<List<String>> expandableList = ExpandableListDataPump.setPost(((GlobalLocMess)getApplicationContext()).getPostsMap());

        ArrayList<String> expandableListTitle = new ArrayList<String>();
        for(int i = 0; i < expandableList.size(); i++) {
            expandableListTitle.add(expandableList.get(i).get(0));
        }
        expandableListAdapter = new PostsListAdapter(this, expandableListTitle, expandableList);
        expandableListView.setAdapter(expandableListAdapter);

        this.mHandler = new Handler();
        m_Runnable.run();
    }

    private final Runnable m_Runnable = new Runnable() {
        public void run() {

            PostsActivity.this.mHandler.postDelayed(m_Runnable,10000);
            GlobalLocMess global = (GlobalLocMess) getApplicationContext();
            if(expandableListAdapter.getGroupCount() != global.getPostsMap().size()){
                expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
                ArrayList<List<String>> expandableList = ExpandableListDataPump.setPost(((GlobalLocMess)getApplicationContext()).getPostsMap());

                ArrayList<String> expandableListTitle = new ArrayList<String>();
                for(int i = 0; i < expandableList.size(); i++) {
                    expandableListTitle.add(expandableList.get(i).get(0));
                }
                expandableListAdapter = new PostsListAdapter(getApplicationContext(), expandableListTitle, expandableList);
                expandableListView.setAdapter(expandableListAdapter);
                Toast.makeText(PostsActivity.this,"in runnable",Toast.LENGTH_SHORT).show();
            }
        }
    };

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
            Intent intent = new Intent(PostsActivity.this, ProfileActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_posts) {

        } else if (id == R.id.nav_myposts) {
            Intent intent = new Intent(PostsActivity.this, MyPostsActivity.class);
            startActivity(intent);

        }  else if (id == R.id.nav_logout) {
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
                Intent intent = new Intent(PostsActivity.this, MainActivity.class);
                startActivity(intent);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}


