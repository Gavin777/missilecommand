package com.kernowbunney.missilecommand;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

public class MainThread extends Thread {
	private final static String TAG = MainThread.class.getSimpleName();
	
	// desired frame rate:
	private final static int	MAX_FPS = 50;
	
	// maximum number of frames to skip if we are lagging behind
	// the desired frame rate:
	private final static int	MAX_FRAME_SKIPS = 5;
	
	// time period of frame (in ms):
	private final static int	FRAME_PERIOD = 1000 / MAX_FPS;
	
	private boolean running;	// game state
	
	private SurfaceHolder surfaceHolder;
	private MainGamePanel mainGamePanel;
	

	public MainThread(SurfaceHolder surfaceHolder, MainGamePanel mainGamePanel) {
		super();
		this.surfaceHolder = surfaceHolder;
		this.mainGamePanel = mainGamePanel;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	@Override
	public void run() {
		Canvas canvas;

		long beginTime; // start of update/render cycle
		long timeDiff; // time taken to complete cycle
		int sleepTime; // how long to sleep to keep frame rate
		int framesSkipped; // number of frames to skip if behind

		Log.d(TAG, "Starting game loop");

		while (running) {
			canvas = null;
			
			if(this.mainGamePanel.getLives() <= 0) {
				// exit game somehow....
			}

			// try locking the canvas for exclusive pixel editing of surface
			try {
				canvas = this.surfaceHolder.lockCanvas();
				synchronized (surfaceHolder) {
					framesSkipped = 0;
					
					// get current time:
					beginTime = System.currentTimeMillis();

					// update game state
					this.mainGamePanel.update();

					// draw canvas on panel:
					this.mainGamePanel.onDraw(canvas);

					// get time taken:
					timeDiff = System.currentTimeMillis() - beginTime;

					// Calculate sleep time to maintain frame rate:
					sleepTime = (int) (FRAME_PERIOD - timeDiff);

					// if sleep time > 0, then we are ok, but if <0
					// then we are taking too long to update and will
					// need to skip some frames to catch up.
					if (sleepTime > 0) {
						// sleep until next required update.
						try {
							Thread.sleep(sleepTime);
						} catch (InterruptedException e) { }
					} 
					
					while(sleepTime < 0 && framesSkipped < MAX_FRAME_SKIPS) {
						// oh dear - we are lagging behind and need
						// to catch up WITHOUT rendering:
						this.mainGamePanel.update();	// update positios only
						
						// skip frames until we are in next frame (i.e sleep
						// time is > 0). But, only skip a predefined maximum
						// frames - any more than this and our game will be
						// unplayable:
						sleepTime += FRAME_PERIOD;
						framesSkipped++;
					}
				}
			} finally {
				// in case of exception, surface is no left in an
				// inconsistent state:
				if (canvas != null) {
					surfaceHolder.unlockCanvasAndPost(canvas);
				}
			}
		}
	}
}
