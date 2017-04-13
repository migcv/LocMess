package pt.ulisboa.tecnico.cmov.locmessServer;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

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

	public void sendRestrictions(String username) {
		User u = getUserByUsername(username);
		HashMap<String, ArrayList<String>> res = LocMess.getUserRestrictions().get(u);
		Socket s = LocMess.getSocket();
		DataOutputStream dataOutputStream;
		if (res == null) {
			try {
				dataOutputStream = new DataOutputStream(s.getOutputStream());
				dataOutputStream.writeUTF("WRONG");
				dataOutputStream.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			Set<String> keySet = res.keySet();
			String response = "";
			for(String key : keySet) {
				for(String restriction : res.get(key)) {
					response += key + "," + restriction + ";:;";
				}
			}
			System.out.println("SEND RESTRICTIONS: " + response);
			try {
				dataOutputStream = new DataOutputStream(s.getOutputStream());
				dataOutputStream.writeUTF(response);
				dataOutputStream.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
