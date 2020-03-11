package com.sri.ai.util.computation.treecomputation.anytime.core;

import static com.sri.ai.util.Util.forAll;
import static com.sri.ai.util.Util.forEach;
import static com.sri.ai.util.Util.mapIntoArrayList;

import java.util.ArrayList;
import java.util.List;

import com.sri.ai.util.Enclosing;
import com.sri.ai.util.base.NullaryFunction;
import com.sri.ai.util.computation.anytime.api.Anytime;
import com.sri.ai.util.computation.anytime.api.Approximation;
import com.sri.ai.util.computation.treecomputation.anytime.api.AnytimeEagerTreeComputation;
import com.sri.ai.util.computation.treecomputation.anytime.api.AnytimeTreeComputation;

public abstract class AbstractLossy<T> implements AnytimeEagerTreeComputation<T> {
	
	/////////// ABSTRACT METHODS

	abstract public Approximation<T> simplify(Approximation<T> approximation);
	
	abstract protected AbstractLossy<T> newInstance(NullaryFunction<Approximation<T>> baseSub);

	/////////// DATA MEMBERS

	private AnytimeTreeComputation<T> base;
	
	/////////// CONSTRUCTOR
	
	public AbstractLossy(AnytimeEagerTreeComputation<T> base) {
		this.base = base;
	}
	
	/////////// GETTERS
	
	public AnytimeTreeComputation<T> getBase() {
		return base;
	}
	
	/////////// SIMPLIFICATION
	
	private Approximation<T> unsimplifiedApproximationUsedToComputeSimplifiedCurrentApproximation;
	private Approximation<T> simplifiedCurrentApproximation;
	
	@Override
	public Approximation<T> getCurrentApproximation() {
		if (getBase().getCurrentApproximation() != unsimplifiedApproximationUsedToComputeSimplifiedCurrentApproximation) {
			unsimplifiedApproximationUsedToComputeSimplifiedCurrentApproximation = getBase().getCurrentApproximation();
			simplifiedCurrentApproximation = simplify(unsimplifiedApproximationUsedToComputeSimplifiedCurrentApproximation);
		}
		return simplifiedCurrentApproximation;
	}

	@Override
	public void setCurrentApproximation(Approximation<T> newCurrentApproximation) {
		throw new Error((new Enclosing()).methodName() + " cannot be invoked for " + getClass() + " because approximations are computed based on base anytime computation.");
	}

	/////////// UPDATING
	
	private boolean currentApproximationIsUpdateableByItself() {
		return
				getCurrentApproximation() == getBase().getCurrentApproximation()
				&&
				unsimplifiedCurrentApproximationIsUpdateableByItself();
	}
	
	private boolean unsimplifiedCurrentApproximationIsUpdateableByItself() {
		return
				subsHaveNotYetBeenMade()
				||
				forAll(getSubs(), AbstractLossy::currentApproximationIsUpdateableByItself);
	}
	
	@Override
	public void refreshFromWithout() {
		if (unsimplifiedCurrentApproximationIsUpdateableByItself()) {
			getBase().refreshFromWithout();
		}
		else {
			forEach(getSubs(), Anytime<T>::refreshFromWithout);
			getBase().refreshFromWithin();
		}
	}

	//////////// PLAIN REDIRECTING
	
	@Override
	public boolean subsHaveNotYetBeenMade() {
		return getBase().subsHaveNotYetBeenMade();
	}

	@Override
	public boolean hasNext() {
		return getBase().hasNext();
	}

	@Override
	public Approximation<T> next() {
		getBase().next();
		return getCurrentApproximation(); // automatically updates simplified computation from base's new approximation
	}

	//////////// SUBS
	
	private ArrayList<? extends AbstractLossy<T>> subs;
	@Override
	public ArrayList<? extends AbstractLossy<T>> getSubs() {
		if (subs == null) {
			subs = mapIntoArrayList(getBase().getSubs(), this::newInstance);
		}
		return subs;
	}

	@Override
	public void refreshFromWithin() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Approximation<T> function(List<Approximation<T>> subsValues) {
		// TODO Auto-generated method stub
		return null;
	}

}
