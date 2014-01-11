package org.sagebionetworks.bridge.webapp.specs;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;

/**
 * This is a large mess and there are a couple of older measure and unit libraries/APIs
 * out there that we should perhaps use, or we'll make mistakes in conversion or 
 * reporting.
 */
public enum Units {
	
	LITER("liter", "L"),
	DECILITER("deciliter", "dL"), 		// 10^-1 L
	CENTILITER("centiliter", "cL"), 	// 10^-2 L
	MILLILITER("millilter", "mL"), 		// 10^-3 L
	MICROLITER("microliter", "mcL", "uL", "µL"), 	// 10^-6 L or µL or uL
	NANOLITER("nanoliter", "nL"), 		// 10^-9 L
	PICOLITER("picoliter", "pL"), 		// 10^-12 L
	FEMTOLITER("femtoliter", "fL"),		// 10^-15
	
	GRAM("gram", "g"),
	DECIGRAM("decigram", "dg"),			// 10^-1 g
	CENTIGRAM("centigram", "cg"),		// 10^-2 g
	MILLIGRAM("milligram", "mg"),		// 10^-3 g
	MICROGRAM("microgram", "mcg"),		// 10^-6 g
	NANOGRAM("nanogram", "ng"),			// 10^-9 g
	PICOGRAM("picogram", "pg"),			// 10^-12 g
	FEMTOGRAM("femtogram", "fg"),		// 10^-15 g
	
	MOLE("mole", "mol"),
	DECIMOLE("decimole", "dmol"),		// 10^-1 mol
	CENTIMOLE("centimole", "cmol"),		// 10^-2 mol
	MILLIMOLE("millimole", "mmol"),		// 10^-3 mol
	MICROMOLE("micromole", "mcmol"),	// 10^-6 mol
	NANOMOLE("nanomole", "nmol"),		// 10^-9 mol
	PICOMOLE("picomole", "pmol"),		// 10^-12 mol
	FEMTOMOLE("femtomole", "fmol"),		// 10^-15 mol

	CUBIC_METER("cubic meter", "cu m"),
	CUBIC_DECIMETER("decimeter", "cu dm"),		// 10^-1 m
	CUBIC_CENTIMETER("centimeter", "cu cm"),	// 10^-2 m
	CUBIC_MILLIMETER("millimeter", "cu mm"),	// 10^-3 m
	CUBIC_MICROMETER("micrometer", "cu mc"),	// 10^-6 m
	CUBIC_NANOMETER("nanometer", "cu nm"),		// 10^-9 m
	CUBIC_PICOMETER("picometer", "cu pm"),		// 10^-12 m
	CUBIC_FEMTOMETER("femtometer", "cu fm"),	// 10^-15 m
	
	PERCENTAGE("percentage", "%"),
	
	THOUSANDS_PER_MICROLITER("thousands per microliter", Units.MICROLITER, "K"),
	BILLIONS_PER_LITER("billions per liter", Units.LITER, "10e9", "9"),
	
	MILLIONS_PER_MICROLITER("millions per microliter", Units.MICROLITER, "M"),
	MILLIONS_PER_CUBIC_MILLIMETER("millions per cubic millimeter", Units.CUBIC_MILLIMETER, "M"),
	TRILLIONS_PER_LITER("trillions per liter", Units.LITER, "10e12", "12");

	private final String label;
	private final List<String> symbols;
	
	private Units(final String label, final String... symbols) {
		this.label = label;
		this.symbols = Lists.newArrayList(symbols);
	}
	
	private Units(final String label, final Units consequent, final String... antecedents) {
		this.label = label;
		this.symbols = Lists.newArrayListWithCapacity(consequent.symbols.size());
		for (String symbol : consequent.symbols) {
			for (String ant : antecedents) {
				this.symbols.add(ant+"/"+symbol);	
			}
		}
	}

	public String getLabel() {
		return label;
	}
	public List<String> getSymbols() {
		return symbols;
	}
	
	public static Units unitFromString(String string) {
		if (!StringUtils.isBlank(string)) {
			for (Units unit : values()) {
				if (string.equals(unit.label)) {
					return unit;
				}
				if (unit.symbols.contains(string)) {
					return unit;
				}
			}
		}
		return null;
	}
}
