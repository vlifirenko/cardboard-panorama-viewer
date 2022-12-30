package ru.cplx.glasses.helpers;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import rajawali.materials.Material;
import rajawali.materials.methods.DiffuseMethod;
import rajawali.materials.textures.ATexture.TextureException;
import rajawali.materials.textures.AnimatedGIFTexture;
import rajawali.materials.textures.Texture;
import rajawali.math.Quaternion;
import rajawali.math.vector.Vector3;
import rajawali.math.vector.Vector3.Axis;
import rajawali.primitives.Sphere;
import rajawali.scene.RajawaliScene;
import ru.cplx.glasses.Marker;
import ru.cplx.glasses.panoramatour.R;
import ru.cplx.glasses.parser.JsonParser;
import ru.cplx.glasses.vo.MarkerVo;
import ru.cplx.glasses.vo.SceneVo;
import android.content.Context;

public class RendererHelper {
	public static final String LOG_TAG = RendererHelper.class.getName();

	private Sphere roomObj;

	private List<SceneVo> scenes;
	private SceneVo currentScene;

	private AnimatedGIFTexture mGifTexture;
	private Material throbberMaterial;
	private Material circleMaterial;

	private RajawaliScene rajawaliScene;

	private List<Marker> markers = new ArrayList<Marker>();

	private Context context;

	private Marker lookingMarker;

	private boolean isChangingScene;

	private ScheduledThreadPoolExecutor executor;

	public RendererHelper(RajawaliScene rajawaliScene, Context context) {
		this.rajawaliScene = rajawaliScene;
		this.context = context;
	}

	public void initScene() throws TextureException {

		try {
			InputStream is = context.getAssets().open("scenes.json");
			JsonParser jsonParser = new JsonParser();
			scenes = jsonParser.readJsonStream(is);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		currentScene = scenes.get(0);

		roomObj = new Sphere(-20, 18, 18);
		roomObj.setPosition(0, 0, 0);
		roomObj.setRotX(roomObj.getRotX() + 180);
		rajawaliScene.addChild(roomObj);

		throbberMaterial = new Material();
		throbberMaterial.setColorInfluence(0);
		mGifTexture = new AnimatedGIFTexture("animGif", R.drawable.throbber);
		throbberMaterial.addTexture(mGifTexture);
		mGifTexture.rewind();

		circleMaterial = new Material();
		circleMaterial.setDiffuseMethod(new DiffuseMethod.Lambert());
		circleMaterial.setColorInfluence(0);
		circleMaterial.enableLighting(true);
		circleMaterial.addTexture(new Texture("circle", R.drawable.circle));

		initRoom();
		initMarkers();
	}

	public void onDrawFrame(float[] mHeadViewMatrix) {
		if (mGifTexture != null) {
			try {
				mGifTexture.update();
			} catch (TextureException e) {
				e.printStackTrace();
			}
		}

		if (isChangingScene)
			return;

		if (mHeadViewMatrix == null)
			return;

		Vector3 vect = getForwardVector(mHeadViewMatrix);
		boolean isLook = false;
		for (int i = 0; i < markers.size(); i++) {
			if (isLooking(markers.get(i).getPosition(), vect, markers.get(i).mR * 1.3f)) {
				if (lookingMarker != null
						&& lookingMarker.mId == markers.get(i).mId)
					return;

				markers.get(i).setMaterial(throbberMaterial);
				mGifTexture.rewind();
				lookingMarker = markers.get(i);

				if (executor != null)
					executor.shutdownNow();

				this.executor = new ScheduledThreadPoolExecutor(1);

				executor.schedule(new Runnable() {
					@Override
					public void run() {
						executor.shutdownNow();
						changeScene();
					}
				}, 5, TimeUnit.SECONDS);

				isLook = true;
			}
		}

		if (!isLook && lookingMarker != null) {
			executor.shutdownNow();
			lookingMarker.setMaterial(circleMaterial);
			lookingMarker = null;
		}
	}

	public Vector3 getForwardVector(float[] mHeadView) {
		return new Vector3((-mHeadView[(8 + 0)]), (-mHeadView[(8 + 1)]),
				(-mHeadView[(8 + 2)]));
	}

	// v2 - camera
	private boolean isLooking(Vector3 v1, Vector3 v2, double delta) {
		Vector3 lv1 = v1.clone();
		Vector3 lv2 = v2.clone();
		lv2.normalize();
		lv2.multiply(lv1.length());
		double resLength = lv1.subtract(lv2).length();
		return delta > resLength;
	}

	private void initRoom() throws TextureException {
		Texture texture = new Texture("tex", context.getResources()
				.getIdentifier(currentScene.img, "drawable",
						context.getPackageName()));
		Material roomMaterial = new Material();
		roomMaterial.setDiffuseMethod(new DiffuseMethod.Lambert());
		roomMaterial.setColorInfluence(0);
		roomMaterial.enableLighting(true);
		roomMaterial.addTexture(texture);
		roomObj.setMaterial(roomMaterial);
		roomObj.setRotation(new Vector3(0, currentScene.rotation, 0));
		lookingMarker = null;
	}

	private void initMarkers() {
		if (markers.size() > 0) {
			for (Marker m : markers) {
				rajawaliScene.removeChild(m);
			}
		}
		markers.clear();
		int i = 0;
		for (MarkerVo markerVo : currentScene.markers) {
			Marker m = new Marker(i);
			m.setMaterial(circleMaterial);
			Quaternion q1 = new Quaternion();
			Quaternion q2 = new Quaternion();
			q1.fromAngleAxis(Axis.Y, markerVo.angles[0]);
			q2.fromAngleAxis(Axis.X, markerVo.angles[1]);
			m.setOrientation(q2.multiply(q1));
			rajawaliScene.addChild(m);
			m.setScene(markerVo.scene);
			markers.add(m);
			i++;
		}
		isChangingScene = false;
	}

	private void changeScene() {
		isChangingScene = true;
		for (SceneVo sceneVo : scenes) {
			if (sceneVo.id.equals(lookingMarker.getScene())) {
				currentScene = sceneVo;
				break;
			}
		}

		try {
			initRoom();
		} catch (TextureException e) {
			e.printStackTrace();
		}
		initMarkers();
	}
}
