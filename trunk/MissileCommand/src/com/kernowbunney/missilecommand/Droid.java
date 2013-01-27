package com.kernowbunney.missilecommand;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Droid {

	private Bitmap bitmap; // the actual droid bitmap
	private int x, y; // coords
	private boolean touched; // if droid is picked up
	private Speed speed;

	public Droid(Bitmap bitmap, int x, int y) {
		this.bitmap = bitmap;
		this.x = x;
		this.y = y;
		speed = new Speed();
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	public Speed getSpeed() {
		return this.speed;
	}

	public boolean isTouched() {

		return touched;

	}

	public void setTouched(boolean touched) {

		this.touched = touched;

	}

	public void draw(Canvas canvas) {

		canvas.drawBitmap(bitmap, x - (bitmap.getWidth() / 2),
				y - (bitmap.getHeight() / 2), null);
	}

	public void handleActionDown(int eventX, int eventY) {
		// check whether touch event was within bitmap bounds:
		if (eventX >= (x - bitmap.getWidth() / 2)
				&& (eventX <= (x + bitmap.getWidth() / 2))) {

			if (eventY >= (y - bitmap.getHeight() / 2)
					&& (y <= (y + bitmap.getHeight() / 2))) {

				// droid touched
				setTouched(true);
			} else {
				setTouched(false);
			}
		} else {
			setTouched(false);
		}
	}
	
	public void update() {
		if(!touched) {
			x += (speed.getXv() * speed.getxDirection());
			y += (speed.getYv() * speed.getyDirection());
		}
	}
}

