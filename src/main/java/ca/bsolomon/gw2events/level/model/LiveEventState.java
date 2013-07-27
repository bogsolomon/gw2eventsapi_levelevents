package ca.bsolomon.gw2events.level.model;

import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;
import org.joda.time.chrono.GJChronology;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

public final class LiveEventState {

	private final String status;
	private final DateTime date;
	private DateTime updateDate;
	private final String event;
	private final String waypoint;
	private final int sequenceId;
	private int count = 0;
	
	private static DateTimeZone zone = DateTimeZone.forID("America/New_York");
	private static Chronology gregorianJuian = GJChronology.getInstance(zone);

	private static PeriodFormatter HHMMSSFormater = new PeriodFormatterBuilder().
			printZeroAlways().minimumPrintedDigits(2).
			appendHours().appendSeparator(":").
			appendMinutes().appendSeparator(":").
			appendSeconds().toFormatter();
	
	private int fHashCode;
	
	public LiveEventState(String status, DateTime date, String event, String waypoint, int sequenceId) {
		this.status = status;
		this.date = date;
		this.event = event;
		this.waypoint = waypoint;
		this.sequenceId = sequenceId;
	}
	
	public String getEvent() {
		return event;
	}
	public String getStatus() {
		return status;
	}
	public DateTime getDate() {
		return date;
	}
	public String getWaypoint() {
		return waypoint;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other == null) return false;
	    if (other == this) return true;
	    if (!(other instanceof LiveEventState))return false;
	    LiveEventState otherObject = (LiveEventState)other;
	    
	    if (this.event.equals(otherObject.getEvent())) {
	    	return true;
	    }
	    
	    return false;
	}
	@Override
	public int hashCode() {
		if (fHashCode == 0) {
			int result = HashCodeUtil.SEED;
			result = HashCodeUtil.hash(result, event);
			fHashCode = result;
		}
		
		return fHashCode;
	}

	public int getSequenceId() {
		return sequenceId;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public DateTime getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(DateTime updateDate) {
		this.updateDate = updateDate;
	}
	
	public String getDuration() {
		DateTime now = new DateTime(gregorianJuian);
		
		Period period = new Period(updateDate, now);
		
		return HHMMSSFormater.print(period);
	}
}
