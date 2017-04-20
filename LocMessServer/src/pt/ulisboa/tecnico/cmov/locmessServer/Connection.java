package pt.ulisboa.tecnico.cmov.locmessServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Set;

public class Connection implements Runnable {

	private Socket s;

	public Connection(Socket s) {
		this.s = s;
	}

	@Override
	public void run() {

		while (true) {

			DataInputStream dis;
			String str = "";
			try {
				dis = new DataInputStream(s.getInputStream());
				str = dis.readUTF();
			} catch (IOException e) {
				break;
			}

			String[] res = parser(str);

			System.out.println("message= " + str);

			if (res[0].equals("Login")) {
				new Login(s, res[1], res[2]);
			}
			if (res[0].equals("SignUp")) {
				new SignUp(s, res[1], res[2], res[3]);
			}
			if (res[0].equals("MYRestrictions")) {
				User u1 = LocMess.getSession().getUserFromSession(res[1]);
				LocMess.getUsers().get(u1.getUsername()).sendRestrictions(s, u1.getUsername());
			}
			if (res[0].equals("AddRestrictions")) {
				User ux = LocMess.getSession().getUserFromSession(res[1]);
				LocMess.getUsers().get(ux.getUsername()).addRestriction(ux.getUsername(), res[2]);
			}
			if (res[0].equals("RemoveRestrictions")) {
				User ux = LocMess.getSession().getUserFromSession(res[1]);
				LocMess.getUsers().get(ux.getUsername()).removeRestriction(ux.getUsername(), res[2]);
			}
			if (res[0].equals("NewPosts") && res[7].equals("WIFI_DIRECT")) {
				User u = LocMess.getSession().getUserFromSession(res[1]);
				u.addPostsWIFI(res[2], res[3], res[4], res[5], res[6], res[7], res[8], res[9]);
			}
			if (res[0].equals("NewPosts") && res[7].equals("GPS")) {
				User u = LocMess.getSession().getUserFromSession(res[1]);
				u.addPostsGPS(res[2], res[3], res[4], res[5], res[6], res[7], res[8], res[9], res[10], res[11]);
			}
			if (res[0].equals("MYPosts")) {
				User ux = LocMess.getSession().getUserFromSession(res[1]);
				ux.sendUserPosts(s);
			}
			if (res[0].equals("RemovePost")) {
				User ux = LocMess.getSession().getUserFromSession(res[1]);
				ux.removePost(res[2]);
			}
			if (res[0].equals("GetAllRestrictions")) {
				getAllRestrictions();
			}
			if (res[0].equals("SignOut")) {
				signOut(res[1]);
			}
		}

	}

	public String[] parser(String revc) {
		return revc.split(";:;");
	}

	private void getAllRestrictions() {
		Set<String> keySet = LocMess.getGlobalRestrictions().keySet();
		String response = "";
		for (String key : keySet) {
			for (String restriction : LocMess.getGlobalRestrictions().get(key)) {
				response += restriction + " (" + key + ")" + ";:;";
			}
		}
		System.out.println("GetAllRestrictions-RESPONSE: " + response);
		DataOutputStream dataOutputStream;
		try {
			dataOutputStream = new DataOutputStream(s.getOutputStream());
			dataOutputStream.writeUTF(response);
			dataOutputStream.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void signOut(String token) {

		LocMess.getSession().removeUserFromSession(token);

		try {
			s.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
