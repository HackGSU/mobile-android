package com.hackgsu.fall2016.android.utils;

import android.content.Context;
import android.graphics.PointF;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * Created by Joshua King on 10/9/16.
 */
public class SmoothLinearLayoutManager extends LinearLayoutManager {
	private static final float MILLISECONDS_PER_INCH = 50f;
	private Context mContext;

	public SmoothLinearLayoutManager (Context context) {
		super(context);
		mContext = context;
	}

	@Override
	public void scrollToPosition (int position) {
		smoothScrollTo(position);
	}

	@Override
	public void scrollToPositionWithOffset (int position, int offset) {
		smoothScrollTo(position);
	}

	@Override
	public void smoothScrollToPosition (RecyclerView recyclerView, RecyclerView.State state, final int position) {
		smoothScrollTo(position);
	}

	private void smoothScrollTo (final int position) {
		LinearSmoothScroller smoothScroller = new LinearSmoothScroller(mContext) {
			@Override
			public PointF computeScrollVectorForPosition (int targetPosition) {
				return SmoothLinearLayoutManager.this.computeScrollVectorForPosition(targetPosition);
			}

			@Override
			protected float calculateSpeedPerPixel (DisplayMetrics displayMetrics) {
				return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
			}

			@Override
			protected int getVerticalSnapPreference () {
				return SNAP_TO_START;
			}

			@Override
			protected void onStop () {
				super.onStop();

				final View viewAtPosition   = findViewByPosition(position);
				int        dyToMakeVisible  = calculateDyToMakeVisible(viewAtPosition, getVerticalSnapPreference());
				int        timeForScrolling = calculateTimeForScrolling(dyToMakeVisible) + calculateTimeForDeceleration(dyToMakeVisible);
				System.out.println(timeForScrolling);
				viewAtPosition.animate()
							  .setDuration(100)
							  .scaleX(1.2f)
							  .scaleY(1.2f)
							  .alpha(0.5f)
							  .setStartDelay(timeForScrolling)
							  .withEndAction(new Runnable() {
								  @Override
								  public void run () {
									  viewAtPosition.animate().setStartDelay(0).setDuration(100).scaleY(1).scaleX(1).alpha(1).start();
								  }
							  })
							  .start();
			}
		};

		smoothScroller.setTargetPosition(position);
		startSmoothScroll(smoothScroller);
	}
}