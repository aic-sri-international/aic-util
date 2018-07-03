package com.sri.ai.util.computation.treecomputation.api;

import java.util.ArrayList;

import com.sri.ai.util.base.NullaryFunction;

/**
 * A {@link TreeComputationEvaluator} that is lazy, that is, computes just one of the sub-computation's result,
 * tries to simplify the function based on that, and eliminates sub-computations that are made irrelevant.
 *  
 * @author braz
 *
 * @param <T>
 */
public interface LazyTreeComputationEvaluator<T> extends TreeComputationEvaluator<T> {
	
	/**
	 * Decides which sub-computation is to be evaluated next, or <code>null</code> if result is already determined.
	 * @return
	 */
	NullaryFunction<T> pickNextSubToBeEvaluated();

	/**
	 * Simplifies the function being evaluated according to result from given sub-computation.
	 * @return
	 */
	void simplifyFunctionWithValueForSub(NullaryFunction<T> nextSub, T nextSubValue);

	/**
	 * Returns the result of the sub-computation if already determined, or <code>null</code> otherwise.
	 * Must be determined if {@link #pickNextSubToBeEvaluated()} returns <code>null</code>,
	 * and return <code>null</code> if {@link #pickNextSubToBeEvaluated()} does not (that is, there are
	 * still sub-computations that need to be evaluated).
	 * @return
	 */
	T getResult();

	@Override
	default T apply(ArrayList<? extends NullaryFunction<T>> subs) {
		NullaryFunction<T> nextSub;
		if ((nextSub = pickNextSubToBeEvaluated()) != null) {
			T nextSubValue = nextSub.apply();
			simplifyFunctionWithValueForSub(nextSub, nextSubValue);
		}
		T result = getResult();
		return result;
	}

}
