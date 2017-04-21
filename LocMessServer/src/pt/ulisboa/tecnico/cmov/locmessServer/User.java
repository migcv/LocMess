package pt.ulisboa.tecnico.cmov.locmessServer;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class User {

	private String username;
	private String password;
	private String email;
	private int numOfPost;
	private String currentLocation;
	private Double currentLatitude;
	private Double currentLongitude;

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

	public String getCurrentLocation() {
		return currentLocation;
	}

	public void setCurrentLocation(String currentLocation) {
		this.currentLocation = currentLocation;
		String[] aux = currentLocation.split(", ");
		setCurrentLatitude(Double.parseDouble(aux[0]));
		setCurrentLongitude(Double.parseDouble(aux[1]));
	}

	public Double getCurrentLatitude() {
		return currentLatitude;
	}

	public void setCurrentLatitude(Double latitude) {
		this.currentLatitude = latitude;
	}

	public Double getCurrentLongitude() {
		return currentLongitude;
	}

	public void setCurrentLongitude(Double longitude) {
		this.currentLongitude = longitude;
	}

	public void addRestriction(String restriction) {
		String[] res = restriction.split(":");
		if (LocMess.getUserRestrictions().get(this) == null) {
			HashMap<String, ArrayList<String>> a = new HashMap<>();
			ArrayList<String> a1 = new ArrayList<>();
			a1.add(res[1]);
			a.put(res[0], a1);
			if (LocMess.getGlobalRestrictions().get(res[0]) == null) {
				LocMess.getGlobalRestrictions().put(res[0], a1);
			} else if(!LocMess.getGlobalRestrictions().get(res[0]).contains(res[1])){
				LocMess.getGlobalRestrictions().get(res[0]).add(res[1]);
			}
			LocMess.getUserRestrictions().put(this, a);
		} else if (LocMess.getUserRestrictions().get(this).containsKey(res[0])) {
			LocMess.getUserRestrictions().get(this).get(res[0]).add(res[1]);
			if(!LocMess.getGlobalRestrictions().get(res[0]).contains(res[1])){
				LocMess.getGlobalRestrictions().get(res[0]).add(res[1]);
			}
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
		ArrayList<Posts> aux = LocMess.getUserPosts().get(this);
		DataOutputStream dataOutputStream;
		if (aux == null) {
			try {
				dataOutputStream = new DataOutputStream(s.getOutputStream());
				dataOutputStream.writeUTF("END");
				dataOutputStream.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
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
	}

	public void addPostsWIFI(String title, String content, String contact, String date, String time,
			String deliveryMode, String restrictionPolicy, String restrictions) {
		setNumOfPost();
		Posts p = new Posts(title, content, contact, date, time, deliveryMode, restrictionPolicy, restrictions,
				getNumOfPost());
		if (LocMess.getUserPosts().get(this) != null)
			LocMess.getUserPosts().get(this).add(p);
		else {
			ArrayList<Posts> aux = new ArrayList<>();
			aux.add(p);
			LocMess.getUserPosts().put(this, aux);
		}

	}

	public void addPostsWIFI(String title, String content, String contact, String date, String time,
			String deliveryMode, String restrictionPolicy) {
		setNumOfPost();
		Posts p = new Posts(title, content, contact, date, time, deliveryMode, restrictionPolicy, getNumOfPost());
		if (LocMess.getUserPosts().get(this) != null)
			LocMess.getUserPosts().get(this).add(p);
		else {
			ArrayList<Posts> aux = new ArrayList<>();
			aux.add(p);
			LocMess.getUserPosts().put(this, aux);
		}

	}

	public void addPostsGPS(String title, String content, String contact, String date, String time, String deliveryMode,
			String coordinates, String radius, String restrictionPolicy, String restrictions) {
		setNumOfPost();
		Posts p = new Posts(title, content, contact, date, time, deliveryMode, coordinates, radius, restrictionPolicy,
				restrictions, getNumOfPost());
		if (LocMess.getUserPosts().get(this) != null)
			LocMess.getUserPosts().get(this).add(p);
		else {
			ArrayList<Posts> aux = new ArrayList<>();
			aux.add(p);
			LocMess.getUserPosts().put(this, aux);
		}
	}

	public void addPostsGPS(String title, String content, String contact, String date, String time, String deliveryMode,
			String coordinates, String radius, String restrictionPolicy) {
		setNumOfPost();
		Posts p = new Posts(title, content, contact, date, time, deliveryMode, coordinates, radius, restrictionPolicy,
				getNumOfPost());
		if (LocMess.getUserPosts().get(this) != null)
			LocMess.getUserPosts().get(this).add(p);
		else {
			ArrayList<Posts> aux = new ArrayList<>();
			aux.add(p);
			LocMess.getUserPosts().put(this, aux);
		}
	}

	public void removePost(String postID) {
		int id = Integer.parseInt(postID);
		ArrayList<Posts> aux = LocMess.getUserPosts().get(this);
		for (int i = 0; i < aux.size(); i++) {
			if (aux.get(i).getId() == id) {
				System.out.println(LocMess.getUserPosts().get(this).get(i).getTitle());
				LocMess.getUserPosts().get(this).remove(i);
				break;
			}
		}

	}

	public void sendLocations(Socket s) {
		ArrayList<Locations> locations = LocMess.getUsersLocations().get(this);
		String toSend = "";
		DataOutputStream dataOutputStream;
		if (locations == null) {
			try {
				dataOutputStream = new DataOutputStream(s.getOutputStream());
				dataOutputStream.writeUTF("END");
				dataOutputStream.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			for (int i = 0; i < locations.size(); i++) {
				if (locations.get(i).getType().equals("GPS")) {
					toSend = toSend.concat("MYLocations" + ";:;" + locations.get(i).getType() + ";:;"
							+ locations.get(i).getLocationName() + ";:;" + locations.get(i).getLatitude().toString()
							+ ", " + locations.get(i).getLongitude().toString());
				} else {
					toSend = toSend.concat("MYLocations" + ";:;" + locations.get(i).getType() + ";:;"
							+ locations.get(i).getLocationName() + ";:;" + locations.get(i).getSSId());
				}

				try {
					System.out.println(toSend);
					dataOutputStream = new DataOutputStream(s.getOutputStream());
					dataOutputStream.writeUTF(toSend);
					dataOutputStream.flush();
					toSend = "";
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

	public void addLocations(String loc) {
		String[] aux = loc.split(";:");
		Locations l1;
		if (aux[0].equals("GPS")) {
			l1 = new Locations(aux[0], aux[1], aux[2]);
		} else {
			l1 = new Locations(aux[0], aux[1]);
		}
		if (LocMess.getUsersLocations().get(this) == null) {
			ArrayList<Locations> a1 = new ArrayList<>();
			a1.add(l1);
			LocMess.getUsersLocations().put(this, a1);
			LocMess.getGlobalLocations().add(l1);
		} else {
			LocMess.getUsersLocations().get(this).add(l1);
			LocMess.getGlobalLocations().add(l1);
		}
	}

	public void removeLocations(String loc) {
		ArrayList<Locations> locations = LocMess.getUsersLocations().get(this);
		String[] aux = loc.split(";:");
		for (int i = 0; i < locations.size(); i++) {
			if (aux.length == 3) {
				if (locations.get(i).getType().equals(aux[0]) && locations.get(i).getLocationName().equals(aux[1])
						&& locations.get(i).getSSId().equals(aux[2])) {
					LocMess.getUsersLocations().get(this).remove(i);
					break;
				}
			} else if (locations.get(i).getType().equals(aux[0]) && locations.get(i).getSSId().equals(aux[2])) {
				LocMess.getUsersLocations().get(this).remove(i);
				break;
			}
		}
	}

	public void sendPosts(Socket s) {
		Set<User> keySet = LocMess.getUserPosts().keySet();
		DataOutputStream dataOutputStream;
		for (User key : keySet) {
			for (int i = 0; i < LocMess.getUserPosts().get(key).size(); i++) {
				if (!this.equals(key)) {
					Posts p = LocMess.getUserPosts().get(key).get(i);
					if (verifyPostRange(this.currentLatitude, this.currentLongitude, p.getLatitude(), p.getLongitude(),
							p.getRadius())) {
						if (p.getRestrictionPolicy().equals("EVERYONE")) {
							String response = "Posts;:;" + p.getId() + "," + p.getTitle() + "," + p.getContent() + ","
									+ p.getContact() + "," + p.getDate() + "," + p.getTime() + ","
									+ p.getDeliveryMode();
							try {
								dataOutputStream = new DataOutputStream(s.getOutputStream());
								dataOutputStream.writeUTF(response);
								dataOutputStream.flush();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						if (p.getRestrictionPolicy().equals("WHITE")) {
							HashMap<String, ArrayList<String>> userRestrictions = LocMess.getUserRestrictions()
									.get(this);
							Set<String> ures = userRestrictions.keySet();
							String restrictions = p.getRestrictions();
							HashMap<String, ArrayList<String>> postRestrictions = getRestrictionsFromPost(restrictions);
							Set<String> pres = postRestrictions.keySet();
							for (String res : pres) {

							}

						}
						if (p.getRestrictionPolicy().equals("BLACK")) {
							// Don't receive the message
						}
					}
				}
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

	public boolean verifyPostRange(double currentLat, double currentLong, double lat, double longi, double radius) {
		double dx = lat - currentLat;
		double dy = longi - currentLong;
		if ((Math.pow(dx, 2) + Math.pow(dy, 2)) <= Math.pow(radius, 2)) {
			return true;
		}
		return false;
	}

	public HashMap<String, ArrayList<String>> getRestrictionsFromPost(String restrictions) {
		String[] aux = restrictions.split(",");
		HashMap<String, ArrayList<String>> newW = new HashMap<>();
		for (int i = 0; i < aux.length; i++) {
			if (aux[i].equals(""))
				;

			else {
				String key = aux[i].substring(aux[i].indexOf("(") + 1, aux[i].indexOf(")"));
				String value = aux[i].split("\\(")[0];

				if (newW.containsKey(key)) {
					newW.get(key).add(value);
				} else {
					ArrayList<String> a = new ArrayList<>();
					a.add(value);
					newW.put(key, a);
				}
			}
		}
		return newW;
	}

}
