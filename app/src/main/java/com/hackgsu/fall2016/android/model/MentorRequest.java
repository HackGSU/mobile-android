package com.hackgsu.fall2016.android.model;

import org.joda.time.LocalDateTime;

/**
 * Created by Joshua King on 10/10/16.
 */
public class MentorRequest {
	private String               description;
	private Mentor               mentor;
	private MentorRequest.Status status;
	private long                 timestamp;
	private String               title;

	public MentorRequest (String title, String description, Status status) {
		this.title = title;
		this.description = description;
		this.status = status;

		setTimestampToNow();
	}

	public MentorRequest (String title, String description) {
		this(title, description, Status.Pending);
	}

	public enum Status {
		InProgress, Completed, Pending, Cancelled
	}

	public void setTimestampToNow () {
		timestamp = System.currentTimeMillis();
	}

	public void setMentor (Mentor mentor) {
		this.mentor = mentor;
	}

	public Mentor getMentor () {
		return mentor;
	}

	public LocalDateTime getTimestamp () {
		return new LocalDateTime(timestamp);
	}

	public String getDescription () {
		return description;
	}

	public Status getStatus () {
		return status;
	}

	public String getTitle () {
		return title;
	}

	public String getSubtitle () {
		String soon   = "soon";
		String prefix = "";
		if (getStatus().equals(Status.Completed)) { prefix = "Was"; }
		else if (getStatus().equals(Status.Pending)) { prefix = "Will be"; }
		else if (getStatus().equals(Status.InProgress)) {
			prefix = "Is being";
			soon = "";
		}
		else if (getStatus().equals(Status.Cancelled)) { return "Cancelled"; }
		String helpedBy = String.format(" helped %s", getMentor() == null ? soon : "by" + getMentor().getName());
		return prefix + helpedBy;
	}
}
