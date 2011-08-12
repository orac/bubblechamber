package org.istic.android.restful;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

final class BubbleChamber {
	private Particle[] particles;
	private Bitmap backbuffer;
	private Random rng;
	
	BubbleChamber(int width, int height) {
		backbuffer = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		backbuffer.eraseColor(Color.WHITE);
		int max_dimension = Math.max(width, height);
		rng = new Random();
		
		particles = new Particle[max_dimension];
		for (int i = 0; i < particles.length; ++i) {
			particles[i] = new Quark();
			particles[i].generate(rng);
		}
	}

	void resize(int width, int height) {
		backbuffer = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		backbuffer.eraseColor(Color.WHITE);
	}
	
	public void draw(Canvas canvas) {
		if (canvas.getWidth() != backbuffer.getWidth() || canvas.getHeight() != backbuffer.getHeight())
			resize(canvas.getWidth(), canvas.getHeight());
		
		canvas.drawBitmap(backbuffer, 0.0f, 0.0f, new Paint());
	}
	
	public void step_all() {
		AddPointCallback cb = new AddPointCallback();
		for (Particle p : particles) {
			p.step(cb, rng);
		}
	}
	
	private final class AddPointCallback extends Particle.StepCallback {
		@Override
		void add_point(PointF position, int colour) {
			int x = (int)((position.x + backbuffer.getWidth()) / 2.0);
			int y = (int)((position.y + backbuffer.getHeight()) / 2.0);
			
			if (0 <= x && x < backbuffer.getWidth() && 0 <= y && y < backbuffer.getHeight())
				backbuffer.setPixel(x, y, colour);
		}
	}
}
