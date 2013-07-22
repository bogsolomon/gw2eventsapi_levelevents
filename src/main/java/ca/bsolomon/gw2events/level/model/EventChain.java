package ca.bsolomon.gw2events.level.model;

import java.util.List;

public class EventChain {

	private final String chainName;
	private final String mapId;
	private final List<EventState> eventStates;

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
}
