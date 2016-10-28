package com.hackgsu.fall2016.android.fragments;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.ColorRes;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.transition.Fade;
import com.hackgsu.fall2016.android.interfaces.BackHandler;
import com.ncapdevi.fragnav.FragNavController;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.ncapdevi.fragnav.FragNavController.*;

/**
 * Created by Joshua King on 9/27/16.
 */
public abstract class BaseFragment extends Fragment implements BackHandler {
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

	@CallSuper
	@Override
	public void onCreate (@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			setEnterTransition(new Fade());
			setReenterTransition(new Fade());
			setReturnTransition(new Fade(Fade.OUT));
			setExitTransition(new Fade(Fade.OUT));
		}
	}

	@Override
	public abstract boolean onBackPressed ();

	public void onReselected () {}

	public void selectTab (FragNavController fragNavController) {
		fragNavController.switchTab(getTabIndex());
	}
}
