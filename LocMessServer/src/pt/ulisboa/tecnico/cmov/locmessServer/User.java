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
	private String currentGPS;
	private ArrayList<String> currentWIFI = new ArrayList<>();
	private Double currentLatitude;
	private Double currentLongitude;

	public User(String username, String password, String email) {
		this.username = username;
		this.password = password;
		this.email = email;
		this.currentWIFI = new ArrayList<>();
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
		return currentGPS;
	}

	public void setCurrentGPS(String currentGPS) {
		this.currentGPS = currentGPS;
		String[] aux = currentGPS.split(", ");
		setCurrentLatitude(Double.parseDouble(aux[0]));
		setCurrentLongitude(Double.parseDouble(aux[1]));
	}

	public ArrayList<String> getCurrentWIFI() {
		return currentWIFI;
	}

	public void setCurrentWIFI(String currentWIFI) {
		String[] aux = currentWIFI.split(",");
		ArrayList<String> current = new ArrayList<>();
		for (int i = 0; i < aux.length; i++) {
			current.add(aux[i]);
		}
		this.currentWIFI = current;

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
			} else if (!LocMess.getGlobalRestrictions().get(res[0]).contains(res[1])) {
				LocMess.getGlobalRestrictions().get(res[0]).add(res[1]);
			}
			LocMess.getUserRestrictions().put(this, a);
		} else if (LocMess.getUserRestrictions().get(this).containsKey(res[0])) {
			LocMess.getUserRestrictions().get(this).get(res[0]).add(res[1]);
			if (!LocMess.getGlobalRestrictions().get(res[0]).contains(res[1])) {
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
					e.printStackTrace();
				}
			}
			System.out.println("SEND RESTRICTIONS: " + response);
			try {
				dataOutputStream = new DataOutputStream(s.getOutputStream());
				dataOutputStream.writeUTF(response);
				dataOutputStream.flush();
			} catch (IOException e) {
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
				e.printStackTrace();
			}
		} else {
			System.out.println("-----------------" + aux.size());
			for (int i = 0; i < aux.size(); i++) {
				String response = null;
				if (aux.get(i).getDeliveryMode().equals("GPS")) {
					response = "MYPosts;:;" + aux.get(i).getId() + "," + aux.get(i).getTitle() + ","
							+ aux.get(i).getContent() + "," + aux.get(i).getContact() + ","
							+ aux.get(i).getCreationDateTime() + "," + aux.get(i).getLimitDateTime() + ","
							+ aux.get(i).getDeliveryMode() + "," + aux.get(i).getLocationName() + "," + aux.get(i).isFlag();
				} else if (aux.get(i).getDeliveryMode().equals("WIFI")) {
					response = "MYPosts;:;" + aux.get(i).getId() + "," + aux.get(i).getTitle() + ","
							+ aux.get(i).getContent() + "," + aux.get(i).getContact() + ","
							+ aux.get(i).getCreationDateTime() + "," + aux.get(i).getLimitDateTime() + ","
							+ aux.get(i).getDeliveryMode() + "," + aux.get(i).getLoc().getLocationName() + "," + aux.get(i).isFlag();
				}
				try {
					System.out.println(response);
					dataOutputStream = new DataOutputStream(s.getOutputStream());
					dataOutputStream.writeUTF(response);
					dataOutputStream.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				dataOutputStream = new DataOutputStream(s.getOutputStream());
				dataOutputStream.writeUTF("END");
				dataOutputStream.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void addPostsGPS(String title, String content, String contact, String creationDateTime, String limitDateTime,
			String deliveryMode, String locationName, String coordinates, String radius, String restrictionPolicy,
			String restrictions, String flag) {
		setNumOfPost();
		Posts p = new Posts(title, content, contact, creationDateTime, limitDateTime, deliveryMode, locationName,
				coordinates, radius, restrictionPolicy, restrictions, getNumOfPost(), flag);
		if (LocMess.getUserPosts().get(this) != null) {
			LocMess.getUserPosts().get(this).add(p);
			System.out.println("-----------------ADD" + LocMess.getUserPosts().get(this).size());
		} else {
			ArrayList<Posts> aux = new ArrayList<>();
			aux.add(p);
			LocMess.getUserPosts().put(this, aux);
		}
	}

	public void addPostsGPS(String title, String content, String contact, String creationDateTime, String limitDateTime,
			String deliveryMode, String locationName, String coordinates, String radius, String restrictionPolicy,
			String flag) {
		setNumOfPost();
		Posts p = new Posts(title, content, contact, creationDateTime, limitDateTime, deliveryMode, locationName,
				coordinates, radius, restrictionPolicy, getNumOfPost(), flag);
		if (LocMess.getUserPosts().get(this) != null){
			LocMess.getUserPosts().get(this).add(p);
			System.out.println("-----------------ADD" + LocMess.getUserPosts().get(this).size());
		}
		else {
			ArrayList<Posts> aux = new ArrayList<>();
			aux.add(p);
			LocMess.getUserPosts().put(this, aux);
		}
	}

	public void addPostsWIFI(String title, String content, String contact, String creationDateTime,
			String limitDateTime, String deliveryMode, String locationName, String restrictionPolicy, String flag) {
		setNumOfPost();
		ArrayList<String> ssid = getSSIDfromLocation(locationName);
		Locations l = new Locations(deliveryMode, locationName, ssid);
		Posts p = new Posts(title, content, contact, creationDateTime, limitDateTime, deliveryMode, l,
				restrictionPolicy, getNumOfPost(), flag);
		if (LocMess.getUserPosts().get(this) != null){
			LocMess.getUserPosts().get(this).add(p);
			System.out.println("-----------------ADD" + LocMess.getUserPosts().get(this).size());
		}
		else {
			ArrayList<Posts> aux = new ArrayList<>();
			aux.add(p);
			LocMess.getUserPosts().put(this, aux);
		}
	}

	public void addPostsWIFI(String title, String content, String contact, String creationDateTime,
			String limitDateTime, String deliveryMode, String locationName, String restrictionPolicy,
			String restrictions, String flag) {
		setNumOfPost();
		ArrayList<String> ssid = getSSIDfromLocation(locationName);
		Locations l = new Locations(deliveryMode, locationName, ssid);
		Posts p = new Posts(title, content, contact, creationDateTime, limitDateTime, deliveryMode, l,
				restrictionPolicy, restrictions, getNumOfPost(), flag);
		if (LocMess.getUserPosts().get(this) != null){
			LocMess.getUserPosts().get(this).add(p);
			System.out.println("-----------------ADD" + LocMess.getUserPosts().get(this).size());
		}
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
				e.printStackTrace();
			}
		} else {
			for (int i = 0; i < locations.size(); i++) {
				System.out.println(locations.get(i).getType());
				if (locations.get(i).getType().equals("GPS")) {
					toSend = toSend.concat("MYLocations" + ";:;" + locations.get(i).getType() + ";:;"
							+ locations.get(i).getLocationName() + ";:;"
							+ String.valueOf(locations.get(i).getLatitude()) + ", "
							+ String.valueOf(locations.get(i).getLongitude()));
				} else {
					String ssIDSend = "";
					for (int j = 0; j < locations.get(i).getSSId().size(); j++) {
						ssIDSend = ssIDSend + locations.get(i).getSSId().get(j) + ",";
					}
					toSend = toSend.concat("MYLocations" + ";:;" + locations.get(i).getType() + ";:;"
							+ locations.get(i).getLocationName() + ";:;" + ssIDSend);
				}

				try {
					System.out.println(toSend);
					dataOutputStream = new DataOutputStream(s.getOutputStream());
					dataOutputStream.writeUTF(toSend);
					dataOutputStream.flush();
					toSend = "";
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				dataOutputStream = new DataOutputStream(s.getOutputStream());
				dataOutputStream.writeUTF("END");
				dataOutputStream.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void addLocations(String loc) {
		String[] aux = loc.split(";:");
		Locations l1 = null;
		if (aux[0].equals("GPS")) {
			l1 = new Locations(aux[0], aux[1], aux[2]);
		}
		if (aux[0].equals("WIFI")) {
			ArrayList<String> wifiIDs = new ArrayList<>();
			for (int i = 2; i < aux.length; i++) {
				wifiIDs.add(aux[i]);
			}
			l1 = new Locations(aux[0], aux[1], wifiIDs);
		}
		if (LocMess.getUsersLocations().get(this) == null && l1 != null) {
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
		if (locations == null) {
			return;
		}
		for (int i = 0; i < locations.size(); i++) {
			if (locations.get(i).getType().equals(aux[0]) && locations.get(i).getLocationName().equals(aux[1])) {
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

					if (p.isFlag().equals("CENTRALIZED") && p.getDeliveryMode().equals("GPS") && this.currentLatitude != null
							&& this.currentLongitude != null) {
						if (verifyPostRange(this.currentLatitude, this.currentLongitude, p.getLatitude(),
								p.getLongitude(), p.getRadius())) {
							postsToSend(p, s, key, p.getDeliveryMode());
						}
					}
					if (p.isFlag().equals("CENTRALIZED")  && p.getDeliveryMode().equals("WIFI") && !this.getCurrentWIFI().isEmpty()) {
						if (verifyPostWIFI(this.getCurrentWIFI(), p.getLoc().getSSId())) {
							postsToSend(p, s, key, p.getDeliveryMode());
						}
					}
				}
			}
		}
		try {
			dataOutputStream = new DataOutputStream(s.getOutputStream());
			dataOutputStream.writeUTF("END");
			dataOutputStream.flush();
			System.out.println("END");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public boolean verifyPostRange(double currentLat, double currentLong, double lat, double longi, double radius) {
		double earthRadius = 6371000; // meters
		double dLat = Math.toRadians(lat - currentLat);
		double dLng = Math.toRadians(longi - currentLong);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat))
				* Math.cos(Math.toRadians(currentLat)) * Math.sin(dLng / 2) * Math.sin(dLng / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		float dist = (float) (earthRadius * c);
		if (dist <= radius) {
			return true;
		}
		return false;
	}

	public boolean verifyPostWIFI(ArrayList<String> currentWIFI, ArrayList<String> postWIFI) {
		for (int k = 0; k < currentWIFI.size(); k++) {
			for (int j = 0; j < postWIFI.size(); j++) {
				if (currentWIFI.get(k).equals(postWIFI.get(j))) {
					return true;
				}
			}
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
					newW.get(key).add(value.substring(0, value.length() - 1));
				} else {
					ArrayList<String> a = new ArrayList<>();
					a.add(value.substring(0, value.length() - 1));
					newW.put(key, a);
				}
			}
		}
		return newW;
	}

	public ArrayList<String> getSSIDfromLocation(String locationName) {
		ArrayList<Locations> allLoc = LocMess.getGlobalLocations();
		ArrayList<String> ssid = new ArrayList<>();
		for (int i = 0; i < allLoc.size(); i++) {
			if (allLoc.get(i).getType().equals("WIFI") && allLoc.get(i).getLocationName().equals(locationName)) {
				ssid = allLoc.get(i).getSSId();
				return ssid;
			}
		}
		return null;

	}

	public void postsToSend(Posts p, Socket s, User key, String type) {
		DataOutputStream dataOutputStream;
		if (p.getRestrictionPolicy().equals("EVERYONE")) {
			String response = null;
			if (type.equals("WIFI")) {
				response = "Posts;:;" + p.getId() + "," + key.getUsername() + "," + p.getTitle() + "," + p.getContent()
						+ "," + p.getContact() + "," + p.getCreationDateTime() + "," + p.getLimitDateTime() + ","
						+ p.getDeliveryMode() + "," + p.getLoc().getLocationName();
			} else if (type.equals("GPS")) {
				response = "Posts;:;" + p.getId() + "," + key.getUsername() + "," + p.getTitle() + "," + p.getContent()
						+ "," + p.getContact() + "," + p.getCreationDateTime() + "," + p.getLimitDateTime() + ","
						+ p.getDeliveryMode() + "," + p.getLocationName();
			}
			try {
				dataOutputStream = new DataOutputStream(s.getOutputStream());
				dataOutputStream.writeUTF(response);
				dataOutputStream.flush();
				System.out.println("EVERYONE: " + response);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (p.getRestrictionPolicy().equals("WHITE")) {
			HashMap<String, ArrayList<String>> userRestrictions = LocMess.getUserRestrictions().get(this);
			Set<String> ures = userRestrictions.keySet();
			String restrictions = p.getRestrictions();
			HashMap<String, ArrayList<String>> postRestrictions = getRestrictionsFromPost(restrictions);
			Set<String> pres = postRestrictions.keySet();
			boolean flag = false;
			for (String res : pres) {
				if (ures.contains(res)) {
					for (int a = 0; a < postRestrictions.get(res).size(); a++) {
						if (userRestrictions.get(res).contains(postRestrictions.get(res).get(a))) {
							String response = null;
							if (type.equals("WIFI")) {
								response = "Posts;:;" + p.getId() + "," + key.getUsername() + "," + p.getTitle() + ","
										+ p.getContent() + "," + p.getContact() + "," + p.getCreationDateTime() + ","
										+ p.getLimitDateTime() + "," + p.getDeliveryMode() + ","
										+ p.getLoc().getLocationName();
							} else if (type.equals("GPS")) {
								response = "Posts;:;" + p.getId() + "," + key.getUsername() + "," + p.getTitle() + ","
										+ p.getContent() + "," + p.getContact() + "," + p.getCreationDateTime() + ","
										+ p.getLimitDateTime() + "," + p.getDeliveryMode() + "," + p.getLocationName();
							}
							try {
								dataOutputStream = new DataOutputStream(s.getOutputStream());
								dataOutputStream.writeUTF(response);
								dataOutputStream.flush();
								System.out.println("WHITE: " + response);
							} catch (IOException e) {
								e.printStackTrace();
							}
							flag = true;
							break;
						}
					}
					if (flag) {
						break;
					}
				}
			}
		} else if (p.getRestrictionPolicy().equals("BLACK")) {
			HashMap<String, ArrayList<String>> userRestrictions = LocMess.getUserRestrictions().get(this);
			Set<String> ures = userRestrictions.keySet();
			String restrictions = p.getRestrictions();
			HashMap<String, ArrayList<String>> postRestrictions = getRestrictionsFromPost(restrictions);
			Set<String> pres = postRestrictions.keySet();
			int counter = 0;
			for (String res : pres) {
				if (ures.contains(res)) {
					for (int a = 0; a < postRestrictions.get(res).size(); a++) {
						if (userRestrictions.get(res).contains(postRestrictions.get(res).get(a))) {
							break;
						} else {
							String response = null;
							if (type.equals("WIFI")) {
								response = "Posts;:;" + p.getId() + "," + key.getUsername() + "," + p.getTitle() + ","
										+ p.getContent() + "," + p.getContact() + "," + p.getCreationDateTime() + ","
										+ p.getLimitDateTime() + "," + p.getDeliveryMode() + ","
										+ p.getLoc().getLocationName();
							} else if (type.equals("GPS")) {
								response = "Posts;:;" + p.getId() + "," + key.getUsername() + "," + p.getTitle() + ","
										+ p.getContent() + "," + p.getContact() + "," + p.getCreationDateTime() + ","
										+ p.getLimitDateTime() + "," + p.getDeliveryMode() + "," + p.getLocationName();
							}
							try {
								dataOutputStream = new DataOutputStream(s.getOutputStream());
								dataOutputStream.writeUTF(response);
								dataOutputStream.flush();
								System.out.println("BLACK: " + response);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				} else {
					counter++;
					if (counter == pres.size()) {
						String response = null;
						if (type.equals("WIFI")) {
							response = "Posts;:;" + p.getId() + "," + key.getUsername() + "," + p.getTitle() + ","
									+ p.getContent() + "," + p.getContact() + "," + p.getCreationDateTime() + ","
									+ p.getLimitDateTime() + "," + p.getDeliveryMode() + ","
									+ p.getLoc().getLocationName();
						} else if (type.equals("GPS")) {
							response = "Posts;:;" + p.getId() + "," + key.getUsername() + "," + p.getTitle() + ","
									+ p.getContent() + "," + p.getContact() + "," + p.getCreationDateTime() + ","
									+ p.getLimitDateTime() + "," + p.getDeliveryMode() + "," + p.getLocationName();
						}
						try {
							dataOutputStream = new DataOutputStream(s.getOutputStream());
							dataOutputStream.writeUTF(response);
							dataOutputStream.flush();
							System.out.println("BLACK: " + response);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

}
