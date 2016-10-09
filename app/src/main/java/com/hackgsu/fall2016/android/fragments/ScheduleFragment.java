package com.hackgsu.fall2016.android.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.hackgsu.fall2016.android.R;
import com.hackgsu.fall2016.android.views.ScheduleRecyclerView;
import com.ncapdevi.fragnav.FragNavController;

/**
 * A simple {@link Fragment} subclass.
 */
public class ScheduleFragment extends BaseFragment {
	public ScheduleFragment () { }

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

		ScheduleRecyclerView scheduleRecyclerView = (ScheduleRecyclerView) view.findViewById(R.id.scheduleRecyclerView);
		RecyclerView.Adapter adapter              = scheduleRecyclerView.getAdapter();
		if (adapter != null) { adapter.notifyDataSetChanged(); }

		return view;
	}
}
