package pt.ulisboa.tecnico.cmov.locmessServer;

import java.util.ArrayList;

public class User {

	private String username;
	private String password;
	private String email;

	public User(String username, String password, String email) {
		this.username = username;
		this.password = password;
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getEmail() {
		return email;
	}

	public User getUserByUsername(String username) {
		return LocMess.getUsers().get(username);
	}

	public void addRestriction(String username, String restriction) {
		User u = getUserByUsername(username);
		String[] res = restriction.split(":");
		if (LocMess.getUserRestrictions().get(u).containsKey(res[0])) {
			LocMess.getUserRestrictions().get(u).get(res[0]).add(res[1]);
			ArrayList<String> aux = LocMess.getGlobalRestrictions().get(res[0]);
			aux.add(res[1]);
			LocMess.getGlobalRestrictions().put(res[0], aux);
		} else {
			ArrayList<String> aux = new ArrayList<>();
			aux.add(res[1]);
			LocMess.getUserRestrictions().get(u).put(res[0], aux);
		}
	}

}
