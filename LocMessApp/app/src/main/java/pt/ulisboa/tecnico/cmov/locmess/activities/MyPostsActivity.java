package pt.ulisboa.tecnico.cmov.locmess.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pt.ulisboa.tecnico.cmov.locmess.utils.ExpandableListDataPump;
import pt.ulisboa.tecnico.cmov.locmess.utils.MyPostsExpandableListaAdapter;
import pt.ulisboa.tecnico.cmov.locmess.R;
import pt.ulisboa.tecnico.cmov.locmess.utils.SocketHandler;

/**
 * Created by Rafael Barreira on 06/04/2017.
 */

public class MyPostsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ArrayList<List<String>> expandableMap;
    ArrayList<String> expandableListTitle;
    ExpandableListAdapter expandableListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_posts_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);

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
            Log.d("MY_POSTS", expandableMap.get(i) == null ? "NULL" : "" + expandableMap.get(i).get(0));
            expandableListTitle.add(expandableMap.get(i).get(1));
            Log.d("MY_POSTS", expandableListTitle.get(i) == null ? "NULL" : i + "" + expandableListTitle.get(i));
        }
        expandableListAdapter = new MyPostsExpandableListaAdapter(this, expandableListTitle, expandableMap);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.posts, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_logout) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

