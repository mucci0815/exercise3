package com.example.muc13_03_bachnigsch;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.FrameLayout;

import com.example.muc13_03_bachnigsch.AR.CameraPreview;
import com.example.muc13_03_bachnigsch.AR.OverlaySurfaceView;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class ARActivity extends Activity implements SensorEventListener {

	private static final String TAG = "ARActivity";

	private Camera mCamera = null;
	private CameraPreview mPreview = null;
	private OverlaySurfaceView mOverlaySurface = null;
	private SensorManager mSensorManager;
	private Sensor mRotationVectorSensor;
	private final float[] mRotationMatrix = new float[16];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ar);

		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mRotationVectorSensor = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ar, menu);
		return true;
	}

	@Override
	protected void onPause() {
		super.onPause();

		mSensorManager.unregisterListener(this);

		// remove and delete camera preview
		if (mPreview != null) {
			FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
			preview.removeView(mPreview);
			mPreview = null;
		}

		// release Camera
		if (mCamera != null) {
			mCamera.release();
			mCamera = null;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		mSensorManager.registerListener(this, mRotationVectorSensor,
				SensorManager.SENSOR_DELAY_NORMAL);

		if (checkCameraHardware(getApplicationContext()) && mCamera == null) {
			// get instance of Camera
			mCamera = getCameraInstance();

			// create preview and set it as content for activity
			mPreview = new CameraPreview(this, mCamera);
			FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
			preview.addView(mPreview);

			mOverlaySurface = new OverlaySurfaceView(this);

			preview.addView(mOverlaySurface);
		}
	}

	/** Check if device has camera */
	private boolean checkCameraHardware(Context context) {
		if (context.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA)) {
			return true;
		} else {
			return false;
		}
	}

	/** get instance of Camera object */
	public static Camera getCameraInstance() {
		Camera c = null;
		try {
			c = Camera.open();
		} catch (Exception e) {
			Log.e(TAG, "Camera is NOT available");
		}
		return c;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// we received a sensor event. it is a good practice to check
		// that we received the proper event
		if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
			// convert the rotation-vector to a 4x4 matrix. the matrix
			// is interpreted by Open GL as the inverse of the
			// rotation-vector, which is what we want.
			SensorManager.getRotationMatrixFromVector(mRotationMatrix,
					event.values);
		}

		float[] v = new float[3];

		v = SensorManager.getOrientation(mRotationMatrix, v);
		
		// v[0] gibt azimuth also winkel zum Nordpol an
		// v[1] gibt "vertikalen Kippwinkel" an und sollte im Optimalfall 0 sein
		// v[2] gibt in landscape den "horizontalen kippwinkel" des handys an => sollte also bei pi/2 liegen

		Log.i(TAG, "v[0]=" + v[0] + "; v[1]=" + v[1] + "; v[2]=" + v[2]);

		mOverlaySurface.invalidate();
	}

}
