package com.hackgsu.fall2016.android;

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

	public static SharedPreferences getPrefs (Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
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

	public static boolean isNullOrEmpty (String s) {
		return s == null || s.equals("");
	}

	public static boolean isOneFalse (boolean... b) {
		for (boolean bool : b) {
			if (!bool) { return true; }
		}
		return false;
	}

	public static void openWebUrl (Context context, String url) {
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		context.startActivity(intent);
	}

	public static String toHumanReadableRelative (LocalDateTime timestamp) {
		return toHumanReadableRelative(timestamp, false);
	}

	public static String toHumanReadableRelative (LocalDateTime timestamp, boolean seconds) {
		return (String) DateUtils.getRelativeTimeSpanString(timestamp.toDateTime()
																	 .getMillis(), System.currentTimeMillis(), seconds ? DateUtils.SECOND_IN_MILLIS : DateUtils.MINUTE_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE);
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
		});
	}

	private void refreshSchedule () {
		DatabaseReference              dbRef          = FirebaseDatabase.getInstance().getReference();
		final ArrayList<ScheduleEvent> scheduleEvents = new ArrayList<>();
		dbRef.child("schedule").orderByKey().getRef().addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange (DataSnapshot snapshot) {
				scheduleEvents.clear();
				for (DataSnapshot child : snapshot.getChildren()) {
					scheduleEvents.add(child.getValue(ScheduleEvent.class));
				}

				BusUtils.post(new ScheduleUpdatedEvent(scheduleEvents));
			}

			@Override
			public void onCancelled (DatabaseError databaseError) {

			}
		});
		DataStore.setScheduleEvents(scheduleEvents);
	}
}
