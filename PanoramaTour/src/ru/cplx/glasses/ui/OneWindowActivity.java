package ru.cplx.glasses.ui;

import rajawali.RajawaliActivity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.vrtoolkit.cardboard.sensors.HeadTracker;

public class OneWindowActivity extends RajawaliActivity {
	private OneWindowRenderer mRenderer;
	
	private HeadTracker mHeadTracker;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN
						| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_FULLSCREEN
						| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		super.onCreate(savedInstanceState);
		
		mSurfaceView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
	            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
	            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
	            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
	            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
	            | View.SYSTEM_UI_FLAG_IMMERSIVE);
		mHeadTracker = new HeadTracker(this);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		mRenderer = new OneWindowRenderer(this);
		mRenderer.setSurfaceView(mSurfaceView);
		mRenderer.setHeadTracker(mHeadTracker);
		setRenderer(mRenderer);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		mHeadTracker.startTracking();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		mHeadTracker.stopTracking();
	}
}
