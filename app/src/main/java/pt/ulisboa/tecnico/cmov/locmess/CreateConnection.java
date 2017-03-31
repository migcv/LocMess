package pt.ulisboa.tecnico.cmov.locmess;

import android.os.AsyncTask;
import android.util.Log;

import java.io.DataOutputStream;
import java.net.Socket;

/**
 * Created by dharuqueshil on 31/03/2017.
 */

public class CreateConnection extends AsyncTask<String, Void, Void> {

    private static final String ip = "192.168.1.66";
    private static final int port = 6666;

    public CreateConnection() {
    }

    @Override
    protected Void doInBackground(String... strings) {
        try {
            Log.d("INITIATE CONNECTION", "CHEGUEIIIIIIIIIIIIIII!");
            Socket s = new Socket(ip, port);
            DataOutputStream dout = new DataOutputStream(s.getOutputStream());
            dout.writeUTF("Hello Server");
            dout.flush();
            dout.close();
            Log.d("INITIATE CONNECTION", "MANDEIIIIIIIIIIIIIIIII!");
            s.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}


