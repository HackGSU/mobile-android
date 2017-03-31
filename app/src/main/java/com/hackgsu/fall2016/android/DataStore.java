package com.hackgsu.fall2016.android;

import android.content.Context;
import com.hackgsu.fall2016.android.controllers.AnnouncementController;
import com.hackgsu.fall2016.android.model.AboutPerson;
import com.hackgsu.fall2016.android.model.Announcement;
import com.hackgsu.fall2016.android.model.ScheduleEvent;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Joshua King on 9/27/16.
 */
public class DataStore {
	private static ArrayList<AboutPerson> aboutPeople;
	private static ArrayList<Announcement> announcements = new ArrayList<>();
	private static String openingCeremoniesRoomNumber;
	private static ArrayList<ScheduleEvent> scheduleEvents = new ArrayList<>();

	static {
		aboutPeople = new ArrayList<>();

		aboutPeople.add(new AboutPerson("Alex Mitchell", "iOS Developer", "https://goo.gl/cHty7J", R.drawable.alex_pic));
		aboutPeople.add(new AboutPerson("Viraj Shah", "iOS Developer", "https://goo.gl/v5YZaL", R.drawable.viraj_pic));
		aboutPeople.add(new AboutPerson("Harsha Goli", "iOS Developer", "https://goo.gl/UCVv11", R.drawable.harsha_pic));
		aboutPeople.add(new AboutPerson("Dylan Welch", "iOS Developer", "https://goo.gl/8RDDyo", R.drawable.dylan_pic));
		aboutPeople.add(new AboutPerson("Josh King", "Android Developer", "https://goo.gl/Izv7vk", R.drawable.josh_pic));
		aboutPeople.add(new AboutPerson("Pranathi Venigandla", "Android Developer", "https://goo.gl/S8KP2A", R.drawable.pranathi_pic));
		aboutPeople.add(new AboutPerson("Solomon Arnett", "Web Developer", "https://goo.gl/QbIimx", R.drawable.solo_pic));
		aboutPeople.add(new AboutPerson("Sri Rajasekaran", "Web Developer", "https://goo.gl/7GZoGB", R.drawable.sri_pic));
		aboutPeople.add(new AboutPerson("Abhinav Reddy", "Web Developer", "https://goo.gl/PSCYbR", R.drawable.abhinav_pic));
	}

	public static ArrayList<AboutPerson> getAboutPeople () {
		return aboutPeople;
	}

	public static ArrayList<Announcement> getAnnouncements (boolean onlyReturnBookmarkedAnnouncements) {
		if (!onlyReturnBookmarkedAnnouncements) { return getAnnouncements(); }

		ArrayList<Announcement> filteredAnnouncements = new ArrayList<>();
		for (Announcement announcement : getAnnouncements()) { if (announcement.isBookmarkedByMe()) { filteredAnnouncements.add(announcement); } }
		Collections.sort(filteredAnnouncements);
		return filteredAnnouncements;
	}

	public static ArrayList<Announcement> getAnnouncements () {
		return announcements;
	}

	public static String getOpeningCeremoniesRoomNumber () {
		return openingCeremoniesRoomNumber;
	}

	public static ArrayList<ScheduleEvent> getScheduleEvents () {
		return scheduleEvents;
	}

	public static void setAnnouncements (Context context, ArrayList<Announcement> announcements) {
		Collections.sort(announcements);
		AnnouncementController.setIsBookmarkedByMeBasedOnPrefs(context, announcements);
		AnnouncementController.setIsLikedByMeBasedOnPrefs(context, announcements);
		DataStore.announcements = announcements;
	}

	public static void setOpeningCeremoniesRoomNumber (String openingCeremoniesRoomNumber) {
		DataStore.openingCeremoniesRoomNumber = openingCeremoniesRoomNumber;
	}

	public static void setScheduleEvents (ArrayList<ScheduleEvent> scheduleEvents) {
		DataStore.scheduleEvents = scheduleEvents;
	}
}