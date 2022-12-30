package ru.cplx.glasses.panoramatour.test;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Test;

import android.util.Log;
import rajawali.math.Quaternion;
import rajawali.math.vector.Vector3;
import rajawali.math.vector.Vector3.Axis;
import ru.cplx.glasses.Marker;

public class MarkerTest extends TestCase {

	@Test
	public void test() {
		// need comment in ru.cplx.glasses.Marker line 63
		Marker marker = new Marker(0);
		Quaternion quat = new Quaternion();
		quat.fromAngleAxis(Axis.Y, 90);
		marker.setOrientation(quat);
		Vector3 markerPosition = marker.getPosition();
		Vector3 checkPosition = new Vector3(3f, 0, 0);
		Assert.assertEquals(markerPosition.x, checkPosition.x, 0.001f);
		Assert.assertEquals(markerPosition.y, checkPosition.y, 0.001f);
		Assert.assertEquals(markerPosition.z, checkPosition.z, 0.001f);
	}	

}
