package org.sagebionetworks.bridge.webapp.specs;

/**
 * A grid, but here we're showing some subset of the data under a specific key, that 
 * should be created by Specification.postProcessParticipantDataRows().
 *
 */
public class TabularGroup extends FormGroup {

	private String modelName;
	
	public TabularGroup(final String label, final String modelName) {
		super(UIType.TABULAR, label);
		this.modelName = modelName;
	}

	public String getModelName() {
		return modelName;
	}
	
}
