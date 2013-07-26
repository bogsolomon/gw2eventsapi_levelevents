package ca.bsolomon.gw2events.level.model;

import java.util.List;

public class EventChain {

	private final String chainName;
	private final String mapId;
	private final List<EventState> eventStates;
	
	private int fHashCode;

	public EventChain(String chainName, String mapId,
			List<EventState> eventStates) {
		this.chainName = chainName;
		this.mapId = mapId;
		this.eventStates = eventStates;
	}

	public String getChainName() {
		return chainName;
	}

	public String getMapId() {
		return mapId;
	}

	public List<EventState> getEventStates() {
		return eventStates;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other == null) return false;
	    if (other == this) return true;
	    if (!(other instanceof EventChain))return false;
	    EventChain otherObject = (EventChain)other;
	    
	    if (this.chainName.equals(otherObject.getChainName())) {
	    	return true;
	    }
	    
	    return false;
	}
	@Override
	public int hashCode() {
		if (fHashCode == 0) {
			int result = HashCodeUtil.SEED;
			result = HashCodeUtil.hash(result, chainName);
			fHashCode = result;
		}
		
		return fHashCode;
	}
}
