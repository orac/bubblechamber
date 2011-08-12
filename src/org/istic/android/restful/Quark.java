package org.istic.android.restful;

import android.graphics.PointF;

public class Quark extends Particle {
	private static final int transparent_black = 0x20000000;
	
	@Override
	void generate_internal(Random generator) {
		theta = generator.getUniform(-(float)Math.PI, (float)Math.PI);
		speed = generator.getUniform(0.5f, 3.0f);
		dthetadt = 0.0f;
		dspeeddt = generator.getUniform(0.996f, 1.001f);
		d2thetadt2 = generator.getTwoRanges(0.001f, 0.1f);
	}

	@Override
	void step_internal(StepCallback cb, Random generator) {
		cb.add_point(position, transparent_black);
		PointF antiposition = new PointF();
		antiposition.set(position);
		antiposition.negate();
		cb.add_point(antiposition, transparent_black);
		
		position.x += speed * Math.sin(theta);
		position.y += speed * Math.cos(theta);
		
		theta += dthetadt;
		dthetadt += d2thetadt2;
		speed *= dspeeddt; /* note use of * so dspeeddt is actually misnamed here */
		
		if (generator.get_boolean(0.003f)) {
			speed = -speed;
			dspeeddt = 2.0f - dspeeddt;
		}
	}
}
