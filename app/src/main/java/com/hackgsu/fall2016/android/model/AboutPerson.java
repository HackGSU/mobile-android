package com.hackgsu.fall2016.android.model;

import android.support.annotation.DrawableRes;

import com.hackgsu.fall2016.android.R;

/**
 * Created by Sri on 10/19/2016.
 */
public class AboutPerson {
    private final String name;
    private final String role;
    private final String linkedInUrl;
    private
    @DrawableRes
    int picture;

    public AboutPerson(String name, String role, String linkedInUrl, int picture) {
        this.name = name;
        this.role = role;
        this.linkedInUrl = linkedInUrl;
        this.picture = picture;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public String getLinkedInUrl() {
        return linkedInUrl;
    }

    public int getPicture() { return picture; }
}
