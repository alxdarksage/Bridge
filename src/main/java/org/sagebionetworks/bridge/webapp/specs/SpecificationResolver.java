package org.sagebionetworks.bridge.webapp.specs;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class SpecificationResolver {

	private Map<String, Specification> specifications = new TreeMap<String, Specification>();

	public void setSpecifications(List<Specification> specifications) {
		if (specifications != null) {
			for (Specification spec : specifications) {
				this.specifications.put(spec.getName(), spec);
			}
		}
	}
	
	public Specification getSpecification(String name) {
		return specifications.get(name);
	}
	
	public Collection<Specification> getAllSpecifications() {
		return specifications.values();
	}
	
}
