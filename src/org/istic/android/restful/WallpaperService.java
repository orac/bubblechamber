package org.istic.android.restful;

import android.graphics.Canvas;
import android.view.SurfaceHolder;
import android.os.Handler;

public class WallpaperService extends
		android.service.wallpaper.WallpaperService {

	private final Handler handler = new Handler();
	
	private class RestfulEngine extends Engine {
		BubbleChamber chamber;
		private final Runnable stepper = new Runnable() {
			public void run() {
				chamber.step_all();
				draw();
				handler.removeCallbacks(this);
				handler.postDelayed(this, 500);
			}
		};
		
		RestfulEngine() {
		}
		
		@Override
		public void onDestroy() {
			handler.removeCallbacks(stepper);
			super.onDestroy();
		}
		
		@Override
		public void onOffsetsChanged(float xOffset, float yOffset, float xStep, float yStep, int xPixels, int yPixels) {
			draw();
		}
		
		@Override
		public void onSurfaceChanged(SurfaceHolder holder, int format,
				int width, int height) {
			super.onSurfaceChanged(holder, format, width, height);
			if (chamber == null) {
				chamber = new BubbleChamber(width, height);
			} else {
				chamber.resize(width, height);
			}
			draw();
		}

		@Override
		public void onVisibilityChanged(boolean visible) {
			super.onVisibilityChanged(visible);
			if (visible) {
				handler.post(stepper);
			}
			else
				handler.removeCallbacks(stepper);
		}

		void draw() {
			final SurfaceHolder holder = getSurfaceHolder();
			Canvas c = null;
			try {
				c = holder.lockCanvas();
				if (c != null) {
					chamber.draw(c);
				}
			} finally {
				if (c != null) holder.unlockCanvasAndPost(c);
			}
		}
	}
	@Override
	public Engine onCreateEngine() {
		return new RestfulEngine();
	}

}
