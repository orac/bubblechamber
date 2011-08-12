package org.istic.android.restful;

import android.graphics.PointF;

/** Colourful, fast particle.
 */
class Muon extends Particle {
	static final int[] palette = {0x3a242b, 0x3b2426, 0x352325, 0x836454, 0x7d5533, 0x8b7352, 0xb1a181, 0xa4632e, 0xbb6b33, 0xb47249, 0xca7239, 0xd29057, 0xe0b87e, 0xd9b166, 0xf5eabe, 0xfcfadf, 0xd9d1b0, 0xfcfadf, 0xd1d1ca, 0xa7b1ac, 0x879a8c, 0x9186ad, 0x776a8e};
	private int colour, anticolour;
	@Override
	void generate_internal(Random generator) {
		speed = generator.getUniform(2.0f, 32.0f);
		dspeeddt = generator.getUniform(0.0001f, 0.001f);
		
		theta = generator.getUniform(-(float)Math.PI, (float)Math.PI);
		dthetadt = 0.0f;
		d2thetadt2 = generator.getTwoRanges(0.001f, 0.1f);
		
		int index = generator.get_int(palette.length);
		colour = 0xaa000000 | palette[index];
		anticolour = 0xaa000000 | palette[palette.length - 1 - index];

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
		
		if (speed < 0.01f)
			generate(generator);
	}

}
