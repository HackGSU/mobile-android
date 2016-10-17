package com.hackgsu.fall2016.android.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.hackgsu.fall2016.android.DataStore;
import com.hackgsu.fall2016.android.HackGSUApplication;
import com.hackgsu.fall2016.android.controllers.AnnouncementController;
import com.hackgsu.fall2016.android.controllers.NotificationController;
import com.hackgsu.fall2016.android.model.Announcement;

import static android.content.ContentValues.TAG;

public class FirebaseService extends Service {
	public FirebaseService () {
	}

	@Override
	public IBinder onBind (Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand (Intent intent, int flags, int startId) {
		final SharedPreferences firebasePrefs = getApplicationContext().getSharedPreferences("firebasePrefs", MODE_PRIVATE);
		FirebaseAuth            firebaseAuth  = FirebaseAuth.getInstance();

		FirebaseAuth.AuthStateListener firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
			@Override
			public void onAuthStateChanged (@NonNull FirebaseAuth firebaseAuth) {
				FirebaseUser user = firebaseAuth.getCurrentUser();
				if (user != null) {
					Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

					HackGSUApplication.refreshSchedule();
					HackGSUApplication.getAnnouncements(getApplicationContext());

					DatabaseReference dbRef            = FirebaseDatabase.getInstance().getReference();
					DatabaseReference announcementsRef = dbRef.child("announcements").getRef();
					announcementsRef.addValueEventListener(new ValueEventListener() {
						@Override
						public void onDataChange (DataSnapshot dataSnapshot) {
							HackGSUApplication.parseDataSnapshotForAnnouncements(getApplicationContext(), dataSnapshot);

							for (Announcement announcement : DataStore.getAnnouncements()) {
								if (AnnouncementController.shouldNotify(getApplicationContext(), announcement)) {
									NotificationController.sendAnnouncementNotification(getApplicationContext(), announcement);
								}
							}
						}

						@Override
						public void onCancelled (DatabaseError databaseError) { }
					});
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
						Toast.makeText(getApplicationContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
					}
					else { firebasePrefs.edit().putBoolean("hasAuthed", true).apply(); }
				}
			});
		}

		return START_STICKY;
	}
}
