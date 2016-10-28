package com.hackgsu.fall2016.android.events;

import com.hackgsu.fall2016.android.model.ScheduleEvent;

import java.util.ArrayList;

/**
 * Created by Joshua King on 10/9/16.
 */
public class ScheduleUpdatedEvent extends BaseEvent<ArrayList<ScheduleEvent>> {
	public ScheduleUpdatedEvent (ArrayList<ScheduleEvent> scheduleEvents) {
		super(scheduleEvents);
	}
}
