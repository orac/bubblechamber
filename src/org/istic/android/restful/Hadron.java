package org.istic.android.restful;

import android.graphics.PointF;

/** Tends to form circular orbits. Draws an embossed effect. Stays close to the centre.
 */
class Hadron extends Particle {
	private static final int lighten = 0x1cffffff;
	private static final int darken = 0x1c000000;
	private boolean stable_orbit;
	@Override
	void generate_internal(Random generator, Palette palette) {
		stable_orbit = false;
		theta = generator.getUniform(0, 2.0f * (float)Math.PI);
		speed = generator.getUniform(0.5f, 3.5f);
		dthetadt = 0.0f;
		dspeeddt = generator.getUniform(0.996f, 1.001f);
		d2thetadt2 = generator.getTwoRanges(0.00001f, 0.001f);
	}

	@Override
	void step_internal(StepCallback cb, Random generator) {
		cb.add_point(new PointF(position.x, position.y - 1), lighten);
		cb.add_point(new PointF(position.x, position.y + 1), darken);
		
		apply_speed();
		
		theta += dthetadt;
		dthetadt += d2thetadt2;
		speed *= dspeeddt;
		
		if (!stable_orbit) {
			if (generator.get_boolean(0.003f)) {
				stable_orbit = true;
				lifetime = generator.get_gaussian_int(1000.0f, 200.0f);
				dspeeddt = 1.0f;
				d2thetadt2 = generator.getGaussian(0.0f, 0.00001f);
			}
		}
	}

}
