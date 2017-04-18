package pt.ulisboa.tecnico.cmov.locmess.services;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;
import com.mapzen.android.lost.api.LocationServices;

/**
 * Created by Miguel on 18/04/2017.
 */

public class LocationService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        Log.d("LOCATION_ SERVICE", "Service created!");

        Thread thread = new Thread(new UserLocation());
        thread.start();
    }


    public class UserLocation implements Runnable {

        public void run(){
            Log.d("LOCATION_ SERVICE", "Thread started!");
            try {
                LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
                Criteria criteria = new Criteria();
                String provider = service.getBestProvider(criteria, false);

                while(true) {
                    try {
                        Location location = service.getLastKnownLocation(provider);
                        Log.d("LOCATION_ SERVICE", location == null ? "Null" : "" + location.getLatitude() + ", " + location.getLongitude());
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }

}
