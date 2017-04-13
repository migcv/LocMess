package pt.ulisboa.tecnico.cmov.locmessServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Set;

public class LocMess {

	private static final int port = 10000;
	private static Socket s;
	private static HashMap<String, User> users = new HashMap<>();
	private static HashMap<User, Posts> posts = new HashMap<>();
	private static HashMap<User, HashMap<String, ArrayList<String>>> userRestrictions = new HashMap<>();
	private static HashMap<String, ArrayList<String>> globalRestrictions = new HashMap<>();
	private static Hashtable<String, String> userSessions = new Hashtable<String, String>();
	private static Session session = new Session();

	public static void main(String[] args) {
		try {
			ServerSocket ss = new ServerSocket(port);
			s = ss.accept();// establishes connection
			System.out.println("ACEITEI ALGUEM!");

			// SIGNUP
			User u = new User("qwerty", "qwerty", "qwerty@gmail.com");
			LocMess.getUsers().put("qwerty", u);
			LocMess.getPosts().put(u, null);

			// Create user restrictions
			HashMap<String, ArrayList<String>> aux = new HashMap<>();
			ArrayList<String> aux1 = new ArrayList<>();
			aux1.add("Macaco");
			aux1.add("Gorila");
			aux1.add("Chimpanze");
			aux.put("Animals", aux1);

			
			ArrayList<String> aux2 = new ArrayList<>();
			aux2.add("Estudante");
			aux2.add("Pedreiro");
			aux2.add("Desempregado");
			aux.put("Jobs", aux2);
			
			LocMess.getUserRestrictions().put(u, aux);
			LocMess.getGlobalRestrictions().put("Animals", aux1);
			LocMess.getGlobalRestrictions().put("Jobs", aux2);
			System.out.println(LocMess.getUsers().get("qwerty").getPassword());

			while (true) {
				DataInputStream dis = new DataInputStream(s.getInputStream());
				String str = dis.readUTF();
				String[] res = parser(str);

				System.out.println("message= " + str);

				if (res[0].equals("Login")) {
					new Login(res[1], res[2]);
				}
				if (res[0].equals("MYRestrictions")) {
					User u1 = session.getUserFromSession(res[1]);
					LocMess.getUsers().get(u1.getUsername()).sendRestrictions(u1.getUsername());
				}
				if (res[0].equals("Restrictions")) {
					User ux = session.getUserFromSession(res[1]);
					LocMess.getUsers().get(ux.getUsername()).addRestriction(res[1], res[2]);
				}
				if (res[0].equals("SignUp")) {
					new SignUp(res[1], res[2], res[3]);
				}
				if (res[0].equals("NewPosts") && res[7].equals("WIFI_DIRECT")) {
					Posts p = new Posts();
					p.addPostsWIFI(res[1], res[2], res[3], res[4], res[5], res[6], res[7]);
				}
				if (res[0].equals("NewPosts") && res[7].equals("GPS")) {
					Posts p = new Posts();
					p.addPostsGPS(res[1], res[2], res[3], res[4], res[5], res[6], res[7], res[8], res[9]);
				}
				if(res[0].equals("GetAllRestrictions")) {
					getAllRestrictions();
				}
				if (res[0].equals("SignOut")) {
					ss.close();
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String[] parser(String revc) {
		return revc.split(";:;");
	}

	public static Socket getSocket() {
		return s;
	}

	public static HashMap<String, User> getUsers() {
		return users;
	}

	public static HashMap<User, Posts> getPosts() {
		return posts;
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

	public static Hashtable<String, String> getUserSessions() {
		return userSessions;
	}
	
	private static void getAllRestrictions() throws IOException {
		Set<String> keySet = globalRestrictions.keySet();
		String response = "";
		for(String key : keySet) {
			for(String restriction : globalRestrictions.get(key)) {
				response += restriction + " (" + key + ")" + ";:;";
			}
		}
		System.out.println("GetAllRestrictions-RESPONSE: " + response);
		DataOutputStream dataOutputStream = new DataOutputStream(s.getOutputStream());
		dataOutputStream.writeUTF(response);
		dataOutputStream.flush();
	}

}
