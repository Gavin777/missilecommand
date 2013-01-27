package com.kernowbunney.missilecommand;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Missile {
	
	private int xpos, ypos;
	private int speed;
	private Bitmap bitmap;
	private boolean hit;	// has the missile been hit?
	
	public Missile(Bitmap bitmap, int speed, int initial_xpos) {
		this.bitmap = bitmap;
		this.speed = speed;
		this.xpos = initial_xpos;
		this.ypos = 0;
		
		hit = false;
	}

	public int getXpos() {
		return xpos;
	}
	
	public int getYpos() {
		return ypos;
	}

	public int getSpeed() {
		return speed;
	}

	public void setXpos(int xpos) {
		this.xpos = xpos;
	}
	
	public void setYpos(int ypos) {
		this.ypos = ypos;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
	
	public void setHit(boolean hit) {
		this.hit = hit;
	}
	
	public boolean isHit() {
		return hit;
	}
	

	public void draw(Canvas canvas) {
		canvas.drawBitmap(bitmap, xpos - bitmap.getWidth() / 2,
					ypos - bitmap.getHeight() / 2, null);
	}
	
	public void update() {
		// missile can only move down, so only need to update
		// the y position on the screen.
		ypos += speed;
	}
}
