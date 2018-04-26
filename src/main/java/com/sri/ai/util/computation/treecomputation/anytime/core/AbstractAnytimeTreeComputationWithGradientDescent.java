package com.sri.ai.util.computation.treecomputation.anytime.core;

import static com.sri.ai.util.Util.argmaxForFloatMapping;

import com.sri.ai.util.computation.anytime.api.Anytime;
import com.sri.ai.util.computation.anytime.core.GradientDescentApproximation;
import com.sri.ai.util.computation.treecomputation.api.TreeComputation;

/**
 * This class provides a framework for anytime computation with smart selection of the subs to iterate
 * based on variation of the volume of the produced approximation as to maximize the decrease in volume
 * at each step of the anytime computation
 * 
 * Need to make sure that any implementation of this abstract class produces subs that are
 * GradientDescentAnytime<T> with the method makeAnytimeVersion
 * 
 * @author redouane
 *
 * @param <T>
 */

public abstract class AbstractAnytimeTreeComputationWithGradientDescent<T> extends AbstractAnytimeTreeComputation<T> {

	public AbstractAnytimeTreeComputationWithGradientDescent(TreeComputation<T> base, GradientDescentApproximation<T> totalIgnorance) {
		super(base, totalIgnorance);
	}

	@Override
	protected Anytime<T> pickNextSubToIterate() {
		Anytime<T> nextSub = getLargestAbsolutePartialDerivativeSub();
		return nextSub;
	}
	
	private Anytime<T> getLargestAbsolutePartialDerivativeSub() {
		Anytime<T> result = argmaxForFloatMapping(getSubs(), this::getAbsolutePartialDerivativeWithRespectTo);
		return result;
	}

	protected abstract Float getAbsolutePartialDerivativeWithRespectTo(Anytime<T> sub);
}
