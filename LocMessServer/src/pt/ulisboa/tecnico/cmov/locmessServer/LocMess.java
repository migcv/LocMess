package pt.ulisboa.tecnico.cmov.locmessServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

public class LocMess {

	private static final int port = 10000;
	private static ServerSocket ss;
	private static HashMap<String, User> users = new HashMap<>();
	private static HashMap<User, ArrayList<Posts>> userPosts = new HashMap<>();
	private static HashMap<User, HashMap<String, ArrayList<String>>> userRestrictions = new HashMap<>();
	private static HashMap<String, ArrayList<String>> globalRestrictions = new HashMap<>();
	private static Hashtable<String, String> userSessions = new Hashtable<String, String>();
	private static Session session = new Session();

	public static void main(String[] args) {
		System.out.println("Server Up");

		if (users.isEmpty()) {
			populate();
		}
		try {
			ss = new ServerSocket(port);
			while (true) {
				System.out.println("entrei no while!");
				Socket s = ss.accept();// establishes connection
				System.out.println("Accepted: " + s.getInetAddress());
				new Thread(new Connection(s)).start();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static HashMap<String, User> getUsers() {
		return users;
	}

	public static HashMap<User, ArrayList<Posts>> getPosts() {
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

	public static Hashtable<String, String> getUserSessions() {
		return userSessions;
	}

	private static void populate() {
		// SIGNUP
		User u = new User("qwerty", "qwerty", "qwerty@gmail.com");
		LocMess.getUsers().put("qwerty", u);
		LocMess.getPosts().put(u, null);

		// Create user restrictions
		HashMap<String, ArrayList<String>> aux = new HashMap<>();
		ArrayList<String> aux1 = new ArrayList<>();
		aux1.add("Monkey");
		aux1.add("Gorilla");
		aux1.add("Chimpanzee");
		aux.put("Animals", aux1);

		ArrayList<String> aux2 = new ArrayList<>();
		aux2.add("Student");
		aux2.add("Mason");
		aux2.add("Unemployed");
		aux.put("Jobs", aux2);

		LocMess.getUserRestrictions().put(u, aux);
		LocMess.getGlobalRestrictions().put("Animals", aux1);
		LocMess.getGlobalRestrictions().put("Jobs", aux2);

	}

}
