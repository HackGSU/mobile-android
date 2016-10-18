package com.hackgsu.fall2016.android.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.hackgsu.fall2016.android.DataStore;
import com.hackgsu.fall2016.android.HackGSUApplication;
import com.hackgsu.fall2016.android.R;
import com.hackgsu.fall2016.android.activities.PostNewAnnouncementActivity;
import com.hackgsu.fall2016.android.controllers.MentorsController;
import com.hackgsu.fall2016.android.events.AnnouncementsUpdatedEvent;
import com.hackgsu.fall2016.android.model.Announcement;
import com.hackgsu.fall2016.android.model.MentorRequest;
import com.hackgsu.fall2016.android.utils.BusUtils;
import com.hackgsu.fall2016.android.utils.CallbackWithType;
import com.hackgsu.fall2016.android.utils.SmoothLinearLayoutManager;
import com.hackgsu.fall2016.android.views.AnnouncementsRecyclerView;
import com.ncapdevi.fragnav.FragNavController;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class AnnouncementsFragment extends BaseFragment {
	private AnnouncementsRecyclerView announcementRecyclerView;
	private SwipeRefreshLayout        announcementSwipeToRefresh;
	private FloatingActionButton      newAnnouncementFab;

	public AnnouncementsFragment () {
		BusUtils.register(this);
	}

	@Override
	public int getPrimaryColor () {
		return R.color.announcementsPrimary;
	}

	@Override
	public int getTabIndex () {
		return FragNavController.TAB1;
	}

	@Override
	public String getTitle () {
		return "Announcements";
	}

	@Override
	public boolean onBackPressed () {
		return false;
	}

	@Override
	public void onReselected () {
		notifyDataSetChanged();
	}

	@Override
	public void onResume () {
		super.onResume();

		notifyDataSetChanged();
		getContext().setTheme(R.style.AppTheme_AnnouncementsScreen);
	}

	@Override
	public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_announcements, container, false);


		announcementRecyclerView = (AnnouncementsRecyclerView) view.findViewById(R.id.announcementRecyclerView);
		newAnnouncementFab = (FloatingActionButton) view.findViewById(R.id.fab);
		announcementSwipeToRefresh = (SwipeRefreshLayout) view.findViewById(R.id.announcement_swipe_to_refresh);
		announcementSwipeToRefresh.setColorSchemeResources(R.color.announcementsPrimary, R.color.announcementsPrimaryDark);
		announcementSwipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh () {
				updateAnnouncements(false);
			}
		});
		notifyDataSetChanged();

		boolean isAdmin = getContext().getResources().getBoolean(R.bool.isAdmin);
		newAnnouncementFab.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
		newAnnouncementFab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick (View v) {
				startActivity(new Intent(getContext(), PostNewAnnouncementActivity.class));
			}
		});

		if (isAdmin) {
			int paddingTop   = announcementRecyclerView.getPaddingTop();
			int paddingRight = announcementRecyclerView.getPaddingRight();
			int paddingLeft  = announcementRecyclerView.getPaddingLeft();
			announcementRecyclerView.setPadding(paddingLeft, paddingTop, paddingRight, (int) HackGSUApplication.convertDpToPx(130, getContext()));
		}

		return view;
	}

	public void setShowOnlyBookmarked (boolean showOnlyBookmarked) {
		if (announcementRecyclerView != null) {
			announcementRecyclerView.setShowOnlyBookmarked(showOnlyBookmarked);
			notifyDataSetChanged();
		}
	}

	@Subscribe
	public void onEvent (AnnouncementsUpdatedEvent announcementsUpdatedEvent) { notifyDataSetChanged(); }

	public void highlightAnnouncement (Announcement announcementToHighlight) {
		int                     index         = -1;
		ArrayList<Announcement> announcements = DataStore.getAnnouncements();
		for (int i = 0; i < announcements.size(); i++) {
			if (announcements.get(i).getFirebaseKey().equals(announcementToHighlight.getFirebaseKey())) { index = i; }
		}

		if (index >= 0) {
			SmoothLinearLayoutManager smoothLinearLayoutManager = announcementRecyclerView.getLayoutManager();
			if (smoothLinearLayoutManager != null) { smoothLinearLayoutManager.scrollToPosition(index); }
		}
	}

	private void updateAnnouncements (boolean shouldShowRefreshing) {
		if (shouldShowRefreshing) { announcementSwipeToRefresh.setRefreshing(true); }

		AsyncTask.execute(new Runnable() {
			@Override
			public void run () {
				MentorsController.updateRequestsForThisDevice(getContext(), new CallbackWithType<ArrayList<MentorRequest>>() {
					@Override
					public void onComplete (final ArrayList<MentorRequest> mentorRequests) {
						HackGSUApplication.delayRunnableOnUI(1000, new Runnable() {
							@Override
							public void run () {
								HackGSUApplication.refreshAnnouncements(getContext());
								announcementSwipeToRefresh.setRefreshing(false);
							}
						});
					}
				});
			}
		});
	}

	private void notifyDataSetChanged () {
		if (announcementRecyclerView != null) {
			RecyclerView.Adapter adapter = announcementRecyclerView.getAdapter();
			if (adapter != null) { adapter.notifyDataSetChanged(); }
		}
	}
}
