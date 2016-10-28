package com.hackgsu.fall2016.android.views;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.hackgsu.fall2016.android.DataStore;
import com.hackgsu.fall2016.android.R;
import com.hackgsu.fall2016.android.model.AboutPerson;

public class AboutRecyclerView extends ThemedEmptyStateRecyclerView {
    private AboutAdapter      adapter;
    private GridLayoutManager layoutManager;

    public AboutRecyclerView(Context context) {
        super(context);
        init(null, 0);
    }

    public AboutRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public AboutRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    public class AboutAdapter extends Adapter<AboutViewHolder> {
        @Override
        public AboutViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View cardView = View.inflate(getContext(), R.layout.about_person_card, null);
            return new AboutViewHolder(cardView);
        }

        @Override
        public void onBindViewHolder(AboutViewHolder holder, int position) {
            holder.loadPerson(DataStore.getAboutPeople().get(position));
        }

        @Override
        public int getItemCount() {
            return DataStore.getAboutPeople().size();
        }
    }

    public class AboutViewHolder extends ViewHolder {


        private final ImageView personImage;
        private final TextView personName;
        private final TextView personRole;

        AboutViewHolder(View cardView) {
            super(cardView);

            personImage = (ImageView) cardView.findViewById(R.id.person_image);
            personName = (TextView) cardView.findViewById(R.id.person_name);
            personRole = (TextView) cardView.findViewById(R.id.person_role);
        }

        public void loadPerson(AboutPerson aboutPerson) {
        personName.setText(aboutPerson.getName());
            personRole.setText(aboutPerson.getRole());
            // TODO: 10/20/16 : Do linked in stuff
            //setText(aboutPerson.getLinkedInUrl());
            personImage.setImageDrawable(ContextCompat.getDrawable(AboutRecyclerView.this.getContext(), aboutPerson.getPicture()));
        }
    }

    @Override
    public AboutAdapter getAdapter() {
        return adapter;
    }

    @Override
    public GridLayoutManager getLayoutManager () {
        return layoutManager;
    }

    @Override
    protected void init(AttributeSet attrs, int defStyle) {
        super.init(attrs, defStyle);

        setClipToPadding(false);
        setLayoutManager(layoutManager = new GridLayoutManager(getContext(), 2, LinearLayoutManager.VERTICAL, false));
        setAdapter(adapter = new AboutAdapter());
    }
}