package ru.cplx.glasses.ui;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import ru.cplx.glasses.panoramatour.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.CheckBox;

@EActivity(R.layout.activity_start)
public class StartActivity extends Activity {
	public static final String LOG_TAG = StartActivity.class.getName();

	@ViewById
	CheckBox checkVR;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
	}

	@Click
	void start() {
		if (checkVR.isChecked())
			startActivity(new Intent(getApplicationContext(), VRActivity_.class));
		else
			startActivity(new Intent(getApplicationContext(), OneWindowActivity.class));
	}
}
