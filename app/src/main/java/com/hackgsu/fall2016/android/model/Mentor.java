package com.hackgsu.fall2016.android.model;

/**
 * Created by Joshua King on 10/10/16.
 */
public class Mentor {
	private String id;
	private String name;

	public Mentor (String name) {
		this.name = name;
	}

	public String getName () {
		return name;
	}

	public String getId () {

		return id;
	}

	public void setId (String id) {
		this.id = id;
	}
}
