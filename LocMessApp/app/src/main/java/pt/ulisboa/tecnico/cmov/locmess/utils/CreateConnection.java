package pt.ulisboa.tecnico.cmov.locmess.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.DataOutputStream;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

/**
 * Created by dharuqueshil on 31/03/2017.
 */

public class CreateConnection extends AsyncTask<String, Void, Void> {

    private static final String ip = "192.168.1.73";
    private static final int port = 10000;
    private static SSLSocketFactory sslSocketFactory;
    public Context c;
    private static Socket s;

    public CreateConnection(Context c) {
        this.c = c;
    }

    @Override
    protected Void doInBackground(String... strings) {
        try {
            Log.d("CONNECTION", "Initiating connection!");
            s = new Socket(ip, port);
            SocketHandler.setSocket(s);
            Log.d("CONNECTION", "Connection successful!");
        } catch (Exception e) {
            Log.d("CONNECTION", "Error connecting to server!");
            e.printStackTrace();
        }
        return null;
    }
}


