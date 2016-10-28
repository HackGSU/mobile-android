package com.hackgsu.fall2016.android.controllers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.hackgsu.fall2016.android.HackGSUApplication;
import com.hackgsu.fall2016.android.model.Mentor;
import com.hackgsu.fall2016.android.model.MentorRequest;
import com.hackgsu.fall2016.android.utils.CallbackWithType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Joshua King on 10/14/16.
 */
public class MentorsController {
	public static final String MENTOR_REQUESTS_KEY   = "mentor_requests";
	public static final String MENTOR_REQUESTS_TABLE = "mentor_requests";

	public static Mentor getMentor (String mentorId) {
		return new Mentor("Mr Mentor");
		// TODO: 10/15/16 : CREATE MENTOR REPO
	}

	public static void sendRequest (MentorRequest mentorRequest, final Context context, final CallbackWithType<DatabaseError> callback) {
		FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
		if (currentUser != null) {
			DatabaseReference dbRef            = FirebaseDatabase.getInstance().getReference();
			DatabaseReference mentorRequestRef = dbRef.child(MENTOR_REQUESTS_TABLE).getRef();
			DatabaseReference mentorRequestRefWithId;
			if (mentorRequest.getFirebaseKey() == null || mentorRequest.getFirebaseKey().equals("")) {
				mentorRequestRefWithId = mentorRequestRef.push();
			}
			else { mentorRequestRefWithId = mentorRequestRef.child(mentorRequest.getFirebaseKey()); }
			mentorRequestRefWithId.setValue(mentorRequest, new DatabaseReference.CompletionListener() {
				@SuppressLint ("CommitPrefEdits")
				@Override
				public void onComplete (DatabaseError databaseError, DatabaseReference databaseReference) {
					SharedPreferences prefs              = HackGSUApplication.getPrefs(context);
					final Set<String> myMentorRequestIds = prefs.getStringSet(MENTOR_REQUESTS_KEY, new HashSet<String>());
					myMentorRequestIds.add(databaseReference.getKey());
					prefs.edit().remove(MENTOR_REQUESTS_KEY).commit();
					prefs.edit().putStringSet(MENTOR_REQUESTS_KEY, myMentorRequestIds).commit();

					callback.onComplete(databaseError);
				}
			});
		}
	}

	public static void updateRequestsForThisDevice (Context context, final CallbackWithType<ArrayList<MentorRequest>> callback) {
		if (context == null) {
			callback.onError();
			return;
		}

		SharedPreferences prefs              = HackGSUApplication.getPrefs(context);
		final Set<String> myMentorRequestIds = prefs.getStringSet(MENTOR_REQUESTS_KEY, new HashSet<String>());
		FirebaseUser      currentUser        = FirebaseAuth.getInstance().getCurrentUser();
		if (currentUser != null) {
			DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
			dbRef.child(MENTOR_REQUESTS_TABLE).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
				@Override
				public void onDataChange (DataSnapshot snapshot) {
					ArrayList<MentorRequest> requests = new ArrayList<>();
					for (DataSnapshot child : snapshot.getChildren()) {
						if (myMentorRequestIds.contains(child.getKey())) {
							MentorRequest mentorRequest = child.getValue(MentorRequest.class);
							mentorRequest.setFirebaseKey(child.getKey());
							requests.add(mentorRequest);
						}
					}
					callback.onComplete(requests);
				}

				@Override
				public void onCancelled (DatabaseError databaseError) {

				}
			});
		}
	}
}
