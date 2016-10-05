package com.hackgsu.fall2016.android;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.format.DateUtils;
import android.widget.Toast;
import com.hackgsu.fall2016.android.model.ScheduleEvent;
import net.danlew.android.joda.JodaTimeAndroid;
import org.joda.time.LocalDateTime;

import java.util.ArrayList;

/**
 * Created by Joshua King on 9/27/16.
 */
public class HackGSUApplication extends Application {
	@NonNull
	public static LocalDateTime getDateTimeOfHackathon () {
		return new LocalDateTime().withDate(2016, 10, 21).withMillisOfSecond(0).withSecondOfMinute(0).withHourOfDay(19);
	}

	public static LocalDateTime getDateTimeOfHackathon (int dayIndex, int hour, int minute) {
		return getDateTimeOfHackathon().plusDays(dayIndex).withHourOfDay(hour).withMinuteOfHour(minute);
	}

	public static String toHumanReadableRelative (LocalDateTime timestamp) {
		return (String) DateUtils.getRelativeTimeSpanString(timestamp.toDateTime()
																	 .getMillis(), System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE);
	}

	@Override
	public void onCreate () {
		super.onCreate();

		JodaTimeAndroid.init(this);

		// TODO: 9/27/16 : Parse, probably, a json file here
		ArrayList<ScheduleEvent> scheduleEvents      = new ArrayList<>();
		Runnable                 openWebsiteRunnable = getUrlRunnable("http://www.hackgsu.com/#schedule");
		scheduleEvents.add(new ScheduleEvent("Early Check-in Begins / Late Registration", "", getDateTimeOfHackathon(0, 17, 0), R.drawable.ic_clipboard)
								   .setAction(openWebsiteRunnable));
		scheduleEvents.add(new ScheduleEvent("Check-in", "", getDateTimeOfHackathon(0, 18, 0), R.drawable.ic_clipboard).setAction(openWebsiteRunnable));
		scheduleEvents.add(new ScheduleEvent("Dinner", "", getDateTimeOfHackathon(0, 18, 30), R.drawable.ic_food));
		scheduleEvents.add(new ScheduleEvent("Opening Ceremonies", "", getDateTimeOfHackathon(0, 19, 0), R.drawable.ic_speaker));
		scheduleEvents.add(new ScheduleEvent("Hacking Begins - Idea Mixer / Team Forming", "", getDateTimeOfHackathon(0, 20, 0), R.drawable.ic_laptop));
		scheduleEvents.add(new ScheduleEvent("Development Workshop A", "", getDateTimeOfHackathon(0, 20, 30), R.drawable.ic_speaker));
		scheduleEvents.add(new ScheduleEvent("Development Workshop B", "", getDateTimeOfHackathon(0, 21, 30), R.drawable.ic_speaker));
		scheduleEvents.add(new ScheduleEvent("Development Workshop C", "", getDateTimeOfHackathon(0, 22, 30), R.drawable.ic_speaker));
		scheduleEvents.add(new ScheduleEvent("Friday Midnight Madness", "", getDateTimeOfHackathon(1, 0, 0), R.drawable.ic_game));
		scheduleEvents.add(new ScheduleEvent("Breakfast", "", getDateTimeOfHackathon(1, 8, 0), R.drawable.ic_food));
		scheduleEvents.add(new ScheduleEvent("Development Workshop D", "", getDateTimeOfHackathon(1, 11, 0), R.drawable.ic_speaker));
		scheduleEvents.add(new ScheduleEvent("Lunch", "", getDateTimeOfHackathon(1, 13, 0), R.drawable.ic_food));
		scheduleEvents.add(new ScheduleEvent("TBD", "", getDateTimeOfHackathon(1, 13, 30), R.drawable.ic_schedule));
		scheduleEvents.add(new ScheduleEvent("Development Workshop E", "", getDateTimeOfHackathon(1, 14, 0), R.drawable.ic_speaker));
		scheduleEvents.add(new ScheduleEvent("TBD", "", getDateTimeOfHackathon(1, 15, 0), R.drawable.ic_schedule));
		scheduleEvents.add(new ScheduleEvent("Dinner", "", getDateTimeOfHackathon(1, 18, 0), R.drawable.ic_food));
		scheduleEvents.add(new ScheduleEvent("Snack", "", getDateTimeOfHackathon(1, 21, 0), R.drawable.ic_food));
		scheduleEvents.add(new ScheduleEvent("Saturday Midnight Madness", "", getDateTimeOfHackathon(2, 0, 0), R.drawable.ic_game));
		scheduleEvents.add(new ScheduleEvent("Breakfast", "", getDateTimeOfHackathon(2, 8, 0), R.drawable.ic_food));
		scheduleEvents.add(new ScheduleEvent("Hacking Ends - Submit to Devpost", "", getDateTimeOfHackathon(2, 9, 0), R.drawable.ic_laptop).setAction(getUrlRunnable("http://hackgsu-fall16.devpost.com/")));
		scheduleEvents.add(new ScheduleEvent("Hack expo", "", getDateTimeOfHackathon(2, 10, 0), R.drawable.ic_speaker));
		scheduleEvents.add(new ScheduleEvent("Finalist Demos", "", getDateTimeOfHackathon(2, 11, 30), R.drawable.ic_devices));
		scheduleEvents.add(new ScheduleEvent("Closing/Awards Ceremonies", "", getDateTimeOfHackathon(2, 12, 15), R.drawable.ic_speaker));

		DataStore.setScheduleEvents(scheduleEvents);

		String string = toHumanReadableRelative(getDateTimeOfHackathon());
		Toast.makeText(this, "Opening Ceremonies " + string.replaceFirst("In", "in"), Toast.LENGTH_LONG).show();
	}

	@NonNull
	private Runnable getUrlRunnable (final String url) {
		return new Runnable() {
			@Override
			public void run () {
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				startActivity(intent);
			}
		};
	}
}
