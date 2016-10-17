package com.hackgsu.fall2016.android.services;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import com.hackgsu.fall2016.android.DataStore;
import com.hackgsu.fall2016.android.HackGSUApplication;
import com.hackgsu.fall2016.android.controllers.AnnouncementController;
import com.hackgsu.fall2016.android.controllers.NotificationController;
import com.hackgsu.fall2016.android.model.Announcement;

public class AnnouncementManipulationService extends Service {
	public static final String ANNOUNCEMENT                       = "announcement";
	public static final String ANNOUNCEMENT_NOTIFICATION_ID_EXTRA = "announcement_notification_id_extra";
	public static final String BOOKMARK_ANNOUNCEMENT              = "bookmark_announcement";
	public static final String LIKE_ANNOUNCEMENT                  = "like_announcement";

	@Nullable
	@Override
	public IBinder onBind (Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand (Intent intent, int flags, int startId) {
		Announcement announcementExtra = (Announcement) intent.getSerializableExtra(ANNOUNCEMENT);
		for (Announcement announcement : DataStore.getAnnouncements()) {
			String announcementFirebaseKey = announcement.getFirebaseKey();
			String firebaseKey             = announcementExtra.getFirebaseKey();
			if (!HackGSUApplication.isNullOrEmpty(announcementFirebaseKey) && !HackGSUApplication.isNullOrEmpty(firebaseKey) && announcementFirebaseKey
					.equals(firebaseKey)) {
				announcementExtra = announcement;
			}
		}
		boolean likeAnnouncement     = intent.getBooleanExtra(LIKE_ANNOUNCEMENT, false);
		boolean bookmarkAnnouncement = intent.getBooleanExtra(BOOKMARK_ANNOUNCEMENT, false);
		int     notificationId       = intent.getIntExtra(ANNOUNCEMENT_NOTIFICATION_ID_EXTRA, -1);

		if (likeAnnouncement) { AnnouncementController.toggleLiked(getApplicationContext(), announcementExtra); }
		if (bookmarkAnnouncement) { AnnouncementController.toggleBookmark(getApplicationContext(), announcementExtra); }

		if (notificationId != -1) {
			NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
			//			notificationManager.cancel(notificationId);
			NotificationController.sendAnnouncementNotification(getApplicationContext(), announcementExtra, notificationId);
		}

		stopSelf();

		return super.onStartCommand(intent, flags, startId);
	}
}
