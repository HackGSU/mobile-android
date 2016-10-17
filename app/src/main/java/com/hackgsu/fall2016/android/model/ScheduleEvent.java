package com.hackgsu.fall2016.android.model;

import android.content.Context;
import com.hackgsu.fall2016.android.HackGSUApplication;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by Joshua King on 9/27/16.
 */
public class ScheduleEvent implements Comparable<ScheduleEvent> {
	private String description;
	private long   timestamp;
	private String title;
	private String url;

	public ScheduleEvent () {
	}

	public ScheduleEvent (String title, String description, long timestamp) {
		this.title = title;
		this.description = description;
		this.timestamp = timestamp;
	}

	@Override
	public int compareTo (ScheduleEvent other) {
		return other == null || other.getTimestamp().isBefore(getTimestamp()) ? 1 : -1;
	}

	public String getDescription () {
		return description;
	}

	public LocalDateTime getTimestamp () {
		return new LocalDateTime(timestamp);
	}

	public String getTitle () {
		return title;
	}

	public String getUrl () {
		return url;
	}

	public String getShareText (Context context) {
		// TODO: 10/6/16 : Maybe make this share message more better..? Also add Play Store link
		DateTimeFormatter dateTimeFormatter = HackGSUApplication.getTimeFormatter24OrNot(context);
		String            time              = getTimestamp().toString(dateTimeFormatter);
		return String.format("Look at this event at hackGSU\n\nTitle: %s\nWhen: %s - %s", getTitle(), HackGSUApplication.toHumanReadableRelative(getTimestamp()), time);
	}
}