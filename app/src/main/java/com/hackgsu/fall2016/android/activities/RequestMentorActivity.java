package com.hackgsu.fall2016.android.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import com.hackgsu.fall2016.android.R;

public class RequestMentorActivity extends AppCompatActivity {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_request_mentor);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { getWindow().setStatusBarColor(getResources().getColor(R.color.mentorsPrimary)); }

		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick (View view) {
				Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
			}
		});
	}
}
