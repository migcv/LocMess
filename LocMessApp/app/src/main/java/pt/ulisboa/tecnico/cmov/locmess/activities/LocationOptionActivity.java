package pt.ulisboa.tecnico.cmov.locmess.activities;

import android.Manifest;
import android.animation.TypeEvaluator;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.annotations.PolygonOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationSource;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.services.android.telemetry.location.LocationEngine;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.inesc.termite.wifidirect.SimWifiP2pDevice;
import pt.inesc.termite.wifidirect.SimWifiP2pDeviceList;
import pt.inesc.termite.wifidirect.SimWifiP2pInfo;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;
import pt.inesc.termite.wifidirect.service.SimWifiP2pService;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketManager;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketServer;
import pt.ulisboa.tecnico.cmov.locmess.R;
import pt.ulisboa.tecnico.cmov.locmess.fragments.ProfileLocationsFragment;
import pt.ulisboa.tecnico.cmov.locmess.utils.GlobalLocMess;
import pt.ulisboa.tecnico.cmov.locmess.utils.Location;
import pt.ulisboa.tecnico.cmov.locmess.utils.NewPost;
import pt.ulisboa.tecnico.cmov.locmess.utils.SimWifiP2pBroadcastReceiver;
import pt.ulisboa.tecnico.cmov.locmess.utils.SocketHandler;
import android.widget.Toast;

import static android.os.Looper.getMainLooper;

public class LocationOptionActivity extends AppCompatActivity implements SimWifiP2pManager.PeerListListener, SimWifiP2pManager.GroupInfoListener {

    private int radius = 250;
    private LatLng userLocation;

    private MapView mapView;
    private MapboxMap map;
    private Marker marker ;
    private LocationEngine locationEngine;

    private RadioButton radioButtonLocations;
    private RadioButton radioButtonWifDirect;

    private ArrayAdapter<Object> adapter;

    private HashMap<String, Location> locationsMap = new HashMap<>();

