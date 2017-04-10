package pt.ulisboa.tecnico.cmov.locmessServer;

import java.util.Hashtable;
import java.util.Random;

public class Session {
	private Hashtable<String, String> userSessions = new Hashtable<String, String>();

	public Hashtable<String, String> getUserSessions() {
		return userSessions;
	}

	public String createToken() {
		Random r = new Random();
		Integer n = r.nextInt(50) + 1;
		String token = Integer.toString(n);
		return token;
	}

	public boolean userInSession(String userToken) {

		if (userSessions.containsKey(userToken)) {
			return true;
		}
		return false;
	}

	public String addUserToSession(String username) {
		String token = createToken();
		userSessions.put(token, username);
		return token;
	}

	public void removeUserFromSession(String token) {
		userSessions.remove(token);
	}

	public User getUserFromSession(String token) {

		if (token == null) {
			return null;
		}

		if (userSessions.containsKey(token)) {
			User u = LocMess.getUsers().get(userSessions.get(token));
			return u;
		}
		return null;
	}

}
