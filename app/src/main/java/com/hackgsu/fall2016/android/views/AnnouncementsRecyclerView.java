package com.hackgsu.fall2016.android.views;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.hackgsu.fall2016.android.DataStore;
import com.hackgsu.fall2016.android.HackGSUApplication;
import com.hackgsu.fall2016.android.R;
import com.hackgsu.fall2016.android.controllers.AnnouncementController;
import com.hackgsu.fall2016.android.model.Announcement;
import com.hackgsu.fall2016.android.utils.SmoothLinearLayoutManager;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import java.util.Locale;

public class AnnouncementsRecyclerView extends ThemedEmptyStateRecyclerView {
	private AnnouncementEventAdapter  adapter;
	private SmoothLinearLayoutManager layoutManager;
	private boolean                   showOnlyBookmarked;

	public AnnouncementsRecyclerView (Context context) {
		super(context);
		init(null, 0);
	}

	public AnnouncementsRecyclerView (Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs, 0);
	}

	public AnnouncementsRecyclerView (Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs, defStyle);
	}

	public class AnnouncementEventAdapter extends Adapter<AnnouncementsEventViewHolder> {
		@Override
		public AnnouncementsEventViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
			View inflate = View.inflate(getContext(), R.layout.announcement_card, null);
			inflate.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
			return new AnnouncementsEventViewHolder(inflate);
		}

		@Override
		public void onBindViewHolder (AnnouncementsEventViewHolder holder, int position) {
			holder.loadAnnouncement(DataStore.getAnnouncements(shouldShowOnlyBookmarked()).get(position));
		}

		@Override
		public int getItemCount () {
			return DataStore.getAnnouncements(shouldShowOnlyBookmarked()).size();
		}
	}

	public class AnnouncementsEventViewHolder extends ViewHolder {
		private AppCompatImageButton bookmarkBtn;
		private TextView             description;
		private ImageView            iconImageView;
		private AppCompatButton      likeBtn;
		private AppCompatButton      shareBtn;
		private TextView             subtitle;
		private TextView             title;

		AnnouncementsEventViewHolder (View itemView) {
			super(itemView);
			title = (TextView) itemView.findViewById(R.id.announcement_title);
			subtitle = (TextView) itemView.findViewById(R.id.announcement_subtitle);
			description = (TextView) itemView.findViewById(R.id.announcement_description);
			bookmarkBtn = (AppCompatImageButton) itemView.findViewById(R.id.announcement_bookmark_btn);
			likeBtn = (AppCompatButton) itemView.findViewById(R.id.announcement_like_btn);
			shareBtn = (AppCompatButton) itemView.findViewById(R.id.announcement_share_btn);
			iconImageView = (ImageView) itemView.findViewById(R.id.icon_imageview);

			likeBtn.setTextColor(getColorTheme());
		}

		public void loadAnnouncement (final Announcement announcement) {
			String timeTillString = HackGSUApplication.toHumanReadableRelative(announcement.getTimestampDateTime());

			DateTimeFormatterBuilder dateTimeFormatterBuilder = new DateTimeFormatterBuilder().appendDayOfWeekText().appendLiteral(" - ");
			DateTimeFormatter        dateTimeFormatter        = HackGSUApplication.getTimeFormatter24OrNot(getContext(), dateTimeFormatterBuilder);

			title.setText(announcement.getTitle());
			description.setText(announcement.getBodyText());
			subtitle.setTypeface(Typeface.MONOSPACE);
			subtitle.setText(String.format("%s | %s", timeTillString, announcement.getTimestampDateTime().toString(dateTimeFormatter)));

			Drawable circleBackground = DrawableCompat.wrap(ContextCompat.getDrawable(getContext(), R.drawable.circle));
			DrawableCompat.setTint(circleBackground, getColorTheme());
			iconImageView.setBackground(circleBackground);

			iconImageView.setImageResource(announcement.getTopicEnum().getIcon());

			bookmarkBtn.setSupportBackgroundTintList(ColorStateList.valueOf(getColorTheme()));
			bookmarkBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick (View v) {
					AnnouncementController.toggleBookmark(getContext(), announcement);
					Snackbar.make(v, String.format("Announcement %sbookmarked", announcement.isBookmarkedByMe() ? "" : "un"), Snackbar.LENGTH_LONG)
							.show();
					updateBookmarkBtnIcon(announcement);
				}
			});
			shareBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick (View v) {
					Intent intent = new Intent(Intent.ACTION_SEND);
					intent.putExtra(Intent.EXTRA_TEXT, announcement.getShareText(getContext()));
					intent.setType("text/plain");
					intent = Intent.createChooser(intent, "Share this event with");
					getContext().startActivity(intent);
				}
			});
			likeBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick (View v) {
					AnnouncementController.toggleLiked(getContext(), announcement);
					updateLikesOnButtonLabel(announcement);
				}
			});

			updateBookmarkBtnIcon(announcement);
			updateLikesOnButtonLabel(announcement);
		}

		private void updateBookmarkBtnIcon (Announcement announcement) { bookmarkBtn.setBackgroundResource(announcement.isBookmarkedByMe() ? R.drawable.ic_bookmarked : R.drawable.ic_not_bookmarked); }

		private void updateLikesOnButtonLabel (Announcement announcement) {
			likeBtn.setText(String.format(Locale.getDefault(), "%s (%d)", announcement.isLikedByMe() ? "Unlike" : "Like", announcement.getLikes()));
		}
	}

	@Override
	public AnnouncementEventAdapter getAdapter () {
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
		adapter = new AnnouncementEventAdapter();

		setClipToPadding(false);
		setLayoutManager(layoutManager);
		setAdapter(adapter);
	}

	public boolean shouldShowOnlyBookmarked () {
		return showOnlyBookmarked;
	}

	public void setShowOnlyBookmarked (boolean showOnlyBookmarked) {
		this.showOnlyBookmarked = showOnlyBookmarked;
	}
}
