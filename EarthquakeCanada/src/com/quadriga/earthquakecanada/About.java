package com.quadriga.earthquakecanada;

import android.os.Bundle;
import android.webkit.WebView;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class About extends SherlockFragmentActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		WebView myBrowser = (WebView) findViewById(R.id.my_webview);
		myBrowser.loadUrl("file:///android_asset/about.html");
	}
}
