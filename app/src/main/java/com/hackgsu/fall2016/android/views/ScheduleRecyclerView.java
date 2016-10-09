package com.hackgsu.fall2016.android.views;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.hackgsu.fall2016.android.DataStore;
import com.hackgsu.fall2016.android.HackGSUApplication;
import com.hackgsu.fall2016.android.R;
import com.hackgsu.fall2016.android.model.ScheduleEvent;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import java.util.ArrayList;

import static com.hackgsu.fall2016.android.views.ScheduleRecyclerView.ScheduleEventViewHolder.NOW_VIEW_TYPE;
import static com.hackgsu.fall2016.android.views.ScheduleRecyclerView.ScheduleEventViewHolder.STANDARD_VIEW_TYPE;

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
		@Override
		public ScheduleEventViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
			if (viewType == NOW_VIEW_TYPE) { return new ScheduleEventViewHolder(View.inflate(getContext(), R.layout.now_layout, null), viewType); }
			else { return new ScheduleEventViewHolder(View.inflate(getContext(), R.layout.schedule_event_card, null), viewType); }
		}

		@Override
		public void onBindViewHolder (ScheduleEventViewHolder holder, int position) {
			int indexOfNowRow = getIndexOfNowRow();
			if (position != indexOfNowRow) {
				if (position > indexOfNowRow) { position--; }

				holder.loadEvent(DataStore.getScheduleEvents().get(position));
			}
		}

		@Override
		public int getItemViewType (int position) {
			return position == getIndexOfNowRow() ? NOW_VIEW_TYPE : STANDARD_VIEW_TYPE;
		}

		@Override
		public int getItemCount () {
			return DataStore.getScheduleEvents().size() + 1;
		}

		public int getIndexOfNowRow () {
			ArrayList<ScheduleEvent> scheduleEvents = DataStore.getScheduleEvents();
			int                      i;
			for (i = 0; i < scheduleEvents.size(); i++) {
				if (scheduleEvents.get(i).getTimestamp().toDateTime().isAfter(System.currentTimeMillis())) { return i; }
				//				if (scheduleEvents.get(i).getTimestamp().toDateTime().isAfter(HackGSUApplication.getDateTimeOfHackathon(1, 14, 0).toDateTime().getMillis())) { return i; }
			}
			return i;
		}
	}

	public class ScheduleEventViewHolder extends RecyclerView.ViewHolder {
		static final int NOW_VIEW_TYPE      = 1;
		static final int STANDARD_VIEW_TYPE = 0;
		private TextView        description;
		private AppCompatButton openBtn;
		private ScheduleEvent   scheduleEvent;
		private AppCompatButton shareBtn;
		private TextView        subtitle;
		private TextView        title;

		ScheduleEventViewHolder (View itemView, int viewType) {
			super(itemView);

			if (viewType == STANDARD_VIEW_TYPE) {
				title = (TextView) itemView.findViewById(R.id.event_title);
				subtitle = (TextView) itemView.findViewById(R.id.event_subtitle);
				description = (TextView) itemView.findViewById(R.id.event_description);
				openBtn = (AppCompatButton) itemView.findViewById(R.id.event_open_btn);
				shareBtn = (AppCompatButton) itemView.findViewById(R.id.event_share_btn);

				openBtn.setTextColor(colorTheme);
			}
			else if (viewType == NOW_VIEW_TYPE) {
				itemView.findViewById(R.id.bar_left).setBackgroundColor(colorTheme);
				itemView.findViewById(R.id.bar_right).setBackgroundColor(colorTheme);
				((TextView) itemView.findViewById(R.id.now_label)).setTextColor(colorTheme);
			}
		}

		public void loadEvent (final ScheduleEvent scheduleEvent) {
			this.scheduleEvent = scheduleEvent;

			String timeTillString = HackGSUApplication.toHumanReadableRelative(scheduleEvent.getTimestamp());

			DateTimeFormatterBuilder dateTimeFormatterBuilder = new DateTimeFormatterBuilder().appendDayOfWeekText().appendLiteral(" - ");
			DateTimeFormatter        dateTimeFormatter        = HackGSUApplication.getTimeFormatter24OrNot(getContext(), dateTimeFormatterBuilder);

			title.setText(scheduleEvent.getTimestamp().toString(dateTimeFormatter));
			description.setText(scheduleEvent.getTitle());
			subtitle.setText(timeTillString);
			shareBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick (View v) {
					Intent intent = new Intent(Intent.ACTION_SEND);
					intent.putExtra(Intent.EXTRA_TEXT, scheduleEvent.getShareText(getContext()));
					intent.setType("text/plain");
					intent = Intent.createChooser(intent, "Share this event with");
					getContext().startActivity(intent);
				}
			});

			if (scheduleEvent.getUrl() == null) {
				openBtn.setVisibility(View.GONE);
			}
			else {
				openBtn.setVisibility(View.VISIBLE);
				openBtn.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick (View v) {
						HackGSUApplication.openWebUrl(getContext(), scheduleEvent.getUrl());
					}
				});
			}
		}
	}

	private void init (AttributeSet attrs, int defStyle) {
		final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ScheduleRecyclerView, defStyle, 0);
		//noinspection deprecation
		colorTheme = a.getColor(R.styleable.ScheduleRecyclerView_colorTheme, getResources().getColor(android.R.color.black));
		a.recycle();

		setClipToPadding(false);
		setLayoutManager(new LinearLayoutManager(getContext()));
		setAdapter(new ScheduleEventAdapter());
	}
}
