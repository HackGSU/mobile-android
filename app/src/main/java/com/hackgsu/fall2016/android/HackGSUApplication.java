package com.hackgsu.fall2016.android;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.hackgsu.fall2016.android.events.ScheduleUpdatedEvent;
import com.hackgsu.fall2016.android.model.ScheduleEvent;
import com.hackgsu.fall2016.android.utils.BusUtils;
import net.danlew.android.joda.JodaTimeAndroid;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by Joshua King on 9/27/16.
 */
public class HackGSUApplication extends Application {
	private FirebaseAuth                   firebaseAuth;
	private FirebaseAuth.AuthStateListener firebaseAuthListener;

	public static void delayRunnableOnUI (final long millsToDelay, final Runnable runnableToRun) {
		new Thread(new Runnable() {
			public void run () {
				try {
					Thread.sleep(millsToDelay);
				} catch (InterruptedException var2) {
					;
				}

				(new Handler(Looper.getMainLooper())).post(runnableToRun);
			}
		}).start();
	}

	public static LocalDateTime getDateTimeOfHackathon (int dayIndex, int hour, int minute) {
		return getDateTimeOfHackathon().plusDays(dayIndex).withHourOfDay(hour).withMinuteOfHour(minute);
	}

	@NonNull
	public static LocalDateTime getDateTimeOfHackathon () {
		return new LocalDateTime().withDate(2016, 10, 21).withMillisOfSecond(0).withSecondOfMinute(0).withHourOfDay(19);
	}

	public static DateTimeFormatter getTimeFormatter24OrNot (Context context) {
		return getTimeFormatter24OrNot(context, new DateTimeFormatterBuilder());
	}

	public static DateTimeFormatter getTimeFormatter24OrNot (Context context, DateTimeFormatterBuilder dateTimeFormatterBuilder) {
		if (DateFormat.is24HourFormat(context)) {
			dateTimeFormatterBuilder.appendHourOfDay(1).appendLiteral(":").appendMinuteOfHour(2);
		}
		else {
			dateTimeFormatterBuilder.appendHourOfHalfday(1).appendLiteral(":").appendMinuteOfHour(2).appendLiteral(" ").appendPattern("a");
		}
		return dateTimeFormatterBuilder.toFormatter();
	}

	@NonNull
	public static Runnable getUrlRunnable (final Context context, final String url) {
		return new Runnable() {
			@Override
			public void run () {
				openWebUrl(context, url);
			}
		};
	}

	public static void openWebUrl (Context context, String url) {
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		context.startActivity(intent);
	}

	public static String toHumanReadableRelative (LocalDateTime timestamp) {
		return (String) DateUtils.getRelativeTimeSpanString(timestamp.toDateTime()
																	 .getMillis(), System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE);
	}

	public static void toast (final Context context, final String string) {
		delayRunnableOnUI(0, new Runnable() {
			@Override
			public void run () {
				Toast.makeText(context, string, Toast.LENGTH_LONG).show();
			}
		});
	}

