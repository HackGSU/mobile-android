package com.hackgsu.fall2016.android;

import android.app.Application;
import net.danlew.android.joda.JodaTimeAndroid;

/**
 * Created by Joshua King on 9/27/16.
 */
public class HackGNUApplication extends Application {
	@Override
	public void onCreate () {
		super.onCreate();

		JodaTimeAndroid.init(this);
	}
}
