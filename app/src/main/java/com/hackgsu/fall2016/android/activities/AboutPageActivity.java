package com.hackgsu.fall2016.android.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import com.hackgsu.fall2016.android.R;

public class AboutPageActivity extends AppCompatActivity {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about_page);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		setTitle("About us");
		ActionBar supportActionBar = getSupportActionBar();
		if (supportActionBar != null) { supportActionBar.setSubtitle("The developer team"); }
		this.getSupportActionBar().setSubtitle("About");
	}
}

