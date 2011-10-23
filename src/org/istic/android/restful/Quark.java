package org.istic.android.restful;

import android.graphics.PointF;

/** Dark, fast, point-symmetric. */
public class Quark extends Particle {
	private int colour;
	
	@Override
	void generate_internal(Random generator, Palette palette) {
		theta = generator.getUniform(-(float)Math.PI, (float)Math.PI);
		float speed_range = generator.get_uniform();
		speed = 0.5f + speed_range * 2.0f;
		/* They all start out going in straight lines. */
		dthetadt = 0.0f;
		dspeeddt = generator.getGaussian(0.96f, 0.03f);
		d2thetadt2 = generator.getTwoRanges(0.00001f, 0.001f);
		
		/* Set opacity in proportion to initial speed, to compensate for the points being spaced further apart. */
		int opacity = (0x20 + (int)(speed_range * 0x50)) << 24;
		colour = opacity | palette.get_quark(generator);
	}

	@Override
	void step_internal(StepCallback cb, Random generator) {
		cb.add_point(position, colour);
		PointF antiposition = new PointF();
		antiposition.set(position);
		antiposition.negate();
		cb.add_point(antiposition, colour);
		
		apply_speed();
		
		theta += dthetadt;
		dthetadt += d2thetadt2;
		speed *= dspeeddt; /* note use of * so dspeeddt is actually misnamed here */
		
		if (generator.get_boolean(0.003f)) {
			speed = -speed;
			dspeeddt = 2.0f - dspeeddt;
		}
	}
}
