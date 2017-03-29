package com.hackgsu.fall2016.android.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.hackgsu.fall2016.android.R;
import com.hackgsu.fall2016.android.activities.PhotoViewActivity;
import com.ncapdevi.fragnav.FragNavController;

import static android.R.attr.button;
import static android.R.attr.onClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class FacilityMapFragment extends BaseFragment {
	public FacilityMapFragment () { }

	@Override
	public int getPrimaryColor () {
		return R.color.facilityMapPrimary;
	}

	@Override
	public int getTabIndex () {
		return FragNavController.TAB3;
	}

	@Override
	public String getTitle () {
		return "Facility Map";
	}

	@Override
	public boolean onBackPressed () {
		return false;
	}

	@Override
	public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_facility_map, container, false);

		View concourseCardView = view.findViewById(R.id.concourse_card_view);
		View floor1CardView = view.findViewById(R.id.floor1_card_view);
		View zoomButtonConcourse = view.findViewById(R.id.button_zoom_concourse);
		View zoomButtonFloor1 = view.findViewById(R.id.button_zoom_floor1);
		View.OnClickListener onClickListenerForConcourse = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getContext(), PhotoViewActivity.class);
				intent.putExtra(PhotoViewActivity.IMAGE_TO_VIEW, R.drawable.ic_concourse);
				startActivity(intent);
			}
		};
		concourseCardView.setOnClickListener(onClickListenerForConcourse);
		zoomButtonConcourse.setOnClickListener(onClickListenerForConcourse);
		View.OnClickListener onClickListenerForFloor1 = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getContext(), PhotoViewActivity.class);
				intent.putExtra(PhotoViewActivity.IMAGE_TO_VIEW, R.drawable.ic_floor_1);
				startActivity(intent);
			}
		};
		floor1CardView.setOnClickListener(onClickListenerForFloor1);
		zoomButtonFloor1.setOnClickListener(onClickListenerForFloor1);
		return view;
	}
}
