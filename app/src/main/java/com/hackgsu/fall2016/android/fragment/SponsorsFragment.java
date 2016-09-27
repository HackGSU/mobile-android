package com.hackgsu.fall2016.android.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.hackgsu.fall2016.android.R;
import com.ncapdevi.fragnav.FragNavController;

/**
 * A simple {@link Fragment} subclass.
 */
public class SponsorsFragment extends BaseFragment {
	public SponsorsFragment () {
		// Required empty public constructor
	}

	@Override
	public int getPrimaryColor () {
		return R.color.sponsorsPrimary;
	}

	@Override
	public int getTabIndex () {
		return FragNavController.TAB5;
	}

	@Override
	public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_sponsors, container, false);
	}
}
