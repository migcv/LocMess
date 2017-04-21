package pt.ulisboa.tecnico.cmov.locmessServer;

public class Locations {

	private String type;
	private String locationName;
	private String id;
	
	public Locations(String type, String locationName, String id) {
		this.type = type;
		this.locationName = locationName;
		this.id = id;
	}
	
	public Locations(String type, String id) {
		this.type = type;
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public String getLocationName() {
		return locationName;
	}

	public String getId() {
		return id;
	}
	
}
