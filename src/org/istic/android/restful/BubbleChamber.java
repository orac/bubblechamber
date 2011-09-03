package org.istic.android.restful;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

final class BubbleChamber {
	private Particle[] particles;
	private Bitmap backbuffer;
	private Canvas canvas;
	private Random rng;
	private Palette palette;
	private int fade_out_frame_counter = 10;
	private Paint fader;
	private long frame_number = 0;
	
	private void set_backbuffer(int width, int height) {
		int max_dimension = Math.max(width, height);
		int min_dimension = Math.min(width, height);
		backbuffer = Bitmap.createBitmap(max_dimension, max_dimension, Bitmap.Config.ARGB_8888);
		backbuffer.eraseColor(palette.get_background() | 0xff000000);
		if (canvas == null) {
			canvas = new Canvas();
		} else {
			canvas.setMatrix(null);
		}
		canvas.setBitmap(backbuffer);
		canvas.translate(backbuffer.getWidth() / 2.0f, backbuffer.getHeight() / 2.0f);
		canvas.scale(min_dimension / 200.0f, min_dimension / 200.0f);
		canvas.drawPaint(fader);
	}
	
	BubbleChamber(int width, int height, String palette) {
		this.palette = new Palette(palette);
		
		fader = new Paint();
		fader.setDither(false);
		fader.setColor(this.palette.get_background());
		fader.setAlpha(20);
		set_backbuffer(width, height);
		
		int max_dimension = Math.max(width, height);
		rng = new Random();
		
		float quark_frac = .3f;
		float muon_frac = .42f;
		float hadron_frac = .21f;
		int num_quarks = (int) (quark_frac / (quark_frac + muon_frac + hadron_frac) * max_dimension);
		int num_hadrons = (int) (hadron_frac / (quark_frac + muon_frac + hadron_frac) * max_dimension);
		
		particles = new Particle[max_dimension];
		int i;
		for (i = 0; i < num_quarks; ++i) {
			particles[i] = new Quark();
			particles[i].generate(rng, this.palette);
		}
		for (; i < num_quarks + num_hadrons; ++i) {
			particles[i] = new Hadron();
			particles[i].generate(rng, this.palette);
		}
		for (; i < particles.length; ++i ) {
			particles[i] = new Muon();
			particles[i].generate(rng, this.palette);
		}
	}
	
	void set_palette(String input) {
		palette = new Palette(input);
		fader.setColor(palette.get_background());
		fader.setAlpha(20);
	}

	void resize(int width, int height) {
		if (width > backbuffer.getWidth() || height > backbuffer.getHeight()) {
			set_backbuffer(width, height);
		}
	}
	
	public void draw(Canvas output) {
		output.drawBitmap(backbuffer,
			(output.getWidth() - canvas.getWidth()) / 2.0f,
			(output.getHeight() - canvas.getHeight()) / 2.0f,
			new Paint());
	}
	
	public void step_all() {
		++frame_number;
		if (--fade_out_frame_counter == 0) {
			fade_out_frame_counter = 10;
			canvas.drawPaint(fader);
		}
		AddPointCallback cb = new AddPointCallback();
		for (Particle p : particles) {
			cb.out_of_bounds = false;
			p.step(cb, rng, palette);
			
			if (cb.out_of_bounds) {
				p.generate(rng, palette);
			}
		}
	}
	
	private final class AddPointCallback extends Particle.StepCallback {
		boolean out_of_bounds;
		
		AddPointCallback() {
			this.out_of_bounds = false;
		}
		
		@Override
		void add_point(PointF position, int colour) {
			Paint paint = new Paint();
			paint.setColor(colour);
			canvas.drawPoint(position.x, position.y, paint);
		}
	}
}
