package pt.ulisboa.tecnico.cmov.locmess.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.Switch;
import android.widget.TextView;

import com.mapbox.mapboxsdk.Mapbox;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;
import pt.inesc.termite.wifidirect.service.SimWifiP2pService;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketManager;
import pt.ulisboa.tecnico.cmov.locmess.utils.CollectionPagerAdapter;
import pt.ulisboa.tecnico.cmov.locmess.fragments.ProfileLocationsFragment;
import pt.ulisboa.tecnico.cmov.locmess.fragments.ProfileInterestsFragment;
import pt.ulisboa.tecnico.cmov.locmess.R;
import pt.ulisboa.tecnico.cmov.locmess.utils.GlobalLocMess;
import pt.ulisboa.tecnico.cmov.locmess.utils.SimWifiP2pBroadcastReceiver;
import pt.ulisboa.tecnico.cmov.locmess.utils.SocketHandler;

/**
 * Created by Rafael Barreira on 03/04/2017.
 */

public class ProfileActivity extends FragmentActivity  implements NavigationView.OnNavigationItemSelectedListener {
    CollectionPagerAdapter mCollectionPagerAdapter;
    ViewPager mViewPager;
    private static final int REQUEST_CREATE_POST = 0;
    TabLayout tabLayout;
    Switch mySwitch;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.profile_activity_view);

        TextView text3 = (TextView) findViewById(R.id.username);
        text3.setText(SocketHandler.getUsername());

        mySwitch = (Switch) findViewById(R.id.termiteSwitch);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        setupViewPager(mViewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setOnTabSelectedListener(onTabSelectedListener(mViewPager));


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(ProfileActivity.this);

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

        //set the switch to OFF
        mySwitch.setChecked(false);
        //attach a listener to check for changes in state
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                } else {

                }
            }
        });
    }


    private TabLayout.OnTabSelectedListener onTabSelectedListener(final ViewPager viewPager) {

        return new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        };
    }

    private void setupViewPager(ViewPager viewPager) {
        CollectionPagerAdapter adapter = new CollectionPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ProfileLocationsFragment(), "About");
        adapter.addFragment(new ProfileInterestsFragment(), "Interests");
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        onTabSelectedListener(viewPager);
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
            Log.d("DEBUG", "TESTE 0");

        } else if (id == R.id.nav_posts) {
            Intent intent = new Intent(ProfileActivity.this, PostsActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_myposts) {
            Intent intent = new Intent(ProfileActivity.this, MyPostsActivity.class);
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
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
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
