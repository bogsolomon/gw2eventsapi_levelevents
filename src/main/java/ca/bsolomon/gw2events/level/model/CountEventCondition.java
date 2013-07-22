package ca.bsolomon.gw2events.level.model;

public class CountEventCondition extends EventCondition {

	private final int coundId;
	private final String maxCountValue;

	public CountEventCondition(String eventId, String state, int coundId,
			String maxCountValue) {
		super(eventId, state);
		this.coundId = coundId;
		this.maxCountValue = maxCountValue;
	}

	public int getCoundId() {
		return coundId;
	}

	public String getMaxCountValue() {
		return maxCountValue;
	}
}
