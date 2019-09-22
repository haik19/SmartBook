package com.guess.hk.smartbook.model;

public class Line {

	private float starX;
	private float startY;
	private float stopY;
	private float stopX;


	public Line(float starX, float startY, float stopX, float stopY) {
		this.starX = starX;
		this.startY = startY;
		this.stopY = stopY;
		this.stopX = stopX;
	}

	public float getStarX() {
		return starX;
	}

	public void setStarX(float starX) {
		this.starX = starX;
	}

	public float getStartY() {
		return startY;
	}

	public void setStartY(float startY) {
		this.startY = startY;
	}

	public float getStopY() {
		return stopY;
	}

	public void setStopY(float stopY) {
		this.stopY = stopY;
	}

	public float getStopX() {
		return stopX;
	}

	public void setStopX(float stopX) {
		this.stopX = stopX;
	}
}
