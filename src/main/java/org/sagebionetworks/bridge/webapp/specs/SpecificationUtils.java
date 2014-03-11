package org.sagebionetworks.bridge.webapp.specs;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.sagebionetworks.bridge.model.data.units.Units;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class SpecificationUtils {

	public static Map<String,FormElement> toMapByName(List<FormElement> elements) {
		Map<String,FormElement> map = Maps.newHashMap();
		for (FormElement element : elements) {
			if (element.getName() != null) {
				map.put(element.getName(), element);
			}
		}
		return map;
	}
	
	public static List<FormElement> toList(Collection<FormElement> elements) {
		List<FormElement> sum = Lists.newArrayList();
		for (FormElement element : elements) {
			walkTree(sum, element);	
		}
		return sum;
	}
	
	public static List<FormElement> toList(Collection<FormElement> e1, Collection<FormElement> e2) {
		List<FormElement> sum = Lists.newArrayList();
		for (FormElement element : e1) {
			walkTree(sum, element);	
		}
		for (FormElement element : e2) {
			walkTree(sum, element);	
		}
		return sum;
	}
	
	
	public static List<FormElement> toList(FormElement element) {
		List<FormElement> sum = Lists.newArrayList();
		walkTree(sum, element);
		return sum;
	}
	
	public static List<String> getLabelsForUnits(Units... units) {
		List<String> symbols = Lists.newArrayList();
		for (Units unit : units) {
			for (String label : unit.getLabels()) {
				symbols.add(label);
			}
		}
		return symbols;
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
