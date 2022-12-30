package ru.cplx.glasses.ui;

import javax.microedition.khronos.opengles.GL10;

import rajawali.Camera;
import rajawali.materials.Material;
import rajawali.materials.textures.ATexture.TextureException;
import rajawali.math.Matrix4;
import rajawali.math.Quaternion;
import rajawali.math.vector.Vector3.Axis;
import rajawali.primitives.ScreenQuad;
import rajawali.renderer.RajawaliRenderer;
import rajawali.renderer.RenderTarget;
import rajawali.scene.RajawaliScene;
import ru.cplx.glasses.helpers.RendererHelper;
import android.content.Context;
import android.opengl.GLES20;

import com.google.vrtoolkit.cardboard.HeadTransform;
import com.google.vrtoolkit.cardboard.sensors.HeadTracker;

public class OneWindowRenderer extends RajawaliRenderer {
	public static final String LOG_TAG = OneWindowRenderer.class.getName();

	private RendererHelper renderHelper;

	protected HeadTracker mHeadTracker;
	protected HeadTransform mHeadTransform;
	protected float[] mHeadViewMatrix;
	protected Matrix4 mHeadViewMatrix4;
	private Quaternion mCameraOrientation;
	
	private Quaternion mScratchQuaternion1 = new Quaternion();
	private Quaternion mScratchQuaternion2 = new Quaternion();
	private final Object mCameraOrientationLock = new Object();

	private Camera mCamera;

	private RenderTarget mLeftRenderTarget;
	private RajawaliScene mUserScene;
	private RajawaliScene mSideBySideScene;
	private ScreenQuad mQuad;
	private Material mQuadMaterial;
	
	public OneWindowRenderer(Context context) {
		super(context);
		mHeadTransform = new HeadTransform();
		mHeadViewMatrix = new float[16];
		mHeadViewMatrix4 = new Matrix4();
		mCameraOrientation = new Quaternion();
	}

	public void setHeadTracker(HeadTracker headTracker) {
		mHeadTracker = headTracker;

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
		
		mCamera = new Camera();
		mCamera.setNearPlane(.01f);
		mCamera.setFieldOfView(getCurrentCamera().getFieldOfView());
		mCamera.setNearPlane(getCurrentCamera().getNearPlane());
		mCamera.setFarPlane(getCurrentCamera().getFarPlane());

		mQuadMaterial = new Material();
		mQuadMaterial.setColorInfluence(0);

		mSideBySideScene = new RajawaliScene(this);

		mQuad = new ScreenQuad();
		mQuad.setMaterial(mQuadMaterial);
		mSideBySideScene.addChild(mQuad);

		addScene(mSideBySideScene);

		mLeftRenderTarget = new RenderTarget("sbsLeftRT", mViewportWidth,
				mViewportHeight);
		mLeftRenderTarget.setFullscreen(false);

		mCamera.setProjectionMatrix(mViewportWidth, mViewportHeight);

		addRenderTarget(mLeftRenderTarget);

		try {
			mQuadMaterial.addTexture(mLeftRenderTarget.getTexture());
		} catch (TextureException e) {
			e.printStackTrace();
		}

		super.initScene();
	}

	@Override
	public void onDrawFrame(GL10 glUnused) {
		super.onDrawFrame(glUnused);
		renderHelper.onDrawFrame(mHeadViewMatrix);
	}

	@Override
	public void onRender(double deltaTime) {
		mHeadTracker.getLastHeadView(mHeadViewMatrix, 0);
		mHeadViewMatrix4.setAll(mHeadViewMatrix);

		mCameraOrientation.fromMatrix(mHeadViewMatrix4);
		mCameraOrientation.x *= -1;
		mCameraOrientation.y *= -1;
		mCameraOrientation.z *= -1;
		setCameraOrientation(mCameraOrientation);
		
		mUserScene = getCurrentScene();

		setRenderTarget(mLeftRenderTarget);
		getCurrentScene().switchCamera(mCamera);
		GLES20.glViewport(0, 0, mViewportWidth, mViewportHeight);
		mCamera.setProjectionMatrix(mViewportWidth, mViewportHeight);
		mCamera.setOrientation(mCameraOrientation);

		render(deltaTime);

		switchSceneDirect(mSideBySideScene);
		GLES20.glViewport(0, 0, mViewportWidth, mViewportHeight);

		setRenderTarget(null);

		render(deltaTime);

		switchSceneDirect(mUserScene);
		
		super.onRender(deltaTime);
	}
	
	public void setCameraOrientation(Quaternion cameraOrientation) {
		synchronized (mCameraOrientationLock) {
			mCameraOrientation.setAll(cameraOrientation);
		}
	}

	public void setSensorOrientation(float[] quaternion) {
		synchronized (mCameraOrientationLock) {
			mCameraOrientation.x = quaternion[1];
			mCameraOrientation.y = quaternion[2];
			mCameraOrientation.z = quaternion[3];
			mCameraOrientation.w = quaternion[0];

			mScratchQuaternion1.fromAngleAxis(Axis.X, -90);
			mScratchQuaternion1.multiply(mCameraOrientation);

			mScratchQuaternion2.fromAngleAxis(Axis.Z, -90);
			mScratchQuaternion1.multiply(mScratchQuaternion2);

			mCameraOrientation.setAll(mScratchQuaternion1);

		}
	}
}
