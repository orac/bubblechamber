package org.istic.android.restful;

import android.graphics.PointF;

/** Colourful. Starts out fast but soon decays to a much slower speed in an unstable orbit, allowing it to make a ring or disc at any point of the screen. 
 */
class Muon extends Particle {
	private ColourPair colour;
	private float terminal_speed;
	
	@Override
	void generate_internal(Random generator, Palette palette) {
		speed = generator.getUniform(2.0f, 32.0f);
		terminal_speed = generator.getUniform(.1f, 3.0f);
		dspeeddt = generator.getUniform(0.0001f, 0.001f);
		
		theta = generator.getUniform(-(float)Math.PI, (float)Math.PI);
		dthetadt = 0.0f;
		d2thetadt2 = generator.getTwoRanges(0.001f, 0.01f);
		
		colour = palette.get_muon_pair(generator);
		colour.positive |= 0x4a000000;
		colour.negative |= 0x4a000000;
	}

	@Override
	void step_internal(StepCallback cb, Random generator) {
		PointF antiposition = new PointF();
		antiposition.set(position);
		antiposition.negate();
		cb.add_point(position, colour.positive);
		cb.add_point(antiposition, colour.negative);
		
		apply_speed();
		theta += dthetadt;
		dthetadt += d2thetadt2;
		speed = (speed - terminal_speed) * 0.8f + terminal_speed;
	}

}
