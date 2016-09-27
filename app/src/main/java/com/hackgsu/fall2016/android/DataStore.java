package com.hackgsu.fall2016.android;

import com.hackgsu.fall2016.android.model.ScheduleEvent;

import java.util.ArrayList;

/**
 * Created by Joshua King on 9/27/16.
 */
public class DataStore {
	private static ArrayList<ScheduleEvent> scheduleEvents;

	public static ArrayList<ScheduleEvent> getScheduleEvents () {
		return scheduleEvents;
	}

	public static void setScheduleEvents (ArrayList<ScheduleEvent> scheduleEvents) {
		DataStore.scheduleEvents = scheduleEvents;
	}
}