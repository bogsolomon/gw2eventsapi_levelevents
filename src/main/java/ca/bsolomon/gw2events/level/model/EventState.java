package ca.bsolomon.gw2events.level.model;

import java.util.List;

public class EventState {

	private final String outputText;
	private final int sequenceId;
	private final List<EventCondition> conditions;
	private final ConditionType conditionType;
	private final String waypoint;

	public EventState(String outputText, int sequenceId,
			List<EventCondition> conditions, ConditionType conditionType, String waypoint) {
		super();
		this.outputText = outputText;
		this.sequenceId = sequenceId;
		this.conditions = conditions;
		this.conditionType = conditionType;
		this.waypoint = waypoint;
	}

	public String getOutputText() {
		return outputText;
	}

	public int getSequenceId() {
		return sequenceId;
	}

	public List<EventCondition> getConditions() {
		return conditions;
	}

	public ConditionType getConditionType() {
		return conditionType;
	}

	public String getWaypoint() {
		return waypoint;
	}
}
