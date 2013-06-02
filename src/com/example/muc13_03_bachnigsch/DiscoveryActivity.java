package com.example.muc13_03_bachnigsch;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

/**
 * 
 * @author Max Nigsch
 * @author Martin Bach
 * 
 * Activity sendet Winkel an Server
 *
 */

public class DiscoveryActivity extends Activity implements SensorEventListener {
	
	private static final String TAG = DiscoveryActivity.class.getName();
	
	private SensorManager mSensorManager;
	private Sensor mSensor;
	private TextView textView;
	private int azimut;
	
	@Override
	protected void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_discovery);
		// Show the Up button in the action bar.
		setupActionBar();
			
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		
		textView = (TextView)findViewById(R.id.textView1);
		
		if (mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION) != null){
			textView.setText("Orientierungssensor vorhanden");
		}
		else {
			// Fehler - kein Kompass
			textView.setText("Kein Orientierungssensor vorhanden");
		}
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.discovery, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		
	    // Azimut
	    float azimut_f = event.values[0];
	    azimut = (int)(azimut_f + 0.5);
	    textView.setText("Azimut: " + azimut + "°");

	}
	
	/**
	 * gets called when user presses button
	 * 
	 * sends current azimut to server
	 * @param view
	 */
	public void sendOrientation(View view){
		
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		
		if (networkInfo != null && networkInfo.isConnected()) {	
			// get username from preferences
			SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
			String username = sharedPref.getString("pref_username", "");
			
			String sendUri = "http://barracuda-vm8.informatik.uni-ulm.de/user/"+username+"/orientation/" + Integer.toString(azimut);
			NetworkHandler networkHandler = new NetworkHandler();
			Log.i(TAG, "sendUri = " + sendUri);
			networkHandler.sendData(sendUri);
			
		} else {
			Log.i(TAG,"No network connection available.");
			//textView.setText("No network connection available.");		    	
		}		
	}
}
