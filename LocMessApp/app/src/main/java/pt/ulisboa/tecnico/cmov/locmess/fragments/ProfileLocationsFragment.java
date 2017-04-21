package pt.ulisboa.tecnico.cmov.locmess.fragments;


import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

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

import pt.ulisboa.tecnico.cmov.locmess.R;
import pt.ulisboa.tecnico.cmov.locmess.activities.PostsActivity;
import pt.ulisboa.tecnico.cmov.locmess.utils.GlobalLocMess;
import pt.ulisboa.tecnico.cmov.locmess.utils.NewPost;
import pt.ulisboa.tecnico.cmov.locmess.utils.SocketHandler;

/**
 * Created by Rafael Barreira on 03/04/2017.
 */

public class ProfileLocationsFragment extends Fragment {

    View view;

    private int radius = 250;
    private LatLng userLocation;

    private MapView mapView;
    private MapboxMap map;
    private Marker marker ;
    private LocationEngine locationEngine;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Mapbox.getInstance(getActivity(), getString(R.string.mapbox_access_token));
        view = inflater.inflate(R.layout.profile_fragment_locations, container, false);

        RadioButton radioButtonLocation = (RadioButton) view.findViewById(R.id.radioButton_GPS);
        radioButtonLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLayoutsGone();
                getView().findViewById(R.id.layout_GPS).setVisibility(View.VISIBLE);
            }
        });
        RadioButton radioButtonWifDirect = (RadioButton) view.findViewById(R.id.radioButton_wifi);
        radioButtonWifDirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLayoutsGone();
                getView().findViewById(R.id.layout_wifi).setVisibility(View.VISIBLE);
            }
        });

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

                DecimalFormat df = new DecimalFormat("##.######");
                ((TextView) locationDialog.findViewById(R.id.text_location)).setText(df.format(marker.getPosition().getLatitude()) + ", " + df.format(marker.getPosition().getLongitude()));

                locationDialog.findViewById(R.id.button_add_location).setOnClickListener( new View.OnClickListener() {
                    public void onClick(View v) {
                        String locationName = ((TextView)locationDialog.findViewById(R.id.input_location_name)).getText().toString();
                        String coordenates = marker.getPosition().getLatitude()+ ", " + marker.getPosition().getLongitude();
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
                            Log.d("NEW_LOCATION", toSend);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        DecimalFormat df = new DecimalFormat("##.######");
                        addContentToLayout((LinearLayout) getView().findViewById(R.id.layout_locations), locationName, df.format(marker.getPosition().getLatitude()) + ", " + df.format(marker.getPosition().getLongitude()));
                        locationDialog.dismiss();
                    }
                });
                locationDialog.findViewById(R.id.button_cancel).setOnClickListener( new View.OnClickListener() {
                    public void onClick(View v) {
                        locationDialog.dismiss();
                    }
                });
                locationDialog.show();
            }
        });

        //addContentToLayout((LinearLayout) view.findViewById(R.id.layout_locations), "Arco do Cego", "38.736109, -9.142490");

        populateLocations();

        return view;
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
                addContentToLayout((LinearLayout) view.findViewById(R.id.layout_locations), locations[2], locations[3]);
                str = dis.readUTF();
                Log.d("MY_LOCATIONS", str);
                locations = str.split(";:;");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addContentToLayout(LinearLayout layout, final String name, String location) {
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
        text_location.setText("" + location);

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
                    String toSend = "RemoveLocations;:;" + SocketHandler.getToken() + ";:;" + "GPS;:Arco do Cego;:32.2343,32.2343";
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