package com.hackgsu.fall2016.android.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.hackgsu.fall2016.android.HackGSUApplication;
import com.hackgsu.fall2016.android.R;
import com.hackgsu.fall2016.android.events.ScheduleUpdatedEvent;
import com.hackgsu.fall2016.android.utils.BusUtils;
import com.hackgsu.fall2016.android.views.ScheduleRecyclerView;
import com.ncapdevi.fragnav.FragNavController;
import org.greenrobot.eventbus.Subscribe;

/**
 * A simple {@link Fragment} subclass.
 */
public class ScheduleFragment extends BaseFragment {
	private ScheduleRecyclerView scheduleRecyclerView;

	public ScheduleFragment () {
		BusUtils.register(this);
	}

	@Override
	public int getPrimaryColor () {
		return R.color.schedulePrimary;
	}

	@Override
	public int getTabIndex () {
		return FragNavController.TAB2;
	}

	@Override
	public String getTitle () {
		return "Schedule";
	}

	@Override
	public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_schedule, container, false);

		scheduleRecyclerView = (ScheduleRecyclerView) view.findViewById(R.id.scheduleRecyclerView);
		notifyDataSetChanged();

		return view;
	}

	@Override
	public void onReselected () {
		notifyDataSetChanged();

		if (scheduleRecyclerView != null) { scheduleRecyclerView.showNowRow(); }
	}

	@Override
	public boolean onBackPressed () {
		return false;
	}

	@Override public void onResume () {
		super.onResume();

		HackGSUApplication.refreshSchedule();
	}

	@Subscribe
	public void onEvent (ScheduleUpdatedEvent scheduleUpdatedEvent) { notifyDataSetChanged(); }

	public ScheduleRecyclerView getScheduleRecyclerView () {
		return scheduleRecyclerView;
	}

	private void notifyDataSetChanged () {
		if (scheduleRecyclerView != null) {
			RecyclerView.Adapter adapter = scheduleRecyclerView.getAdapter();
			if (adapter != null) { adapter.notifyDataSetChanged(); }
		}
	}
}
