package com.hackgsu.fall2016.android.model;

import android.graphics.drawable.Drawable;
import org.joda.time.LocalDateTime;

/**
 * Created by Joshua King on 9/27/16.
 */
public class ScheduleEvent {
	private boolean       bookmarked;
	private String        description;
	private Drawable      icon;
	private String        subtitle;
	private LocalDateTime timestamp;
	private String        title;

	public ScheduleEvent (String title, String subtitle, String description, LocalDateTime timestamp) {
		this.title = title;
		this.subtitle = subtitle;
		this.description = description;
		this.timestamp = timestamp;
	}

	public ScheduleEvent (String title, String subtitle, String description, LocalDateTime timestamp, Drawable icon) {

		this.title = title;
		this.subtitle = subtitle;
		this.description = description;
		this.timestamp = timestamp;
		this.icon = icon;
	}

	public ScheduleEvent (String title, String subtitle, String description, LocalDateTime timestamp, boolean bookmarked) {

		this.title = title;
		this.subtitle = subtitle;
		this.description = description;
		this.timestamp = timestamp;
		this.bookmarked = bookmarked;
	}

	public ScheduleEvent (String title, String subtitle, String description, LocalDateTime timestamp, Drawable icon, boolean bookmarked) {

		this.title = title;
		this.subtitle = subtitle;
		this.description = description;
		this.timestamp = timestamp;
		this.icon = icon;
		this.bookmarked = bookmarked;
	}

	public Drawable getIcon () {
		return icon;
	}

	public boolean isBookmarked () {
		return bookmarked;
	}

	public String getDescription () {
		return description;
	}

	public String getSubtitle () {
		return subtitle;
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
}