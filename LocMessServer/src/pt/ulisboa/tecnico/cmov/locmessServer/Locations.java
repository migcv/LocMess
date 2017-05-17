package pt.ulisboa.tecnico.cmov.locmessServer;

import java.util.ArrayList;

public class Locations {

	private String type;
	private String locationName;
	private ArrayList<String> ssID;
	private Double latitude;
	private Double longitude;

	public Locations(String type, String locationName, String coordinates) {
		this.type = type;
		this.locationName = locationName;
		String[] latlong = coordinates.split(", ");
		this.latitude = Double.parseDouble(latlong[0]);
		this.longitude = Double.parseDouble(latlong[1]);
	}

	public Locations(String type, String locationName, ArrayList<String> ssid) {
		this.type = type;
		this.locationName = locationName;
		this.ssID = ssid;
	}

	public String getType() {
		return type;
	}

	public String getLocationName() {
		return locationName;
	}

	public ArrayList<String> getSSId() {
		return ssID;
	}

	public Double getLatitude() {
		return latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

}
