package org.istic.android.restful;

import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.view.SurfaceHolder;
import android.os.Handler;

public class WallpaperService extends
		android.service.wallpaper.WallpaperService {

	private final Handler handler = new Handler();
	static final String preferences_name = "org.istic.android.BubbleChamberprefs";
	
	private class RestfulEngine
		extends Engine
		implements SharedPreferences.OnSharedPreferenceChangeListener
	{
		BubbleChamber chamber;
		private SharedPreferences prefs;
		private volatile int millis_per_frame;
		
		private final Runnable stepper = new Runnable() {
			public void run() {
				chamber.step_all();
				draw();
				handler.removeCallbacks(this);
				handler.postDelayed(this, millis_per_frame);
			}
		};
		
		RestfulEngine() {
			prefs = WallpaperService.this.getSharedPreferences(preferences_name, MODE_PRIVATE);
			prefs.registerOnSharedPreferenceChangeListener(this);
			onSharedPreferenceChanged(prefs, null);
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

		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
			if (key == null || key.equals(new String("framerate"))) {
				millis_per_frame = sharedPreferences.getInt("framerate", 500);
			}
			
		}
	}
	@Override
	public Engine onCreateEngine() {
		return new RestfulEngine();
	}

}
