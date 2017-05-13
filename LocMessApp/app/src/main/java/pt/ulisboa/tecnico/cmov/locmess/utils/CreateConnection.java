package pt.ulisboa.tecnico.cmov.locmess.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import java.io.InputStream;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import pt.ulisboa.tecnico.cmov.locmess.R;


/**
 * Created by dharuqueshil on 31/03/2017.
 */

public class CreateConnection extends AsyncTask<String, Void, Void> {

    private static final String ip = "192.168.1.73";
    private static final int port = 10000;
    public Context c;
    private Socket s;

    public CreateConnection(Context c) {
        this.c = c;
    }

    /*private static SSLSocketFactory sslSocketFactory;
    /**
     * Returns a SSL Factory instance that accepts all server certificates.
     * <pre>SSLSocket sock =
     *     (SSLSocket) getSocketFactory.createSocket ( host, 443 ); </pre>
     * @return  An SSL-specific socket factory.
     **/
   /* public SSLSocketFactory getSocketFactory() {
        if ( sslSocketFactory == null ) {
            try {
                TrustManager[] tm = new TrustManager[] { new NaiveTrustManager(c) };
                SSLContext context = SSLContext.getInstance ("TLSv1.2");
                context.init(new KeyManager[0], tm, new SecureRandom());
                sslSocketFactory = (SSLSocketFactory) context.getSocketFactory();

            } catch (KeyManagementException e) {
                Log.d("KEY", e.getMessage());
            } catch (NoSuchAlgorithmException e) {
                Log.d("ALGO", e.getMessage());
            }
        }
        return sslSocketFactory;
    }


    class MyHandshakeListener implements HandshakeCompletedListener {
        public void handshakeCompleted(HandshakeCompletedEvent e) {
            System.out.println("Handshake succesful!");
            System.out.println("Using cipher suite: " + e.getCipherSuite());
        }
    }*/

    @Override
    protected Void doInBackground(String... strings) {
        try {
            Log.d("CONNECTION", "Initiating connection!");

            /*KeyStore ks = KeyStore.getInstance("BKS");
            InputStream keystore = c.getResources().openRawResource(R.raw.keystore);
            ks.load(keystore, "testing".toCharArray());

            //Create an instance of SSLSocket (TRUST ONLY OUR CERT)
            SSLSocketFactory sslSocketFactory = getSocketFactory();
            SSLSocket sslSocket = (SSLSocket) sslSocketFactory.createSocket(ip,port);

            // Set protocol (we want TLSv1.2)
            String[] protocols = sslSocket.getEnabledProtocols(); // gets available protocols
            for(String a: protocols) {
                if (a.equalsIgnoreCase("TLSv1.2")) {
                    sslSocket.setEnabledProtocols(new String[]{a}); // set protocol to TLSv1.2
                }
            }

            for(String a : sslSocket.getEnabledProtocols()){
                Log.d("PROTOCOL" , a);
            }

            // Set protocol (we want TLS_RSA_WITH_AES_128_CBC_SHA)
            String[] ciphers = sslSocket.getEnabledCipherSuites(); // gets available ciphers
            for(String a: ciphers) {
                if(a.equalsIgnoreCase("TLS_RSA_WITH_AES_128_CBC_SHA")){
                    sslSocket.setEnabledCipherSuites(new String[]{a});
                }
            }

            for(String a : sslSocket.getEnabledCipherSuites()){
                Log.d("CIPHER" , a);
            }

            sslSocket.addHandshakeCompletedListener(new MyHandshakeListener());

            sslSocket.startHandshake();

            if(sslSocket.isConnected()){
                SocketHandler.setSocket(sslSocket);
            }*/
            
            s = new Socket(ip, port);
            SocketHandler.setSocket(s);

            Log.d("CONNECTION", s.getInetAddress().getHostAddress());

        } catch (Exception e) {
            Log.d("CONNECTION", "Error connecting to server!");
            e.printStackTrace();
        }
        return null;
    }
}


