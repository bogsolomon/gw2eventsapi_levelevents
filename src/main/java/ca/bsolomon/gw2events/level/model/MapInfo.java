package ca.bsolomon.gw2events.level.model;

public class MapInfo {

	private final String mapId;
	private final int lowLevelRange;
	private final int highLevelRange;
	
	private int fHashCode;
	
	public MapInfo(String mapId, int lowLevelRange, int highLevelRange) {
		this.mapId = mapId;
		this.lowLevelRange = lowLevelRange;
		this.highLevelRange = highLevelRange;
	}

	public String getMapId() {
		return mapId;
	}

	public int getLowLevelRange() {
		return lowLevelRange;
	}

	public int getHighLevelRange() {
		return highLevelRange;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other == null) return false;
	    if (other == this) return true;
	    if (!(other instanceof MapInfo))return false;
	    MapInfo otherObject = (MapInfo)other;
	    
	    if (this.mapId.equals(otherObject.getMapId())) {
	    	return true;
	    }
	    
	    return false;
	}
	@Override
	public int hashCode() {
		if (fHashCode == 0) {
			int result = HashCodeUtil.SEED;
			result = HashCodeUtil.hash(result, mapId);
			fHashCode = result;
		}
		
		return fHashCode;
	}
}
