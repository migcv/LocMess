package pt.ulisboa.tecnico.cmov.locmessServer;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class SignUp {

	public SignUp(String username, String password, String email) {
		Socket s = LocMess.getSocket();
		DataOutputStream dataOutputStream;
		ArrayList<User> users = (ArrayList<User>) LocMess.getUsers().values();
		String toSend = "";

		for (User u : users) {
			if (u.getEmail().equals(email)) {
				toSend = "Email";
				break;
			}
		}
		if (LocMess.getUsers().containsKey(username)) {
			toSend = toSend.concat(":Username");
		}

		if (!toSend.equals("")) {
			try {
				dataOutputStream = new DataOutputStream(s.getOutputStream());
				dataOutputStream.writeUTF(toSend);
				dataOutputStream.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		User u = new User(username, password, email);
		LocMess.getUsers().put(username, u);
		LocMess.getPosts().put(u, null);
		LocMess.getUserRestrictions().put(u, null);
	}

}
