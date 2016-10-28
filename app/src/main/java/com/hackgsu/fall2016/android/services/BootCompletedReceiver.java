package com.hackgsu.fall2016.android.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootCompletedReceiver extends BroadcastReceiver {
	@Override
	public void onReceive (Context context, Intent intent) {
		Intent serviceIntent = new Intent(context, FirebaseService.class);
		context.startService(serviceIntent);
	}
}