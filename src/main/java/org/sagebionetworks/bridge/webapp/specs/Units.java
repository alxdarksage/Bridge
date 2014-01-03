package org.sagebionetworks.bridge.webapp.specs;

/**
 * This is a large mess and there are a couple of older measure and unit libraries/APIs
 * out there that we should perhaps use, or we'll make mistakes in conversion or 
 * reporting.
 */
public enum Units {
	
	LITER("L", "liter"),
	DECILITER("dL", "deciliter"), 		// 10^-1 L
	CENTILITER("cL", "centiliter"), 	// 10^-2 L
	MILLILITER("mL", "millilter"), 		// 10^-3 L
	MICROLITER("mcL", "microliter"), 	// 10^-6 L or µL or uL
	NANOLITER("nL", "nanoliter"), 		// 10^-9 L
	PICOLITER("pL", "picoliter"), 		// 10^-12 L
	FEMTOLITER("fL", "femtoliter"),		// 10^-15
	
	GRAM("g", "gram"),
	DECIGRAM("dg", "decigram"),			// 10^-1 g
	CENTIGRAM("cg", "centigram"),		// 10^-2 g
	MILLIGRAM("mg", "milligram"),		// 10^-3 g
	MICROGRAM("mcg", "microgram"),		// 10^-6 g
	NANOGRAM("ng", "nanogram"),			// 10^-9 g
	PICOGRAM("pg", "picogram"),			// 10^-12 g
	FEMTOGRAM("fg", "femtogram"),		// 10^-15 g
	
	MOLE("mol", "mole"),
	DECIMOLE("dmol", "decimole"),		// 10^-1 mol
	CENTIMOLE("cmol", "centimole"),		// 10^-2 mol
	MILLIMOLE("mmol", "millimole"),		// 10^-3 mol
	MICROMOLE("mcmol", "micromole"),	// 10^-6 mol
	NANOMOLE("nmol", "nanomole"),		// 10^-9 mol
	PICOMOLE("pmol", "picomole"),		// 10^-12 mol
	FEMTOMOLE("fmol", "femtomole"),		// 10^-15 mol

	CUBIC_METER("cubic m", "mole"),
	CUBIC_DECIMETER("cubic dm", "decimole"),	// 10^-1 m
	CUBIC_CENTIMETER("cubic cm", "centimeter"),	// 10^-2 m
	CUBIC_MILLIMETER("cubic mm", "millimeter"),	// 10^-3 m
	CUBIC_MICROMETER("cubic mc", "micrometer"),	// 10^-6 m
	CUBIC_NANOMETER("cubic nm", "nanometer"),	// 10^-9 m
	CUBIC_PICOMETER("cubic pm", "picometer"),	// 10^-12 m
	CUBIC_FEMTOMETER("cubic fm", "femtometer"),	// 10^-15 m
	
	CELLS("cells", "cells"), // straight count
	PERCENTAGE("%", "percentage");
	
	private final String abbrev;
	private final String label;
	
	private Units(final String abbrev, final String label) {
		this.abbrev = abbrev;
		this.label = label;
	}

	public String getAbbrev() {
		return abbrev;
	}
	public String getLabel() {
		return label;
	}
}
