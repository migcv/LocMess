package pt.ulisboa.tecnico.cmov.locmessServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class LocMess {

	private static final int port = 10000;
	private static ServerSocket ss;
	//private static SSLServerSocket sslServer;

	private static HashMap<String, User> users = new HashMap<>();
	private static HashMap<User, ArrayList<Posts>> userPosts = new HashMap<>();
	private static HashMap<User, HashMap<String, ArrayList<String>>> userRestrictions = new HashMap<>();
	private static HashMap<User, ArrayList<Locations>> usersLocations = new HashMap<>();
	private static HashMap<String, ArrayList<String>> globalRestrictions = new HashMap<>();
	private static HashMap<String, String> userSessions = new HashMap<>();
	private static ArrayList<Locations> globalLocations = new ArrayList<>();
	private static Session session = new Session();

	public static void main(String[] args) {

		if (users.isEmpty()) {
			populate();
		}
		try {
			ss = new ServerSocket(port);

			System.out.println("Starting server...");

			/*KeyStore ks = KeyStore.getInstance("JCEKS");
			FileInputStream fis = new FileInputStream("keystoreserver.jks");
			ks.load(fis, "testing".toCharArray());
			KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			kmf.init(ks, "testing".toCharArray());
			SSLContext sslcontext = SSLContext.getInstance("TLSv1.2");
			sslcontext.init(kmf.getKeyManagers(), null, new SecureRandom());
			ServerSocketFactory ssf = sslcontext.getServerSocketFactory();
			sslServer = (SSLServerSocket) ssf.createServerSocket(port);

			// Set protocol (we want TLSv1.2)
			String[] protocols = sslServer.getEnabledProtocols();
			for (String a : protocols) {
				if (a.equalsIgnoreCase("TLSv1.2")) {
					sslServer.setEnabledProtocols(new String[] { a });
				}
			}

			for (String a : sslServer.getEnabledProtocols()) {
				System.out.println("PROTOCOL: " + a);
			}

			// Set protocol (we want
			// TLS_ECDHE_ECDSA_WITH_CHACHA20_POLY1305_SHA256)
			String[] ciphers = sslServer.getEnabledCipherSuites();
			for (String a : ciphers) {
				if (a.equalsIgnoreCase("TLS_RSA_WITH_AES_128_CBC_SHA")) {
					sslServer.setEnabledCipherSuites(new String[] { a });
				}
			}

			for (String a : sslServer.getEnabledCipherSuites()) {
				System.out.println("CIPHER: " + a);
			}*/

			System.out.println("Server Up " + ss.getLocalPort());

			while (true) {
				System.out.println("Listening!");

				Socket s = ss.accept();// establishes connection

				//Socket s = sslServer.accept(); // establishes connection

				System.out.println("Accepted: " + s.getInetAddress());

				new Thread(new Connection(s)).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*class MyHandshakeListener implements HandshakeCompletedListener {
		public void handshakeCompleted(HandshakeCompletedEvent e) {
			System.out.println("Handshake succesful!");
			System.out.println("Using cipher suite: " + e.getCipherSuite());
		}
	}*/

	public static HashMap<String, User> getUsers() {
		return users;
	}

	public static HashMap<User, ArrayList<Posts>> getUserPosts() {
		return userPosts;
	}

	public static HashMap<User, HashMap<String, ArrayList<String>>> getUserRestrictions() {
		return userRestrictions;
	}

	public static HashMap<String, ArrayList<String>> getGlobalRestrictions() {
		return globalRestrictions;
	}

	public static Session getSession() {
		return session;
	}

	public static HashMap<String, String> getUserSessions() {
		return userSessions;
	}

	public static HashMap<User, ArrayList<Locations>> getUsersLocations() {
		return usersLocations;
	}

	public static ArrayList<Locations> getGlobalLocations() {
		return globalLocations;
	}

	private static void populate() {
		// SIGNUP
		User u = new User("qwerty", "qwerty", "qwerty@gmail.com");
		LocMess.getUsers().put("qwerty", u);

		User u1 = new User("asdasd", "asdasd", "asdasd@gmail.com");
		LocMess.getUsers().put("asdasd", u1);

		// Create user restrictions
		HashMap<String, ArrayList<String>> aux = new HashMap<>();
		HashMap<String, ArrayList<String>> aux4 = new HashMap<>();
		ArrayList<String> aux1 = new ArrayList<>();
		aux1.add("Monkey");
		aux1.add("Gorilla");
		aux1.add("Chimpanzee");
		aux.put("Animals", aux1);

		ArrayList<String> aux2 = new ArrayList<>();
		aux2.add("Student");
		aux2.add("Mason");
		aux2.add("Unemployed");
		aux4.put("Job", aux2);

		LocMess.getUserRestrictions().put(u, aux);
		LocMess.getUserRestrictions().put(u1, aux4);
		LocMess.getGlobalRestrictions().put("Animals", aux1);
		LocMess.getGlobalRestrictions().put("Jobs", aux2);

		// Create posts
		ArrayList<Posts> posts = new ArrayList<>();
		u.setNumOfPost();
		Posts p = new Posts("Procuro colega de casa", "adsadsd", "1213243", "1545931375100", "1545931375100", "GPS",
				"Arco do Cego", "38.736151, -9.142168", "100", "WHITE", "Student (Job)", u.getNumOfPost(), "CENTRALIZED");
		u.setNumOfPost();
		Posts p1 = new Posts("Beber uma cerveja", "adsadsd", "1213243", "1545931375100", "1545931375100", "GPS",
				"Arco do Cego", "38.736151, -9.142168", "100", "WHITE", "Student (Job)", u.getNumOfPost(), "DECENTRALIZED");
		u.setNumOfPost();

		ArrayList<Posts> posts1 = new ArrayList<>();
		u1.setNumOfPost();
		Posts p2 = new Posts("Procuro alunos do Tecnico", "adsadsd", "1213243", "1493283131847", "1545931375100", "GPS",
				"Arco do Cego", "38.736151, -9.142168", "100", "EVERYONE", u1.getNumOfPost(), "CENTRALIZED");
		u1.setNumOfPost();
		Posts p3 = new Posts("Beber um copo de vinho", "adsadsd", "1213243", "1493283131847", "1545931375100", "GPS",
				"Arco do Cego", "38.736151, -9.142168", "100", "WHITE", "Student (Jobs),Gorilla (Animals)",
				u1.getNumOfPost(), "CENTRALIZED");
		u1.setNumOfPost();

		ArrayList<String> ssids = new ArrayList<>();
		ssids.add("C");
		ssids.add("B");
		ssids.add("WiredSSID");
		ssids.add("Wifi");
		Locations l10 = new Locations("WIFI", "Edurom", ssids);

		Posts p4 = new Posts("Pessoas que utilizem o facebook", "adsadsd", "1213243", "1493283131847", "1545931375100",
				"WIFI", l10, "EVERYONE", u1.getNumOfPost(), "CENTRALIZED");
		u1.setNumOfPost();
		Posts p5 = new Posts("Pessoas giras SIM Employed", "adsadsd", "1213243", "1493283131847", "1545931375100", "GPS",
				"Arco do Cego", "38.736151, -9.142168", "100", "BLACK", "Employed (Job)", u1.getNumOfPost(), "CENTRALIZED");
		u1.setNumOfPost();
		Posts p6 = new Posts("Pessoas giras NAO GORILLA", "adsadsd", "1213243", "1493283131847", "1545931375100", "GPS",
				"Arco do Cego", "38.736151, -9.142168", "100", "BLACK", "Gorilla (Animals)", u1.getNumOfPost(), "CENTRALIZED");
		u1.setNumOfPost();
		Posts p7 = new Posts("Pessoas giras NAO ", "adsadsd", "1213243", "1493283131847", "1545931375100", "GPS",
				"Arco do Cego", "38.736151, -9.142168", "100", "BLACK", "Student (Jobs),Gorilla (Animals)", u1.getNumOfPost(), "CENTRALIZED");
		u1.setNumOfPost();

		posts.add(p);
		posts.add(p1);
		posts1.add(p4);
		posts1.add(p2);
		posts1.add(p3);
		posts1.add(p5);
		posts1.add(p6);
		posts1.add(p7);
		LocMess.getUserPosts().put(u, posts);
		LocMess.getUserPosts().put(u1, posts1);

		ArrayList<Locations> aux3 = new ArrayList<>();
		Locations l1 = new Locations("GPS", "Arco do Cego", "38.736151, -9.142168");
		Locations l2 = new Locations("GPS", "Portimao", "37.141785, -8.533509");
		Locations l3 = new Locations("GPS", "Faro", "37.019355, -7.930440");

		ArrayList<String> ssid = new ArrayList<>();
		ssid.add("ALENTEJO");
		ssid.add("Eventos-Lusiada");
		ssid.add("WiredSSID");
		Locations l4 = new Locations("WIFI", "Edurom", ssid);

		aux3.add(l1);
		aux3.add(l2);
		aux3.add(l3);
		aux3.add(l4);

		ArrayList<Locations> aux6 = new ArrayList<>();
		Locations l5 = new Locations("GPS", "Arco do Cego", "38.736151, -9.142168");
		Locations l6 = new Locations("GPS", "Portimao", "37.141785, -8.533509");
		Locations l7 = new Locations("GPS", "Faro", "37.019355, -7.930440");
		aux6.add(l5);
		aux6.add(l6);
		aux6.add(l7);

		LocMess.getUsersLocations().put(u, aux3);
		LocMess.getUsersLocations().put(u1, aux6);
		LocMess.getGlobalLocations().add(l1);
		LocMess.getGlobalLocations().add(l2);
		LocMess.getGlobalLocations().add(l3);
		LocMess.getGlobalLocations().add(l4);

	}

}
