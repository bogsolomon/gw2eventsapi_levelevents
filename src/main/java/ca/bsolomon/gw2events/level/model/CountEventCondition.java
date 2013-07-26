package ca.bsolomon.gw2events.level.model;

public class CountEventCondition extends EventCondition {

	private final String maxCountValue;

	public CountEventCondition(String eventId, String state,
			String maxCountValue) {
		super(eventId, state);
		this.maxCountValue = maxCountValue;
	}

	public String getMaxCountValue() {
		return maxCountValue;
	}
}
