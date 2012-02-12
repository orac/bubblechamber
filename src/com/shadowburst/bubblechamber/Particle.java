package com.shadowburst.bubblechamber;

import android.graphics.PointF;

abstract class Particle {
	interface StepCallback {
		abstract void add_point(PointF position, int colour);
	}
	protected PointF position;
	protected float theta;
	protected float speed;
	protected float dthetadt;
	protected float dspeeddt;
	protected float d2thetadt2;
	protected int lifetime;
	
	protected void apply_speed() {
		position.x += speed * Math.sin(theta);
		position.y += speed * Math.cos(theta);
	}
	
	abstract void step_internal(StepCallback cb, Random generator);
	abstract void generate_internal(Random generator, Palette palette);
	
	public final void step(StepCallback cb, Random generator, Palette palette) {
		if (lifetime-- < 0) {
			generate(generator, palette);
		} else {
			step_internal(cb, generator);
			if (speed < 0.02f && 10 < lifetime)
				lifetime = 10;
		}
	}
	
	public final void generate(Random generator, Palette palette) {
		position = new PointF();
		lifetime = generator.get_gaussian_int(10000.0f, 100.0f);
		generate_internal(generator, palette);
	}
}
