package pt.ulisboa.tecnico.cmov.locmessServer;

public class Posts {

	private String title;
	private String content;
	private String contact;
	private String date;
	private String time;
	private String deliveryMode;
	private Double latitude;
	private Double longitude;
	private Double radius;
	private Integer id;
	private String restrictionPolicy;
	private String restrictions;

	public Posts(String title, String content, String contact, String date, String time, String deliveryMode,
			String restrictionPolicy, String restrictions, int id) {
		this.title = title;
		this.content = content;
		this.contact = contact;
		this.date = date;
		this.time = time;
		this.deliveryMode = deliveryMode;
		this.restrictionPolicy = restrictionPolicy;
		this.restrictions = restrictions;
		this.id = id;
	}

	public Posts(String title, String content, String contact, String date, String time, String deliveryMode,
			String restrictionPolicy, int id) {
		this.title = title;
		this.content = content;
		this.contact = contact;
		this.date = date;
		this.time = time;
		this.deliveryMode = deliveryMode;
		this.restrictionPolicy = restrictionPolicy;
		this.id = id;
	}

	public Posts(String title, String content, String contact, String date, String time, String deliveryMode,
			String coordinates, String radius, String restrictionPolicy, String restrictions, int id) {
		this.title = title;
		this.content = content;
		this.contact = contact;
		this.date = date;
		this.time = time;
		this.deliveryMode = deliveryMode;
		String[] latlong = coordinates.split(", ");
		this.latitude = Double.parseDouble(latlong[0]);
		this.longitude = Double.parseDouble(latlong[1]);
		this.radius = Double.parseDouble(radius);
		this.restrictionPolicy = restrictionPolicy;
		this.restrictions = restrictions;
		this.id = id;
	}

	public Posts(String title, String content, String contact, String date, String time, String deliveryMode,
			String coordinates, String radius, String restrictionPolicy, int id) {
		this.title = title;
		this.content = content;
		this.contact = contact;
		this.date = date;
		this.time = time;
		this.deliveryMode = deliveryMode;
		String[] latlong = coordinates.split(", ");
		this.latitude = Double.parseDouble(latlong[0]);
		this.longitude = Double.parseDouble(latlong[1]);
		this.radius = Double.parseDouble(radius);
		this.restrictionPolicy = restrictionPolicy;
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

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getRestrictionPolicy() {
		return restrictionPolicy;
	}

	public String getRestrictions() {
		return restrictions;
	}

	public Double getRadius() {
		return radius;
	}

	public Double getLatitude() {
		return latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

}
