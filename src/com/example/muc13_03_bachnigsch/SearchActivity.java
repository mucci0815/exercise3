package com.example.muc13_03_bachnigsch;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
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

public class SearchActivity extends Activity {
	
	private TextView textView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);
		return true;
	}
	
	
	private String receiveUri = "http://barracuda-vm8.informatik.uni-ulm.de/orientations/snapshot";
	
	
	public void receiveOrientation(View view){

		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
	
		if (networkInfo != null && networkInfo.isConnected()) {
			
			NetworkHandler networkHandler = new NetworkHandler();
			int receivedAzimut = networkHandler.receiveData(receiveUri);
			
			while (receivedAzimut == 0){
				receivedAzimut = networkHandler.getReceivedAzimut();
			}
			
			textView = (TextView)findViewById(R.id.textView2);
			textView.setText("Received Angle: " + Integer.toString(receivedAzimut) + "°");
			
		} else {
			//System.out.print("No network connection available.");
			textView.setText("No network connection available.");
		    	
		}
	}
}
