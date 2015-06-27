package com.example.aspiraondroid.view.pieces;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * @author Sunil Rao
 */
public abstract class Entity {
	private Paint paint;
	private Context ctx;
	


	private float topLeftX;

	private float topLeftY;
	
	private float pivotY;
	
	public float getPivotY() {
		return pivotY;
	}

	public void setPivotY(float pivotY) {
		this.pivotY = pivotY;
	}

	private Rect bounds;

	public Entity(Rect bounds,Context ctx,float pivotY) {
		this.ctx = ctx;
		this.bounds = bounds;
		this.pivotY = pivotY;
	}

	public Rect getBounds() {
		return bounds;
	}

	public void setBounds(Rect rect) {
		this.bounds = rect;
	}

	public abstract void draw(Canvas canvas);
	
	public abstract void processAI();
	
	public abstract boolean performClick();
	
	public boolean performClickOnce() {
		return false;
	}


	public float getTopLeftX() {
		return topLeftX;
	}



	public void setTopLeftX(float topLeftX) {
		this.topLeftX = topLeftX;
	}




	public float getTopLeftY() {
		return topLeftY;
	}



	public void setTopLeftY(float topLeftY) {
		this.topLeftY = topLeftY;
	}


	public Paint getPaint() {
		return paint;
	}



	public void setPaint(Paint paint) {
		this.paint = paint;
	}
	
	public Context getCtx() {
		return ctx;
	}

	public void setCtx(Context ctx) {
		this.ctx = ctx;
	}



}
