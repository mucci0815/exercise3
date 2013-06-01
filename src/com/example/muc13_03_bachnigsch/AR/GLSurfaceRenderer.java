package com.example.muc13_03_bachnigsch.AR;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.annotation.TargetApi;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Build;
import android.util.Log;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class GLSurfaceRenderer implements GLSurfaceView.Renderer,
		SensorEventListener {

	private SensorManager mSensorManager;
	private Sensor mRotationVectorSensor;
	private Square mSquare;
	
	private long last_timestamp = 0;
	float next = -1.0f;

	private final float[] mRotationMatrix = new float[16];

	public GLSurfaceRenderer(SensorManager sensorManager) {
		mSensorManager = sensorManager;
		// get rotation-vector sensor
		mRotationVectorSensor = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

		mSquare = new Square();

		// initialize the rotation matrix to identity
		mRotationMatrix[0] = 1;
		mRotationMatrix[4] = 1;
		mRotationMatrix[8] = 1;
		mRotationMatrix[12] = 1;
	}

	public void start() {
		// enable our sensor when the activity is resumed, ask for
		// 10 ms updates.
		mSensorManager.registerListener(this, mRotationVectorSensor, 10000);
	}

	public void stop() {
		// turn off sensor
		mSensorManager.unregisterListener(this);
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
			float[] temp = new float[3];
			temp[0] = event.values[0];
			temp[1] = event.values[1];
			temp[2] = event.values[2];
			SensorManager.getRotationMatrixFromVector(mRotationMatrix,
					temp);
		}

	}

	@Override
	public void onDrawFrame(GL10 gl) {
		// clear screen
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		// set-up modelview matrix
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
//		gl.glTranslatef(0, 0, -1.0f);
		
//		if(System.currentTimeMillis() - last_timestamp > 1000) {
//			last_timestamp = System.currentTimeMillis();
//			
//			next += 0.02f;
//			Log.i("Turn", "next= "+next);
//			float[] rot = { 0f, next, 0f };
//			SensorManager.getRotationMatrixFromVector(mRotationMatrix,
//					rot);
//			
//		}
		
		gl.glMultMatrixf(mRotationMatrix, 0);
//		
//		float[] orientation = new float[3];
//		SensorManager.getOrientation(mRotationMatrix, orientation);
//		
//		float pi = (float) Math.PI;
//		float rad2deg = 180/pi;
//
//		// Get the pitch, yaw and roll from the sensor. 
//
//		float yaw = orientation[0] * rad2deg;
//		float pitch = orientation[1] * rad2deg;
//		float roll = orientation[2] * rad2deg;
//
//		// Convert pitch, yaw and roll to a vector
//
//		float x = (float)(Math.cos( yaw ) * Math.cos( pitch ));
//		float y = (float)(Math.sin( yaw ) * Math.cos( pitch ));
//		float z = (float)(Math.sin( pitch ));
//
//		GLU.gluLookAt( gl, 0.0f, 0.0f, 0.0f, x, y, z, 0.0f, 1.0f, 0.0f ); 

		// draw
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

		mSquare.draw(gl);

	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// set view-port
		gl.glViewport(0, 0, width, height);
		// set projection matrix
		float ratio = (float) width / height;
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glFrustumf(-ratio, ratio, -1, 1, 1, 10);

	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// dither is enabled by default, we don't need it
		gl.glDisable(GL10.GL_DITHER);

	}

	class Square {
		private FloatBuffer mVertexBuffer;
		private FloatBuffer mColorBuffer;
		private ByteBuffer mIndexBuffer;

		public Square() {
			final float vertices[] = { -1.0f, 1.0f, -5f, -1.0f, -1.0f, -5f,
					1.0f, -1.0f, -5f, 1.0f, 1.0f, -5f };

			// red for each point
			final float colors[] = { 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0,
					0, 1 };

			final byte indices[] = { 0, 1, 1, 2, 2, 3, 3, 0 };

			ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
			vbb.order(ByteOrder.nativeOrder());
			mVertexBuffer = vbb.asFloatBuffer();
			mVertexBuffer.put(vertices);
			mVertexBuffer.position(0);

			ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length * 4);
			cbb.order(ByteOrder.nativeOrder());
			mColorBuffer = cbb.asFloatBuffer();
			mColorBuffer.put(colors);
			mColorBuffer.position(0);

			mIndexBuffer = ByteBuffer.allocateDirect(indices.length);
			mIndexBuffer.put(indices);
			mIndexBuffer.position(0);
		}

		public void draw(GL10 gl) {
			gl.glEnable(GL10.GL_CULL_FACE); // only face-polygons are culled
			gl.glFrontFace(GL10.GL_CW); // front-face: counterclockwise
			gl.glShadeModel(GL10.GL_SMOOTH); // since we only need one color, we
											// use flat shading
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);
			gl.glColorPointer(4, GL10.GL_FLOAT, 0, mColorBuffer);
			gl.glLineWidth(3);
			gl.glDrawElements(GL10.GL_LINES, 8, GL10.GL_UNSIGNED_BYTE,
					mIndexBuffer);
		}
	}

}
