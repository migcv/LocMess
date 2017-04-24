package pt.ulisboa.tecnico.cmov.locmessServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
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
				e.toString();
				break;
			}

			String[] res = parser(str);

			System.out.println("message= " + str);

			expiredPosts();

			if (res[0].equals("Login")) {
				new Login(s, res[1], res[2]);
			}
			if (res[0].equals("SignUp")) {
				new SignUp(s, res[1], res[2], res[3]);
			}
			if (res[0].equals("CurrentGPS")) {
				User u1 = LocMess.getSession().getUserFromSession(res[1]);
				u1.setCurrentGPS(res[2]);
				u1.sendPostsGPS(s);
			}
			if (res[0].equals("CurrentWIFI")) {
				User u1 = LocMess.getSession().getUserFromSession(res[1]);
				u1.setCurrentWIFI(res[2]);
				u1.sendPostsWIFI(s);
			}
			if (res[0].equals("GetAllRestrictions")) {
				getAllRestrictions();
			}
			if (res[0].equals("MYRestrictions")) {
				User u1 = LocMess.getSession().getUserFromSession(res[1]);
				u1.sendRestrictions(s);
			}
			if (res[0].equals("AddRestrictions")) {
				User ux = LocMess.getSession().getUserFromSession(res[1]);
				ux.addRestriction(res[2]);
			}
			if (res[0].equals("RemoveRestrictions")) {
				User ux = LocMess.getSession().getUserFromSession(res[1]);
				ux.removeRestriction(res[2]);
			}
			if (res[0].equals("NewPosts") && res[7].equals("WIFI_DIRECT") && !(res[9].equals("EVERYONE"))) {
				User u = LocMess.getSession().getUserFromSession(res[1]);
				u.addPostsWIFI_DIRECT(res[2], res[3], res[4], res[5], res[6], res[7], res[8], res[9], res[10]);
			}
			if (res[0].equals("NewPosts") && res[7].equals("WIFI_DIRECT") && res[9].equals("EVERYONE")) {
				User u = LocMess.getSession().getUserFromSession(res[1]);
				u.addPostsWIFI_DIRECT(res[2], res[3], res[4], res[5], res[6], res[7], res[8], res[10]);
			}
			if (res[0].equals("NewPosts") && res[7].equals("GPS") && !(res[11].equals("EVERYONE"))) {
				User u = LocMess.getSession().getUserFromSession(res[1]);
				u.addPostsGPS(res[2], res[3], res[4], res[5], res[6], res[7], res[8], res[9], res[10], res[11],
						res[12]);
			}
			if (res[0].equals("NewPosts") && res[7].equals("GPS") && res[11].equals("EVERYONE")) {
				User u = LocMess.getSession().getUserFromSession(res[1]);
				u.addPostsGPS(res[2], res[3], res[4], res[5], res[6], res[7], res[8], res[9], res[10], res[11]);
			}
			if (res[0].equals("NewPosts") && res[7].equals("WIFI") && !(res[9].equals("EVERYONE"))) {
				User u = LocMess.getSession().getUserFromSession(res[1]);
				u.addPostsWIFI(res[2], res[3], res[4], res[5], res[6], res[7], res[8], res[9], res[10]);
			}
			if (res[0].equals("NewPosts") && res[7].equals("WIFI") && res[9].equals("EVERYONE")) {
				User u = LocMess.getSession().getUserFromSession(res[1]);
				u.addPostsWIFI(res[2], res[3], res[4], res[5], res[6], res[7], res[8], res[9]);
			}
			if (res[0].equals("MYPosts")) {
				User ux = LocMess.getSession().getUserFromSession(res[1]);
				ux.sendUserPosts(s);
			}
			if (res[0].equals("RemovePost")) {
				User ux = LocMess.getSession().getUserFromSession(res[1]);
				ux.removePost(res[2]);
			}
			if (res[0].equals("MYLocations")) {
				User u = LocMess.getSession().getUserFromSession(res[1]);
				u.sendLocations(s);
			}
			if (res[0].equals("GetAllLocations")) {
				sendAllLocations();
			}
			if (res[0].equals("AddLocations")) {
				User u = LocMess.getSession().getUserFromSession(res[1]);
				u.addLocations(res[2]);
			}
			if (res[0].equals("RemoveLocations")) {
				User u = LocMess.getSession().getUserFromSession(res[1]);
				u.removeLocations(res[2]);
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

	private void sendAllLocations() {
		ArrayList<Locations> locations = LocMess.getGlobalLocations();
		String response = "";
		DataOutputStream dataOutputStream;
		for (int i = 0; i < locations.size(); i++) {
			if (locations.get(i).getType().equals("GPS")) {
				response += response + locations.get(i).getType() + ";:;" + locations.get(i).getLocationName() + ";:;"
						+ locations.get(i).getLatitude() + ", " + locations.get(i).getLongitude() + ";:;";
			} else if (locations.get(i).getType().equals("WIFI")) {
				String ssIDSend = "";
				for (int j = 0; j < locations.get(i).getSSId().size(); j++) {
					ssIDSend = ssIDSend + locations.get(i).getSSId().get(j) + ",";
				}

				response += response + locations.get(i).getType() + ";:;" + locations.get(i).getLocationName() + ";:;"
						+ ssIDSend + ";:;";
			}
			try {
				System.out.println(response);
				dataOutputStream = new DataOutputStream(s.getOutputStream());
				dataOutputStream.writeUTF(response);
				dataOutputStream.flush();
				response = "";
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

	private void expiredPosts() {
		HashMap<User, ArrayList<Posts>> aux = LocMess.getUserPosts();
		if (aux.isEmpty()) {
			return;
		}
		Set<User> users = aux.keySet();
		for (User u : users) {
			ArrayList<Posts> posts = aux.get(u);
			for (int i = 0; i < posts.size(); i++) {
				Long postLimitTime = Long.valueOf(posts.get(i).getLimitDateTime());
				Long currentTime = System.currentTimeMillis();
				if (postLimitTime - currentTime < 0) {
					aux.get(u).remove(i);
				}
			}
		}
	}

	private void signOut(String token) {
		LocMess.getSession().removeUserFromSession(token);
		try {
			s.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
