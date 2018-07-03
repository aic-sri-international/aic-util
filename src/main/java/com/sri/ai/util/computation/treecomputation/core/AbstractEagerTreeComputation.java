package com.sri.ai.util.computation.treecomputation.core;

import java.util.List;

import com.sri.ai.util.computation.treecomputation.api.EagerTreeComputationEvaluator;
import com.sri.ai.util.computation.treecomputation.api.TreeComputation;

/**
 * A {@link TreeComputation} whose {@link TreeComputationEvaluator} is defined as an {@link EagerTreeComputationEvaluator} 
 * based on an abstract method {@link #function(List)}.
 * 
 * @author braz
 *
 * @param <T>
 */
public abstract class AbstractEagerTreeComputation<T> implements TreeComputation<T> {
	
	public abstract T function(List<T> subsValues);

	@Override
	public EagerTreeComputationEvaluator<T> getEvaluator() {
		return this::function;
	}
}
