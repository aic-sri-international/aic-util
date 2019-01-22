package com.sri.ai.util.planning.core;

import static com.sri.ai.util.Util.fill;
import static com.sri.ai.util.Util.join;
import static com.sri.ai.util.Util.list;
import static com.sri.ai.util.collect.FunctionIterator.functionIterator;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import com.sri.ai.util.planning.api.Plan;

public abstract class AbstractCompoundPlan implements Plan {
	
	protected abstract String operatorName();
	
	private List<? extends Plan> subPlans;
	
	public AbstractCompoundPlan(List<? extends Plan> subPlans) {
		this.subPlans = subPlans;
	}

	public List<Plan> getSubPlans() {
		return Collections.unmodifiableList(subPlans);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((subPlans == null) ? 0 : subPlans.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractCompoundPlan other = (AbstractCompoundPlan) obj;
		if (subPlans == null) {
			if (other.subPlans != null)
				return false;
		} else if (!subPlans.equals(other.subPlans))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return operatorName() + "(" + join(getSubPlans()) + ")";
	}

	@Override
	public String nestedString(int level) {
		return 
				padding(level) + operatorName() + "(\n"
				+ join("\n", functionIterator(getSubPlans(), s -> s.nestedString(level + 1))) + ")"; 
	}

	public String padding(int level) {
		return fill(level*4, ' ');
	}

	/**
	 * Flattens a given collection by recognizing items in it in a given class that can be expanded into sub-items.
	 * @param items
	 * @param classToFlatten
	 * @param getSubItems
	 * @return
	 */
	public static <T, T1> List<? extends T> flatten(List<? extends T> items, Class<T1> classToFlatten, Function<T1, List<? extends T>> getSubItems) {
		List<T> result = list();
		for (T item : items) {
			if (classToFlatten.isInstance(item)) {
				@SuppressWarnings("unchecked")
				T1 itemAsT1 = (T1) item;
				List<? extends T> flattenedItem = flatten(getSubItems.apply(itemAsT1), classToFlatten, getSubItems);
				result.addAll(flattenedItem);
			}
			else {
				result.add(item);
			}
		}
		return result;
	}
}
