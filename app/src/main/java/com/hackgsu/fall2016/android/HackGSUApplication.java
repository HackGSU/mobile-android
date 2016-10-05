package com.hackgsu.fall2016.android;

import android.app.Application;
import com.hackgsu.fall2016.android.model.ScheduleEvent;
import net.danlew.android.joda.JodaTimeAndroid;
import org.joda.time.LocalDateTime;

import java.util.ArrayList;

/**
 * Created by Joshua King on 9/27/16.
 */
public class HackGSUApplication extends Application {
	@Override
	public void onCreate () {
		super.onCreate();

		JodaTimeAndroid.init(this);

		// TODO: 9/27/16 : Parse, probably, a json file here
		ArrayList<ScheduleEvent> scheduleEvents = new ArrayList<>();
		scheduleEvents.add(new ScheduleEvent("Schedule Event", "Subtitle", "Description", new LocalDateTime().minusMinutes(10), true));
		scheduleEvents.add(new ScheduleEvent("Schedule Event", "Subtitle", "Description", new LocalDateTime().plusMinutes(30), false));
		scheduleEvents.add(new ScheduleEvent("Schedule Event", "Subtitle", "Description", new LocalDateTime().plusMinutes(40), true));
		scheduleEvents.add(new ScheduleEvent("Schedule Event", "Subtitle", "Description", new LocalDateTime().plusMinutes(50), false));
		scheduleEvents.add(new ScheduleEvent("Schedule Event", "Subtitle", "Description", new LocalDateTime().plusMinutes(60), true));
		scheduleEvents.add(new ScheduleEvent("Schedule Event", "Subtitle", "Description", new LocalDateTime().plusMinutes(70), false));
		scheduleEvents.add(new ScheduleEvent("Schedule Event", "Subtitle", "Description", new LocalDateTime().plusMinutes(80), true));
		scheduleEvents.add(new ScheduleEvent("Schedule Event", "Subtitle", "Description", new LocalDateTime().plusMinutes(90), false));
		scheduleEvents.add(new ScheduleEvent("Schedule Event", "Subtitle", "Description", new LocalDateTime().plusMinutes(100), true));
		scheduleEvents.add(new ScheduleEvent("Schedule Event", "Subtitle", "Description", new LocalDateTime().plusMinutes(110), false));
		scheduleEvents.add(new ScheduleEvent("Schedule Event", "Subtitle", "Description", new LocalDateTime().plusMinutes(120), true));
		scheduleEvents.add(new ScheduleEvent("Schedule Event", "Subtitle", "Description", new LocalDateTime().plusMinutes(130), false));
		scheduleEvents.add(new ScheduleEvent("Schedule Event", "Subtitle", "Description", new LocalDateTime().plusMinutes(140), true));
		scheduleEvents.add(new ScheduleEvent("Schedule Event", "Subtitle", "Description", new LocalDateTime().plusMinutes(150), false));
		scheduleEvents.add(new ScheduleEvent("Schedule Event", "Subtitle", "Description", new LocalDateTime().plusMinutes(160), true));
		DataStore.setScheduleEvents(scheduleEvents);
	}
}
