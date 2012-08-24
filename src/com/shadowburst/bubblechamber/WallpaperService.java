package com.shadowburst.bubblechamber;

import com.shadowburst.TimeIntervalPreference;

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
		private volatile boolean reset_enabled;
		private volatile long frame_to_reset;
		private int x_offset, y_offset;
		
		private final Runnable stepper = new Runnable() {
			public void run() {
				long frame = chamber.step_all();
				draw();
				if (reset_enabled && frame >= frame_to_reset) {
					chamber.reset();
				}
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
			super.onOffsetsChanged(xOffset, yOffset, xStep, yStep, xPixels, yPixels);
			x_offset = xPixels;
			y_offset = yPixels;
			draw();
		}
		
		@Override
		public void onDesiredSizeChanged(int desiredWidth, int desiredHeight) {
			if (chamber != null) {
				chamber.resize(desiredWidth, desiredHeight);
			}
		}
		
		@Override
		public void onSurfaceCreated(SurfaceHolder holder) {
			if (chamber == null) {
				String defaultPalette = WallpaperService.this.getResources().getStringArray(R.array.palettevalues)[0];
				String palette = prefs.getString("palette", defaultPalette);
				float num_particles = prefs.getInt("num_particles", 50) / 100.0f;
				chamber = new BubbleChamber(getDesiredMinimumWidth(), getDesiredMinimumHeight(), palette, num_particles);
			}
		}
		
		@Override
		public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			super.onSurfaceChanged(holder, format, width, height);
			int radius = Math.min(width, height)/ 2;
			chamber.set_radius(radius);
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
					c.translate(x_offset, y_offset);	
					chamber.draw(c);
				}
			} finally {
				if (c != null) holder.unlockCanvasAndPost(c);
			}
		}

		private void update_frame_to_reset() {
			if (reset_enabled) {
				long current_frame = 0;
				long reset_interval_frames = TimeIntervalPreference.to_milliseconds(prefs.getInt("reset_time", 0)) / millis_per_frame;
				frame_to_reset = current_frame + reset_interval_frames;
			}
		}
		
		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
			assert(sharedPreferences == prefs);
			if (key == null || key.equals(new String("framerate"))) {
				millis_per_frame = sharedPreferences.getInt("framerate", 500);
				update_frame_to_reset();
			}
			if (chamber != null && (key == null || key.equals(new String("palette")))) {
				String defaultPalette = WallpaperService.this.getResources().getStringArray(R.array.palettevalues)[0];
				chamber.set_palette(sharedPreferences.getString("palette", defaultPalette));
			}
			if (chamber != null && (key == null || key.equals(new String("num_particles")))) {
				chamber.set_particle_fraction(sharedPreferences.getInt("num_particles", 50) / 100.0f);
			}
			if (key == null || key.equals(new String("reset")) || key.equals(new String("reset_time"))) {
				reset_enabled = sharedPreferences.getBoolean("reset", false);
				update_frame_to_reset();
			}
		}
	}

	@Override
	public Engine onCreateEngine() {
		//android.os.Debug.waitForDebugger();
		return new RestfulEngine();
	}

}
