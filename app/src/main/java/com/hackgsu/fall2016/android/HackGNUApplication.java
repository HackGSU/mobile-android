package com.hackgsu.fall2016.android;

import android.app.Application;
import com.hackgsu.fall2016.android.model.ScheduleEvent;
import net.danlew.android.joda.JodaTimeAndroid;

import java.util.ArrayList;

/**
 * Created by Joshua King on 9/27/16.
 */
public class HackGNUApplication extends Application {
	@Override
	public void onCreate () {
		super.onCreate();

		JodaTimeAndroid.init(this);

		// TODO: 9/27/16 : Parse, probably, a json file here
		ArrayList<ScheduleEvent> scheduleEvents = new ArrayList<>();
		scheduleEvents.add(new ScheduleEvent());
		scheduleEvents.add(new ScheduleEvent());
		scheduleEvents.add(new ScheduleEvent());
		scheduleEvents.add(new ScheduleEvent());
		scheduleEvents.add(new ScheduleEvent());
		scheduleEvents.add(new ScheduleEvent());
		DataStore.setScheduleEvents(scheduleEvents);
	}
}
