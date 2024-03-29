package pt.ulisboa.tecnico.cmov.locmessServer;

public class Posts {

	private String title;
	private String content;
	private String contact;
	private String creationDateTime;
	private String limitDateTime;
	private String deliveryMode;
	private String locationName;
	private Double latitude;
	private Double longitude;
	private Double radius;
	private Integer id;
	private String restrictionPolicy;
	private String restrictions;
	private Locations loc;
	private String flag;

	public Posts(String title, String content, String contact, String creationDateTime, String limitDateTime,
			String deliveryMode, Locations loc, String restrictionPolicy, String restrictions, int id, String flag) {
		this.title = title;
		this.content = content;
		this.contact = contact;
		this.creationDateTime = creationDateTime;
		this.limitDateTime = limitDateTime;
		this.deliveryMode = deliveryMode;
		this.loc = loc;
		this.restrictionPolicy = restrictionPolicy;
		this.restrictions = restrictions;
		this.id = id;
		this.flag = flag;
	}

	public Posts(String title, String content, String contact, String creationDateTime, String limitDateTime,
			String deliveryMode, Locations loc, String restrictionPolicy, int id, String flag) {
		this.title = title;
		this.content = content;
		this.contact = contact;
		this.creationDateTime = creationDateTime;
		this.limitDateTime = limitDateTime;
		this.deliveryMode = deliveryMode;
		this.loc = loc;
		this.restrictionPolicy = restrictionPolicy;
		this.id = id;
		this.flag = flag;
	}

	public Posts(String title, String content, String contact, String creationDateTime, String limitDateTime,
			String deliveryMode, String locationName, String coordinates, String radius, String restrictionPolicy,
			String restrictions, int id, String flag) {
		this.title = title;
		this.content = content;
		this.contact = contact;
		this.creationDateTime = creationDateTime;
		this.limitDateTime = limitDateTime;
		this.deliveryMode = deliveryMode;
		this.locationName = locationName;
		String[] latlong = coordinates.split(", ");
		this.latitude = Double.parseDouble(latlong[0]);
		this.longitude = Double.parseDouble(latlong[1]);
		this.radius = Double.parseDouble(radius);
		this.restrictionPolicy = restrictionPolicy;
		this.restrictions = restrictions;
		this.id = id;
		this.flag = flag;
	}

	public Posts(String title, String content, String contact, String creationDateTime, String limitDateTime,
			String deliveryMode, String locationName, String coordinates, String radius, String restrictionPolicy,
			int id, String flag) {
		this.title = title;
		this.content = content;
		this.contact = contact;
		this.creationDateTime = creationDateTime;
		this.limitDateTime = limitDateTime;
		this.deliveryMode = deliveryMode;
		this.locationName = locationName;
		String[] latlong = coordinates.split(", ");
		this.latitude = Double.parseDouble(latlong[0]);
		this.longitude = Double.parseDouble(latlong[1]);
		this.radius = Double.parseDouble(radius);
		this.restrictionPolicy = restrictionPolicy;
		this.id = id;
		this.flag = flag;
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

	public String getDeliveryMode() {
		return deliveryMode;
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

	public String getCreationDateTime() {
		return creationDateTime;
	}

	public String getLimitDateTime() {
		return limitDateTime;
	}

	public String getLocationName() {
		return locationName;
	}

	public Locations getLoc() {
		return loc;
	}

	public String isFlag() {
		return flag;
	}

}
