package com.hackgsu.fall2016.android.views;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.hackgsu.fall2016.android.DataStore;
import com.hackgsu.fall2016.android.HackGSUApplication;
import com.hackgsu.fall2016.android.R;
import com.hackgsu.fall2016.android.model.ScheduleEvent;
import com.hackgsu.fall2016.android.utils.SmoothLinearLayoutManager;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import java.util.ArrayList;

import static com.hackgsu.fall2016.android.views.ScheduleRecyclerView.ScheduleEventViewHolder.NOW_VIEW_TYPE;
import static com.hackgsu.fall2016.android.views.ScheduleRecyclerView.ScheduleEventViewHolder.STANDARD_VIEW_TYPE;

public class ScheduleRecyclerView extends ThemedEmptyStateRecyclerView {
	private ScheduleEventAdapter      adapter;
	private SmoothLinearLayoutManager layoutManager;

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

	public class ScheduleEventAdapter extends RecyclerView.Adapter<ScheduleEventViewHolder> {
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

				openBtn.setTextColor(getColorTheme());
			}
			else if (viewType == NOW_VIEW_TYPE) {
				itemView.findViewById(R.id.bar_left).setBackgroundColor(getColorTheme());
				itemView.findViewById(R.id.bar_right).setBackgroundColor(getColorTheme());
				((TextView) itemView.findViewById(R.id.now_label)).setTextColor(getColorTheme());
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

	@Override
	public ScheduleEventAdapter getAdapter () {
		return adapter;
	}

	@Override
	public SmoothLinearLayoutManager getLayoutManager () {
		return layoutManager;
	}

	@Override
	protected void init (AttributeSet attrs, int defStyle) {
		super.init(attrs, defStyle);

		layoutManager = new SmoothLinearLayoutManager(getContext());
		adapter = new ScheduleEventAdapter();

		setClipToPadding(false);
		setLayoutManager(layoutManager);
		setAdapter(adapter);
	}

	public void showNowRow () {
		HackGSUApplication.delayRunnableOnUI(250, new Runnable() {
			@Override
			public void run () {
				layoutManager.smoothScrollToPosition(ScheduleRecyclerView.this, null, adapter.getIndexOfNowRow());
			}
		});
	}
}