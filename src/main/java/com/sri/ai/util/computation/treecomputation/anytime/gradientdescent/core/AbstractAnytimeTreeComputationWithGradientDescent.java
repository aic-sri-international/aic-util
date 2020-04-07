package com.sri.ai.util.computation.treecomputation.anytime.gradientdescent.core;

import static com.sri.ai.util.Util.argmax;

import java.util.List;

import com.sri.ai.util.computation.anytime.api.Anytime;
import com.sri.ai.util.computation.anytime.api.Approximation;
import com.sri.ai.util.computation.anytime.gradientdescent.api.GradientDescentAnytime;
import com.sri.ai.util.computation.treecomputation.anytime.core.AbstractAnytimeTreeComputationBasedOnTreeComputation;
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

public abstract class AbstractAnytimeTreeComputationWithGradientDescent<T> extends AbstractAnytimeTreeComputationBasedOnTreeComputation<T> implements GradientDescentAnytime<T> {

	public AbstractAnytimeTreeComputationWithGradientDescent(TreeComputation<T> base, Approximation<T> totalIgnorance) {
		super(base, totalIgnorance);
	}

	@Override
	public Anytime<T> pickNextSubToIterate() {
		Anytime<T> nextSub = null;
		nextSub = getLargestAbsolutePartialDerivativeSub();
		return nextSub;
	}
	
	private Anytime<T> getLargestAbsolutePartialDerivativeSub() {
		Anytime<T> result = argmax(getSubs(), this::getAbsoluteVolumeVariationWithRespectTo);
		return result;
	}
	
	@Override
	public abstract Approximation<T> function(List<Approximation<T>> subsApproximations);

}
