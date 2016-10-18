package com.hackgsu.fall2016.android.controllers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hackgsu.fall2016.android.HackGSUApplication;
import com.hackgsu.fall2016.android.model.Announcement;
import com.hackgsu.fall2016.android.utils.CallbackWithType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Joshua King on 10/15/16.
 */
public class AnnouncementController {
	public static final String ANNOUNCEMENTS_BOOKMARKED_KEY = "announcements_bookmarked";
	public static final String ANNOUNCEMENTS_LIKED_KEY      = "announcements_liked";
	public static final String ANNOUNCEMENTS_NOTIFIED_KEY   = "announcements_notified";
	public static final String ANNOUNCEMENTS_TABLE          = "announcements";

	public static void sendOrUpdateAnnouncement (Announcement announcement, final CallbackWithType<Void> callback) {
		FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
		if (currentUser != null) {
			DatabaseReference dbRef           = FirebaseDatabase.getInstance().getReference();
			DatabaseReference announcementRef = dbRef.child(ANNOUNCEMENTS_TABLE).getRef();
			DatabaseReference announcementRefWithId;
			if (announcement.getFirebaseKey() == null || announcement.getFirebaseKey().equals("")) {
				announcementRefWithId = announcementRef.push();
			}
			else { announcementRefWithId = announcementRef.child(announcement.getFirebaseKey()); }
			announcementRefWithId.setValue(announcement, new DatabaseReference.CompletionListener() {
				@SuppressLint ("CommitPrefEdits")
				@Override
				public void onComplete (DatabaseError databaseError, DatabaseReference databaseReference) {
					if (callback != null) {
						if (databaseError == null) { callback.onComplete(null); }
						else { callback.onError(); }
					}
				}
			});
		}
	}

	public static void setIsBookmarkedByMeBasedOnPrefs (Context context, ArrayList<Announcement> announcements) {
		if (context != null && announcements != null) {
			SharedPreferences prefs                     = HackGSUApplication.getPrefs(context);
			Set<String>       bookmarkedAnnouncementIds = prefs.getStringSet(ANNOUNCEMENTS_BOOKMARKED_KEY, new HashSet<String>());

			for (Announcement announcement : announcements) {
				announcement.setBookmarkedByMe(bookmarkedAnnouncementIds.contains(announcement.getFirebaseKey()));
			}
		}
	}

	public static void setIsLikedByMeBasedOnPrefs (Context context, ArrayList<Announcement> announcements) {
		if (context != null && announcements != null) {
			SharedPreferences prefs                     = HackGSUApplication.getPrefs(context);
			Set<String>       bookmarkedAnnouncementIds = prefs.getStringSet(ANNOUNCEMENTS_LIKED_KEY, new HashSet<String>());

			for (Announcement announcement : announcements) {
				announcement.setLikedByMe(bookmarkedAnnouncementIds.contains(announcement.getFirebaseKey()));
			}
		}
	}

	@SuppressLint ("CommitPrefEdits")
	public static boolean shouldNotify (Context context, Announcement announcement) {
		boolean returnValue = true;
		if (context != null && announcement != null && !HackGSUApplication.isNullOrEmpty(announcement.getFirebaseKey())) {
			SharedPreferences prefs                   = HackGSUApplication.getPrefs(context);
			Set<String>       notifiedAnnouncementIds = prefs.getStringSet(ANNOUNCEMENTS_NOTIFIED_KEY, new HashSet<String>());
			returnValue = !notifiedAnnouncementIds.contains(announcement.getFirebaseKey());
			// TODO: 10/16/16 : put this line back in
			if (returnValue) {
				notifiedAnnouncementIds.add(announcement.getFirebaseKey());
				prefs.edit().remove(ANNOUNCEMENTS_NOTIFIED_KEY).commit();
				prefs.edit().putStringSet(ANNOUNCEMENTS_NOTIFIED_KEY, notifiedAnnouncementIds).apply();
			}
		}
		else { return true; }
		return returnValue;
	}

	@SuppressLint ("CommitPrefEdits")
	public static void toggleBookmark (Context context, Announcement announcement) {
		if (context != null && announcement != null && announcement.getFirebaseKey() != null) {
			announcement.setBookmarkedByMe(!announcement.isBookmarkedByMe());

			SharedPreferences prefs                     = HackGSUApplication.getPrefs(context);
			Set<String>       bookmarkedAnnouncementIds = prefs.getStringSet(ANNOUNCEMENTS_BOOKMARKED_KEY, new HashSet<String>());

			if (announcement.isBookmarkedByMe()) { bookmarkedAnnouncementIds.add(announcement.getFirebaseKey()); }
			else { bookmarkedAnnouncementIds.remove(announcement.getFirebaseKey()); }

			prefs.edit().remove(ANNOUNCEMENTS_BOOKMARKED_KEY).commit();
			prefs.edit().putStringSet(ANNOUNCEMENTS_BOOKMARKED_KEY, bookmarkedAnnouncementIds).apply();
		}
	}

	@SuppressLint ("CommitPrefEdits")
	public static void toggleLiked (Context context, Announcement announcement) {
		if (context != null && announcement != null && announcement.getFirebaseKey() != null) {
			announcement.toggleLikedByMe();

			SharedPreferences prefs                     = HackGSUApplication.getPrefs(context);
			Set<String>       bookmarkedAnnouncementIds = prefs.getStringSet(ANNOUNCEMENTS_LIKED_KEY, new HashSet<String>());

			if (announcement.isLikedByMe()) { bookmarkedAnnouncementIds.add(announcement.getFirebaseKey()); }
			else { bookmarkedAnnouncementIds.remove(announcement.getFirebaseKey()); }

			sendOrUpdateAnnouncement(announcement, null);

			prefs.edit().remove(ANNOUNCEMENTS_LIKED_KEY).commit();
			prefs.edit().putStringSet(ANNOUNCEMENTS_LIKED_KEY, bookmarkedAnnouncementIds).apply();
		}
	}
}
