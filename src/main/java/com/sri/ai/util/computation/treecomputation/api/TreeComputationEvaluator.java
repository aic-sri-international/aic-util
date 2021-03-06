package com.sri.ai.util.computation.treecomputation.api;

import java.util.ArrayList;

import com.sri.ai.util.base.NullaryFunction;

/**
 * An interface for objects responsible for evaluating the result of a {@link TreeComputation} based on its sub-computations.
 * The reason this is separate from {@link TreeComputation} is that it allows us to compositionally combine different implementations
 * of {@link TreeComputation} with different evaluation strategies (such as eager and lazy).
 * @author braz
 *
 * @param <T>
 */
@FunctionalInterface
public interface TreeComputationEvaluator<T> {
	
	T apply(ArrayList<? extends NullaryFunction<T>> subs);

}
