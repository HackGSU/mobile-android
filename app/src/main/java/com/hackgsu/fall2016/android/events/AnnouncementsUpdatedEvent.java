package com.hackgsu.fall2016.android.events;

import com.hackgsu.fall2016.android.model.Announcement;

import java.util.ArrayList;

/**
 * Created by Joshua King on 10/15/16.
 */
public class AnnouncementsUpdatedEvent extends BaseEvent<ArrayList<Announcement>> {
	public AnnouncementsUpdatedEvent (ArrayList<Announcement> announcements) {
		super(announcements);
	}
}
