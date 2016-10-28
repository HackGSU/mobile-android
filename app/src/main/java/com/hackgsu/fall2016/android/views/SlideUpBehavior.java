package com.hackgsu.fall2016.android.views;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.View;
import com.hackgsu.fall2016.android.R;

/**
 * Created by Joshua King on 10/17/16.
 */
public class SlideUpBehavior extends FloatingActionButton.Behavior {
	private boolean isDown;

	public SlideUpBehavior (Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onStartNestedScroll (final CoordinatorLayout coordinatorLayout,
										final FloatingActionButton child,
										View directTargetChild,
										View target,
										int nestedScrollAxes) {
		return coordinatorLayout.getResources().getBoolean(R.bool.isAdmin);
	}
}