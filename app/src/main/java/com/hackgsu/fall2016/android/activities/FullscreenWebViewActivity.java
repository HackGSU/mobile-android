package com.hackgsu.fall2016.android.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import com.hackgsu.fall2016.android.HackGSUApplication;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenWebViewActivity extends AppCompatActivity {
	public static final String EXTRA_URL = "extra_url";
	private WebView webView;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		webView = new WebView(this);
		webView.getSettings().setJavaScriptEnabled(true);
		setContentView(webView);

		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.hide();
		}
	}

	@Override
	protected void onStart () {
		super.onStart();

		String url = getIntent().getStringExtra(EXTRA_URL);
		if (HackGSUApplication.isNullOrEmpty(url)) {
			HackGSUApplication.toast(this, "I had trouble loading that url..");
			finish();
		}
		else { webView.loadUrl(url); }
	}

	@Override
	protected void onPause () {
		super.onPause();

		finish();
	}
}