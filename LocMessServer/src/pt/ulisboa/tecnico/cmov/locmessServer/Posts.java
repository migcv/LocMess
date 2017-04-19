package pt.ulisboa.tecnico.cmov.locmessServer;

public class Posts {

	private String title;
	private String content;
	private String contact;
	private String date;
	private String time;
	private String deliveryMode;
	private String coordinates;
	private String radius;
	private Integer id;

	public Posts(String title, String content, String contact, String date, String time, String deliveryMode, int id) {
		this.title = title;
		this.content = content;
		this.contact = contact;
		this.date = date;
		this.time = time;
		this.deliveryMode = deliveryMode;
		this.id = id;
	}

	public Posts(String title, String content, String contact, String date, String time, String deliveryMode,
			String coordinates, String radius, int id) {
		this.title = title;
		this.content = content;
		this.contact = contact;
		this.date = date;
		this.time = time;
		this.deliveryMode = deliveryMode;
		this.coordinates = coordinates;
		this.radius = radius;
		this.id = id;
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

	public void setId(Integer id) {
		this.id = id;
	}

}
