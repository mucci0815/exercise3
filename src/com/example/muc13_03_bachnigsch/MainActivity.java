package com.example.muc13_03_bachnigsch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

/**
 * 
 * @author Max Nigsch
 * @author Martin Bach
 * 
 *         MainActivity - laesst Benutzer auswahlen ob er suchen oder gefunden
 *         werden will
 * 
 */

public class MainActivity extends Activity {

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			startActivity(new Intent(this, SettingsActivity.class));
			return true;
		default:
			
			return super.onOptionsItemSelected(item);
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void callDiscoveryActivity(View view) {
		Intent intent = new Intent(this, DiscoveryActivity.class);
		startActivity(intent);
	}

	public void callSearchActivity(View view) {
		Intent intent = new Intent(this, SearchActivity.class);
		startActivity(intent);
	}

}