	@Override
	public void onCreate () {
		super.onCreate();

		JodaTimeAndroid.init(this);
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
		new Thread(new Runnable() {
			@Override
			public void run () {

				final SharedPreferences firebasePrefs = getSharedPreferences("firebasePrefs", MODE_PRIVATE);
				firebaseAuth = FirebaseAuth.getInstance();

				firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
					@Override
					public void onAuthStateChanged (@NonNull FirebaseAuth firebaseAuth) {
						FirebaseUser user = firebaseAuth.getCurrentUser();
						if (user != null) {
							Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

							refreshSchedule();
						}
						else {
							Log.d(TAG, "onAuthStateChanged:signed_out");
						}
					}
				};

				firebaseAuth.addAuthStateListener(firebaseAuthListener);

				if (!firebasePrefs.getBoolean("hasAuthed", false)) {
					firebaseAuth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
						@Override
						public void onComplete (@NonNull Task<AuthResult> task) {
							Log.d(TAG, "signInAnonymously:onComplete:" + task.isSuccessful());

							if (!task.isSuccessful()) {
								Log.w(TAG, "signInAnonymously", task.getException());
								Toast.makeText(HackGSUApplication.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
							}
							else { firebasePrefs.edit().putBoolean("hasAuthed", true).apply(); }
						}
					});
				}

				String string = toHumanReadableRelative(getDateTimeOfHackathon());
				string = "Opening Ceremonies " + string.replaceFirst("In", "in");
				toast(HackGSUApplication.this, string);
			}
		}).start();
	}

	private void refreshSchedule () {
		DatabaseReference              dbRef          = FirebaseDatabase.getInstance().getReference();
		final ArrayList<ScheduleEvent> scheduleEvents = new ArrayList<>();
		dbRef.child("schedule").orderByKey().getRef().addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange (DataSnapshot snapshot) {
				for (DataSnapshot child : snapshot.getChildren()) {
					scheduleEvents.add(child.getValue(ScheduleEvent.class));
				}

				BusUtils.post(new ScheduleUpdatedEvent(scheduleEvents));
			}

			@Override
			public void onCancelled (DatabaseError databaseError) {

			}
		});

		//		Runnable openWebsiteRunnable = getUrlRunnable("http://www.hackgsu.com/#schedule");
		//		scheduleEvents.add(new ScheduleEvent("Early Check-in Begins / Late Registration", "", getDateTimeOfHackathon(0, 17, 0), R.drawable.ic_clipboard)
		//								   .setAction(openWebsiteRunnable));
		//		scheduleEvents.add(new ScheduleEvent("Check-in", "", getDateTimeOfHackathon(0, 18, 0), R.drawable.ic_clipboard).setAction(openWebsiteRunnable));
		//		scheduleEvents.add(new ScheduleEvent("Dinner", "", getDateTimeOfHackathon(0, 18, 30), R.drawable.ic_food));
		//		scheduleEvents.add(new ScheduleEvent("Opening Ceremonies", "", getDateTimeOfHackathon(0, 19, 0), R.drawable.ic_speaker));
		//		scheduleEvents.add(new ScheduleEvent("Hacking Begins - Idea Mixer / Team Forming", "", getDateTimeOfHackathon(0, 20, 0), R.drawable.ic_laptop));
		//		scheduleEvents.add(new ScheduleEvent("Development Workshop A", "", getDateTimeOfHackathon(0, 20, 30), R.drawable.ic_speaker));
		//		scheduleEvents.add(new ScheduleEvent("Development Workshop B", "", getDateTimeOfHackathon(0, 21, 30), R.drawable.ic_speaker));
		//		scheduleEvents.add(new ScheduleEvent("Development Workshop C", "", getDateTimeOfHackathon(0, 22, 30), R.drawable.ic_speaker));
		//		scheduleEvents.add(new ScheduleEvent("Friday Midnight Madness", "", getDateTimeOfHackathon(1, 0, 0), R.drawable.ic_game));
		//		scheduleEvents.add(new ScheduleEvent("Breakfast", "", getDateTimeOfHackathon(1, 8, 0), R.drawable.ic_food));
		//		scheduleEvents.add(new ScheduleEvent("Development Workshop D", "", getDateTimeOfHackathon(1, 11, 0), R.drawable.ic_speaker));
		//		scheduleEvents.add(new ScheduleEvent("Lunch", "", getDateTimeOfHackathon(1, 13, 0), R.drawable.ic_food));
		//		scheduleEvents.add(new ScheduleEvent("TBD", "", getDateTimeOfHackathon(1, 13, 30), R.drawable.ic_schedule));
		//		scheduleEvents.add(new ScheduleEvent("Development Workshop E", "", getDateTimeOfHackathon(1, 14, 0), R.drawable.ic_speaker));
		//		scheduleEvents.add(new ScheduleEvent("TBD", "", getDateTimeOfHackathon(1, 15, 0), R.drawable.ic_schedule));
		//		scheduleEvents.add(new ScheduleEvent("Dinner", "", getDateTimeOfHackathon(1, 18, 0), R.drawable.ic_food));
		//		scheduleEvents.add(new ScheduleEvent("Snack", "", getDateTimeOfHackathon(1, 21, 0), R.drawable.ic_food));
		//		scheduleEvents.add(new ScheduleEvent("Saturday Midnight Madness", "", getDateTimeOfHackathon(2, 0, 0), R.drawable.ic_game));
		//		scheduleEvents.add(new ScheduleEvent("Breakfast", "", getDateTimeOfHackathon(2, 8, 0), R.drawable.ic_food));
		//		scheduleEvents.add(new ScheduleEvent("Hacking Ends - Submit to Devpost", "", getDateTimeOfHackathon(2, 9, 0), R.drawable.ic_laptop).setAction(getUrlRunnable("http://hackgsu-fall16.devpost.com/")));
		//		scheduleEvents.add(new ScheduleEvent("Hack expo", "", getDateTimeOfHackathon(2, 10, 0), R.drawable.ic_speaker));
		//		scheduleEvents.add(new ScheduleEvent("Finalist Demos", "", getDateTimeOfHackathon(2, 11, 30), R.drawable.ic_devices));
		//		scheduleEvents.add(new ScheduleEvent("Closing/Awards Ceremonies", "", getDateTimeOfHackathon(2, 12, 15), R.drawable.ic_speaker));
		//
		DataStore.setScheduleEvents(scheduleEvents);
	}
}
