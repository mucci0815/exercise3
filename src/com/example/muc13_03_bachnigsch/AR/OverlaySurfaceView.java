package com.example.muc13_03_bachnigsch.AR;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.view.View;

public class OverlaySurfaceView extends View {

	private final String TAG = OverlaySurfaceView.class.getName();

	private Paint zeichenfarbe = new Paint();
	

	public OverlaySurfaceView(Context context) {
		super(context);

		Log.v(TAG, "Created!");

		zeichenfarbe.setAntiAlias(true);
		zeichenfarbe.setColor(Color.RED);
		zeichenfarbe.setStyle(Paint.Style.STROKE);
		zeichenfarbe.setStrokeWidth(3);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		int breite = canvas.getWidth();
		int hoehe = canvas.getHeight();
		canvas.drawRect(10, 10, 100, 100, zeichenfarbe);
	}

}
