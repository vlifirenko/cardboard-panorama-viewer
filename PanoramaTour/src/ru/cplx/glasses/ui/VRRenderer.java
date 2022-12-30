package ru.cplx.glasses.ui;

import javax.microedition.khronos.opengles.GL10;

import rajawali.vr.RajawaliVRRenderer;
import ru.cplx.glasses.helpers.RendererHelper;
import android.content.Context;

public class VRRenderer extends RajawaliVRRenderer {
	public static final String LOG_TAG = VRRenderer.class.getName();

	private RendererHelper renderHelper;

	public VRRenderer(Context context) {
		super(context);
	}

	@Override
	public void initScene() {
		setFrameRate(60);
		renderHelper = new RendererHelper(getCurrentScene(), getContext());
		try {
			renderHelper.initScene();
		} catch (Exception e) {
			e.printStackTrace();
		}

		super.initScene();
	}

	@Override
	public void onDrawFrame(GL10 glUnused) {
		super.onDrawFrame(glUnused);
		renderHelper.onDrawFrame(mHeadViewMatrix);
	}
}
