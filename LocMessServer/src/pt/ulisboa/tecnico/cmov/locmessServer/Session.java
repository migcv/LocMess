package pt.ulisboa.tecnico.cmov.locmessServer;

import java.util.UUID;

public class Session {

	public String createToken() {
		String token = UUID.randomUUID().toString();
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
		if (LocMess.getUserSessions().containsKey(token)) {
			LocMess.getUserSessions().remove(token);
		}
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
