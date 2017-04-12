package pt.ulisboa.tecnico.cmov.locmessServer;

import java.util.Hashtable;
import java.util.Random;

public class Session {

	public String createToken() {
		Random r = new Random();
		Integer n = r.nextInt(50) + 1;
		String token = Integer.toString(n);
		return token;
	}

	public boolean userInSession(String userToken) {

		if (LocMess.getUserSessions().containsKey(userToken)) {
			return true;
		}
		return false;
	}

	public String addUserToSession(String username) {
		String token = createToken();
		while (userInSession(token)) {
			token = createToken();
		}
		LocMess.getUserSessions().put(token, username);
		return token;
	}

	public void removeUserFromSession(String token) {
		LocMess.getUserSessions().remove(token);
	}

	public User getUserFromSession(String token) {
		if (token == null) {
			return null;
		}
		if (LocMess.getUserSessions().containsKey(token)) {
			User u = LocMess.getUsers().get(LocMess.getUserSessions().get(token));
			return u;
		}
		return null;
	}

}
