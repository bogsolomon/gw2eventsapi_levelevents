package ca.bsolomon.gw2events.level.model;

public class SequenceEventCondition extends EventCondition {

	private final int sequenceId;

	public SequenceEventCondition(String eventId, String state, int sequenceId) {
		super(eventId, state);
		this.sequenceId = sequenceId;
	}

	public int getSequenceId() {
		return sequenceId;
	}	
}
