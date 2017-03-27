package com.hackgsu.fall2016.android;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.hackgsu.fall2016.android.activities.FullscreenWebViewActivity;
import com.hackgsu.fall2016.android.controllers.AnnouncementController;
import com.hackgsu.fall2016.android.events.AnnouncementsUpdatedEvent;
import com.hackgsu.fall2016.android.events.ScheduleUpdatedEvent;
import com.hackgsu.fall2016.android.model.Announcement;
import com.hackgsu.fall2016.android.model.ScheduleEvent;
import com.hackgsu.fall2016.android.services.FirebaseService;
import com.hackgsu.fall2016.android.utils.BusUtils;
import net.danlew.android.joda.JodaTimeAndroid;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Joshua King on 9/27/16.
 */
public class HackGSUApplication extends Application {
	public static final String IS_IN_DEV_MODE        = "is_in_dev_mode";
	public static final String NOTIFICATIONS_ENABLED = "notifications_enabled";
	private FirebaseAuth                   firebaseAuth;
	private FirebaseAuth.AuthStateListener firebaseAuthListener;

	public static boolean areNotificationsEnabled (Context context) {
		return getPrefs(context).getBoolean(NOTIFICATIONS_ENABLED, true);
	}

	@NonNull
	private static Announcement convertDataSnapshotToAnnouncement (DataSnapshot child) {
		Announcement announcement = child.getValue(Announcement.class);
		announcement.setFirebaseKey(child.getKey());
		return announcement;
	}

	public static float convertDpToPx (float dp, Context context) {
		return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
	}

	public static void delayRunnableOnUI (final long millsToDelay, final Runnable runnableToRun) {
		new Thread(new Runnable() {
			public void run () {
				try {
					Thread.sleep(millsToDelay);
				} catch (InterruptedException ignored) {}

				(new Handler(Looper.getMainLooper())).post(runnableToRun);
			}
		}).start();
	}

	public static LocalDateTime getDateTimeOfHackathon (int dayIndex, int hour, int minute) {
		return getDateTimeOfHackathon().plusDays(dayIndex).withHourOfDay(hour).withMinuteOfHour(minute);
	}

	@NonNull
	public static LocalDateTime getDateTimeOfHackathon () {
		return new LocalDateTime().withDate(2017, 3, 31).withMillisOfSecond(0).withSecondOfMinute(0).withMinuteOfHour(0).withHourOfDay(19);
	}

	public static SharedPreferences getPrefs (Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
	}

	public static DateTimeFormatter getTimeFormatter24OrNot (Context context) {
		return getTimeFormatter24OrNot(context, new DateTimeFormatterBuilder());
	}

	public static DateTimeFormatter getTimeFormatter24OrNot (Context context, DateTimeFormatterBuilder dateTimeFormatterBuilder) {
		if (DateFormat.is24HourFormat(context)) {
			dateTimeFormatterBuilder.appendHourOfDay(2).appendLiteral(":").appendMinuteOfHour(2);
		}
		else {
			dateTimeFormatterBuilder.appendHourOfHalfday(1).appendLiteral(":").appendMinuteOfHour(2).appendLiteral(" ").appendPattern("a");
		}
		return dateTimeFormatterBuilder.toFormatter();
	}

	@NonNull
	public static Runnable getUrlRunnable (final Context context, final String url, final boolean openInApp) {
		return new Runnable() {
			@Override
			public void run () {
				openWebUrl(context, url, openInApp);
			}
		};
	}

