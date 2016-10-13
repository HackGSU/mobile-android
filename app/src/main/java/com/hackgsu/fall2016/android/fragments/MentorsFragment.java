package com.hackgsu.fall2016.android.fragments;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.hackgsu.fall2016.android.HackGSUApplication;
import com.hackgsu.fall2016.android.R;
import com.hackgsu.fall2016.android.adapters.MentorsRequestRecyclerViewAdapter;
import com.hackgsu.fall2016.android.model.MentorRequest;
import com.hackgsu.fall2016.android.utils.SmoothLinearLayoutManager;
import com.ncapdevi.fragnav.FragNavController;

import java.util.ArrayList;

/**
 * Created by Joshua King on 10/10/16.
 */
public class MentorsFragment extends BaseFragment {
	private MentorsRequestRecyclerViewAdapter adapter;
	private RequestMentorFragment             requestMentorFragment;
	private SwipeRefreshLayout                swipeRefreshLayout;

	public MentorsFragment () {
	}

	@Override
	public void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_mentor_request_list, container, false);

		final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.mentor_requests_recyclerview);
		recyclerView.setLayoutManager(new SmoothLinearLayoutManager(getContext()));
		adapter = new MentorsRequestRecyclerViewAdapter();
		recyclerView.setAdapter(adapter);

		swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_to_refresh);
		swipeRefreshLayout.setColorSchemeResources(R.color.mentorsPrimary, R.color.mentorsPrimaryDark);
		swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh () {
				updateMentorsData(false);
			}
		});

		getContext().setTheme(R.style.AppTheme_MentorsScreen);
		requestMentorFragment = new RequestMentorFragment();

		FloatingActionButton fab = ((FloatingActionButton) view.findViewById(R.id.fab));
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick (View v) {
				getFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
					@Override
					public void onBackStackChanged () {
						System.out.println("sdfsf");
					}
				});
				requestMentorFragment = new RequestMentorFragment();
				requestMentorFragment.show(getFragmentManager(), requestMentorFragment.getTag());
			}
		});

		updateMentorsData(true);

		return view;
	}

	@Override
	public int getPrimaryColor () {
		return R.color.mentorsPrimary;
	}

	@Override
	public int getTabIndex () {
		return FragNavController.TAB4;
	}

	@Override
	public String getTitle () {
		return "Mentors";
	}

	@Override
	public boolean onBackPressed () {
		return requestMentorFragment.onBackPressed();
	}

	private void updateMentorsData (boolean shouldShowRefreshing) {
		if (shouldShowRefreshing) { swipeRefreshLayout.setRefreshing(true); }

		final ArrayList<MentorRequest> mentorRequests = new ArrayList<>();
		mentorRequests.add(new MentorRequest("Our mobile app keeps crashing and won't even give us any error", "I can't get the iOS app to start because iOS is stupid and should just be destroyed."));

		HackGSUApplication.delayRunnableOnUI(1000, new Runnable() {
			@Override
			public void run () {
				adapter.setMentorRequests(mentorRequests);
				adapter.notifyDataSetChanged();
				swipeRefreshLayout.setRefreshing(false);
			}
		});
	}
}