package org.sagebionetworks.bridge.webapp.specs;

import java.util.List;

import com.google.common.collect.Lists;

public class SpecificationUtils {

	public static List<FormElement> toList(List<FormElement> elements) {
		List<FormElement> sum = Lists.newArrayList();
		for (FormElement element : elements) {
			walkTree(sum, element);	
		}
		return sum;
	}
	
	public static List<FormElement> toList(FormElement element) {
		List<FormElement> sum = Lists.newArrayList();
		walkTree(sum, element);
		return sum;
	}
	
	private static void walkTree(List<FormElement> sum, FormElement element) {
		sum.add(element);
		if (element.getChildren() != null) {
			for (FormElement child : element.getChildren()) {
				walkTree(sum, child);
			}
		}
	}	
}
