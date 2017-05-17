package pt.ulisboa.tecnico.cmov.locmessServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class LocMess {

	private static final int port = 10000;
	private static ServerSocket ss;
	// private static SSLServerSocket sslServer;

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

			/*
			 * KeyStore ks = KeyStore.getInstance("JCEKS"); FileInputStream fis
			 * = new FileInputStream("keystoreserver.jks"); ks.load(fis,
			 * "testing".toCharArray()); KeyManagerFactory kmf =
			 * KeyManagerFactory.getInstance(KeyManagerFactory.
			 * getDefaultAlgorithm()); kmf.init(ks, "testing".toCharArray());
			 * SSLContext sslcontext = SSLContext.getInstance("TLSv1.2");
			 * sslcontext.init(kmf.getKeyManagers(), null, new SecureRandom());
			 * ServerSocketFactory ssf = sslcontext.getServerSocketFactory();
			 * sslServer = (SSLServerSocket) ssf.createServerSocket(port);
			 * 
			 * // Set protocol (we want TLSv1.2) String[] protocols =
			 * sslServer.getEnabledProtocols(); for (String a : protocols) { if
			 * (a.equalsIgnoreCase("TLSv1.2")) {
			 * sslServer.setEnabledProtocols(new String[] { a }); } }
			 * 
			 * for (String a : sslServer.getEnabledProtocols()) {
			 * System.out.println("PROTOCOL: " + a); }
			 * 
			 * // Set protocol (we want //
			 * TLS_ECDHE_ECDSA_WITH_CHACHA20_POLY1305_SHA256) String[] ciphers =
			 * sslServer.getEnabledCipherSuites(); for (String a : ciphers) { if
			 * (a.equalsIgnoreCase("TLS_RSA_WITH_AES_128_CBC_SHA")) {
			 * sslServer.setEnabledCipherSuites(new String[] { a }); } }
			 * 
			 * for (String a : sslServer.getEnabledCipherSuites()) {
			 * System.out.println("CIPHER: " + a); }
			 */

			System.out.println("Server Up " + ss.getLocalPort());

			while (true) {
				System.out.println("Listening!");

				Socket s = ss.accept();// establishes connection

				// Socket s = sslServer.accept(); // establishes connection

				System.out.println("Accepted: " + s.getInetAddress());

				new Thread(new Connection(s)).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * class MyHandshakeListener implements HandshakeCompletedListener { public
	 * void handshakeCompleted(HandshakeCompletedEvent e) {
	 * System.out.println("Handshake succesful!");
	 * System.out.println("Using cipher suite: " + e.getCipherSuite()); } }
	 */

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
		User u = new User("andre", "andre", "andre@gmail.com");
		LocMess.getUsers().put("andre", u);

		User u1 = new User("joao", "joao", "joao@gmail.com");
		LocMess.getUsers().put("joao", u1);

		User u2 = new User("margarida", "margarida", "margarida@gmail.com");
		LocMess.getUsers().put("margarida", u2);

		// Create user restrictions
		HashMap<String, ArrayList<String>> aux = new HashMap<>();
		ArrayList<String> aux1 = new ArrayList<>();
		aux1.add("Monkey");
		aux1.add("Gorilla");
		aux1.add("Chimpanzee");
		aux.put("Animals", aux1);
		LocMess.getUserRestrictions().put(u, aux);
		LocMess.getGlobalRestrictions().put("Animals", aux1);

		HashMap<String, ArrayList<String>> aux4 = new HashMap<>();
		ArrayList<String> aux2 = new ArrayList<>();
		aux2.add("Student");
		aux2.add("Mason");
		aux2.add("Unemployed");
		aux4.put("Jobs", aux2);
		LocMess.getUserRestrictions().put(u1, aux4);
		LocMess.getGlobalRestrictions().put("Jobs", aux2);

		HashMap<String, ArrayList<String>> aux3 = new HashMap<>();
		ArrayList<String> aux9 = new ArrayList<>();
		aux9.add("Real Madrid");
		aux3.put("Club", aux9);
		LocMess.getUserRestrictions().put(u2, aux3);
		LocMess.getGlobalRestrictions().put("Club", aux9);

		// create locations
		ArrayList<Locations> aux10 = new ArrayList<>();
		Locations l1 = new Locations("GPS", "Arco do Cego", "38.736151, -9.142168");
		Locations l2 = new Locations("GPS", "Portimao", "37.141785, -8.533509");
		Locations l3 = new Locations("GPS", "Faro", "37.019355, -7.930440");

		ArrayList<String> ssid = new ArrayList<>();
		ssid.add("edurom");
		ssid.add("edurom-guest");
		ssid.add("tecnico-guest");
		Locations l4 = new Locations("WIFI", "IST-WIFI", ssid);

		aux10.add(l1);
		aux10.add(l2);
		aux10.add(l3);
		aux10.add(l4);
		LocMess.getUsersLocations().put(u, aux10);

		ArrayList<Locations> aux6 = new ArrayList<>();
		Locations l5 = new Locations("GPS", "Apolo 50", "38.742493, -9.148020");
		Locations l6 = new Locations("GPS", "NYBC Bagel Cafe", "38.741115, -9.136532");
		Locations l7 = new Locations("GPS", "Guilty By Olivier", "38.721329, -9.148527");
		aux6.add(l5);
		aux6.add(l6);
		aux6.add(l7);
		LocMess.getUsersLocations().put(u1, aux6);

		ArrayList<Locations> aux11 = new ArrayList<>();
		Locations l8 = new Locations("GPS", "Taj Mahal", "27.174901, 78.042048");
		Locations l9 = new Locations("GPS", "Ronald McDonald House", "-32.924791, 151.694571");
		Locations l11 = new Locations("GPS", "East Harlem", "40.804920, -73.941652");
		aux11.add(l8);
		aux11.add(l9);
		aux11.add(l11);

		ArrayList<String> ssID = new ArrayList<>();
		ssID.add("B");
		ssID.add("C");
		ssID.add("D");
		Locations l12 = new Locations("WIFI", "Termite", ssID);
		aux11.add(l12);
		LocMess.getUsersLocations().put(u2, aux11);

		LocMess.getGlobalLocations().add(l1);
		LocMess.getGlobalLocations().add(l2);
		LocMess.getGlobalLocations().add(l3);
		LocMess.getGlobalLocations().add(l4);
		LocMess.getGlobalLocations().add(l5);
		LocMess.getGlobalLocations().add(l6);
		LocMess.getGlobalLocations().add(l7);
		LocMess.getGlobalLocations().add(l8);
		LocMess.getGlobalLocations().add(l9);
		LocMess.getGlobalLocations().add(l11);
		LocMess.getGlobalLocations().add(l12);

		// Create posts
		ArrayList<Posts> posts = new ArrayList<>();
		u.setNumOfPost();
		Posts p = new Posts("Searching for a flat mate", "Need flat mate that is calm.", "1213243", "1545931375100",
				"1545931375100", "GPS", "Arco do Cego", "38.736151, -9.142168", "100", "WHITE", "Student (Job)",
				u.getNumOfPost(), "CENTRALIZED");
		u.setNumOfPost();
		Posts p1 = new Posts("Searching for a job", "Searching for a job in the IT area", "1213243", "1545931375100",
				"1545931375100", "GPS", "Arco do Cego", "38.736151, -9.142168", "400", "BLACK", "Student (Job)",
				u.getNumOfPost(), "DECENTRALIZED");
		u.setNumOfPost();

		ArrayList<Posts> posts1 = new ArrayList<>();
		u1.setNumOfPost();
		Posts p2 = new Posts("Looking for a beer company", "In Lisbon exists beer company's? ", "1213243", "1493283131847", "1545931375100", "GPS", "Arco do Cego",
				"38.736151, -9.142168", "200", "EVERYONE", u1.getNumOfPost(), "CENTRALIZED");
		u1.setNumOfPost();
		Posts p3 = new Posts("Animals", "I'm doing a gorilla study and I want your support to help me to protect these animals.", "1213243", "1493283131847", "1545931375100", "GPS",
				"Arco do Cego", "38.736151, -9.142168", "100", "WHITE", "Student (Jobs),Gorilla (Animals)",
				u1.getNumOfPost(), "CENTRALIZED");
		u1.setNumOfPost();

		Posts p4 = new Posts("Wine", "Anyone for a glass of wine?", "1213243", "1493283131847", "1545931375100",
				"WIFI", l12, "EVERYONE", u1.getNumOfPost(), "CENTRALIZED");
		u1.setNumOfPost();
		Posts p5 = new Posts("Party", "Meo Sudoeste presentation in Faro.", "1213243", "1493283131847", "1545931375100",
				"GPS", "Faro", "37.019355, -7.930440", "300", "BLACK", "Employed (Job)", u1.getNumOfPost(),
				"CENTRALIZED");
		u1.setNumOfPost();
		Posts p6 = new Posts("Guide", "I want to visit Taj Mahal, any guide here?", "1213243", "1493283131847", "1545931375100", "GPS",
				"Taj Mahal", "27.174901, 78.042048", "200", "BLACK", "Student (Jobs)", u1.getNumOfPost(),
				"CENTRALIZED");
		u1.setNumOfPost();
		Posts p7 = new Posts("Beach", "Were is the best beach in Portugal?", "1213243", "1493283131847", "1545931375100", "GPS",
				"Arco do Cego", "38.736151, -9.142168", "500", "EVERYONE",
				u1.getNumOfPost(), "CENTRALIZED");
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

	}

}
