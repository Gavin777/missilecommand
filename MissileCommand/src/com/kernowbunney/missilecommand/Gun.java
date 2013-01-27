package com.kernowbunney.missilecommand;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.Log;

public class Gun {

	private static final String TAG = "Gun";
	
	private final int READY_COLOR = Color.BLUE;
	private final int RECHARGING_COLOR = Color.RED;
	
	private int xpos;				// guns position on ground
	private RectF rect;				// rectangle describing gun
	
	private ArrayList<Projectile> projectiles;	// the projectiles currently in flight
	private boolean recharging;		// if true, we cannot fire
	private long lastLaunchTime;
	private long rechargeTime = 1000;	// 1 second recharge time
	
	private Paint painter;

	
	public Gun(int xpos) {
		this.xpos = xpos;
		this.rect = new RectF(xpos - 15f, MainGamePanel.groundpos,
				xpos + 15f, MainGamePanel.groundpos - 30);
		
		// initialise some stuff
		recharging = false;
		
		// create list for storing projectiles:
		projectiles = new ArrayList<Projectile>();
		
		// painter for drawing gun:
		painter = new Paint();
		painter.setColor(READY_COLOR);
		painter.setStyle(Style.FILL);
		
	}
	
	
	public boolean fire(int targetx, int targety) {
		if(recharging) {
			Log.d(TAG, "Can't fire - recharging!");
			return false;
		}
		
		projectiles.add(new Projectile(
				xpos, MainGamePanel.groundpos,
				targetx, targety, 4));
		
		// keep track of launch time and start recharging gun:
		lastLaunchTime = System.currentTimeMillis();
		recharging = true;
		
		return true;
	}
	
	public void update() {
		// are we recharging?
		if(recharging && (System.currentTimeMillis() - lastLaunchTime) < rechargeTime) {
			// still recharging, make gun red.
			painter.setColor(RECHARGING_COLOR);
		} else {
			recharging = false;
			// recharged - set gun to green
			painter.setColor(READY_COLOR);
		}
	}
	
	
	public void draw(Canvas canvas) {
		canvas.drawRect(rect, painter);
	}
	
	public ArrayList<Projectile> getProjectiles() {
		return projectiles;
	}
}
