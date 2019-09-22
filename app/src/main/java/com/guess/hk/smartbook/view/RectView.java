package com.guess.hk.smartbook.view;

import java.util.ArrayList;
import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.guess.hk.smartbook.model.Corner;
import com.guess.hk.smartbook.PicterActionsKt;
import com.guess.hk.smartbook.R;

public class RectView extends View {

	private int touchAreaSize = 50;

	private int balID;
	private float startX = -1;
	private float startY = -1;
	private Paint rectanglePaint;
	private Paint roundsPaint;
	private PointF[] points = new PointF[4];
	private ArrayList<Corner> corners = new ArrayList<>();
	private Paint linePaint;

	protected float fingerSpacing = 0;
	protected double zoomLevel = 0f;
	protected float maximumZoomLevel = 5;
	private ZoomChangeListener zoomChangeListener;


	public RectView(Context context) {
		this(context, null);
	}

	public RectView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		rectanglePaint = new Paint();
		roundsPaint = new Paint();
		linePaint = new Paint();
		linePaint.setColor(Color.GREEN);
		rectanglePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
		rectanglePaint.setAntiAlias(true);
		setWillNotDraw(false);
		setLayerType(LAYER_TYPE_HARDWARE, null);

		setFocusable(true);
		float screenHeight = PicterActionsKt.getScreenHeightInPx(context);
		float screenWidth = PicterActionsKt.getScreenWidthInPx(context);
		float rectHeight = PicterActionsKt.convertDpToPixel(120);
		float rectWidth = PicterActionsKt.convertDpToPixel(120);

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

		//draw line
		canvas.drawLine(corners.get(0).getX() + 50, (corners.get(0).getY() + corners.get(1).getY()) / 2, corners.get(3).getX() - 50, (corners.get(0).getY() + corners.get(1).getY()) / 2, linePaint);
	}

	public boolean onTouchEvent(MotionEvent event) {
		if (event.getPointerCount() == 2) {
			if (event.getAction() == MotionEvent.ACTION_MOVE) {
				float currentFingerSpacing = getFingerSpacing(event);
				if (fingerSpacing != 0) {
					if (currentFingerSpacing > fingerSpacing && maximumZoomLevel > zoomLevel) {
						zoomLevel += 0.05;
					} else if (currentFingerSpacing < fingerSpacing && zoomLevel > 0) {
						zoomLevel -= 0.05;
					}
					zoomChangeListener.zoomTo(zoomLevel);
				}
				fingerSpacing = currentFingerSpacing;
			}
		} else if (event.getPointerCount() == 1) {
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
					startX = -1;
					startY = -1;
					break;
			}
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

	private float getFingerSpacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return (float) Math.sqrt(x * x + y * y);
	}

	public interface ZoomChangeListener {
		void zoomTo(double zoomLevel);
	}

	public void setZoomChangeListener(ZoomChangeListener zoomChangeListener) {
		this.zoomChangeListener = zoomChangeListener;
	}

	public PointF[] getPoints() {
		return points;
	}
}