package pt.ulisboa.tecnico.cmov.locmessServer;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Login {

	public Login(String username, String password) {
		Socket s = LocMess.getSocket();
		DataOutputStream dataOutputStream;
		if (LocMess.getUsers().get(username).getPassword().equals(password)) {
			try {
				dataOutputStream = new DataOutputStream(s.getOutputStream());
				dataOutputStream.writeUTF("OK");
				dataOutputStream.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			try {
				dataOutputStream = new DataOutputStream(s.getOutputStream());
				dataOutputStream.writeUTF("WRONG");
				dataOutputStream.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

}
