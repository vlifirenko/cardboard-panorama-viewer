package ru.cplx.glasses.ui;

import org.androidannotations.annotations.EActivity;

import rajawali.vr.RajawaliVRActivity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

@EActivity
public class VRActivity extends RajawaliVRActivity {
	private VRRenderer mRenderer;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN
						| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_FULLSCREEN
						| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		super.onCreate(savedInstanceState);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		mRenderer = new VRRenderer(this);
		mRenderer.setSurfaceView(mSurfaceView);
		setRenderer(mRenderer);
	}
}
