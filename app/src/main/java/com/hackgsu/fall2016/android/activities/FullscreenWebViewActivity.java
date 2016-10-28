package com.hackgsu.fall2016.android.activities;

import android.os.Bundle;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.hackgsu.fall2016.android.HackGSUApplication;
import com.hackgsu.fall2016.android.R;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenWebViewActivity extends AppCompatActivity {
	public static final String EXTRA_URL = "extra_url";
	private ContentLoadingProgressBar progressBar;
	private WebView                   webView;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_fullscreen_web_view);
		webView = (WebView) findViewById(R.id.web_view);
		progressBar = (ContentLoadingProgressBar) findViewById(R.id.progress_bar);

		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished (WebView view, String url) {
				super.onPageFinished(view, url);

				progressBar.hide();
			}
		});
		progressBar.show();

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