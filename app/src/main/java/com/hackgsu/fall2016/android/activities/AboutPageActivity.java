package com.hackgsu.fall2016.android.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import com.hackgsu.fall2016.android.HackGSUApplication;
import com.hackgsu.fall2016.android.R;

public class AboutPageActivity extends AppCompatActivity {
	@Override protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about_page);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		setTitle("About us");
		ActionBar supportActionBar = getSupportActionBar();
		if (supportActionBar != null) { supportActionBar.setSubtitle("The developer team"); }
		this.getSupportActionBar().setSubtitle("About");

		SwitchCompat devModeSwitch = (SwitchCompat) findViewById(R.id.dev_switch);
		devModeSwitch.setVisibility(getResources().getBoolean(R.bool.isAdmin) ? View.VISIBLE : View.GONE);
		devModeSwitch.setChecked(HackGSUApplication.isInDevMode(this));
		devModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override public void onCheckedChanged (CompoundButton buttonView, boolean isChecked) {
				HackGSUApplication.setIsInDevMode(AboutPageActivity.this, isChecked);
				HackGSUApplication.delayRunnableOnUI(250, new Runnable() {
					@Override public void run () {
						HackGSUApplication.refreshAnnouncements(getApplicationContext());
					}
				});
			}
		});
	}
}

