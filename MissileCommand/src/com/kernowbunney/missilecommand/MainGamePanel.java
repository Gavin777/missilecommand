package com.kernowbunney.missilecommand;

import java.util.ArrayList;
import java.util.Iterator;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MainGamePanel extends SurfaceView implements
		SurfaceHolder.Callback {

	private final static String TAG = MainGamePanel.class.getSimpleName();

	public static final int groundpos = 400;

	private MainThread thread;

	private ArrayList<Missile> missiles;
	private final int MAX_MISSILES = 5;
	private long lastLaunchTime;

	private int lives;

	private Gun gun; // our defence gun!
	
	private Paint painter;
	
	public MainGamePanel(Context context) {
		super(context);

		// adding the callback (this) to the surface holder to intercept events
		// (we can do this as we have implemented SurfaceHolder.Callback)
		getHolder().addCallback(this);

		// a container for our missiles:
		missiles = new ArrayList<Missile>();

		// create out defence gun emplacement at 100 pixels in:
		gun = new Gun(100);

		// create the game loop thread
		thread = new MainThread(getHolder(), this);

		// make the GamePanel focusable so it can handle events
		setFocusable(true);

		// set number of lives to 5
		lives = 5;
		
		// allocate painter for drawing ground:
		painter = new Paint();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {

		thread.setRunning(true);
		thread.start();

		// initialise last missile launch time:
		lastLaunchTime = System.currentTimeMillis();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		boolean retry = true;
		
		Log.d(TAG, "SURFACE IS BEING DESTROYED: STOPPING THE THREAD");
		while (retry) {
			try {
				thread.setRunning(false); // added to shutdown cleanly (see comments: http://obviam.net/index.php/a-very-basic-the-game-loop-for-android/)
				thread.join();
				((Activity)getContext()).finish();	// added to shutdown cleanly
				retry = false;
			} catch (InterruptedException e) {
				// try shutting down thread again
			}
		}
		thread.setRunning(false);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			// fire!!!
			gun.fire((int) event.getX(), (int) event.getY());
		}
		return true;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// black background:
		canvas.drawColor(Color.BLACK);

		// draw ground at 400 pixels
		painter.setColor(Color.GREEN);
		painter.setStrokeWidth(10);
		canvas.drawLine(0, 400, canvas.getWidth(), 400, painter);

		// draw gun emplacement:
		gun.draw(canvas);

		// loop over missiles and draw them
		for (int i = 0; i < missiles.size(); i++) {
			Missile m = missiles.get(i);
			m.draw(canvas);
		}

		// loop over projectiles and draw them:
		ArrayList<Projectile> projectiles = gun.getProjectiles();
		Iterator<Projectile> itp = projectiles.iterator();
		while (itp.hasNext()) {
			Projectile projectile = itp.next();
			projectile.draw(canvas);
		}

		// show number of lives at bottom of screen:
		painter.setColor(Color.WHITE);
		canvas.drawText(lives + " lives left", 50, 430, painter);

		// dead?
		if (lives <= 0) {
			painter.setTextSize(20);
			painter.setColor(Color.RED);
			painter.setTextAlign(Align.CENTER);
			canvas.drawText("You Suck! Game over!", getWidth() / 2,
					getHeight() / 2, painter);
		}
	}

	public void update() {

		if (lives <= 0) {
			return; // you're dead!
		}

		ArrayList<Projectile> projectiles = gun.getProjectiles();
		Iterator<Projectile> itp;
		int dx, dy;
		double dist;

		// if 3 seconds has passed since last missile launch, then
		// launch another:
		if (System.currentTimeMillis() - lastLaunchTime >= 3000
				&& missiles.size() <= MAX_MISSILES) {

			// add a new missile into the fray!
			int speed = Math.max(1, (int) (Math.random() * 3));
			int xpos = 20 + (int) (Math.random() * (getWidth() - 40));
			missiles.add(new Missile(BitmapFactory.decodeResource(
					getResources(), R.drawable.missile_sm), speed, xpos));

			// reset last hostile missile launch time:
			lastLaunchTime = System.currentTimeMillis();

		}

		// loop over missiles
		Iterator<Missile> itm = missiles.iterator();

		while (itm.hasNext()) {
			Missile m = itm.next();

			// check missile collision with ground
			if (m.getYpos() + m.getBitmap().getHeight() / 2 >= 400) {
				// missile has hit the ground.
				m.setSpeed(0);

				// remove from list
				itm.remove();

				// not good:
				lives -= 1;
			}

			// check if missile has hit explosion:
			// iterate over missiles
			itp = projectiles.iterator();
			while (itp.hasNext()) {
				Projectile p = itp.next();

				// calc distance to explosion center:
				dx = m.getXpos() - p.getXpos();
				dy = m.getYpos() - p.getYpos();
				dist = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
				if (dist <= (double) p.getExplosionRadius()) {
					// hit!
					m.setHit(true);
					Log.d(TAG, "Missile hit!");
				}
			}

			if (m.isHit()) {
				// destroy missile
				itm.remove();
			} else {
				// update missile position
				m.update();
			}
		}

		// update the gun emplacement:
		gun.update();

		// update any projectiles fired from gun:
		itp = projectiles.iterator();
		while (itp.hasNext()) {
			Projectile p = itp.next();
			p.update();

			// if projectile has finished exploding (done == true), then remove:
			if (p.isFinished()) {
				itp.remove();
				continue;
			}

			// delete projectile if it goes off the screen
			if (p.getXpos() < 0 || p.getXpos() > getWidth() || p.getYpos() < 0) {
				itp.remove();
				continue;
			}

		}

		// are we still alive?
		if (lives <= 0) {
			// dead!
			thread.setRunning(false);
			//((Activity)getContext()).finish();
		}
	}

	public int getLives() {
		return lives;
	}

}
