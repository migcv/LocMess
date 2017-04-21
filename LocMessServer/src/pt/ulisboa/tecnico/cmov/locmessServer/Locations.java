package pt.ulisboa.tecnico.cmov.locmessServer;

public class Locations {

	private String type;
	private String locationName;
	private String ssID;
	private Double latitude;
	private Double longitude;
	
	public Locations(String type, String locationName, String coordinates) {
		this.type = type;
		this.locationName = locationName;
		String[] latlong = coordinates.split(", ");
		this.latitude = Double.parseDouble(latlong[0]);
		this.longitude = Double.parseDouble(latlong[1]);
	}
	
	public Locations(String type, String ssid) {
		this.type = type;
		this.ssID = ssid;
	}

	public String getType() {
		return type;
	}

	public String getLocationName() {
		return locationName;
	}

	public String getSSId() {
		return ssID;
	}

	public Double getLatitude() {
		return latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

}
