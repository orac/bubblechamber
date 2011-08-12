package org.istic.android.restful;

import android.graphics.PointF;

/** Colourful, fast particle.
 */
class Muon extends Particle {
	private int colour, anticolour;
	@Override
	void generate_internal(Random generator) {
		speed = generator.getUniform(2.0f, 32.0f);
		dspeeddt = generator.getUniform(0.0001f, 0.001f);
		
		theta = generator.getUniform(-(float)Math.PI, (float)Math.PI);
		dthetadt = 0.0f;
		d2thetadt2 = generator.getTwoRanges(0.001f, 0.1f);
		
		colour = 0x2aff0000;
		anticolour = 0x2a00ff00;

	}

	@Override
	void step_internal(StepCallback cb, Random generator) {
		PointF antiposition = new PointF();
		antiposition.set(position);
		antiposition.negate();
		cb.add_point(position, colour);
		cb.add_point(antiposition, anticolour);
		
		apply_speed();
		theta += dthetadt;
		dthetadt += d2thetadt2;
		speed -= dspeeddt;
	}

}
