package org.istic.android.restful;

import android.graphics.PointF;

abstract class Particle {
	static abstract class StepCallback {
		abstract void add_point(PointF position, int colour);
	}
	protected PointF position;
	protected float theta;
	protected float speed;
	protected float dthetadt;
	protected float dspeeddt;
	protected float d2thetadt2;
	
	protected void apply_speed() {
		position.x += speed * Math.sin(theta);
		position.y += speed * Math.cos(theta);
	}
	
	abstract void step_internal(StepCallback cb, Random generator);
	abstract void generate_internal(Random generator);
	
	public final void step(StepCallback cb, Random generator) {
		step_internal(cb, generator);
	}
	
	public final void generate(Random generator) {
		position = new PointF();
		generate_internal(generator);
	}
}
