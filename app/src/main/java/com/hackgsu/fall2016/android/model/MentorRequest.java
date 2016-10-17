package com.hackgsu.fall2016.android.model;

import com.google.firebase.database.Exclude;
import com.hackgsu.fall2016.android.controllers.MentorsController;
import org.joda.time.LocalDateTime;

import java.io.Serializable;

/**
 * Created by Joshua King on 10/10/16.
 */
public class MentorRequest implements Serializable {
	private String               category;
	private String               description;
	private String               firebaseKey;
	private String               floor;
	private String               location;
	private String               mentor;
	private String               slackUsername;
	private MentorRequest.Status status;
	private String               teamName;
	private long                 timestamp;
	private String               title;

	public MentorRequest () { }

	public MentorRequest (String title,
						  String teamName,
						  String floor,
						  String location, String category,
						  String slackUsername,
						  String description,
						  Status status) {
		this.title = title;
		this.teamName = teamName;
		this.floor = floor;
		this.location = location;
		this.category = category;
		this.slackUsername = slackUsername;
		this.description = description;
		this.status = status;

		setTimestampToNow();
	}

	public MentorRequest (String title, String teamName, String floor, String location, String category, String slackUsername, String description) {
		this(title, teamName, floor, location, category, slackUsername, description, Status.Pending);
	}

	public enum Status {
		Completed, Pending, Cancelled
	}

	@Exclude
	public Status getStatusEnum () {
		return status;
	}

	public String getStatus () {
		if (status == null) { return null; }
		else { return status.name(); }
	}

	public void setStatus (String statusString) {
		if (statusString == null) { this.status = null; }
		else { this.status = Status.valueOf(statusString); }
	}

	public void setTimestampToNow () {
		timestamp = System.currentTimeMillis();
	}

	public void setMentor (String mentor) {
		this.mentor = mentor;
	}

	public String getDescription () {
		return description;
	}

	public String getFloor () {
		return floor;
	}

	public String getLocation () {
		return location;
	}

	public void setDescription (String description) {
		this.description = description;
	}

	public void setFloor (String floor) {
		this.floor = floor;
	}

	public void setLocation (String location) {
		this.location = location;
	}

	public void setCategory (String category) {
		this.category = category;
	}

	public void setSlackUsername (String slackUsername) {
		this.slackUsername = slackUsername;
	}

	public void setTeamName (String teamName) {
		this.teamName = teamName;
	}

	public void setTimestamp (long timestamp) {
		this.timestamp = timestamp;
	}

	public void setTitle (String title) {
		this.title = title;
	}

	public String getMentor () {
		return mentor;
	}

	public String getCategory () {
		return category;
	}

	public String getSlackUsername () {
		return slackUsername;
	}

	public String getTeamName () {
		return teamName;
	}

	public long getTimestamp () {
		return timestamp;
	}

	@Exclude
	public LocalDateTime getTimestampDateTime () {
		return new LocalDateTime(getTimestamp());
	}

	public String getTitle () {
		return title;
	}

	@Exclude
	public String getSubtitle () {
		String soon   = "soon";
		String prefix = "";
		if (getStatusEnum().equals(Status.Completed)) {
			prefix = "Was";
			soon = "";
		}
		else if (getStatusEnum().equals(Status.Pending)) { prefix = "Will be"; }
		else if (getStatusEnum().equals(Status.Cancelled)) { return "Cancelled"; }
		String helpedBy = String.format(" helped %s", getMentor() == null ? soon : "by " + MentorsController.getMentor(getMentor()).getName());

		return prefix + helpedBy;
	}

	@Exclude
	public void setFirebaseKey (String firebaseKey) {
		this.firebaseKey = firebaseKey;
	}

	@Exclude
	public String getFirebaseKey () {
		return firebaseKey;
	}

	@Exclude
	public void setStatusEnum (Status status) {
		this.status = status;
	}
}
