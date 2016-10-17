package com.hackgsu.fall2016.android.model;

import android.content.Context;
import android.support.annotation.DrawableRes;
import com.google.firebase.database.Exclude;
import com.hackgsu.fall2016.android.HackGSUApplication;
import com.hackgsu.fall2016.android.R;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormatter;

import java.io.Serializable;

/**
 * Created by Joshua King on 10/15/16.
 */
public class Announcement implements Serializable, Comparable<Announcement> {
	private String  bodyText;
	private boolean bookmarkedByMe;
	private String  firebaseKey;
	private boolean likedByMe;
	private int     likes;
	private long    timestamp;
	private String  title;
	private Topic   topic;

	public enum Topic {
		GENERAL(R.drawable.ic_announcements), TECH(R.drawable.ic_devices), FOOD(R.drawable.ic_food);
		private
		@DrawableRes int icon;

		Topic (int icon) {
			this.icon = icon;
		}

		public
		@DrawableRes
		int getIcon () {
			return icon;
		}
	}

	@Override
	public int compareTo (Announcement other) {
		return other == null || other.getTimestampDateTime().isBefore(getTimestampDateTime()) ? 1 : -1;
	}

	@Exclude
	public void setFirebaseKey (String firebaseKey) {
		this.firebaseKey = firebaseKey;
	}

	@Exclude
	public String getFirebaseKey () {
		return firebaseKey;
	}

	public String getTitle () {
		return title;
	}

	public void setTitle (String title) {
		this.title = title;
	}

	public int getLikes () {
		return likes;
	}

	public void setLikes (int likes) {
		this.likes = likes;
	}

	public String getBodyText () {
		return bodyText;
	}

	public void setBodyText (String bodyText) {
		this.bodyText = bodyText;
	}

	public long getTimestamp () {
		return timestamp;
	}

	@Exclude
	public LocalDateTime getTimestampDateTime () {
		return new LocalDateTime(getTimestamp());
	}

	public void setTimestamp (long timestamp) {
		this.timestamp = timestamp;
	}

	public String getTopic () {
		return topic == null ? null : topic.name();
	}

	public void setTopic (String topic) {
		try {
			this.topic = Topic.valueOf(topic);
		} catch (IllegalArgumentException ignored) {}
	}

	@Exclude
	public void setTopicEnum (Topic topic) {
		this.topic = topic;
	}

	@Exclude
	public Topic getTopicEnum () {
		return topic;
	}

	@Exclude
	public String getShareText (Context context) {
		// TODO: 10/6/16 : Maybe make this share message more better..? Also add Play Store link
		DateTimeFormatter dateTimeFormatter = HackGSUApplication.getTimeFormatter24OrNot(context);
		String            time              = getTimestampDateTime().toString(dateTimeFormatter);
		return String.format("Look at this announcement at hackGSU\n\ntitle: %s\nWhen: %s - %s\nMessage: %s", getTitle(), HackGSUApplication.toHumanReadableRelative(getTimestampDateTime()), time, getBodyText());
	}

	@Exclude
	public void toggleLikedByMe () {
		if (isLikedByMe()) { unlike(); }
		else { like(); }
	}

	@Exclude
	public boolean isLikedByMe () {
		return likedByMe;
	}

	@Exclude
	public boolean isBookmarkedByMe () {
		return bookmarkedByMe;
	}

	@Exclude
	public void setBookmarkedByMe (boolean bookmarkedByMe) {
		this.bookmarkedByMe = bookmarkedByMe;
	}

	@Exclude
	public void setLikedByMe (boolean likedByMe) {
		this.likedByMe = likedByMe;
	}

	@Exclude
	private void like () {
		if (!isLikedByMe()) { likes++; }
		setLikedByMe(true);
	}

	@Exclude
	private void unlike () {
		if (isLikedByMe()) { likes--; }
		setLikedByMe(false);
	}
}
