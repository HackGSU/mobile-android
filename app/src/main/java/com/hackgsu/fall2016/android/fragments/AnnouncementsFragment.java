package com.hackgsu.fall2016.android.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.hackgsu.fall2016.android.DataStore;
import com.hackgsu.fall2016.android.R;
import com.hackgsu.fall2016.android.events.AnnouncementsUpdatedEvent;
import com.hackgsu.fall2016.android.model.Announcement;
import com.hackgsu.fall2016.android.utils.BusUtils;
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
	}

	@Override
	public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_announcements, container, false);

		announcementRecyclerView = (AnnouncementsRecyclerView) view.findViewById(R.id.announcementRecyclerView);
		notifyDataSetChanged();

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

	private void notifyDataSetChanged () {
		if (announcementRecyclerView != null) {
			RecyclerView.Adapter adapter = announcementRecyclerView.getAdapter();
			if (adapter != null) { adapter.notifyDataSetChanged(); }
		}
	}
}
