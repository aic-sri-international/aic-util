package com.sri.ai.util.computation.treecomputation.anytime.core;

import static com.sri.ai.util.Util.forAll;
import static com.sri.ai.util.Util.forEach;
import static com.sri.ai.util.Util.map;
import static com.sri.ai.util.Util.mapIntoArrayList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sri.ai.util.base.NullaryFunction;
import com.sri.ai.util.computation.anytime.api.Anytime;
import com.sri.ai.util.computation.anytime.api.Approximation;
import com.sri.ai.util.computation.treecomputation.anytime.api.AnytimeEagerTreeComputation;

/**
 * A specialization of {@link AbstractAnytimeEagerTreeComputation}
 * that wraps a base {@link AnytimeEagerTreeComputation} but adds the ability to
 * simplify approximations to a more efficient representation that however does not support {@link #refreshFromWithout()}
 * without recomputation.
 * <p>
 * In other words, situation in which simplifications are possible but "taint"
 * approximations so that they are no longer updateable to new external contexts.
 * If such updating is needed, it requires recomputing them from their untainted sub-computations,
 * which may be tainted themselves and require re-computation as well.
 * <p>
 * This class keeps track of where simplifications happened so far,
 * so that necessary recomputations are automatically performed without burdening concrete implementations with such details.
 * <p>
 * More precisely, this class wraps itself around an {@link AnytimeEagerTreeComputation}
 * which is used for providing the underlying tree,
 * but wraps each node in an instance of self that contains both an unsimplified approximation
 * computed from sub-approximations, and a simplified approximation.
 * <p>
 * We define an approximation to be <i>updateable by itself</i> if it can be modified to the correct approximation
 * given the new external context (as per {@link #refreshFromWithout()})
 * without any modification to or information from their sub-computations.
 * Because "total ignorance" approximations are not computed from sub-computations,
 * they are always considered updateable-by-themselves.
 * <p>
 * When there is need to update, this class uses {@link #updateByItself(Approximation)}
 * for updateable-by-themselves approximations. For the non-updateable-by-themselves approximations,
 * it updates its sub-computations and recomputes them, again keeping track of those that are updateable-by-themselves or not.
 * 
 * DEPRECATED: the idea was to have a non-intrusive class that could piggyback on a non-simplified computation.
 * However, in the course of writing it I realized that it still depends on the normal iteration of the base computation,
 * which is designed to occur while computing the non-simplified values.
 * Therefore, this will still require all non-simplified values to be computed, thus defeating the purpose
 * of the class which was to prevent some of that computation.
 * Perhaps there would be a way to iterate the base computation without actually computing its values,
 * but then the base computation would have to become somewhat unnaturally complicated, again defeating the purpose
 * of leaving it simple and untouched.
 * 
 * @author braz
 */
@Deprecated
public abstract class AbstractAnytimeEagerTreeComputationWithSimplification<T> extends AbstractAnytimeEagerTreeComputation<T> {
	
	/////////// ABSTRACT METHODS

	/**
	 * Implements the construction of an instance of this class based on a base approximate computation (typically a sub of this one's base). 
	 * @param base
	 * @return
	 */
	abstract protected AbstractAnytimeEagerTreeComputationWithSimplification<T> newInstance(NullaryFunction<Approximation<T>> base);
	
	/**
	 * Implements the simplification of approximations.
	 */
	abstract public Approximation<T> simplify(Approximation<T> approximation);

	/**
	 * Implements the updating of an approximation that is updateable-by-itself. 
	 */
	abstract public Approximation<T> updateByItself(Approximation<T> approximation);

	/////////// DATA MEMBERS

	private AnytimeEagerTreeComputation<T> base;
	
	private Approximation<T> unsimplified;
	private Approximation<T>   simplified;
	
	/////////// CONSTRUCTOR
	
	public AbstractAnytimeEagerTreeComputationWithSimplification(AnytimeEagerTreeComputation<T> base) {
		super(base.getTotalIgnorance());
		this.base = base;
		this.unsimplified = base.getTotalIgnorance();
		this.simplified = simplify(this.unsimplified);
	}
	
	/////////// GETTERS
	
	public AnytimeEagerTreeComputation<T> getBase() {
		return base;
	}

	/////////// FUNCTION

	@Override
	public Approximation<T> function(List<Approximation<T>> subsApproximations) {
		unsimplified = getBase().function(subsApproximations);
		simplified = simplify(unsimplified);
//		if (!unsimplifiedCurrentApproximationIsUpdateableByItself()) {
//			unsimplified = null; // it won't be useful anymore since updating it will require recomputation
//		}
		return simplified;
	}
	
	@Override
	public Approximation<T> next() {
		getBase().next(); // TODO: causing an exception because sometimes it gets here without a next element. Not sure why, since this object has been picked for iteration with next precisely because there was a next element. In any case, it is not worth debugging it right now for the deprecation reasons given in class javadoc.
		return super.next();
	}

	/////////// UPDATING

	@Override
	public void refreshFromWithout() {
		if (unsimplifiedCurrentApproximationIsUpdateableByItself()) {
			unsimplified = updateByItself(unsimplified);
			simplified = simplify(unsimplified);
			setCurrentApproximation(simplified);
			// TODO: the first abstract implementation should have a method delegating this computation and setting the current approximation itself.
		}
		else {
			forEach(getSubs(), Anytime<T>::refreshFromWithout);
			refreshFromWithin();
			// TODO: the first abstract implementation should have a method delegating this computation and setting the current approximation itself.
			// Then here we would just need the delegated computation and return it, without worrying about setting the current implementation.
		}
	}

	private boolean unsimplifiedCurrentApproximationIsUpdateableByItself() {
		return
				subsHaveNotYetBeenMade()
				||
				forAll(getSubs(), AbstractAnytimeEagerTreeComputationWithSimplification::currentApproximationIsUpdateableByItself);
	}

	private boolean currentApproximationIsUpdateableByItself() {
		return
				(unsimplified != null && simplified == unsimplified)
				&&
				unsimplifiedCurrentApproximationIsUpdateableByItself();
	}

	/////////////// SUBS
	
	@Override
	public ArrayList<? extends Anytime<T>> makeSubs() {
		return mapIntoArrayList(getBase().getSubs(), this::makeSubForBaseSub);
	}

	private Map<NullaryFunction<Approximation<T>>, AbstractAnytimeEagerTreeComputationWithSimplification<T>> fromBaseSubToSub = map();
	
	private AbstractAnytimeEagerTreeComputationWithSimplification<T> makeSubForBaseSub(NullaryFunction<Approximation<T>> baseSub) {
		var newSub = newInstance(baseSub); 
		fromBaseSubToSub.put(baseSub, newSub); 
		return newSub;
	}

	@Override
	public Anytime<T> pickNextSubToIterate() {
		var nextBaseSubToIterate = getBase().pickNextSubToIterate();
		if (nextBaseSubToIterate == null) {
			return null;
		}
		else {
			return fromBaseSubToSub.get(nextBaseSubToIterate);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<? extends AbstractAnytimeEagerTreeComputationWithSimplification<T>> getSubs() {
		return (ArrayList<? extends AbstractAnytimeEagerTreeComputationWithSimplification<T>>) super.getSubs();
	}
	
	/////////////// TOTAL IGNORANCE EFFECT
	
	@Override
	public boolean evenOneSubWithTotalIgnoranceRendersApproximationEqualToTotalIgnorance() {
		return getBase().evenOneSubWithTotalIgnoranceRendersApproximationEqualToTotalIgnorance();
	}
}
