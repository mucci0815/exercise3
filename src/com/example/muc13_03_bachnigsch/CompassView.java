package com.example.muc13_03_bachnigsch;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

/**
 * 
 * @author Max Nigsch
 * @author Martin Bach
 * 
 * CompassView - Zeigt einen Kompasszeiger an
 *
 */

@SuppressLint("DrawAllocation")
public class CompassView extends View {
	private float winkel=0;
	private Paint zeichenfarbe = new Paint();
	
	public CompassView(Context context) {
		super(context);
		zeichenfarbe.setAntiAlias(true);
		zeichenfarbe.setColor(Color.WHITE);
		zeichenfarbe.setStyle(Paint.Style.STROKE);
		zeichenfarbe.setStrokeWidth(0);
	}

	public void setWinkel(float winkel) {
		this.winkel = -winkel;
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(Color.BLACK);
		int breite = canvas.getWidth();
		int hoehe = canvas.getHeight();
		int laenge = Math.min(breite, hoehe);
		Path pfad = new Path();
		pfad.moveTo(0, -laenge/2);
		pfad.lineTo(laenge/20, laenge/2);
		pfad.lineTo(-laenge/20, laenge/2);
		pfad.close();
		canvas.translate(breite/2, hoehe/2);
		canvas.rotate(winkel);
		canvas.drawPath(pfad, zeichenfarbe);
	}
}