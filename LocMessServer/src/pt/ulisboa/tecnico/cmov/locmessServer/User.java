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
			LocMess.getGlobalRestrictions().get(res[0]).add(res[1]);
			System.out.println(LocMess.getUserRestrictions().get(u).get(res[0]).size());
		} else {
			ArrayList<String> aux = new ArrayList<>();
			aux.add(res[1]);
			LocMess.getUserRestrictions().get(u).put(res[0], aux);
			LocMess.getGlobalRestrictions().put(res[0], aux);
			System.out.println(LocMess.getUserRestrictions().get(u).get(res[0]).size());
		}
	}

	public void removeRestriction(String username, String restriction) {
		User u = getUserByUsername(username);
		String[] res = restriction.split(":");
		if (LocMess.getUserRestrictions().get(u).containsKey(res[0])) {
			LocMess.getUserRestrictions().get(u).get(res[0]).remove(res[1]);
			System.out.println(LocMess.getUserRestrictions().get(u).get(res[0]).size());
		}
		if (LocMess.getUserRestrictions().get(u).get(res[0]).isEmpty()) {
			LocMess.getUserRestrictions().get(u).remove(res[0]);
		}

	}

	public void sendRestrictions(Socket s, String username) {
		User u = getUserByUsername(username);
		HashMap<String, ArrayList<String>> res = LocMess.getUserRestrictions().get(u);
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
			for (String key : keySet) {
				for (String restriction : res.get(key)) {
					response += key + "," + restriction + ";:;";
				}
			}
			if (response.equals("")) {
				try {
					dataOutputStream = new DataOutputStream(s.getOutputStream());
					dataOutputStream.writeUTF("WRONG");
					dataOutputStream.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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

	public void sendUserPosts(Socket s) {
		ArrayList<Posts> aux = LocMess.getPosts().get(this);
		DataOutputStream dataOutputStream;
		for (int i = 0; i < aux.size(); i++) {
			String response = "MYPosts;:; " + aux.get(i).getTitle() + "," + aux.get(i).getContent() + ","
					+ aux.get(i).getContact() + "," + aux.get(i).getDate() + "," + aux.get(i).getTime() + ","
					+ aux.get(i).getDeliveryMode();
			try {
				dataOutputStream = new DataOutputStream(s.getOutputStream());
				dataOutputStream.writeUTF(response);
				dataOutputStream.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			dataOutputStream = new DataOutputStream(s.getOutputStream());
			dataOutputStream.writeUTF("END");
			dataOutputStream.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
