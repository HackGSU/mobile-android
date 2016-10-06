package com.hackgsu.fall2016.android.model;

import android.support.annotation.DrawableRes;
import com.hackgsu.fall2016.android.HackGSUApplication;
import org.joda.time.LocalDateTime;

/**
 * Created by Joshua King on 9/27/16.
 */
public class ScheduleEvent {
	private      Runnable      action;
	private      boolean       bookmarked;
	private      String        description;
	private
	@DrawableRes int           icon;
	private      LocalDateTime timestamp;
	private      String        title;

	public ScheduleEvent (String title, String description, LocalDateTime timestamp) {
		this.title = title;
		this.description = description;
		this.timestamp = timestamp;
	}

	public ScheduleEvent (String title, String description, LocalDateTime timestamp, @DrawableRes int icon) {

		this.title = title;
		this.description = description;
		this.timestamp = timestamp;
		this.icon = icon;
	}

	public ScheduleEvent (String title, String description, LocalDateTime timestamp, boolean bookmarked) {

		this.title = title;
		this.description = description;
		this.timestamp = timestamp;
		this.bookmarked = bookmarked;
	}

	public ScheduleEvent (String title, String description, LocalDateTime timestamp, @DrawableRes int icon, boolean bookmarked) {

		this.title = title;
		this.description = description;
		this.timestamp = timestamp;
		this.icon = icon;
		this.bookmarked = bookmarked;
	}

	public
	@DrawableRes
	int getIcon () {
		return icon;
	}

	public boolean isBookmarked () {
		return bookmarked;
	}

	public String getDescription () {
		return description;
	}

	public LocalDateTime getTimestamp () {
		return timestamp;
	}

	public String getTitle () {
		return title;
	}

	public void setIsBookmarked (boolean isBookmarked) {
		this.bookmarked = isBookmarked;
	}

	public ScheduleEvent setAction (Runnable action) {
		this.action = action;
		return this;
	}

	public Runnable getAction () {
		return action;
	}

	public String getShareText () {
		// TODO: 10/6/16 : Maybe make this share message more better..? Also add Play Store link
		return String.format("Look at this event at hackGSU\n\nTitle: %s\nWhen: %s", getTitle(), HackGSUApplication.toHumanReadableRelative(getTimestamp()));
	}
}