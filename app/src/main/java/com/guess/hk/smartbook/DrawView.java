package com.guess.hk.smartbook;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DrawView extends View {
	Point[] points = new Point[4];

	/**
	 * point1 and point 3 are of same group and same as point 2 and point4
	 */
	int groupId = -1;
	private ArrayList<ColorBall> colorballs = new ArrayList<ColorBall>();
	private int balID = 0;
	Paint paint;
	Canvas canvas;
	float starX;
	float deltaX;

	float startY;
	float deltaY;
	int screenWidth;
	int screenHeitg;
	int rectHegth;
	int rectWidth;

	public DrawView(Context context) {
		this(context,null);
	}

	public DrawView(Context context, AttributeSet attrs, int defStyle) {
		super(context,attrs,defStyle);
		paint = new Paint();
		setFocusable(true);
		canvas = new Canvas();
		screenHeitg = PicterActionsKt.getScreenHeigthInPx(context);
		screenWidth = PicterActionsKt.getScreenWidthInPx(context);
		rectHegth = PicterActionsKt.convertDpToPixel(100);
		rectWidth = PicterActionsKt.convertDpToPixel(100);

		points[0] = new Point();
		points[0].x = screenWidth / 2 - rectWidth / 2;
		points[0].y = screenHeitg / 2 + rectHegth / 2;

		points[1] = new Point();
		points[1].x = screenWidth / 2 - rectWidth / 2;
		points[1].y = screenHeitg / 2 - rectHegth / 2;

		points[2] = new Point();
		points[2].x = screenWidth / 2 + rectWidth / 2;
		points[2].y = screenHeitg / 2 - rectHegth / 2;

		points[3] = new Point();
		points[3].x = screenWidth / 2 + rectWidth / 2;
		points[3].y = screenHeitg / 2 + rectHegth / 2;

		balID = 2;
		groupId = 1;
		colorballs.add(new ColorBall(getContext(), R.drawable.bottom_lefth, points[0]));
		colorballs.add(new ColorBall(getContext(), R.drawable.top_letfh, points[1]));
		colorballs.add(new ColorBall(getContext(), R.drawable.top_rigth, points[2]));
		colorballs.add(new ColorBall(getContext(), R.drawable.bottom_rigth, points[3]));

		invalidate();
	}

	public DrawView(Context context, AttributeSet attrs) {
		this(context, attrs,0);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (points[3] == null)
			return;
		int left, top, right, bottom;
		left = points[0].x;
		top = points[0].y;
		right = points[0].x;
		bottom = points[0].y;
		for (int i = 1; i < points.length; i++) {
			left = left > points[i].x ? points[i].x : left;
			top = top > points[i].y ? points[i].y : top;
			right = right < points[i].x ? points[i].x : right;
			bottom = bottom < points[i].y ? points[i].y : bottom;
		}
		paint.setAntiAlias(true);
		paint.setColor(getResources().getColor(R.color.white_transparent));

		canvas.drawRect(
				left + colorballs.get(0).getWidthOfBall() / 2,
				top + colorballs.get(0).getWidthOfBall() / 2,
				right + colorballs.get(2).getWidthOfBall() / 2,
				bottom + colorballs.get(2).getWidthOfBall() / 2, paint);

		for (int i = 0; i < colorballs.size(); i++) {
			ColorBall ball = colorballs.get(i);
			paint.setColor(Color.GREEN);
			canvas.drawBitmap(ball.getBitmap(), ball.getX(), ball.getY(),  //draw corners
					paint);
		}

	}

	public boolean onTouchEvent(MotionEvent event) {
		int eventaction = event.getAction();

		int X = (int) event.getX();
		int Y = (int) event.getY();

		switch (eventaction) {
			case MotionEvent.ACTION_DOWN:
				starX = event.getX();
				startY = event.getY();
				balID = -1;
				groupId = -1;
				for (int i = colorballs.size() - 1; i >= 0; i--) {
					ColorBall ball = colorballs.get(i);
					int centerX = ball.getX() + ball.getWidthOfBall();
					int centerY = ball.getY() + ball.getHeightOfBall();
					paint.setColor(Color.CYAN);
					double radCircle = Math
							.sqrt((double) (((centerX - X) * (centerX - X)) + (centerY - Y)
									* (centerY - Y)));

					if (radCircle < ball.getWidthOfBall()) {
						balID = ball.getID();
						break;
					}
				}
				break;

			case MotionEvent.ACTION_MOVE: // touch drag with the ball
				if(event.getPointerCount() > 1){
					return false;
				}
				deltaX = starX - event.getX();
				deltaY = startY - event.getY();

				if (balID > -1) {
					colorballs.get(balID).setX(X);
					colorballs.get(balID).setY(Y);
					paint.setColor(Color.CYAN);
					switch (balID) {
						case 0:
							colorballs.get(1).setY(((int) (colorballs.get(1).getY() + deltaY)));
							colorballs.get(1).setX(colorballs.get(0).getX());
							colorballs.get(3).setX((int) (colorballs.get(3).getX() + deltaX));
							colorballs.get(3).setY(colorballs.get(0).getY());

							colorballs.get(2).setY(colorballs.get(1).getY());
							colorballs.get(2).setX(colorballs.get(3).getX());
							break;

						case 1:
							colorballs.get(0).setY(((int) (colorballs.get(0).getY() + deltaY)));
							colorballs.get(0).setX(colorballs.get(1).getX());
							colorballs.get(2).setX((int) (colorballs.get(2).getX() + deltaX));
							colorballs.get(2).setY(colorballs.get(1).getY());

							colorballs.get(3).setX(colorballs.get(2).getX());
							colorballs.get(3).setY(colorballs.get(0).getY());
							break;
						case 2:
							colorballs.get(3).setY(((int) (colorballs.get(3).getY() + deltaY)));
							colorballs.get(3).setX(colorballs.get(2).getX());
							colorballs.get(1).setX((int) (colorballs.get(1).getX() + deltaX));
							colorballs.get(1).setY(colorballs.get(2).getY());

							colorballs.get(0).setY(colorballs.get(3).getY());
							colorballs.get(0).setX(colorballs.get(1).getX());
							break;

						case 3:
							colorballs.get(2).setY(((int) (colorballs.get(2).getY() + deltaY)));
							colorballs.get(2).setX(colorballs.get(3).getX());
							colorballs.get(0).setX((int) (colorballs.get(0).getX() + deltaX));
							colorballs.get(0).setY(colorballs.get(3).getY());

							colorballs.get(1).setY(colorballs.get(2).getY());
							colorballs.get(1).setX(colorballs.get(0).getX());
							break;
					}
					deltaX = 0;
					starX = event.getX();
					deltaY = 0;
					startY = event.getY();

					invalidate();
				}

				break;

			case MotionEvent.ACTION_UP:
				deltaX = 0;
				deltaY = 0;
				break;
		}
		invalidate();
		return true;
	}
}