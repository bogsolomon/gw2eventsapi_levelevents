package ca.bsolomon.gw2events.level.model;

import java.util.ArrayList;
import java.util.List;

import ca.bsolomon.gw2event.api.GW2EventsAPI;

public class Server {

	private final ServerID servId;
	private final List<LiveEventState> eventChains = new ArrayList<>();
	
	private int fHashCode;
	
	public Server(ServerID servId) {
		this.servId = servId;
	}

	public ServerID getServId() {
		return servId;
	}

	public List<LiveEventState> getEventChains() {
		return eventChains;
	}	
	
	@Override
	public boolean equals(Object other) {
		if (other == null) return false;
	    if (other == this) return true;
	    if (!(other instanceof Server))return false;
	    Server otherObject = (Server)other;
	    
	    if (otherObject.getServId() == servId)
	    	return true;
	    else 
	    	return false;
	}

	@Override
	public int hashCode() {
		if (fHashCode == 0) {
			int result = HashCodeUtil.SEED;
			result = HashCodeUtil.hash(result, servId);
			fHashCode = result;
		}
		
		return fHashCode;
	}
	
	public String getServerName() {
		return servId.getName();
	}
}
