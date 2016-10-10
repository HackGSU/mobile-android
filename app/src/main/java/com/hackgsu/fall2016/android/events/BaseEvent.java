package com.hackgsu.fall2016.android.events;

/**
 * Created by Joshua King on 10/9/16.
 */
public abstract class BaseEvent <T> {
	private T t;

	public BaseEvent (T t) { this.t = t; }

	public T getPayload () {
		return t;
	}

	public Class getPayloadClass () {
		return t.getClass();
	}
}

