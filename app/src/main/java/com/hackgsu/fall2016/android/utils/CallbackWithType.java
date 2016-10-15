package com.hackgsu.fall2016.android.utils;

/**
 * Created by Joshua King on 10/14/16.
 */
public abstract class CallbackWithType <T> {
	public abstract void onComplete (T t);

	public void onError () {}
}
