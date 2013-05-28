package com.example.muc13_03_bachnigsch;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

/**
 * 
 * @author Max Nigsch
 * @author Martin Bach
 * 
 * Activity empfängt Winkel und Zeigt Weg an
 *
 */

public class SearchActivity extends Activity implements SensorEventListener {
	
	private TextView textView;
	private CompassView compassView;
	private SensorManager mSensorManager;
	private Sensor mSensor;
	private String receiveUri = "http://barracuda-vm8.informatik.uni-ulm.de/orientations/snapshot";
	private int azimut;
	private int receivedAzimut;
	
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
		setContentView(R.layout.activity_search);

		
		// Show the Up button in the action bar.
		setupActionBar();
			
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		
		textView = (TextView)findViewById(R.id.textView2);
		
		if (mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION) != null){
		//	textView.setText("Orientierungssensor vorhanden");
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
		getMenuInflater().inflate(R.menu.search, menu);
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
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
	    // Azimut
	    float azimut_f = event.values[0];
	    azimut = (int)(azimut_f + 0.5);
//	    
//	    
//		textView.setText("Received Azimut: " + Integer.toString(receivedAzimut) + "°");
//		textView.setText("Personal Angle: " + Integer.toString(azimut) + "°");
//		
		System.out.println("Ich wuerde hier: " + receivedAzimut);
//		
		if (compassView != null) {
			System.out.println("Ich setze hier: " + azimut);
			compassView.setWinkel(-azimut);
			//compassView.setWinkel(-(azimut+180-receivedAzimut));
		}
		
	}
	
	
	
	
	
	
	public void receiveOrientation(View view){

		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
	
		if (networkInfo != null && networkInfo.isConnected()) {
			
			System.out.println("Ich werde aufgerufen");
			
			NetworkHandler networkHandler = new NetworkHandler(this);
			networkHandler.receiveData(receiveUri);
					
		} else {
			//System.out.print("No network connection available.");
			textView.setText("No network connection available.");
		    	
		}
		
		compassView = new CompassView(this);
		setContentView(compassView);
	}

	public void setAzimut(int receivedAzimut) {
		this.receivedAzimut = receivedAzimut;
	}
	

}