    private SimWifiP2pManager mManager = null;
    private SimWifiP2pManager.Channel mChannel = null;
    private Messenger mService = null;
    private boolean mBound = false;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_location_option);

        // initialize the Termite API
        SimWifiP2pSocketManager.Init(getApplicationContext());
        IntentFilter filter = new IntentFilter();
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_STATE_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_PEERS_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_NETWORK_MEMBERSHIP_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_GROUP_OWNERSHIP_CHANGED_ACTION);
        SimWifiP2pBroadcastReceiver receiver = new SimWifiP2pBroadcastReceiver(this);
        registerReceiver(receiver, filter);

        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
                if(radioButtonLocations.isChecked()) {
                    NewPost.location_name = ((AutoCompleteTextView) findViewById(R.id.autocomplete_locations)).getText().toString();
                    if(locationsMap.get(NewPost.location_name) == null) {
                        Snackbar.make(view, "Location Selected Not Valid", Snackbar.LENGTH_SHORT)
                                .setAction("Action", null).show();
                        return;
                    }
                    if(locationsMap.get(NewPost.location_name).getType().equals("GPS")) {
                        NewPost.deliveryMode = NewPost.GPS;
                        NewPost.location = marker.getPosition();
                        NewPost.radius = radius;
                    } else if(locationsMap.get(NewPost.location_name).getType().equals("WIFI")) {
                        NewPost.deliveryMode = NewPost.WIFI;
                    }
                }
                else if(radioButtonWifDirect.isChecked()) {
                   // NewPost.deliveryMode = NewPost.WIFI_DIRECT;
                }

                Intent intent = new Intent(getApplicationContext(), RestritionOptionActivity.class);
                startActivity(intent);
            }
        });

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }

        populateLocations();
        final Object[] locations = locationsMap.keySet().toArray();

        final AutoCompleteTextView autoComplete_locations = (AutoCompleteTextView) findViewById(R.id.autocomplete_locations);
        // Create the adapter and set it to the AutoCompleteTextView
        adapter = new ArrayAdapter<Object>(this, android.R.layout.simple_list_item_1, locations);
        autoComplete_locations.setAdapter(adapter);
        autoComplete_locations.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event){
                autoComplete_locations.showDropDown();
                return false;
            }
        });
        ((AutoCompleteTextView) findViewById(R.id.autocomplete_locations)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String content = editable.toString();
                if(content.isEmpty() || locationsMap.get(content) == null) {
                    map.setCameraPosition(new CameraPosition.Builder()
                            .target(userLocation)
                            .build());
                    marker.setPosition(userLocation);
                    map.addPolygon(new PolygonOptions().addAll(polygonCircleForCoordinate(userLocation, radius)).fillColor(Color.parseColor("#4285F4")).alpha((float) 0.4));
                    map.removePolygon(map.getPolygons().get(0));
                } else if(locationsMap.get(content).getType().equals("GPS")) {
                    setLayoutsGone();
                    findViewById(R.id.layout_gps).setVisibility(View.VISIBLE);

                    Location loc = locationsMap.get(content);
                    LatLng latlog = new LatLng(loc.getLatitude(), loc.getLongitude());
                    map.setCameraPosition(new CameraPosition.Builder()
                            .target(latlog)
                            .build());
                    marker.setPosition(latlog);
                    map.addPolygon(new PolygonOptions().addAll(polygonCircleForCoordinate(latlog, radius)).fillColor(Color.parseColor("#4285F4")).alpha((float) 0.4));
                    map.removePolygon(map.getPolygons().get(0));
                } else {
                    setLayoutsGone();
                    findViewById(R.id.layout_wifi).setVisibility(View.VISIBLE);
                    ((TextView) findViewById(R.id.text_wifi)).setText("" + locationsMap.get(content).getLocation());
                }
            }
        });
        /*findViewById(R.id.button_set_location).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = autoComplete_locations.getText().toString();
                if(locationsMap.get(content).getType().equals("GPS")) {
                    setLayoutsGone();
                    findViewById(R.id.layout_gps).setVisibility(View.VISIBLE);

                    Location loc = locationsMap.get(content);
                    LatLng latlog = new LatLng(loc.getLatitude(), loc.getLongitude());
                    map.setCameraPosition(new CameraPosition.Builder()
                            .target(latlog)
                            .build());
                    marker.setPosition(latlog);
                    map.addPolygon(new PolygonOptions().addAll(polygonCircleForCoordinate(latlog, radius)).fillColor(Color.parseColor("#4285F4")).alpha((float) 0.4));
                    map.removePolygon(map.getPolygons().get(0));
                } else {
                    setLayoutsGone();
                    findViewById(R.id.layout_wifi).setVisibility(View.VISIBLE);
                    ((TextView) findViewById(R.id.text_wifi)).setText("" + locationsMap.get(content).getLocation());
                }
            }
        });*/

        locationEngine = LocationSource.getLocationEngine(this);
        locationEngine.activate();

        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                userLocation = new LatLng(((GlobalLocMess) getApplicationContext()).getLatitude(), ((GlobalLocMess) getApplicationContext()).getLongitude());
                Log.d("LOCATION_OPTION", "User Location: " + userLocation.getLatitude() + ", " + userLocation.getLongitude());
                mapboxMap.setCameraPosition(new CameraPosition.Builder()
                        .target(userLocation) // Sets the new camera position
                        .zoom(14) // Sets the zoom
                        .bearing(0) // Rotate the camera
                        .tilt(0) // Set the camera tilt
                        .build() // Creates a CameraPosition from the builder);
                );
                marker = mapboxMap.addMarker(new MarkerViewOptions().position(userLocation));
                LatLng location = userLocation;
                mapboxMap.addPolygon(new PolygonOptions().addAll(polygonCircleForCoordinate(location, radius)).fillColor(Color.parseColor("#4285F4")).alpha((float)0.4));
                map = mapboxMap;
            }
        });
        SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar_radius);
        final TextView textRadius = (TextView) findViewById(R.id.text_radius);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                radius = 500 * progress / 100;
                map.getPolygons().get(0).setPoints(polygonCircleForCoordinate(marker.getPosition(), radius));
                textRadius.setText("" + radius + "m");
            }

        });

        radioButtonLocations = (RadioButton) findViewById(R.id.radioButton_locations);
        radioButtonWifDirect = (RadioButton) findViewById(R.id.radioButton_wifi_direct);
        radioButtonWifDirect.setOnClickListener(wifi_direct_listener);
    }

    View.OnClickListener wifi_direct_listener = new View.OnClickListener(){
        public void onClick(View v) {
            Log.d("TERMITE", "oi");
            Intent intent = new Intent(v.getContext(), SimWifiP2pService.class);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

            if (mBound && mManager != null) {
                mManager.requestPeers(mChannel, LocationOptionActivity.this);
            } else {
                Toast.makeText(getApplicationContext(), "Service not bound", Toast.LENGTH_SHORT).show();
            }
        }
    };


    /*
	 * Asynctasks implementing message exchange
	 */

    public class IncommingCommTask extends AsyncTask<Void, String, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            Log.d("TERMITE", "IncommingCommTask started (" + this.hashCode() + ").");
            SimWifiP2pSocketServer sockSer = null;
            try {
               sockSer = new SimWifiP2pSocketServer(Integer.parseInt(getString(R.string.port)));
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    SimWifiP2pSocket sock = sockSer.accept();
                    try {
                        BufferedReader sockIn = new BufferedReader(
                                new InputStreamReader(sock.getInputStream()));
                        String st = sockIn.readLine();
                        publishProgress(st);
                        sock.getOutputStream().write(("\n").getBytes());
                    } catch (IOException e) {
                        Log.d("Error reading socket:", e.getMessage());
                    } finally {
                        sock.close();
                    }
                } catch (IOException e) {
                    Log.d("Error socket:", e.getMessage());
                    break;
                    //e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            //mTextOutput.append(values[0] + "\n");
        }
    }

    private void addContentToLayout(LinearLayout layout, String name, String location) {
        final LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));

        LinearLayout ll_text = new LinearLayout(this);
        ll_text.setOrientation(LinearLayout.VERTICAL);
        ll_text.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1.0f));

        TextView text_name = new TextView(this);
        text_name.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f));
        text_name.setTypeface(text_name.getTypeface(), Typeface.BOLD);
        text_name.setTextSize(14);
        text_name.setText("" + name);

        ll_text.addView(text_name);

        TextView text_location = new TextView(this);
        text_location.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f));
        text_location.setText("" + location);

        ll_text.addView(text_location);

        ll.addView(ll_text);

        layout.addView(ll);
    }

    private void populateLocations() {
        try {
            Socket s = SocketHandler.getSocket();
            DataOutputStream dout = new DataOutputStream(s.getOutputStream());
            dout.writeUTF("GetAllLocations;:;" + SocketHandler.getToken());
            dout.flush();
            DataInputStream dis = new DataInputStream(s.getInputStream());
            String str = dis.readUTF();
            String[] locations = str.split(";:;");
            while(!str.equals("END")) {
                Log.d("GET_ALL_LOCATIONS", str);
                if(locations[0].equals("GPS")) { // GPS
                    locationsMap.put(locations[1], new Location(locations[0], locations[2]));
                } else { // WIFI
                    locationsMap.put(locations[1], new Location(locations[0], locations[2]));
                }
                str = dis.readUTF();
                locations = str.split(";:;");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onStop(){
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onGroupInfoAvailable(SimWifiP2pDeviceList simWifiP2pDeviceList, SimWifiP2pInfo simWifiP2pInfo) {
       // TODO
    }

    @Override
    public void onPeersAvailable(SimWifiP2pDeviceList peers) {
        StringBuilder peersStr = new StringBuilder();

        // compile list of devices in range
        for (SimWifiP2pDevice device : peers.getDeviceList()) {
            String devstr = "" + device.deviceName + " (" + device.getVirtIp() + ")\n";
            peersStr.append(devstr);
        }

        LinearLayout peersLayout =  (LinearLayout) findViewById(R.id.layout_wifi_direct);


        TextView tv = new TextView(this.getApplicationContext());
        tv.setText(peersStr.toString());
        tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
        peersLayout.addView(tv);
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        // callbacks for service binding, passed to bindService()
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);
            mManager = new SimWifiP2pManager(mService);
            mChannel = mManager.initialize(getApplication(), getMainLooper(), null);
            mBound = false;
        }
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mService = null;
            mManager = null;
            mChannel = null;
            mBound = false;
        }
    };

    // Include method in your activity
    private static class LatLngEvaluator implements TypeEvaluator<LatLng> {
        private LatLng latLng = new LatLng();
        @Override
        public LatLng evaluate(float fraction, LatLng startValue, LatLng endValue) {
            latLng.setLatitude(startValue.getLatitude() +
                    ((endValue.getLatitude() - startValue.getLatitude()) * fraction));
            latLng.setLongitude(startValue.getLongitude() +
                    ((endValue.getLongitude() - startValue.getLongitude()) * fraction));
            return latLng;
        }
    }

    private ArrayList<LatLng> polygonCircleForCoordinate(LatLng location, double radius){
        int degreesBetweenPoints = 8; //45 sides
        int numberOfPoints = (int) Math.floor(360 / degreesBetweenPoints);
        double distRadians = radius / 6371000.0; // earth radius in meters
        double centerLatRadians = location.getLatitude() * Math.PI / 180;
        double centerLonRadians = location.getLongitude() * Math.PI / 180;
        ArrayList<LatLng> polygons = new ArrayList<>(); //array to hold all the points
        for (int index = 0; index < numberOfPoints; index++) {
            double degrees = index * degreesBetweenPoints;
            double degreeRadians = degrees * Math.PI / 180;
            double pointLatRadians = Math.asin(Math.sin(centerLatRadians) * Math.cos(distRadians) + Math.cos(centerLatRadians) * Math.sin(distRadians) * Math.cos(degreeRadians));
            double pointLonRadians = centerLonRadians + Math.atan2(Math.sin(degreeRadians) * Math.sin(distRadians) * Math.cos(centerLatRadians),
                    Math.cos(distRadians) - Math.sin(centerLatRadians) * Math.sin(pointLatRadians));
            double pointLat = pointLatRadians * 180 / Math.PI;
            double pointLon = pointLonRadians * 180 / Math.PI;
            LatLng point = new LatLng(pointLat, pointLon);
            polygons.add(point);
        }
        return polygons;
    }

    private void setLayoutsGone(){
        findViewById(R.id.layout_gps).setVisibility(View.GONE);
        findViewById(R.id.layout_wifi).setVisibility(View.GONE);
    }

    private boolean adapterContains(String content) {
        for(int i = 0; i < adapter.getCount(); i++) {
            if(adapter.getItem(i).equals(content)) {
                return true;
            }
        }
        return false;
    }

}