package com.shadowburst.bubblechamber;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

final class BubbleChamber {
	private Particle[] particles;
	private Bitmap backbuffer;
	private Canvas canvas;
	
	/** Supplies random numbers to particles.
	 * @invariant Not null.
	 */
	private Random rng;
	private Palette palette;
	private int fade_out_frame_counter = 20;
	
	/** Translucent solid colour.
	 * 
	 * Used to fade old trails to the background colour.
	 * @invariant Not null.
	 */
	private Paint fader;
	
	/** A default-constructed Paint.
	 * Stashed to avoid some calls to new.
	 * @invariant Not null.
	 */
	private final Paint noop;
	private long frame_number = 0;
	
	/** The callback for Particle::step.
	 * Since it doesn't have any state that doesn't get reset for each particle anyway, I thought I might as well keep it here rather than recreate it each frame. 
	 */
	private AddPointCallback cb;

	private void set_backbuffer(int width, int height) {
		int max_dimension = Math.max(width, height);
		int min_dimension = Math.min(width, height);
		backbuffer = Bitmap.createBitmap(max_dimension, max_dimension,
				Bitmap.Config.ARGB_8888);
		clear();
		if (canvas == null) {
			canvas = new Canvas();
		} else {
			canvas.setMatrix(null);
		}
		canvas.setBitmap(backbuffer);
		canvas.translate(backbuffer.getWidth() / 2.0f,
				backbuffer.getHeight() / 2.0f);
		canvas.scale(min_dimension / 200.0f, min_dimension / 200.0f);
		canvas.drawPaint(fader);
	}

	BubbleChamber(int width, int height, String palette, float particle_frac) {
		noop = new Paint();
		cb = new AddPointCallback();
		this.palette = new Palette(palette);
		rng = new Random();
		
		fader = new Paint();
		fader.setDither(false);
		fader.setColor(this.palette.get_background());
		fader.setAlpha(10);
		
		set_backbuffer(width, height);

		set_particle_fraction(particle_frac);
	}

	void set_palette(String input) {
		palette = new Palette(input);

		fader.setColor(palette.get_background());
		reset();
	}

	void reset() {
		clear();
		frame_number = 0;

		for (Particle p : particles) {
			p.generate(rng, palette);
		}	
	}
	
	/**
	 * @brief Sets how many particles there are.
	 * 
	 * BubbleChamber uses the area of the screen to decide how many
	 * particles to have. The value you provide scales this default
	 * number of particles.
	 * 
	 * @param particle_frac Scale factor for how many particles to have. Normally in the range [0,1] but you can have more than this.
	 */
	void set_particle_fraction(float particle_frac) {
		int num_particles = (int) ((backbuffer.getWidth()
				* backbuffer.getHeight() * particle_frac) / 500.0f);
		num_particles = Math.max(num_particles, 1);
		
		if (particles.length != num_particles) {
			final float quark_frac = .3f;
			final float muon_frac = .42f;
			final float hadron_frac = .21f;
			final int num_quarks = (int) (quark_frac
					/ (quark_frac + muon_frac + hadron_frac) * num_particles);
			final int num_hadrons = (int) (hadron_frac
					/ (quark_frac + muon_frac + hadron_frac) * num_particles);
	
			particles = new Particle[num_particles];
			int i;
			for (i = 0; i < num_quarks; ++i) {
				particles[i] = new Quark();
				particles[i].generate(rng, this.palette);
			}
			for (; i < num_quarks + num_hadrons; ++i) {
				particles[i] = new Hadron();
				particles[i].generate(rng, this.palette);
			}
			for (; i < particles.length; ++i) {
				particles[i] = new Muon();
				particles[i].generate(rng, this.palette);
			}
			clear();
		}
	}
	
	private void clear() {
		if (backbuffer != null)
			backbuffer.eraseColor(palette.get_background() | 0xff000000);
	}

	void resize(int width, int height) {
		if (width > backbuffer.getWidth() || height > backbuffer.getHeight()) {
			set_backbuffer(width, height);
		}
	}

	public void draw(Canvas output) {
		output.drawBitmap(backbuffer,
				(output.getWidth() - canvas.getWidth()) / 2.0f, (output
						.getHeight() - canvas.getHeight()) / 2.0f, noop);
	}

	public long get_frame_number() {
		return frame_number;
	}
	
	public long step_all() {
		if (--fade_out_frame_counter == 0) {
			fade_out_frame_counter = 20;
			canvas.drawPaint(fader);
		}
		for (Particle p : particles) {
			cb.out_of_bounds = false;
			p.step(cb, rng, palette);

			if (cb.out_of_bounds) {
				p.generate(rng, palette);
			}
		}
		return ++frame_number;
	}

	private final class AddPointCallback implements Particle.StepCallback {
		boolean out_of_bounds;
		Paint p;

		AddPointCallback() {
			this.out_of_bounds = false;
			p = new Paint();
		}

		public	void add_point(PointF position, int colour) {
			p.setColor(colour);
			canvas.drawPoint(position.x, position.y, p);
		}
	}
}
