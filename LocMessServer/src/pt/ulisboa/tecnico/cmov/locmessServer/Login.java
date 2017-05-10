package pt.ulisboa.tecnico.cmov.locmessServer;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Login {

	public Login(Socket s, String username, String password) {
		DataOutputStream dataOutputStream;
		
		System.out.println("USERNAME: " + username);
		System.out.println("PASSWORD: " + password);
		if (LocMess.getUsers().isEmpty() || LocMess.getUsers().get(username) == null) {
			try {
				dataOutputStream = new DataOutputStream(s.getOutputStream());
				dataOutputStream.writeUTF("WRONG");
				dataOutputStream.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		else if (LocMess.getUsers().get(username).getPassword().equals(password)) {
			Session ss = new Session();
			String token = ss.addUserToSession(username);
			System.out.println("TOKEN: " + token);
			try {
				dataOutputStream = new DataOutputStream(s.getOutputStream());
				dataOutputStream.writeUTF("OK;:;" + token);
				dataOutputStream.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} else {
			try {
				dataOutputStream = new DataOutputStream(s.getOutputStream());
				dataOutputStream.writeUTF("WRONG");
				dataOutputStream.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

}
