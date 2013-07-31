package ca.bsolomon.gw2events.level.model;

import java.util.ArrayList;
import java.util.List;

public class MapEvents implements Comparable<MapEvents>{

	private final String mapName;
	private final List<LiveEventState> events = new ArrayList<>();
	
	private int fHashCode;
	
	public MapEvents(String mapName) {
		super();
		this.mapName = mapName;
	}

	public String getMapName() {
		return mapName;
	}

	public List<LiveEventState> getEvents() {
		return events;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other == null) return false;
	    if (other == this) return true;
	    if (!(other instanceof MapEvents))return false;
	    MapEvents otherObject = (MapEvents)other;
	    
	    if (this.mapName.equals(otherObject.getMapName())) {
	    	return true;
	    }
	    
	    return false;
	}
	@Override
	public int hashCode() {
		if (fHashCode == 0) {
			int result = HashCodeUtil.SEED;
			result = HashCodeUtil.hash(result, mapName);
			fHashCode = result;
		}
		
		return fHashCode;
	}

	@Override
	public int compareTo(MapEvents other) {
		int result = mapName.compareTo(other.getMapName());
		
		return result;
	}
}
