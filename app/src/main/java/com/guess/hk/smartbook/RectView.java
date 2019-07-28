package com.guess.hk.smartbook;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class RectView extends View {

	private int touchAreaSize = 30;

	private int balID;
	private float startX = -1;
	private float startY = -1;
	private Paint rectanglePaint;
	private Paint roundsPaint;
	private PointF[] points = new PointF[4];
	private ArrayList<Corner> corners = new ArrayList<>();

	public RectView(Context context) {
		this(context, null);
	}

	public RectView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		rectanglePaint = new Paint();
		roundsPaint = new Paint();
		rectanglePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
		rectanglePaint.setAntiAlias(true);
		setWillNotDraw(false);
		setLayerType(LAYER_TYPE_HARDWARE, null);

		setFocusable(true);
		float screenHeight = PicterActionsKt.getScreenHeigthInPx(context);
		float screenWidth = PicterActionsKt.getScreenWidthInPx(context);
		float rectHeight = PicterActionsKt.convertDpToPixel(100);
		float rectWidth = PicterActionsKt.convertDpToPixel(100);

		points[0] = new PointF();
		points[0].x = screenWidth / 2 - rectWidth / 2;
		points[0].y = screenHeight / 2 + rectHeight / 2;
		points[1] = new PointF();
		points[1].x = screenWidth / 2 - rectWidth / 2;
		points[1].y = screenHeight / 2 - rectHeight / 2;
		points[2] = new PointF();
		points[2].x = screenWidth / 2 + rectWidth / 2;
		points[2].y = screenHeight / 2 - rectHeight / 2;
		points[3] = new PointF();
		points[3].x = screenWidth / 2 + rectWidth / 2;
		points[3].y = screenHeight / 2 + rectHeight / 2;

		corners.add(new Corner(getContext(), R.drawable.bottom_lefth, points[0]));
		corners.add(new Corner(getContext(), R.drawable.top_letfh, points[1]));
		corners.add(new Corner(getContext(), R.drawable.top_rigth, points[2]));
		corners.add(new Corner(getContext(), R.drawable.bottom_rigth, points[3]));
		invalidate();
	}

	public RectView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawRect(points[1].x, points[1].y, points[2].x, points[3].y, rectanglePaint); // draw rect
		//draw corners
		canvas.drawBitmap(corners.get(0).getBitmap(), corners.get(0).getX(), corners.get(0).getY() - corners.get(0).getHeightOfBall(), roundsPaint);
		canvas.drawBitmap(corners.get(1).getBitmap(), corners.get(1).getX(), corners.get(1).getY(), roundsPaint);
		canvas.drawBitmap(corners.get(2).getBitmap(), corners.get(2).getX() - corners.get(2).getWidthOfBall(), corners.get(2).getY(), roundsPaint);
		canvas.drawBitmap(corners.get(3).getBitmap(), corners.get(3).getX() - corners.get(3).getWidthOfBall(), corners.get(3).getY() - corners.get(3).getHeightOfBall(), roundsPaint);
	}

	public boolean onTouchEvent(MotionEvent event) {
		float X = event.getX();
		float Y = event.getY();

		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				balID = findCorner(event);
				break;

			case MotionEvent.ACTION_MOVE:
				if (event.getPointerCount() > 1 || balID == -1) {
					return false;
				}

				if (startX == -1) {
					startX = corners.get(balID).getX();
				}

				if (startY == -1) {
					startY = corners.get(balID).getY();
				}

				float deltaX = startX - event.getX();
				float deltaY = startY - event.getY();

				corners.get(balID).setX(X);
				corners.get(balID).setY(Y);
				switch (balID) {
					case 0:
						corners.get(1).setY((corners.get(1).getY() + deltaY));
						corners.get(1).setX(corners.get(0).getX());
						corners.get(3).setX((corners.get(3).getX() + deltaX));
						corners.get(3).setY(corners.get(0).getY());
						corners.get(2).setY(corners.get(1).getY());
						corners.get(2).setX(corners.get(3).getX());
						break;

					case 1:
						corners.get(0).setY(corners.get(0).getY() + deltaY);
						corners.get(0).setX(corners.get(1).getX());
						corners.get(2).setX(corners.get(2).getX() + deltaX);
						corners.get(2).setY(corners.get(1).getY());
						corners.get(3).setX(corners.get(2).getX());
						corners.get(3).setY(corners.get(0).getY());
						break;
					case 2:
						corners.get(3).setY(corners.get(3).getY() + deltaY);
						corners.get(3).setX(corners.get(2).getX());
						corners.get(1).setX((corners.get(1).getX() + deltaX));
						corners.get(1).setY(corners.get(2).getY());
						corners.get(0).setY(corners.get(3).getY());
						corners.get(0).setX(corners.get(1).getX());
						break;

					case 3:
						corners.get(2).setY(corners.get(2).getY() + deltaY);
						corners.get(2).setX(corners.get(3).getX());
						corners.get(0).setX(corners.get(0).getX() + deltaX);
						corners.get(0).setY(corners.get(3).getY());
						corners.get(1).setY(corners.get(2).getY());
						corners.get(1).setX(corners.get(0).getX());
						break;
				}
				startX = event.getX();
				startY = event.getY();
				invalidate();
				break;

			case MotionEvent.ACTION_UP:
				startX  = -1;
				startY  = -1;
				break;
		}
		return true;
	}

	private int findCorner(MotionEvent event) {
		for (int i = corners.size() - 1; i >= 0; i--) {
			if (Math.abs(event.getX() - corners.get(i).getX()) < touchAreaSize && Math.abs(event.getY() - corners.get(i).getY()) < touchAreaSize) {
				return i;
			}
		}
		return -1;
	}
}