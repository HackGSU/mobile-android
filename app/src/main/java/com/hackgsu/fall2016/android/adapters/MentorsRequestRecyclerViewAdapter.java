package com.hackgsu.fall2016.android.adapters;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.hackgsu.fall2016.android.HackGSUApplication;
import com.hackgsu.fall2016.android.R;
import com.hackgsu.fall2016.android.controllers.MentorsController;
import com.hackgsu.fall2016.android.fragments.MentorsFragment;
import com.hackgsu.fall2016.android.fragments.RequestMentorFragment;
import com.hackgsu.fall2016.android.model.MentorRequest;

import java.util.ArrayList;

public class MentorsRequestRecyclerViewAdapter extends RecyclerView.Adapter<MentorsRequestRecyclerViewAdapter.ViewHolder> {
	private FragmentManager          fragmentManager;
	private ArrayList<MentorRequest> mentorRequests;
	private MentorsFragment          mentorsFragment;

	public MentorsRequestRecyclerViewAdapter (FragmentManager fragmentManager, MentorsFragment mentorsFragment) {
		this.fragmentManager = fragmentManager;
		this.mentorsFragment = mentorsFragment;
		mentorRequests = new ArrayList<>();
	}

	class ViewHolder extends RecyclerView.ViewHolder {
		private final TextView description;
		private final TextView subtitle;
		private final TextView timestamp;
		private final TextView title;

		ViewHolder (View view) {
			super(view);
			title = (TextView) view.findViewById(R.id.title);
			description = (TextView) view.findViewById(R.id.description);
			timestamp = (TextView) view.findViewById(R.id.timestamp);
			subtitle = (TextView) view.findViewById(R.id.subtitle);
		}

		@Override
		public String toString () {
			return super.toString() + " '" + description.getText() + "'";
		}

		public void loadMentorRequest (final MentorRequest mentorRequest) {
			title.setText(mentorRequest.getTitle());
			boolean hasDescription = !HackGSUApplication.isNullOrEmpty(mentorRequest.getDescription());
			description.setText(hasDescription ? mentorRequest.getDescription() : "(No description)");
			timestamp.setText(HackGSUApplication.toHumanReadableRelative(mentorRequest.getTimestampDateTime(), true));
			subtitle.setText(mentorRequest.getSubtitle());

			if (hasDescription) { description.setTypeface(Typeface.DEFAULT); }
			else { description.setTypeface(Typeface.MONOSPACE); }

			this.itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick (View v) {
					RequestMentorFragment requestMentorFragment = new RequestMentorFragment();
					requestMentorFragment.setTargetFragment(mentorsFragment, 0);
					Bundle args = new Bundle();
					args.putSerializable(MentorsController.MENTOR_REQUESTS_KEY, mentorRequest);
					requestMentorFragment.setArguments(args);
					requestMentorFragment.show(fragmentManager, requestMentorFragment.getTag());
				}
			});
		}
	}

	@Override
	public ViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mentor_request_card, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder (final ViewHolder holder, int position) {
		holder.loadMentorRequest(mentorRequests.get(position));
	}

	@Override
	public int getItemCount () {
		return mentorRequests.size();
	}

	public void setMentorRequests (ArrayList<MentorRequest> mentorRequests) {
		this.mentorRequests = mentorRequests;
	}
}
