package com.hackgsu.fall2016.android.fragments;

import android.app.Dialog;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.*;
import android.support.v7.widget.Toolbar;
import android.view.*;
import com.hackgsu.fall2016.android.R;
import com.hackgsu.fall2016.android.interfaces.BackHandler;

/**
 * Created by Joshua King on 10/12/16.
 */
public class RequestMentorFragment extends BottomSheetDialogFragment implements BackHandler {
	private CoordinatorLayout.Behavior bottomSheetBehavior;
	private View                       contentView;
	private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {
		@Override
		public void onStateChanged (@NonNull View bottomSheet, int newState) {
			if (newState == BottomSheetBehavior.STATE_HIDDEN) {
				dismiss();
			}
			else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
				((AppBarLayout) contentView.findViewById(R.id.app_bar)).setExpanded(false, true);
			}
			else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
				((AppBarLayout) contentView.findViewById(R.id.app_bar)).setExpanded(true, true);
			}
		}

		@Override
		public void onSlide (@NonNull View bottomSheet, float slideOffset) {
		}
	};

	//	@Override
	//	public void dismiss () {
	//		 TODO: 10/13/16 :
	//		if (!onBackPressed()) {  }
	//	}

	@Override
	public void onStart () {
		super.onStart();

		Window window = getDialog().getWindow();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && window != null) {
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
			window.setNavigationBarColor(getResources().getColor(R.color.mentorsPrimary));
		}
	}

	@Override
	public void setupDialog (Dialog dialog, int style) {
		super.setupDialog(dialog, style);

		getContext().setTheme(R.style.AppTheme_MentorsScreen);

		contentView = View.inflate(getContext(), R.layout.activity_request_mentor, null);
		dialog.setContentView(contentView);

		Toolbar toolbar = (Toolbar) contentView.findViewById(R.id.toolbar);
		toolbar.setTitle(R.string.title_activity_request_mentor);

		int peekHeight = (int) getContext().getResources().getDimension(R.dimen.app_bar_height) + 190;

		FloatingActionButton fab = (FloatingActionButton) contentView.findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick (View view) {
				Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
			}
		});

		contentView.setFocusableInTouchMode(true);
		contentView.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey (View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK && getBottomSheetState() == BottomSheetBehavior.STATE_EXPANDED) {
					setBottomSheetState(BottomSheetBehavior.STATE_COLLAPSED);
					//					((AppBarLayout) contentView.findViewById(R.id.app_bar)).setExpanded(true, true);
					return true;
				}
				else if (keyCode == KeyEvent.KEYCODE_BACK && getBottomSheetState() == BottomSheetBehavior.STATE_COLLAPSED) {
					setBottomSheetState(BottomSheetBehavior.STATE_HIDDEN);
					return true;
				}
				return false;
			}
		});

		((AppBarLayout) contentView.findViewById(R.id.app_bar)).setExpanded(false);

		View                           contentViewParent = (View) contentView.getParent();
		CoordinatorLayout.LayoutParams params            = (CoordinatorLayout.LayoutParams) contentViewParent.getLayoutParams();
		params.height = ViewGroup.LayoutParams.MATCH_PARENT;
		contentViewParent.setLayoutParams(params);
		CoordinatorLayout.Behavior behavior = params.getBehavior();

		if (behavior != null && behavior instanceof BottomSheetBehavior) {
			((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
			((BottomSheetBehavior) behavior).setPeekHeight(peekHeight);
			setBottomSheetState(BottomSheetBehavior.STATE_EXPANDED);
		}
	}

	@Override
	public boolean onBackPressed () {
		//		if (getBottomSheetState() == BottomSheetBehavior.STATE_EXPANDED) {
		//			setBottomSheetState(BottomSheetBehavior.STATE_COLLAPSED);
		//			return true;
		//		}
		//		else if (getBottomSheetState() == BottomSheetBehavior.STATE_COLLAPSED) {
		//			setBottomSheetState(BottomSheetBehavior.STATE_HIDDEN);
		//			return true;
		//		}
		return false;
	}

	private void setBottomSheetState (int state) {
		if (bottomSheetBehavior == null) {
			View                           contentViewParent = (View) contentView.getParent();
			CoordinatorLayout.LayoutParams params            = (CoordinatorLayout.LayoutParams) contentViewParent.getLayoutParams();
			bottomSheetBehavior = params.getBehavior();
		}
		if (bottomSheetBehavior != null && bottomSheetBehavior instanceof BottomSheetBehavior) {
			((BottomSheetBehavior) bottomSheetBehavior).setState(state);
		}
	}

	private int getBottomSheetState () {
		if (bottomSheetBehavior == null) {
			View                           contentViewParent = (View) contentView.getParent();
			CoordinatorLayout.LayoutParams params            = (CoordinatorLayout.LayoutParams) contentViewParent.getLayoutParams();
			bottomSheetBehavior = params.getBehavior();
		}
		if (bottomSheetBehavior != null && bottomSheetBehavior instanceof BottomSheetBehavior) {
			return ((BottomSheetBehavior) bottomSheetBehavior).getState();
		}
		return -1;
	}
}