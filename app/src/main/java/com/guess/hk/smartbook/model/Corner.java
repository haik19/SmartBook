package com.guess.hk.smartbook;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;

public class Corner {

	private Bitmap bitmap;
	private PointF point;
	private int id;
	private static int count = 0;

	public Corner(Context context, int resourceId, PointF point) {
		this.id = count++;
		bitmap = BitmapFactory.decodeResource(context.getResources(),
				resourceId);
		this.point = point;
	}

	public float getX() {
		return point.x;
	}

	public float getY() {
		return point.y;
	}

	public int getID() {
		return id;
	}

	public void setX(float x) {
		point.x = x;
	}

	public void setY(float y) {
		point.y = y;
	}

	public int getWidthOfBall() {
		return bitmap.getWidth();
	}

	public int getHeightOfBall() {
		return bitmap.getHeight();
	}

	public Bitmap getBitmap() {
		return bitmap;
	}
}
