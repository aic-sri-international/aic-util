package com.sri.ai.util.computation.treecomputation.anytime.api;

import static com.sri.ai.util.Util.in;
import static com.sri.ai.util.Util.mapIntoArrayList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.common.base.Function;
import com.sri.ai.util.base.NullaryFunction;
import com.sri.ai.util.collect.CartesianProductIterator;
import com.sri.ai.util.computation.anytime.api.Approximation;
import com.sri.ai.util.computation.anytime.api.ConvexBoundApproximation;


public abstract class AbstractConvexBoundsApproximationScheme<T> implements ApproximationScheme<T>{

	protected abstract T indicator(T var);
	
	@Override
	public  Approximation<T> totalIgnorance(List<T> components) {
		// create a list of indicator functions
		ArrayList<T> resultList = new ArrayList<>();
		for(T c : components) {
			T i = indicator(c);
			resultList.add(i);
		}
		
		ConvexBoundApproximation<T> result = new ConvexBoundApproximation<>(resultList);
		return result;
	}
	
	public abstract ConvexBoundApproximation<T> uniformDistribution();
	
	@Override
	public Approximation<T> apply(Function<List<T>, T> function,
			List<? extends Approximation<T>> argumentApproximations) {

		List<ConvexBoundApproximation<T>> bounds = new ArrayList<>();
		
		for(Approximation<T> app : argumentApproximations) {
			bounds.add((ConvexBoundApproximation<T>) app);
		}
		
		ConvexBoundApproximation<T> result = applyFunctionToListOfBounds(function, bounds);
		
		return result;
	}
	
	private ConvexBoundApproximation<T> applyFunctionToListOfBounds(Function<List<T>, T> function,
			List<ConvexBoundApproximation<T>> argumentApproximations) {
		
		if(argumentApproximations.size() == 0) {
			ConvexBoundApproximation<T> result = uniformDistribution();
			return result;
		}
		
		ArrayList<NullaryFunction<Iterator<T>>> iteratorForBoundList = 
				mapIntoArrayList(argumentApproximations, bound -> () -> bound.getVertices().iterator());
		
		Iterator<ArrayList<T>> cartesianProduct = new CartesianProductIterator<>(iteratorForBoundList);
		
		if (!cartesianProduct.hasNext()) {
			return null; //TODO : RETURN ERROR message (throw exception)
						 // no "hasNext" means one of the sets is empty, which is an error
		}
		
		ArrayList<T> resultList = new ArrayList<>();
		for (ArrayList<T> element : in(cartesianProduct)) {
			if (element == null || element.get(0) == null) {
				return null;
			}
			T evaluation = function.apply(element);
			resultList.add(evaluation);
		}
		
		ConvexBoundApproximation<T> result = new ConvexBoundApproximation<T>(resultList);
		
		return result;
	}

	
}

