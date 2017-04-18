package pt.ulisboa.tecnico.cmov.locmess.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

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
        Toast.makeText(this, "Service was Created", Toast.LENGTH_SHORT).show();
        Log.d("SERVICE", "CREATED");
        Thread thread = new Thread(new UserLocation());
        thread.run();
    }


    public class UserLocation implements Runnable {
        public void run(){
            System.out.println("MyRunnable running");
        }
    }

}
