package pt.ulisboa.tecnico.cmov.locmessServer;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class LocMess {

	private static final int port = 6666;
	private static Socket s;
	private static HashMap<String, User> users = new HashMap<>();
	private static HashMap<User, Posts> posts = new HashMap<>();
	private static HashMap<User, HashMap<String, ArrayList<String>>> userRestrictions = new HashMap<>();
	private static HashMap<String, ArrayList<String>> globalRestrictions = new HashMap<>();
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
			LocMess.getUserRestrictions().put(u, null);
			System.out.println(LocMess.getUsers().get("qwerty").getPassword());

			while (true) {
				DataInputStream dis = new DataInputStream(s.getInputStream());
				String str = dis.readUTF();
				String[] res = parser(str);

				System.out.println("message= " + str);

				if (res[0].equals("Login")) {
					new Login(res[1], res[2]);
				}
				if (res[0].equals("Restrictions")) {

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

}