	public static void hideKeyboard (View parentViewToGetFocus, Context context) {
		View view = parentViewToGetFocus.findFocus();
		if (view != null) {
			InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	public static boolean isInDevMode (Context context) { return getPrefs(context).getBoolean(IS_IN_DEV_MODE, false); }

	public static boolean isNullOrEmpty (String s) {
		return s == null || s.equals("");
	}

	public static boolean isOneFalse (boolean... b) {
		for (boolean bool : b) {
			if (!bool) { return true; }
		}
		return false;
	}

	public static void openWebUrl (Context context, String url, boolean withinApp) {
		if (withinApp) {
			Intent codeOfConductViewIntent = new Intent(context, FullscreenWebViewActivity.class);
			codeOfConductViewIntent.putExtra(FullscreenWebViewActivity.EXTRA_URL, url);
			codeOfConductViewIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(codeOfConductViewIntent);
		}
		else {
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			context.startActivity(intent);
		}
	}

	public static void parseDataSnapshotForAnnouncements (Context context, DataSnapshot snapshot) {
		ArrayList<Announcement> announcements = new ArrayList<>();
		for (DataSnapshot child : snapshot.getChildren()) {
			Announcement announcement = convertDataSnapshotToAnnouncement(child);
			announcements.add(announcement);
		}

		Collections.sort(announcements);
		DataStore.setAnnouncements(context, announcements);
		BusUtils.post(new AnnouncementsUpdatedEvent(announcements));
	}

	public static void refreshAnnouncements (final Context context) {
		DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
		dbRef.child(AnnouncementController.getAnnouncementsTableString(context)).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange (DataSnapshot snapshot) {
				parseDataSnapshotForAnnouncements(context, snapshot);
			}

			@Override
			public void onCancelled (DatabaseError databaseError) { }
		});
	}

	public static void refreshSchedule () {
		DatabaseReference              dbRef          = FirebaseDatabase.getInstance().getReference();
		final ArrayList<ScheduleEvent> scheduleEvents = new ArrayList<>();
		dbRef.child("schedule").orderByKey().getRef().addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange (DataSnapshot snapshot) {
				scheduleEvents.clear();
				for (DataSnapshot child : snapshot.getChildren()) {
					scheduleEvents.add(child.getValue(ScheduleEvent.class));
				}

				Collections.sort(scheduleEvents);
				BusUtils.post(new ScheduleUpdatedEvent(scheduleEvents));
			}

			@Override
			public void onCancelled (DatabaseError databaseError) { }
		});
		DataStore.setScheduleEvents(scheduleEvents);
	}

	public static void runOnUI (Runnable runnable) {
		HackGSUApplication.delayRunnableOnUI(0, runnable);
	}

	public static void setIsInDevMode (final Activity context, boolean isInDevMode) {
		getPrefs(context).edit().putBoolean(IS_IN_DEV_MODE, isInDevMode).apply();

		HackGSUApplication.toast(context, String.format("You're in %s mode! :D", isInDevMode ? "Dev" : "Prod"));
		HackGSUApplication.delayRunnableOnUI(100, new Runnable() {
			@Override
			public void run () {
				context.stopService(new Intent(context, FirebaseService.class));
				//				context.finishAffinity();
			}
		});
	}

	public static void setNotificationsEnabled (Context context, boolean newValue) {
		getPrefs(context).edit().putBoolean(NOTIFICATIONS_ENABLED, newValue).apply();
	}

	public static void showKeyboard (View parentViewToGetFocus, Context context) {
		View view = parentViewToGetFocus.findFocus();
		if (view != null) {
			InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
			imm.showSoftInput(view, 0);
		}
	}

	public static String toHumanReadableRelative (LocalDateTime timestamp) {
		return toHumanReadableRelative(timestamp, false, true);
	}

	public static String toHumanReadableRelative (LocalDateTime timestamp, boolean seconds, boolean abreviate) {
		int  flags        = abreviate ? DateUtils.FORMAT_ABBREV_RELATIVE : 0;
		long secondsFlags = seconds ? DateUtils.SECOND_IN_MILLIS : DateUtils.MINUTE_IN_MILLIS;
		return (String) DateUtils.getRelativeTimeSpanString(timestamp.toDateTime().getMillis(), System.currentTimeMillis(), secondsFlags, flags);
	}

	public static void toast (final Context context, final String string) {
		runOnUI(new Runnable() {
			@Override
			public void run () {
				Toast.makeText(context, string, Toast.LENGTH_LONG).show();
			}
		});
	}

	@Override
	public void onCreate () {
		super.onCreate();

		connectToFirebaseAndDownloadData();
	}

	@Override
	public void onTerminate () {
		super.onTerminate();

		if (firebaseAuthListener != null) {
			firebaseAuth.removeAuthStateListener(firebaseAuthListener);
		}
	}

	private void connectToFirebaseAndDownloadData () {
		AsyncTask.execute(new Runnable() {
			@Override
			public void run () {
				JodaTimeAndroid.init(HackGSUApplication.this);

				startService(new Intent(HackGSUApplication.this, FirebaseService.class));
			}
		});
	}
}
