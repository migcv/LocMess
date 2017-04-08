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

    private static final String ip = "194.210.230.1";
    private static final int port = 6666;
    private static SSLSocketFactory sslSocketFactory;
    public Context c;

    public CreateConnection(Context c) {
        this.c = c;
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
                TrustManager[] tm = new TrustManager[] { new NaiveTrustManager(c) };
                SSLContext context = SSLContext.getInstance ("TLSv1.2");
                context.init( new KeyManager[0], tm, new SecureRandom( ) );

                sslSocketFactory = (SSLSocketFactory) context.getSocketFactory ();

            } catch (KeyManagementException e) {
                Log.d("No SSL" , e.getMessage());
            } catch (NoSuchAlgorithmException e) {
                Log.d("Naive key management.", e.getMessage());
            }
        }
        return sslSocketFactory;
    }

    @Override
    protected Void doInBackground(String... strings) {
        try {
            /*Log.d("INITIATE CONNECTION", "CHEGUEIIIIIIIIIIIIIII!");
            SSLSocketFactory sslSocketFactory = getSocketFactory();
            SSLSocket s = (SSLSocket) sslSocketFactory.createSocket(ip, port);

            // Set protocol (we want TLSv1.2)
            String[] protocols = s.getEnabledProtocols(); // gets available protocols
            for(String st: protocols) {
                if(st.equalsIgnoreCase("TLSv1.2")) {
                    s.setEnabledProtocols(new String[] {st}); // set protocol to TLSv1.2
                    System.out.println("CIPHER: "+ s.getEnabledCipherSuites()[0]);
                    System.out.println("Using: "+s.getEnabledProtocols()[0]);
                }
            }
            //Socket s = new Socket(ip, port);
            DataOutputStream dout = new DataOutputStream(s.getOutputStream());
            dout.writeUTF("Hello Server");
            dout.flush();
            dout.close();
            Log.d("INITIATE CONNECTION", "MANDEIIIIIIIIIIIIIIIII!");
            s.close();*/

            Log.d("CONNECTION", "Initiating connection!");
            Socket s = new Socket(ip, port);
            DataOutputStream dout = new DataOutputStream(s.getOutputStream());
            dout.writeUTF("Hello Server");
            dout.flush();
            dout.close();
            Log.d("CONNECTION", "Connection successful!");
            s.close();
        } catch (Exception e) {
            Log.d("CONNECTION", "Error connecting to server!");
            e.printStackTrace();
        }
        return null;
    }
}


