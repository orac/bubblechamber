package com.shadowburst.bubblechamber;

import java.util.regex.*;
import java.util.ArrayList;

public class Palette {
	private int background;
	private int quark;
	private ArrayList<ColourPair> muon;
	
	/** Get the background colour.
	 * 
	 * @return A colour to use for the background.
	 */
	int get_background() {
		return background;
	}
	
	/** Get a random colour.
	 * 
	 * @param rng The random number generator to use.
	 * @return A colour randomly selected from the palette.
	 */
	int get_quark(Random rng) {
		return quark;
	}
	
	/** Get a random matching pair of colours for muons.
	 * 
	 * @param rng The random number generator to use.
	 * @return A matching pair of colours, randomly selected from the muon palette.
	 */
	ColourPair get_muon_pair(Random rng) {
		return muon.get( rng.get_int(muon.size()) );
	}
	
	Palette(String input) throws PaletteParseException {
		muon = new ArrayList<ColourPair>(15);
		Pattern bg = Pattern.compile("^bg\\s+(0x\\p{XDigit}+)\\s*;\\s*quark\\s+(0x\\p{XDigit}+)\\s*;\\s*muon\\s+");
		Pattern pair = Pattern.compile("(0x\\p{XDigit}+),(0x\\p{XDigit}+)\\s+");
		Matcher m = bg.matcher(input);
		if (m.find()) {
			try {
				background = Integer.decode(m.group(1));
				quark = Integer.decode(m.group(2));
				int end = m.end();
				m.usePattern(pair);
				m.region(end, m.regionEnd());
				while (m.find()) {
					ColourPair muon_pair = new ColourPair(Integer.decode(m.group(1)), Integer.decode(m.group(2)));
					muon.add(muon_pair);
				}
			} catch (NumberFormatException e) {
				throw new PaletteParseException();
			}
		} else {
			throw new PaletteParseException();
		}
		if (muon.size() == 0) {
			throw new PaletteParseException();
		}
	}
}
