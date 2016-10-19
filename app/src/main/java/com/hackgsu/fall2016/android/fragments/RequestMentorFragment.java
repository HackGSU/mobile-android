package com.hackgsu.fall2016.android.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.*;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import com.google.firebase.database.DatabaseError;
import com.hackgsu.fall2016.android.HackGSUApplication;
import com.hackgsu.fall2016.android.R;
import com.hackgsu.fall2016.android.controllers.MentorsController;
import com.hackgsu.fall2016.android.interfaces.BackHandler;
import com.hackgsu.fall2016.android.model.MentorRequest;
import com.hackgsu.fall2016.android.utils.CallbackWithType;

/**
 * Created by Joshua King on 10/12/16.
 */
public class RequestMentorFragment extends BottomSheetDialogFragment implements BackHandler, AppBarLayout.OnOffsetChangedListener {
	private AppBarLayout               appBar;
	private CoordinatorLayout.Behavior bottomSheetBehavior;
	private View                       contentView;
	private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {
		@Override
		public void onStateChanged (@NonNull View bottomSheet, int newState) {
			if (newState == BottomSheetBehavior.STATE_HIDDEN) {
				if (getTargetFragment() != null && getTargetFragment() instanceof MentorsFragment) {
					MentorsFragment mentorsFragment = (MentorsFragment) getTargetFragment();
					mentorsFragment.updateMentorsData(true);
				}
				dismiss();
			}
			else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
				setAppBarExpandedWithAnimation(false);
				contentView.findViewById(R.id.title_layout).animate().alpha(1).setDuration(500).start();
				translucentNavigationBar();
			}
			else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
				setAppBarExpandedWithAnimation(true);
				contentView.findViewById(R.id.title_layout).animate().alpha(0).setDuration(500).start();
				transparentNavigationBar();
				HackGSUApplication.hideKeyboard(RequestMentorFragment.this.contentView, RequestMentorFragment.this.getContext());
			}
		}

		@Override
		public void onSlide (@NonNull View bottomSheet, float slideOffset) {
		}
	};
	private MentorRequest mentorRequest;

	@Override
	public void onStart () {
		super.onStart();

		Dialog dialog = getDialog();
		dialog.setCanceledOnTouchOutside(false);
		dialog.setOnKeyListener(new Dialog.OnKeyListener() {
			@Override
			public boolean onKey (DialogInterface arg0, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
					if (!onBackPressed()) { getDialog().dismiss(); }
				}
				return true;
			}
		});
		translucentNavigationBar();
	}

	@Override
	public void setupDialog (Dialog dialog, int style) {
		super.setupDialog(dialog, style);

		getContext().setTheme(R.style.AppTheme_MentorsScreen);

		contentView = View.inflate(getContext(), R.layout.activity_request_mentor, null);
		dialog.setContentView(contentView);

		int                            peekHeight        = (int) getContext().getResources().getDimension(R.dimen.app_bar_height) + 190;
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

		Toolbar toolbar = (Toolbar) contentView.findViewById(R.id.toolbar);
		toolbar.setTitle(R.string.title_activity_request_mentor);

		appBar = ((AppBarLayout) contentView.findViewById(R.id.app_bar));
		appBar.setExpanded(false);
		appBar.addOnOffsetChangedListener(this);

		TextView                   swipeToLabel        = (TextView) contentView.findViewById(R.id.swipe_to_label);
		Button                     cancelRequestButton = (Button) contentView.findViewById(R.id.cancel_request);
		final TextInputEditText    titleEditText       = (TextInputEditText) contentView.findViewById(R.id.title);
		final ProgressBar          activityProgressBar = (ProgressBar) contentView.findViewById(R.id.activity_progress_bar);
		final TextInputEditText    teamNameEditText    = (TextInputEditText) contentView.findViewById(R.id.team_name);
		final Spinner              floorSpinner        = (Spinner) contentView.findViewById(R.id.floor_spinner);
		final TextInputEditText    locationEditText    = (TextInputEditText) contentView.findViewById(R.id.location);
		final TextInputEditText    platformEditText    = (TextInputEditText) contentView.findViewById(R.id.category);
		final TextInputEditText    descriptionEditText = (TextInputEditText) contentView.findViewById(R.id.description);
		final FloatingActionButton fab                 = (FloatingActionButton) contentView.findViewById(R.id.fab);

		if (getArguments() != null) {
			mentorRequest = (MentorRequest) getArguments().getSerializable(MentorsController.MENTOR_REQUESTS_KEY);
			if (mentorRequest != null) {
				int      floorIndex     = 0;
				String[] aderholdFloors = getContext().getResources().getStringArray(R.array.aderhold_floors);
				for (int i = 0; i < aderholdFloors.length; i++) {
					if (aderholdFloors[i].equals(mentorRequest.getFloor())) {
						floorIndex = i;
						break;
					}
				}

				cancelRequestButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick (View v) {
						mentorRequest.setStatusEnum(MentorRequest.Status.Cancelled);
						MentorsController.sendRequest(mentorRequest, getContext(), new CallbackWithType<DatabaseError>() {
							@Override
							public void onComplete (DatabaseError databaseError) {

								if (databaseError == null) {
									activityProgressBar.setVisibility(View.VISIBLE);
									fab.setEnabled(false);
									setBottomSheetState(BottomSheetBehavior.STATE_HIDDEN);
									HackGSUApplication.toast(getContext(), "Your request has been cancelled!");
								}
								else { onError(); }
							}

							@Override
							public void onError () {
								activityProgressBar.setVisibility(View.GONE);
								fab.setEnabled(true);

								HackGSUApplication.toast(getContext(), "Darn. That didn't work.");
							}
						});
					}
				});

				boolean hasNotBeenCancelled = !mentorRequest.getStatusEnum().equals(MentorRequest.Status.Cancelled);

				if (!hasNotBeenCancelled) {
					HackGSUApplication.toast(getContext(), "This request cannot be edited. It has already been cancelled");
					fab.hide();
				}

				titleEditText.setEnabled(hasNotBeenCancelled);
				teamNameEditText.setEnabled(hasNotBeenCancelled);
				floorSpinner.setEnabled(hasNotBeenCancelled);
				locationEditText.setEnabled(hasNotBeenCancelled);
				platformEditText.setEnabled(hasNotBeenCancelled);
				descriptionEditText.setEnabled(hasNotBeenCancelled);

				swipeToLabel.setText("Swipe down to close");
				cancelRequestButton.setVisibility(hasNotBeenCancelled ? View.VISIBLE : View.GONE);
				titleEditText.setText(mentorRequest.getTitle());
				teamNameEditText.setText(mentorRequest.getTeamName());
				floorSpinner.setSelection(floorIndex);
				locationEditText.setText(mentorRequest.getLocation());
				platformEditText.setText(mentorRequest.getCategory());
				descriptionEditText.setText(mentorRequest.getDescription());
			}
		}

		View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange (View v, boolean hasFocus) {
				if (!hasFocus && v instanceof TextInputEditText) {
					TextInputEditText textInputEditText = (TextInputEditText) v;
					if (!HackGSUApplication.isNullOrEmpty(String.valueOf(textInputEditText.getText()))) {
						setErrorOnEditText(textInputEditText, null);
					}
				}
			}
		};
		titleEditText.setOnFocusChangeListener(focusChangeListener);
		teamNameEditText.setOnFocusChangeListener(focusChangeListener);
		locationEditText.setOnFocusChangeListener(focusChangeListener);
		platformEditText.setOnFocusChangeListener(focusChangeListener);
		descriptionEditText.setOnFocusChangeListener(focusChangeListener);

		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick (View view) {
				String title       = String.valueOf(titleEditText.getText());
				String teamName    = String.valueOf(teamNameEditText.getText());
				String floor       = String.valueOf(floorSpinner.getSelectedItem());
				String location    = String.valueOf(locationEditText.getText());
				String platform    = String.valueOf(platformEditText.getText());
				String description = String.valueOf(descriptionEditText.getText());

				boolean titleValid    = !HackGSUApplication.isNullOrEmpty(title);
				boolean teamNameValid = !HackGSUApplication.isNullOrEmpty(teamName);
				boolean locationValid = !HackGSUApplication.isNullOrEmpty(location);
				boolean platformValid = !HackGSUApplication.isNullOrEmpty(platform);
				if (HackGSUApplication.isOneFalse(titleValid, teamNameValid, locationValid, platformValid)) {
					if (locationValid) { locationEditText.setError(null); }
					else { setErrorOnEditText(locationEditText, "Please describe about where you are"); }
					if (platformValid) { platformEditText.setError(null); }
					else { setErrorOnEditText(platformEditText, "Please give a category"); }
					if (teamNameValid) { teamNameEditText.setError(null); }
					else { setErrorOnEditText(teamNameEditText, "Please provide a name"); }
					if (titleValid) { titleEditText.setError(null); }
					else { setErrorOnEditText(titleEditText, "Please provide a title"); }
				}
				else {
					HackGSUApplication.hideKeyboard(RequestMentorFragment.this.contentView, RequestMentorFragment.this.getContext());
					activityProgressBar.setVisibility(View.VISIBLE);
					fab.setEnabled(false);

					if (mentorRequest == null) {
						mentorRequest = new MentorRequest(title, teamName, floor, location, platform, description);
					}
					else {
						mentorRequest.setTitle(title);
						mentorRequest.setTeamName(teamName);
						mentorRequest.setFloor(floor);
						mentorRequest.setLocation(location);
						mentorRequest.setCategory(platform);
						mentorRequest.setDescription(description);
					}
					MentorsController.sendRequest(mentorRequest, getContext(), new CallbackWithType<DatabaseError>() {
						@Override
						public void onComplete (DatabaseError databaseError) {
							activityProgressBar.setVisibility(View.GONE);
							fab.setEnabled(true);

							if (databaseError == null) {
								setBottomSheetState(BottomSheetBehavior.STATE_HIDDEN);
								HackGSUApplication.toast(getContext(), "Your request has been sent!");
							}
							else {
								onError();
							}
						}

						@Override
						public void onError () {
							activityProgressBar.setVisibility(View.GONE);
							fab.setEnabled(true);

							HackGSUApplication.toast(getContext(), "Darn. That didn't work.");
						}
					});
				}
			}
		});
	}

	@Override
	public void onCancel (DialogInterface dialog) {
		if (!onBackPressed()) { super.onCancel(dialog); }
	}

	@Override
	public boolean onBackPressed () {
		if (getBottomSheetState() == BottomSheetBehavior.STATE_EXPANDED) {
			setBottomSheetState(BottomSheetBehavior.STATE_COLLAPSED);
			return true;
		}
		else if (getBottomSheetState() == BottomSheetBehavior.STATE_COLLAPSED) {
			setBottomSheetState(BottomSheetBehavior.STATE_EXPANDED);
			return true;
		}
		else if (getBottomSheetState() == BottomSheetBehavior.STATE_SETTLING || getBottomSheetState() == BottomSheetBehavior.STATE_DRAGGING) {
			return true;
		}
		return false;
	}

	@Override
	public void onOffsetChanged (AppBarLayout appBarLayout, int verticalOffset) {
		appBarLayout.setExpanded(false, false);
	}

	private void setErrorOnEditText (TextInputEditText editText, String error) {
		if (HackGSUApplication.isNullOrEmpty(error)) {
			error = "";
		}
		else {
			editText.requestFocus();
			HackGSUApplication.showKeyboard(editText, getContext());
		}

		if (editText.getParent() != null && editText.getParent().getParent() instanceof TextInputLayout) {
			((TextInputLayout) editText.getParent().getParent()).setErrorEnabled(true);
			((TextInputLayout) editText.getParent().getParent()).setError(error);
		}
		else { editText.setError(error); }
	}

	private void transparentNavigationBar () {
		Window window = getDialog().getWindow();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && window != null) {
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}
	}

	private void translucentNavigationBar () {
		Dialog dialog = getDialog();
		Window window = dialog.getWindow();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && window != null) {
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}
	}

	private void setAppBarExpandedWithAnimation (boolean expanded) {
		appBar.removeOnOffsetChangedListener(RequestMentorFragment.this);
		appBar.setExpanded(expanded, true);
		HackGSUApplication.delayRunnableOnUI(600, new Runnable() {
			@Override
			public void run () {
				appBar.addOnOffsetChangedListener(RequestMentorFragment.this);
			}
		});
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