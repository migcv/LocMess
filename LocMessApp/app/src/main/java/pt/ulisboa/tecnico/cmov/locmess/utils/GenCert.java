package pt.ulisboa.tecnico.cmov.locmess.utils;

import android.content.Context;
import android.util.Log;

import org.bouncycastle.x509.X509V1CertificateGenerator;

import java.io.FileOutputStream;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import javax.security.auth.x500.X500Principal;


public class GenCert  implements X509TrustManager {

    private X509Certificate cert;
    private static SSLSocketFactory sslSocketFactory;

    public GenCert() {
    }

    public X509Certificate generateCertificate() throws Exception {

        // JCEKS refers the KeyStore implementation from SunJCE provider
        KeyStore ks = KeyStore.getInstance("JCEKS");
        // Load the null Keystore and set the password”
        ks.load(null, SocketHandler.getUsername().toCharArray());

        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA", "BC");
        keyGen.initialize(2048);
        KeyPair keypair = keyGen.genKeyPair();

        // yesterday
        Date validityBeginDate = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
        // in 2 years
        Date validityEndDate = new Date(System.currentTimeMillis() + 2 * 365 * 24 * 60 * 60 * 1000);

        // GENERATE THE X509 CERTIFICATE
        X509V1CertificateGenerator certGen = new X509V1CertificateGenerator();
        X500Principal dnName = new X500Principal("CN=CMU");

        certGen.setSerialNumber(BigInteger.valueOf(System.currentTimeMillis()));
        certGen.setSubjectDN(dnName);
        certGen.setIssuerDN(dnName); // use the same
        certGen.setNotBefore(validityBeginDate);
        certGen.setNotAfter(validityEndDate);
        certGen.setPublicKey(keypair.getPublic());
        certGen.setSignatureAlgorithm("SHA256WithRSAEncryption");

        cert = certGen.generate(keypair.getPrivate(), "BC");

        ks.setKeyEntry("alias", keypair.getPrivate(), SocketHandler.getUsername().toCharArray(), new java.security.cert.Certificate[] { cert });

        // Create a new file to store the KeyStore object
        FileOutputStream fos = new FileOutputStream("keystorefilealias.jce");
        // Write the KeyStore into the file
        ks.store(fos, SocketHandler.getUsername().toCharArray());
        // Close the file stream
        fos.close();

        return cert;
    }

    @Override
    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

    }

    @Override
    public void checkServerTrusted(X509Certificate[] cert, String s) throws CertificateException {
        System.out.println("CERTIFICATE Received: <" + cert[0].getIssuerDN().getName() + ">");
        X509Certificate[] acceptedCertificats = getAcceptedIssuers();
        for(int i = 0; i < acceptedCertificats.length; i++) {
            if(acceptedCertificats[i].getPublicKey().equals(cert[0].getPublicKey())) {
                System.out.println("CERTIFICATE received ACCEPTED");
                return;
            }
        }
        System.out.println("CERTIFICATE received REJECTED");
        throw new CertificateException();
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        X509Certificate[] myTrustedAnchors = new X509Certificate[0];
        try {
            X509Certificate cert = generateCertificate();
            myTrustedAnchors[0] = cert;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return myTrustedAnchors;
    }
}