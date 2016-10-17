package com.hackgsu.fall2016.android;

import android.content.Context;
import com.hackgsu.fall2016.android.controllers.AnnouncementController;
import com.hackgsu.fall2016.android.model.Announcement;
import com.hackgsu.fall2016.android.model.ScheduleEvent;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Joshua King on 9/27/16.
 */
public class DataStore {
	private static ArrayList<Announcement>  announcements  = new ArrayList<>();
	private static ArrayList<ScheduleEvent> scheduleEvents = new ArrayList<>();

	public static ArrayList<Announcement> getAnnouncements () {
		return announcements;
	}

	public static ArrayList<Announcement> getAnnouncements (boolean onlyReturnBookmarkedAnnouncements) {
		if (!onlyReturnBookmarkedAnnouncements) { return getAnnouncements(); }

		ArrayList<Announcement> filteredAnnouncements = new ArrayList<>();
		for (Announcement announcement : getAnnouncements()) { if (announcement.isBookmarkedByMe()) { filteredAnnouncements.add(announcement); } }
		Collections.sort(filteredAnnouncements);
		return filteredAnnouncements;
	}

	public static ArrayList<ScheduleEvent> getScheduleEvents () {
		return scheduleEvents;
	}

	public static void setAnnouncements (Context context, ArrayList<Announcement> announcements) {
		AnnouncementController.setIsBookmarkedByMeBasedOnPrefs(context, announcements);
		AnnouncementController.setIsLikedByMeBasedOnPrefs(context, announcements);
		DataStore.announcements = announcements;
	}

	public static void setScheduleEvents (ArrayList<ScheduleEvent> scheduleEvents) {
		DataStore.scheduleEvents = scheduleEvents;
	}
}