package pt.ulisboa.tecnico.cmov.locmess.fragments;


import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationSource;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.services.android.telemetry.location.LocationEngine;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.inesc.termite.wifidirect.SimWifiP2pDevice;
import pt.inesc.termite.wifidirect.SimWifiP2pDeviceList;
import pt.inesc.termite.wifidirect.SimWifiP2pInfo;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;
import pt.inesc.termite.wifidirect.service.SimWifiP2pService;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketManager;
import pt.ulisboa.tecnico.cmov.locmess.R;
import pt.ulisboa.tecnico.cmov.locmess.activities.LocationOptionActivity;
import pt.ulisboa.tecnico.cmov.locmess.utils.GlobalLocMess;
import pt.ulisboa.tecnico.cmov.locmess.utils.SimWifiP2pBroadcastReceiver;
import pt.ulisboa.tecnico.cmov.locmess.utils.SocketHandler;

import static android.os.Looper.getMainLooper;

/**
 * Created by Rafael Barreira on 03/04/2017.
 */

public class ProfileLocationsFragment extends Fragment implements SimWifiP2pManager.PeerListListener, SimWifiP2pManager.GroupInfoListener {

    View view;

    private int radius = 250;
    private LatLng userLocation;

    private MapView mapView;
    private MapboxMap map;
    private Marker marker ;
    private LocationEngine locationEngine;

    private WifiManager mainWifi;
    private WifiReceiver receiverWifi;

    private ArrayList<String> wifiSSIDList = new ArrayList<>();

    private RadioButton radioButtonGPS;
    private RadioButton radioButtonWIFI;

    private SimWifiP2pBroadcastReceiver mReceiver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Mapbox.getInstance(getActivity(), getString(R.string.mapbox_access_token));
        view = inflater.inflate(R.layout.profile_fragment_locations, container, false);

        // initialize the WDSim API
        SimWifiP2pSocketManager.Init(view.getContext());

        // register broadcast receiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_STATE_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_PEERS_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_NETWORK_MEMBERSHIP_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_GROUP_OWNERSHIP_CHANGED_ACTION);
        mReceiver = new SimWifiP2pBroadcastReceiver(this.getActivity());
        getContext().registerReceiver(mReceiver, filter);

        radioButtonGPS = (RadioButton) view.findViewById(R.id.radioButton_GPS);
        radioButtonWIFI = (RadioButton) view.findViewById(R.id.radioButton_wifi);

        radioButtonGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLayoutsGone();
                getView().findViewById(R.id.layout_GPS).setVisibility(View.VISIBLE);
            }
        });
        radioButtonWIFI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLayoutsGone();
                getView().findViewById(R.id.layout_wifi).setVisibility(View.VISIBLE);
                GlobalLocMess globalLM = ((GlobalLocMess)view.getContext().getApplicationContext());
                Log.d("TERMITE", "oi");
                Intent intent = new Intent(view.getContext(), SimWifiP2pService.class);
                view.getContext().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

                if (globalLM.ismBound() && globalLM.getSimWifiP2pManager() != null) {
                    globalLM.getSimWifiP2pManager().requestPeers(globalLM.getmChannel(), ProfileLocationsFragment.this);
                } else {
                    Toast.makeText(view.getContext(), "Service not bound",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }

        locationEngine = LocationSource.getLocationEngine(getContext());
        locationEngine.activate();

        mapView = (MapView) view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                userLocation = new LatLng(((GlobalLocMess) getActivity().getApplicationContext()).getLatitude(), ((GlobalLocMess) getActivity().getApplicationContext()).getLongitude());
                Log.d("LOCATION_OPTION", "User Location: " + userLocation.getLatitude() + ", " + userLocation.getLongitude());
                mapboxMap.setCameraPosition(new CameraPosition.Builder()
                        .target(userLocation) // Sets the new camera position
                        .zoom(15) // Sets the zoom
                        .bearing(0) // Rotate the camera
                        .tilt(0) // Set the camera tilt
                        .build() // Creates a CameraPosition from the builder);
                );
                marker = mapboxMap.addMarker(new MarkerViewOptions().position(userLocation));
                mapboxMap.setOnMapClickListener(new MapboxMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng point) {
                        ValueAnimator markerAnimator = ObjectAnimator.ofObject(marker, "position",
                                new ProfileLocationsFragment.LatLngEvaluator(), marker.getPosition(), point);
                        markerAnimator.setDuration(250);
                        markerAnimator.start();

                        CameraPosition position = new CameraPosition.Builder()
                                .target(point) // Sets the new camera position
                                .zoom(15) // Sets the zoom
                                .bearing(0) // Rotate the camera
                                .tilt(0) // Set the camera tilt
                                .build(); // Creates a CameraPosition from the builder

                        map.animateCamera(CameraUpdateFactory
                                .newCameraPosition(position), 1000);
                    }
                });
                map = mapboxMap;
            }
        });

        view.findViewById(R.id.button_add_location).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog locationDialog = new Dialog(getView().getContext());
                locationDialog.setContentView(R.layout.dialog_new_location);

                if(radioButtonGPS.isChecked()) {
                    DecimalFormat df = new DecimalFormat("##.######");
                    locationDialog.findViewById(R.id.layout_wifi_SSID).setVisibility(View.GONE);
                    locationDialog.findViewById(R.id.text_location).setVisibility(View.VISIBLE);
                    ((TextView) locationDialog.findViewById(R.id.text_location)).setText(df.format(marker.getPosition().getLatitude()) + ", " + df.format(marker.getPosition().getLongitude()));

                    locationDialog.findViewById(R.id.button_add_location).setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            String locationName = ((TextView) locationDialog.findViewById(R.id.input_location_name)).getText().toString();
                            String coordenates = marker.getPosition().getLatitude() + ", " + marker.getPosition().getLongitude();
                            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                            StrictMode.setThreadPolicy(policy);
                            try {
                                //Mudar esta String
                                String toSend = "AddLocations;:;" + SocketHandler.getToken() + ";:;" + "GPS;:" + locationName + ";:" + coordenates;
                                Socket s = SocketHandler.getSocket();
                                Log.d("CONNECTION", "Connection successful!");
                                DataOutputStream dout = new DataOutputStream(s.getOutputStream());
                                dout.writeUTF(toSend);
                                dout.flush();
                                //dout.close();
                                Log.d("NEW_LOCATION_GPS", toSend);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            DecimalFormat df = new DecimalFormat("##.######");
                            addLocationToLayout((LinearLayout) getView().findViewById(R.id.layout_locations), "GPS", locationName, df.format(marker.getPosition().getLatitude()) + ", " + df.format(marker.getPosition().getLongitude()));
                            locationDialog.dismiss();
                        }
                    });
                }
                else if(radioButtonWIFI.isChecked()) {
                    locationDialog.findViewById(R.id.layout_wifi_SSID).setVisibility(View.VISIBLE);
                    locationDialog.findViewById(R.id.text_location).setVisibility(View.GONE);

                    locationDialog.findViewById(R.id.button_add_location).setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            String locationName = ((TextView) locationDialog.findViewById(R.id.input_location_name)).getText().toString();

                            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                            StrictMode.setThreadPolicy(policy);
                            try {
                                String ssids = "";
                                for(String ssid : wifiSSIDList) {
                                    addWifiToLayout((LinearLayout) locationDialog.findViewById(R.id.layout_wifi_SSID), ssid);
                                    ssids = ssids + "" + ssid + ",";
                                }

                                final String toSend = "AddLocations;:;" + SocketHandler.getToken() + ";:;" + "WIFI;:" + locationName + ";:" + ssids;

                                Socket s = SocketHandler.getSocket();
                                Log.d("CONNECTION", "Connection successful!");
                                DataOutputStream dout = new DataOutputStream(s.getOutputStream());
                                dout.writeUTF(toSend);
                                dout.flush();
                                //dout.close();
                                Log.d("NEW_LOCATION_WIFI", toSend);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            addLocationToLayout((LinearLayout) getView().findViewById(R.id.layout_locations), "WIFI", locationName, "Wifi");
                            locationDialog.dismiss();
                        }
                    });
                }
                locationDialog.findViewById(R.id.button_cancel).setOnClickListener( new View.OnClickListener() {
                    public void onClick(View v) {
                        locationDialog.dismiss();
                    }
                });
                locationDialog.show();
            }
        });
        /*try {
            mainWifi = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);

            receiverWifi = new WifiReceiver();
            getContext().registerReceiver(receiverWifi, new IntentFilter(
                    WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
            if (mainWifi.isWifiEnabled() == false) {
                mainWifi.setWifiEnabled(true);
            }
            Log.d("LOCATIONS_WIFI", "Enable Wifi");

            mainWifi.startScan();
        } catch (SecurityException e) {
            e.printStackTrace();
        }*/

        populateLocations();


        /*radioButtonWIFI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GlobalLocMess globalLM = ((GlobalLocMess)view.getContext().getApplicationContext());
                Log.d("TERMITE", "oi");
                Intent intent = new Intent(view.getContext(), SimWifiP2pService.class);
                view.getContext().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

                if (globalLM.ismBound() && globalLM.getSimWifiP2pManager() != null) {
                    globalLM.getSimWifiP2pManager().requestPeers(globalLM.getmChannel(), ProfileLocationsFragment.this);

                } else {
                    Toast.makeText(view.getContext(), "Service not bound",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });*/

        return view;
    }

    @Override
    public void onGroupInfoAvailable(SimWifiP2pDeviceList simWifiP2pDeviceList, SimWifiP2pInfo simWifiP2pInfo) {
        // TODO
    }

    @Override
    public void onPeersAvailable(SimWifiP2pDeviceList peers) {
        // Restart/Clean all elements
        wifiSSIDList = new ArrayList<>();
        if(((LinearLayout) getView().findViewById(R.id.layout_wifi_list)).getChildCount() > 0) {
            ((LinearLayout) getView().findViewById(R.id.layout_wifi_list)).removeAllViews();
        }
        // Insert elements in List and Layout
        for (SimWifiP2pDevice device : peers.getDeviceList()) {
            String wifiSSID = device.deviceName + "\n";
            wifiSSIDList.add(wifiSSID);
            addWifiToLayout((LinearLayout) getView().findViewById(R.id.layout_wifi_list), wifiSSID);
            Log.d("MACACOOOOOOOOOOOOOO", wifiSSID);
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        // callbacks for service binding, passed to bindService()
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            GlobalLocMess globalLM = ((GlobalLocMess)view.getContext().getApplicationContext());
            globalLM.setmService(new Messenger(service));
            globalLM.setSimWifiP2pManager(new SimWifiP2pManager(globalLM.getmService()));
            globalLM.setmChannel(globalLM.getSimWifiP2pManager().initialize(view.getContext().getApplicationContext(), getMainLooper(),
                    null));
            globalLM.setmBound(true);
        }
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            GlobalLocMess globalLM = ((GlobalLocMess)view.getContext().getApplicationContext());
            globalLM.setmService(null);
            globalLM.setSimWifiP2pManager(null);
            globalLM.setmChannel(null);
            globalLM.setmBound(false);

        }
    };

    private class WifiReceiver extends BroadcastReceiver {
        public void onReceive(Context c, Intent intent) {
            ArrayList<String> connections=new ArrayList<String>();

            List<ScanResult> wifiList;
            wifiList = mainWifi.getScanResults();
            for(int i = 0; i < wifiList.size(); i++) {
                connections.add(wifiList.get(i).SSID);
                Log.d("LOCATION_WIFI", i + " " + wifiList.get(i).SSID);
                if(!wifiSSIDList.contains(wifiList.get(i).SSID)) {
                    wifiSSIDList.add(wifiList.get(i).SSID);
                    addWifiToLayout((LinearLayout) getView().findViewById(R.id.layout_wifi_list), wifiList.get(i).SSID);
                }
            }
        }
    }

    private void populateLocations() {
        try {
            Socket s = SocketHandler.getSocket();
            DataOutputStream dout = new DataOutputStream(s.getOutputStream());
            dout.writeUTF("MYLocations;:;" + SocketHandler.getToken());
            dout.flush();
            DataInputStream dis = new DataInputStream(s.getInputStream());
            String str = dis.readUTF();
            String[] locations = str.split(";:;");
            while(!str.equals("END")) {
                addLocationToLayout((LinearLayout) view.findViewById(R.id.layout_locations),locations[1], locations[2], locations[3]);
                str = dis.readUTF();
                Log.d("MY_LOCATIONS", str);
                locations = str.split(";:;");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addLocationToLayout(LinearLayout layout, final String type, final String name, final String value) {
        final LinearLayout ll = new LinearLayout(getContext());
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));

        LinearLayout ll_text = new LinearLayout(getContext());
        ll_text.setOrientation(LinearLayout.VERTICAL);
        ll_text.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1.0f));

        TextView text_name = new TextView(getContext());
        text_name.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f));
        text_name.setTypeface(text_name.getTypeface(), Typeface.BOLD);
        text_name.setTextSize(14);
        text_name.setText("" + name);

        ll_text.addView(text_name);

        TextView text_location = new TextView(getContext());
        text_location.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f));
        text_location.setText("" + value);

        ll_text.addView(text_location);

        ll.addView(ll_text);

        Button deleteButton = new Button(getContext());
        deleteButton.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        deleteButton.setText("X");
        deleteButton.setTextColor(getContext().getResources().getColorStateList(R.color.colorWhite));
        deleteButton.setBackgroundTintList(getContext().getResources().getColorStateList(R.color.colorRemove));
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ll.setVisibility(View.GONE);
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                try {
                    //Mudar esta String
                    String toSend = "RemoveLocations;:;" + SocketHandler.getToken() + ";:;" + type + ";:" + name + ";:" + value;
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
            }

        });

        ll.addView(deleteButton);

        layout.addView(ll);
    }

    private void addWifiToLayout(LinearLayout layout, String wifi) {

        TextView text_wifi = new TextView(getContext());
        text_wifi.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f));
        text_wifi.setTypeface(text_wifi.getTypeface(), Typeface.BOLD);
        text_wifi.setTextSize(14);
        text_wifi.setText("" + wifi);

        layout.addView(text_wifi);
    }

    private void setLayoutsGone(){
        view.findViewById(R.id.layout_GPS).setVisibility(View.GONE);
        view.findViewById(R.id.layout_wifi).setVisibility(View.GONE);
    }

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
}