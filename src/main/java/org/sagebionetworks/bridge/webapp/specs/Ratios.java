package org.sagebionetworks.bridge.webapp.specs;

public enum Ratios {
	
	GRAMS_PER_DECILITER(Units.GRAM, Units.DECILITER),
	GRAMS_PER_LITER(Units.GRAM, Units.LITER),
	
	MILLIMOLES_PER_LITER(Units.MILLIMOLE, Units.LITER),
	
	CELLS_PER_CMM(Units.CELLS, Units.CUBIC_MILLIMETER),
	CELLS_PER_MICROLITER(Units.CELLS, Units.MICROLITER),
	CELLS_PER_LITER(Units.CELLS, Units.LITER);
	
	private final Units antecedent;
	private final Units consequent;
	
	private Ratios(Units antecedent, Units consequent) {
		this.antecedent = antecedent;
		this.consequent = consequent;
	}
	
	public String getAbbrev() {
		return String.format("%s/%s", antecedent.getAbbrev(), consequent.getAbbrev());
	}
	public String getLabel() {
		return String.format("%s per %s", antecedent.getAbbrev(), consequent.getAbbrev());
	}
}
