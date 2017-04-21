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
	private int numOfPost;

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

	public int getNumOfPost() {
		return numOfPost;
	}

	public void setNumOfPost() {
		this.numOfPost++;
	}

	public void addRestriction(String restriction) {
		String[] res = restriction.split(":");
		if (LocMess.getUserRestrictions().get(this).containsKey(res[0])) {
			LocMess.getUserRestrictions().get(this).get(res[0]).add(res[1]);
			LocMess.getGlobalRestrictions().get(res[0]).add(res[1]);
			System.out.println(LocMess.getUserRestrictions().get(this).get(res[0]).size());
		} else {
			ArrayList<String> aux = new ArrayList<>();
			aux.add(res[1]);
			LocMess.getUserRestrictions().get(this).put(res[0], aux);
			LocMess.getGlobalRestrictions().put(res[0], aux);
			System.out.println(LocMess.getUserRestrictions().get(this).get(res[0]).size());
		}
	}

	public void removeRestriction(String restriction) {
		String[] res = restriction.split(":");
		if (LocMess.getUserRestrictions().get(this).containsKey(res[0])) {
			LocMess.getUserRestrictions().get(this).get(res[0]).remove(res[1]);
			System.out.println(LocMess.getUserRestrictions().get(this).get(res[0]).size());
		}
		if (LocMess.getUserRestrictions().get(this).get(res[0]).isEmpty()) {
			LocMess.getUserRestrictions().get(this).remove(res[0]);
		}

	}

	public void sendRestrictions(Socket s) {
		HashMap<String, ArrayList<String>> res = LocMess.getUserRestrictions().get(this);
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
			String response = "MYPosts;:;" + aux.get(i).getId() + "," + aux.get(i).getTitle() + ","
					+ aux.get(i).getContent() + "," + aux.get(i).getContact() + "," + aux.get(i).getDate() + ","
					+ aux.get(i).getTime() + "," + aux.get(i).getDeliveryMode();
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

	public void addPostsWIFI(String title, String content, String contact, String date, String time,
			String deliveryMode, String restrictionPolicy, String restrictions) {
		setNumOfPost();
		Posts p = new Posts(title, content, contact, date, time, deliveryMode, restrictionPolicy, restrictions,
				getNumOfPost());
		if (LocMess.getPosts().containsKey(this))
			LocMess.getPosts().get(this).add(p);
		else {
			ArrayList<Posts> aux = new ArrayList<>();
			aux.add(p);
			LocMess.getPosts().put(this, aux);
		}

	}

	public void addPostsGPS(String title, String content, String contact, String date, String time, String deliveryMode,
			String coordinates, String radius, String restrictionPolicy, String restrictions) {
		setNumOfPost();
		Posts p = new Posts(title, content, contact, date, time, deliveryMode, coordinates, radius, restrictionPolicy,
				restrictions, getNumOfPost());
		if (LocMess.getPosts().containsKey(this))
			LocMess.getPosts().get(this).add(p);
		else {
			ArrayList<Posts> aux = new ArrayList<>();
			aux.add(p);
			LocMess.getPosts().put(this, aux);
		}
	}

	public void removePost(String postID) {
		int id = Integer.parseInt(postID);
		ArrayList<Posts> aux = LocMess.getPosts().get(this);
		for (int i = 0; i < aux.size(); i++) {
			if (aux.get(i).getId() == id) {
				System.out.println(LocMess.getPosts().get(this).get(i).getTitle());
				LocMess.getPosts().get(this).remove(i);
				break;
			}
		}

	}

	public void sendLocations(Socket s) {
		ArrayList<Locations> locations = LocMess.getUsersLocations().get(this);
		String toSend = "";
		DataOutputStream dataOutputStream;
		for (int i = 0; i < locations.size(); i++) {
			if (locations.get(i).getType().equals("GPS")) {
				toSend = toSend.concat("MYLocations" + ";:;" + locations.get(i).getType() + ";:;"
						+ locations.get(i).getLocationName() + ";:;" + locations.get(i).getId());
			} else {
				toSend = toSend
						.concat("MYLocations" + ";:;" + locations.get(i).getType() + ";:;" + locations.get(i).getId());
			}

			try {
				dataOutputStream = new DataOutputStream(s.getOutputStream());
				dataOutputStream.writeUTF(toSend);
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

	public void addLocations(String loc) {
		String[] aux = loc.split(";:");
		Locations l1;
		if (aux[0].equals("GPS")) {
			l1 = new Locations(aux[0], aux[1], aux[2]);
		} else {
			l1 = new Locations(aux[0], aux[1]);
		}
		LocMess.getUsersLocations().get(this).add(l1);
	}

	public void removeLocations(String loc) {
		ArrayList<Locations> locations = LocMess.getUsersLocations().get(this);
		String[] aux = loc.split(";:");
		for (int i = 0; i < locations.size(); i++) {
			if (aux.length == 3) {
				if (locations.get(i).getType().equals(aux[0]) && locations.get(i).getLocationName().equals(aux[1])
						&& locations.get(i).getId().equals(aux[2])) {
					LocMess.getUsersLocations().get(this).remove(i);
					break;
				}
			} else if (locations.get(i).getType().equals(aux[0]) && locations.get(i).getId().equals(aux[2])) {
				LocMess.getUsersLocations().get(this).remove(i);
				break;
			}
		}
	}

}
