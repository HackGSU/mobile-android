package com.hackgsu.fall2016.android.activities;

import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import com.hackgsu.fall2016.android.HackGSUApplication;
import com.hackgsu.fall2016.android.R;
import com.hackgsu.fall2016.android.controllers.AnnouncementController;
import com.hackgsu.fall2016.android.model.Announcement;
import com.hackgsu.fall2016.android.utils.CallbackWithType;
import com.hackgsu.fall2016.android.views.AnnouncementsRecyclerView;
import org.joda.time.LocalDateTime;

public class PostNewAnnouncementActivity extends AppCompatActivity {
	Announcement.Topic[] topics = new Announcement.Topic[] { Announcement.Topic.GENERAL, Announcement.Topic.FOOD, Announcement.Topic.TECH };
	private boolean isPosting;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post_new_announcement);
		final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		final TextInputEditText newAnnouncementTitle       = (TextInputEditText) findViewById(R.id.new_announcement_title);
		final TextInputEditText newAnnouncementBody        = (TextInputEditText) findViewById(R.id.new_announcement_body);
		final SeekBar           newAnnouncementTypeSeekbar = (SeekBar) findViewById(R.id.new_announcement_type_seekbar);
		final ProgressBar       progressBar                = (ProgressBar) findViewById(R.id.progress_bar);

		final TextView             announcementPreviewTitle         = (TextView) findViewById(R.id.announcement_title);
		TextView                   announcementPreviewSubtitle      = (TextView) findViewById(R.id.announcement_subtitle);
		final TextView             announcementPreviewDescription   = (TextView) findViewById(R.id.announcement_description);
		final ImageView            announcementPreviewIcon          = (ImageView) findViewById(R.id.icon_imageview);
		final AppCompatImageButton announcementPreviewBookmarksIcon = (AppCompatImageButton) findViewById(R.id.announcement_bookmark_btn);

		announcementPreviewTitle.setText("title");
		announcementPreviewDescription.setText("Event brief desc");
		announcementPreviewIcon.setImageResource(topics[0].getIcon());
		announcementPreviewSubtitle.setText(AnnouncementsRecyclerView.getTimestampString(this, new LocalDateTime(System.currentTimeMillis())));
		newAnnouncementTitle.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged (CharSequence s, int start, int count, int after) { }

			@Override
			public void onTextChanged (CharSequence s, int start, int before, int count) {
				if (HackGSUApplication.isNullOrEmpty(s.toString())) {
					announcementPreviewTitle.setText("title");
					setErrorOnEditText(newAnnouncementTitle, "Please provide a title");
				}
				else {
					announcementPreviewTitle.setText(s);
					setErrorOnEditText(newAnnouncementTitle, null);
				}
			}

			@Override
			public void afterTextChanged (Editable s) { }
		});
		newAnnouncementBody.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged (CharSequence s, int start, int count, int after) { }

			@Override
			public void onTextChanged (CharSequence s, int start, int before, int count) {
				if (HackGSUApplication.isNullOrEmpty(s.toString())) {
					announcementPreviewDescription.setText("Event brief desc");
					setErrorOnEditText(newAnnouncementBody, "Please provide an announcement body");
				}
				else {
					announcementPreviewDescription.setText(s);
					setErrorOnEditText(newAnnouncementBody, null);
				}
			}

			@Override
			public void afterTextChanged (Editable s) { }
		});
		newAnnouncementTypeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged (SeekBar seekBar, int progress, boolean fromUser) {
				announcementPreviewIcon.setImageResource(topics[progress].getIcon());
			}

			@Override
			public void onStartTrackingTouch (SeekBar seekBar) { }

			@Override
			public void onStopTrackingTouch (SeekBar seekBar) { }
		});

		announcementPreviewTitle.setSelected(true);
		announcementPreviewSubtitle.setTypeface(Typeface.MONOSPACE);
		Drawable circleBackground         = DrawableCompat.wrap(ContextCompat.getDrawable(this, R.drawable.circle));
		int      announcementPrimaryColor = ContextCompat.getColor(this, R.color.announcementsPrimary);
		DrawableCompat.setTint(circleBackground, announcementPrimaryColor);
		announcementPreviewIcon.setBackground(circleBackground);
		announcementPreviewBookmarksIcon.setSupportBackgroundTintList(ColorStateList.valueOf(announcementPrimaryColor));

		final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick (final View view) {
				String newAnnouncementTitleText = String.valueOf(newAnnouncementTitle.getText());
				String newAnnouncementBodyText  = String.valueOf(newAnnouncementBody.getText());
				if (HackGSUApplication.isNullOrEmpty(newAnnouncementTitleText)) {
					setErrorOnEditText(newAnnouncementTitle, "Please provide a title");
				}
				else if (HackGSUApplication.isNullOrEmpty(newAnnouncementBodyText)) {
					setErrorOnEditText(newAnnouncementBody, "Please provide an announcement body");
				}
				else if (!isPosting) {
					isPosting = true;
					progressBar.setVisibility(View.VISIBLE);
					Snackbar.make(view, "Posting...", Snackbar.LENGTH_INDEFINITE).show();

					newAnnouncementTitle.setEnabled(false);
					newAnnouncementBody.setEnabled(false);
					newAnnouncementTypeSeekbar.setEnabled(false);

					Announcement announcement = new Announcement(newAnnouncementTitleText, newAnnouncementBodyText, topics[newAnnouncementTypeSeekbar.getProgress()]);
					AnnouncementController.sendOrUpdateAnnouncement(announcement, new CallbackWithType<Void>() {
						@Override
						public void onComplete (Void aVoid) {
							HackGSUApplication.refreshAnnouncements(getApplicationContext());
							PostNewAnnouncementActivity.this.finish();
						}

						@Override
						public void onError () {
							newAnnouncementTitle.setEnabled(true);
							newAnnouncementBody.setEnabled(true);
							newAnnouncementTypeSeekbar.setEnabled(true);

							Snackbar.make(view, "An error occurred... Sorry.", Snackbar.LENGTH_LONG).show();
						}
					});
				}
			}
		});
	}

	private void setErrorOnEditText (TextInputEditText editText, String error) {
		if (HackGSUApplication.isNullOrEmpty(error)) {
			error = "";
		}
		else { editText.requestFocus(); }

		if (editText.getParent() != null && editText.getParent().getParent() instanceof TextInputLayout) {
			((TextInputLayout) editText.getParent().getParent()).setErrorEnabled(true);
			((TextInputLayout) editText.getParent().getParent()).setError(error);
		}
		else { editText.setError(error); }
	}
}
