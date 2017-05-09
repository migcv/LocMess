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
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

/**
 * Created by dharuqueshil on 31/03/2017.
 */

public class CreateConnection extends AsyncTask<String, Void, Void> {

    private static final String ip = "194.210.159.144";
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

            // Create an instance of SSLSocket (TRUST ONLY OUR CERT)
            /*SSLSocketFactory sslSocketFactory = getSocketFactory();
            SSLSocket sslsocket = (SSLSocket) sslSocketFactory.createSocket(ip, port);


            // Set protocol (we want TLSv1.2)
            String[] protocols = sslsocket.getEnabledProtocols(); // gets available protocols
            for(String s: protocols) {
                if(s.equalsIgnoreCase("TLSv1.2")) {
                    sslsocket.setEnabledProtocols(new String[] {s}); // set protocol to TLSv1.2
                    System.out.println("CIPHER: "+ sslsocket.getEnabledCipherSuites()[0]);
                    System.out.println("Using: "+ sslsocket.getEnabledProtocols()[0]);
                }
            }

            SocketHandler.setSslSocket(sslsocket);*/

        } catch (Exception e) {
            Log.d("CONNECTION", "Error connecting to server!");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Returns a SSL Factory instance that accepts all server certificates.
     * <pre>SSLSocket sock =
     *     (SSLSocket) getSocketFactory.createSocket ( host, 443 ); </pre>
     * @return  An SSL-specific socket factory.
     **/
    public SSLSocketFactory getSocketFactory() {
        if ( sslSocketFactory == null ) {
            try {
                TrustManager[] tm = new TrustManager[] {new GenCert()};
                SSLContext context = SSLContext.getInstance("TLSv1.2");
                context.init( new KeyManager[0], tm, new SecureRandom());

                sslSocketFactory = context.getSocketFactory();

            } catch (KeyManagementException e) {
                Log.e("No SSL algorithm: " , e.getMessage());
            } catch (NoSuchAlgorithmException e) {
                Log.e("Setting Key management.", e.getMessage());
            }
        }
        return sslSocketFactory;
    }

}


