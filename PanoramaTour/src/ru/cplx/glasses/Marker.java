package ru.cplx.glasses;

import rajawali.Object3D;
import rajawali.math.Quaternion;
import rajawali.math.vector.Vector3;

public class Marker extends Object3D {

	protected float mDistance;

	protected String scene;
	public long mId;
	public float mR;

	private Vector3 position;

	/**
	 * Create a marker primitive.
	 */
	public Marker(long id) {
		this(id, 3f, .4f);
	}

	/**
	 * Creates a marker primitive.
	 * 
	 * @param id
	 *            id of the marker
	 * @param distance
	 *            distance between scene center and the marker
	 * @param r
	 *            the marker edge size
	 */
	public Marker(long id, float distance, float r) {
		super();
		mId = id;
		mDistance = distance;
		mR = r;
		position = new Vector3(0, 0, distance);
		setTransparent(true);
		init();
	}

	public String getScene() {
		return scene;
	}

	public void setScene(String scene) {
		this.scene = scene;
	}

	private void init() {
		float[] vertices = { -mR / 2f, -mR / 2f, mDistance, // 0 0
				-mR / 2f, mR / 2f, mDistance, // 0 1
				mR / 2f, mR / 2f, mDistance, // 1 1
				mR / 2f, -mR / 2f, mDistance // 1 0
		};

		float[] textureCoords = { 1, 1, 1, 0, 0, 0, 0, 1 };

		int[] indices = { 2, 3, 0, 2, 0, 1 };

		setData(vertices, null, textureCoords, null, indices);
	}

	@Override
	public void setOrientation(Quaternion quat) {
		super.setOrientation(quat);
		position.transform(quat);
	}
	
	@Override
	public Vector3 getPosition() {
		return position;
	}

	@Override
	public String toString() {
		return String.valueOf(mId);
	}
}