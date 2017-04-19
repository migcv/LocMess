package pt.ulisboa.tecnico.cmov.locmessServer;

import java.util.ArrayList;

public class Posts {

	private String title;
	private String content;
	private String contact;
	private String date;
	private String time;
	private String deliveryMode;
	private String coordinates;
	private String radius;
	private Integer id = 0;

	public Posts(String title, String content, String contact, String date, String time, String deliveryMode) {
		this.title = title;
		this.content = content;
		this.contact = contact;
		this.date = date;
		this.time = time;
		this.deliveryMode = deliveryMode;
		this.id++;
	}

	public Posts(String title, String content, String contact, String date, String time, String deliveryMode,
			String coordinates, String radius) {
		this.title = title;
		this.content = content;
		this.contact = contact;
		this.date = date;
		this.time = time;
		this.deliveryMode = deliveryMode;
		this.coordinates = coordinates;
		this.radius = radius;
		this.id++;
	}

	public Posts() {
	}

	public static void addPostsWIFI(String token, String title, String content, String contact, String date,
			String time, String deliveryMode) {

		Session ss = LocMess.getSession();
		User u = ss.getUserFromSession(token);
		Posts p = new Posts(title, content, contact, date, time, deliveryMode);
		if (LocMess.getPosts().containsKey(u))
			LocMess.getPosts().get(u).add(p);
		else {
			ArrayList<Posts> aux = new ArrayList<>();
			aux.add(p);
			LocMess.getPosts().put(u, aux);
		}

	}

	public static void addPostsGPS(String token, String title, String content, String contact, String date, String time,
			String deliveryMode, String coordinates, String radius) {

		Session ss = LocMess.getSession();
		User u = ss.getUserFromSession(token);
		Posts p = new Posts(title, content, contact, date, time, deliveryMode, coordinates, radius);
		if (LocMess.getPosts().containsKey(u))
			LocMess.getPosts().get(u).add(p);
		else {
			ArrayList<Posts> aux = new ArrayList<>();
			aux.add(p);
			LocMess.getPosts().put(u, aux);
		}
	}

	public String getTitle() {
		return title;
	}

	public String getContent() {
		return content;
	}

	public String getContact() {
		return contact;
	}

	public String getTime() {
		return time;
	}

	public String getDeliveryMode() {
		return deliveryMode;
	}

	public String getDate() {
		return date;
	}

	public String getRadius() {
		return radius;
	}

	public String getCoordinates() {
		return coordinates;
	}

	public Integer getId() {
		return id;
	}

}
