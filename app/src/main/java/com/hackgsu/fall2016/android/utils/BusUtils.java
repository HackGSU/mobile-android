package com.hackgsu.fall2016.android.utils;

/**
 * Created by Joshua King on 10/9/16.
 */

import android.util.Log;
import com.hackgsu.fall2016.android.HackGSUApplication;
import com.hackgsu.fall2016.android.events.BaseEvent;
import org.greenrobot.eventbus.EventBus;

public final class BusUtils {
	private static final EventBus BUS = EventBus.getDefault();
	private static final String   TAG = BusUtils.class.getSimpleName();

	private BusUtils () { }

	public static EventBus get () { return BUS; }

	public static void post (final BaseEvent event) { post(event, false); }

	public static void post (final BaseEvent event, final boolean sticky) {
		Log.i(TAG, "post: " + event);
		get().removeStickyEvent(event.getClass());
		HackGSUApplication.runOnUI(new Runnable() {
			@Override
			public void run () {
				if (sticky) { get().postSticky(event); }
				else { get().post(event); }
			}
		});
	}

	public static void register (Object object) {
		try { get().register(object); } catch (Exception ignored) { }
	}
}