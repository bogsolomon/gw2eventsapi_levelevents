package ca.bsolomon.gw2events.level.model;

public class EventCondition {

	private final String eventId;
	private final String state;

	public EventCondition(String eventId, String state) {
		super();
		this.eventId = eventId;
		this.state = state;
	}

	public String getEventId() {
		return eventId;
	}

	public String getState() {
		return state;
	}
}
