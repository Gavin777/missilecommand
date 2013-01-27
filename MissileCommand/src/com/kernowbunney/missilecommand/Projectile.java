package com.kernowbunney.missilecommand;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.Log;

public class Projectile {

	private static final String TAG = "Projectile";

	private final int MAX_EXP_RADIUS = 50;

	private double xpos, ypos;
	private int targetx, targety, speed;
	private double theta;
	Paint painter, target_painter, exp_painter;
	private boolean detonated, finished;
	private int exprad;	// explosion radius
	private int drad;	// change in explosion radius.

	public Projectile(int startx, int starty, int targetx, int targety,
			int speed) {
		this.xpos = (int) startx;
		this.ypos = (int) starty;
		this.targetx = targetx;
		this.targety = targety;
		this.speed = speed;

		theta = Math.atan2(targety - starty, targetx - startx);

		// default style for paining projectile:
		painter = new Paint();
		painter.setColor(Color.YELLOW);
		painter.setStyle(Style.FILL);

		target_painter = new Paint();
		target_painter.setColor(Color.RED);

		exp_painter = new Paint();
		exp_painter.setColor(Color.YELLOW);
		exp_painter.setStyle(Style.FILL);
		exp_painter.setAlpha(128);

		detonated = false;
		finished = false;
		exprad = 1; // initial explosion radius
		drad   = 1; // change in explosion radius

		Log.d(TAG, "Firing! Start loc=" + startx + "," + starty + " Target="
				+ targetx + "," + targety + " ANGLE=" + theta);
	}

	public int getXpos() {
		return (int) xpos;
	}

	public int getYpos() {
		return (int) ypos;
	}

	public int getTargetx() {
		return (int) targetx;
	}

	public int getTargety() {
		return (int) targety;
	}

	public int getSpeed() {
		return speed;
	}
	
	public int getExplosionRadius() {
		return exprad;
	}

	public void setXpos(int xpos) {
		this.xpos = (double) xpos;
	}

	public void setYpos(int ypos) {
		this.ypos = (double) ypos;
	}

	public void setTargetx(int targetx) {
		this.targetx = targetx;
	}

	public void setTargety(int targety) {
		this.targety = targety;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public boolean isDetonated() {
		return detonated;
	}

	public boolean isFinished() {
		return finished;
	}

	public void update() {

		if (!detonated) {
			// check if we are within 2 pixels of target:
			if (Math.abs(xpos - targetx) < 2 && Math.abs(ypos - targety) < 2) {
				// missile has reached target:
				Log.d(TAG, "BANG!");
				speed = 0;
				exprad = 1;
				detonated = true;
			} else {
				// missile is moving towards target
				xpos += Math.cos(theta) * speed;
				ypos += Math.sin(theta) * speed;
			}
		}

		if (detonated) {
			// update explosion radius (only if explosion is growing):
			if (drad > 0 && exprad >= MAX_EXP_RADIUS) {
				// reverse explosion:
				Log.d(TAG, "Reversing explosion");
				drad = -drad;
			} else if(exprad <= 0) {
				// if exprad is 0 then explosion has finished:
				finished = true;
				exprad = 0;
			} else {
				// increase explosion radius
				exprad += drad;
			}
		}
	}

	public void draw(Canvas canvas) {

		if (!detonated) {
			// draw projectile:
			canvas.drawCircle((int) xpos, (int) ypos, 3, painter);

			// draw target crosshair:
			canvas.drawLine(targetx, targety - 4, targetx, targety + 4,
					target_painter);
			canvas.drawLine(targetx - 4, targety, targetx + 4, targety,
					target_painter);
		} else if (!finished) {
			// draw the explosion:
			canvas.drawCircle(targetx, targety, exprad, exp_painter);
		}
	}

}
