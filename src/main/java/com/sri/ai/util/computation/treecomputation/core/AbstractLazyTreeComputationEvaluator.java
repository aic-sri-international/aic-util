package com.sri.ai.util.computation.treecomputation.core;

import static com.sri.ai.util.Util.set;

import java.util.ArrayList;
import java.util.Set;

import com.sri.ai.util.base.NullaryFunction;
import com.sri.ai.util.computation.treecomputation.api.TreeComputationEvaluator;

/**
 * A {@link TreeComputationEvaluator} that is lazy, that is, computes just one of the sub-computation's result,
 * tries to simplify the function based on that, and eliminates sub-computations that are made irrelevant.
 *  
 * @author braz
 *
 * @param <T>
 */
public abstract class AbstractLazyTreeComputationEvaluator<T> implements TreeComputationEvaluator<T> {
	
	private ArrayList<? extends NullaryFunction<T>> subs;
	private Set<NullaryFunction<T>> alreadyEvaluatedSubs;
	
	protected abstract void reset();
	
	/**
	 * Register sub-computations to be used
	 * @return
	 */
	protected void registerSubs(ArrayList<? extends NullaryFunction<T>> subs) {
		this.subs = subs;
		this.alreadyEvaluatedSubs = set();
	}
	
	protected boolean hasAlreadyBeenEvaluated(NullaryFunction<T> sub) {
		boolean result = alreadyEvaluatedSubs.contains(sub);
		return result;
	}

	/**
	 * Decides which sub-computation is to be evaluated next, or <code>null</code> if result is already determined.
	 * @return
	 */
	protected abstract NullaryFunction<T> pickNextSubToBeEvaluated();

	/**
	 * Simplifies the function being evaluated according to result from given sub-computation.
	 * @return
	 */
	protected abstract void simplifyFunctionWithValueForSub(NullaryFunction<T> nextSub, T nextSubValue);

	/**
	 * Returns the result of the sub-computation if already determined (if not already determined, usage is illegal and undefined).
	 * @return
	 */
	protected abstract T finishComputingResultOnceAllRelevantSubComputationsHaveBeenTakenIntoAccount();

	protected ArrayList<? extends NullaryFunction<T>> getSubs() {
		return subs;
	}
	
	@Override
	public T apply(ArrayList<? extends NullaryFunction<T>> subs) {
		reset();
		registerSubs(subs);
		NullaryFunction<T> nextSub;
		while ((nextSub = pickNextSubToBeEvaluated()) != null) {
			T nextSubValue = evaluate(nextSub);
			simplifyFunctionWithValueForSub(nextSub, nextSubValue);
		}
		T result = finishComputingResultOnceAllRelevantSubComputationsHaveBeenTakenIntoAccount();
		return result;
	}

	private T evaluate(NullaryFunction<T> nextSub) {
		T nextSubValue = nextSub.apply();
		alreadyEvaluatedSubs.add(nextSub);
		return nextSubValue;
	}
}
