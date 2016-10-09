package com.hackgsu.fall2016.android.fragments;

import android.support.annotation.ColorRes;
import android.support.annotation.IntDef;
import android.support.v4.app.Fragment;
import com.ncapdevi.fragnav.FragNavController;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.ncapdevi.fragnav.FragNavController.*;

/**
 * Created by Joshua King on 9/27/16.
 */
public abstract class BaseFragment extends Fragment {
	public abstract
	@ColorRes
	int getPrimaryColor ();
	public abstract
	@FragNavControllerTabIndex
	int getTabIndex ();
	public abstract String getTitle ();
	@IntDef ({ TAB1, TAB2, TAB3, TAB4, TAB5 })
	@Retention (RetentionPolicy.SOURCE)
	public @interface FragNavControllerTabIndex { }

	public void onFocus () {}

	public void selectTab (FragNavController fragNavController) {
		fragNavController.switchTab(getTabIndex());
	}
}
