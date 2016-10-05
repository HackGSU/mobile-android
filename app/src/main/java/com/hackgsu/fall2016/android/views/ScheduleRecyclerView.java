package com.hackgsu.fall2016.android.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.design.widget.Snackbar;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.hackgsu.fall2016.android.DataStore;
import com.hackgsu.fall2016.android.HackGSUApplication;
import com.hackgsu.fall2016.android.R;
import com.hackgsu.fall2016.android.model.ScheduleEvent;

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
		@Override
		public ScheduleEventViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
			return new ScheduleEventViewHolder(View.inflate(getContext(), R.layout.schedule_event_card, null));
		}

		@Override
		public void onBindViewHolder (ScheduleEventViewHolder holder, int position) {
			holder.loadEvent(DataStore.getScheduleEvents().get(position));
		}

		@Override
		public int getItemCount () {
			return DataStore.getScheduleEvents().size();
		}
	}

	private class ScheduleEventViewHolder extends RecyclerView.ViewHolder {
		private final ImageButton     bookmarkBtn;
		private final TextView        description;
		private final ImageView       icon;
		private final AppCompatButton openBtn;
		private final AppCompatButton shareBtn;
		private final TextView        subtitle;
		private final TextView        title;
		private       ScheduleEvent   scheduleEvent;

		public ScheduleEventViewHolder (View itemView) {
			super(itemView);

			icon = (ImageView) itemView.findViewById(R.id.icon_imageview);
			title = (TextView) itemView.findViewById(R.id.event_title);
			subtitle = (TextView) itemView.findViewById(R.id.event_subtitle);
			description = (TextView) itemView.findViewById(R.id.event_description);
			openBtn = (AppCompatButton) itemView.findViewById(R.id.event_open_btn);
			shareBtn = (AppCompatButton) itemView.findViewById(R.id.event_share_btn);
			bookmarkBtn = (ImageButton) itemView.findViewById(R.id.event_bookmark_btn);

			//noinspection deprecation
			Drawable drawable = DrawableCompat.wrap(getResources().getDrawable(R.drawable.circle));
			DrawableCompat.setTint(drawable, colorTheme);
			icon.setBackground(drawable);
			openBtn.setTextColor(colorTheme);
		}

		public void loadEvent (final ScheduleEvent scheduleEvent) {
			this.scheduleEvent = scheduleEvent;

			String timeTillString = HackGSUApplication.toHumanReadableRelative(scheduleEvent.getTimestamp());

			title.setText(scheduleEvent.getTitle());
			description.setText(scheduleEvent.getDescription());
			subtitle.setText(timeTillString);
			icon.setImageResource(scheduleEvent.getIcon() == 0 ? R.drawable.ic_schedule : scheduleEvent.getIcon());
			bookmarkBtn.setImageResource(scheduleEvent.isBookmarked() ? R.drawable.ic_bookmarked : R.drawable.ic_not_bookmarked);
			bookmarkBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick (View v) {
					bookmarkBtnClicked();
				}
			});

			if (scheduleEvent.getDescription() == null || scheduleEvent.getDescription().length() == 0) {
				description.setVisibility(View.GONE);
			}
			else {
				description.setVisibility(View.VISIBLE);
			}

			if (scheduleEvent.getAction() == null) {
				openBtn.setVisibility(View.GONE);
			}
			else {
				openBtn.setVisibility(View.VISIBLE);
				openBtn.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick (View v) {
						scheduleEvent.getAction().run();
					}
				});
			}
		}

		private void bookmarkBtnClicked () {
			boolean isBookmarked = !scheduleEvent.isBookmarked();
			scheduleEvent.setIsBookmarked(isBookmarked);
			bookmarkBtn.setImageResource(isBookmarked ? R.drawable.ic_bookmarked : R.drawable.ic_not_bookmarked);
			Snackbar.make(itemView, isBookmarked ? R.string.bookmarked_event : R.string.unbookmarked_event, Snackbar.LENGTH_SHORT)
					.setAction("Undo", new OnClickListener() {
						@Override
						public void onClick (View v) {
							bookmarkBtnClicked();
						}
					})
					.show();
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
