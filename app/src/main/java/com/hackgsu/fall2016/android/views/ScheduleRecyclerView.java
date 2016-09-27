package com.hackgsu.fall2016.android.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import com.hackgsu.fall2016.android.DataStore;
import com.hackgsu.fall2016.android.R;
import com.hackgsu.fall2016.android.model.ScheduleEvent;

import java.util.ArrayList;

/**
 * TODO: document your custom view class.
 */
public class ScheduleRecyclerView extends RecyclerView {
	private int colorTheme;

	public ScheduleRecyclerView (Context context) {
		super(context);
		init(null, 0);
	}

	public ScheduleRecyclerView (Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs, 0);
	}

	public ScheduleRecyclerView (Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs, defStyle);
	}

	private class ScheduleEventAdapter extends RecyclerView.Adapter<ScheduleEventViewHolder> {
		private ArrayList<ScheduleEvent> scheduleEvents;

		public ScheduleEventAdapter (ArrayList<ScheduleEvent> scheduleEvents) {
			this.scheduleEvents = scheduleEvents;
		}

		@Override
		public ScheduleEventViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
			// TODO: 9/27/16 :
			return new ScheduleEventViewHolder(null);
		}

		@Override
		public void onBindViewHolder (ScheduleEventViewHolder holder, int position) {

		}

		@Override
		public int getItemCount () {
			return scheduleEvents.size();
		}
	}

	private class ScheduleEventViewHolder extends RecyclerView.ViewHolder {
		public ScheduleEventViewHolder (View itemView) {
			super(itemView);
		}
	}

	private void init (AttributeSet attrs, int defStyle) {
		final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ScheduleRecyclerView, defStyle, 0);
		//noinspection deprecation
		colorTheme = a.getColor(R.styleable.ScheduleRecyclerView_colorTheme, getResources().getColor(android.R.color.black));
		a.recycle();

		setClipToPadding(false);
		setAdapter(new ScheduleEventAdapter(DataStore.getScheduleEvents()));
	}
}
