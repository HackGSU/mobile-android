package com.hackgsu.fall2016.android.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.hackgsu.fall2016.android.HackGSUApplication;
import com.hackgsu.fall2016.android.R;
import com.hackgsu.fall2016.android.adapters.MentorsRequestRecyclerViewAdapter;
import com.hackgsu.fall2016.android.controllers.MentorsController;
import com.hackgsu.fall2016.android.model.MentorRequest;
import com.hackgsu.fall2016.android.utils.BusUtils;
import com.hackgsu.fall2016.android.utils.CallbackWithType;
import com.hackgsu.fall2016.android.utils.SmoothLinearLayoutManager;
import com.ncapdevi.fragnav.FragNavController;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

/**
 * Created by Joshua King on 10/10/16.
 */
public class MentorsFragment extends BaseFragment {
	private MentorsRequestRecyclerViewAdapter adapter;
	private RequestMentorFragment             requestMentorFragment;
	private View                              rootView;
	private SwipeRefreshLayout                swipeRefreshLayout;

	public MentorsFragment () {
		BusUtils.register(this);
	}

	@Override public void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_mentor_request_list, container, false);

		final RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.mentor_requests_recyclerview);
		recyclerView.setLayoutManager(new SmoothLinearLayoutManager(getContext()));
		adapter = new MentorsRequestRecyclerViewAdapter(getFragmentManager(), this);
		recyclerView.setAdapter(adapter);

		swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_to_refresh);
		swipeRefreshLayout.setColorSchemeResources(R.color.mentorsPrimary, R.color.mentorsPrimaryDark);
		swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override public void onRefresh () {
				updateMentorsData(false);
			}
		});

		//		getContext().setTheme(R.style.AppTheme_MentorsScreen);

		FloatingActionButton fab = ((FloatingActionButton) rootView.findViewById(R.id.fab));
		fab.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick (View v) {
				requestAMentor();
			}
		});

		return rootView;
	}

	@Override public void onResume () {
		super.onResume();

		updateMentorsData(true);
	}

	@Override public int getPrimaryColor () {
		return R.color.mentorsPrimary;
	}

	@Override public int getTabIndex () {
		return FragNavController.TAB4;
	}

	@Override public String getTitle () {
		return "Mentors";
	}

	@Override public boolean onBackPressed () {
		return requestMentorFragment != null && requestMentorFragment.onBackPressed();
	}

	@Override public void onPause () {
		super.onPause();

		requestMentorFragment.dismiss();
	}

	@Subscribe public void onEvent (com.hackgsu.fall2016.android.events.RequestAMentorEvent requestAMentorEvent) {
		try {
			requestAMentor();
		} catch (Exception e) {
			e.printStackTrace();

			Toast.makeText(getContext(), "An error occurred", Toast.LENGTH_SHORT).show();
		}
	}

	public void requestAMentor () {
		requestMentorFragment = new RequestMentorFragment();
		requestMentorFragment.setTargetFragment(MentorsFragment.this, 0);
		requestMentorFragment.show(getFragmentManager(), requestMentorFragment.getTag());
	}

	public void updateMentorsData (boolean shouldShowRefreshing) {
		if (shouldShowRefreshing) { swipeRefreshLayout.setRefreshing(true); }

		AsyncTask.execute(new Runnable() {
			@Override public void run () {
				MentorsController.updateRequestsForThisDevice(getContext(), new CallbackWithType<ArrayList<MentorRequest>>() {
					@Override public void onComplete (final ArrayList<MentorRequest> mentorRequests) {
						HackGSUApplication.delayRunnableOnUI(1000, new Runnable() {
							@Override public void run () {
								adapter.setMentorRequests(mentorRequests);
								adapter.notifyDataSetChanged();
								swipeRefreshLayout.setRefreshing(false);
							}
						});
					}
				});
			}
		});
	}
}