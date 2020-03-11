package com.sri.ai.util.computation.treecomputation.anytime.core;

import static com.sri.ai.util.Util.forAll;
import static com.sri.ai.util.Util.forEach;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Predicate;
import com.sri.ai.util.base.NullaryFunction;
import com.sri.ai.util.base.Procedure;
import com.sri.ai.util.computation.anytime.api.Approximation;
import com.sri.ai.util.computation.treecomputation.anytime.api.AnytimeEagerTreeComputation;
import com.sri.ai.util.computation.treecomputation.api.TreeComputation;

/**
 * A specialization of {@link AbstractAnytimeTreeComputationBasedOnTreeComputation}
 * providing mechanisms for dealing with the situation in which simplifications are possible but "taint"
 * approximations so that they are no longer updateable to new external contexts.
 * If such updating is needed, it requires recomputing them from their untainted sub-computations,
 * which may be tainted themselves and require re-computation as well.
 * <p>
 * This class keeps track of where simplifications happened so far,
 * so that necessary recomputations are automatically performed without burdening concrete implementations with such details.
 * <p>
 * More precisely, this class provides a base for classes that can simplify an approximation to a more efficient one,
 * under the penalty that the simplified approximation, as well as any other approximations computed from it up the tree,
 * cannot be <i>updated by themselves</i>.
 * <p>
 * We define an approximation to be <i>updateable by itself</i> if it can be modified to the correct approximation
 * given the new external context (as per {@link #refreshFromWithout()})
 * without any modification to or information from their sub-computations.
 * Because "total ignorance" approximations are not computed from sub-computations, they are always considered updateable-by-themselves.
 * <p>
 * When there is need to update, this class uses {@link #computeUpdatedApproximationGivenThatExternalContextHasChangedByItself(Approximation)}
 * for updateable-by-themselves approximations. For the non-updateable-by-themselves approximations,
 * it updates its sub-computations and recomputes them, again keeping track of those that are updateable-by-themselves or not.
 * <p>
 * To perform this task, this class requires implementations of several abstract methods of its own,
 * besides those inherited from its parent class.
 * Please refer to their documentations for requirements on their implementation. 
 * <p>
 * Note that, unlike its super class, this class requires that its sub-computations are also instances of this same class.
 * If this is not followed, class cast exceptions will be thrown.
 * 
 * @author braz
 */
public abstract class AbstractAnytimeTreeComputationWithLossySimplification<T> extends AbstractAnytimeTreeComputationBasedOnTreeComputation<T> {

	////////////////////// ABSTRACT METHODS
	
	@Override
	abstract protected AbstractAnytimeTreeComputationWithLossySimplification<T> makeAnytimeVersion(NullaryFunction<T> baseSub);

	@Override
	abstract protected AbstractAnytimeTreeComputationWithLossySimplification<T> pickNextSubToIterate();

	@Override
	abstract protected boolean evenOneSubWithTotalIgnoranceRendersApproximationEqualToTotalIgnorance();

	/**
	 * Simplifies an approximation to a new one and assumes it is updateable by itself.
	 * Note that if we know simplified approximations are always updateable by themselves,
	 * then this class should not be used at all and {@link AbstractAnytimeTreeComputationBasedOnTreeComputation} should be used instead. 
	 * (For clarity and further elaboration, assuming this class were to be used
	 * then the simplification should simply be made part of {@link #function(List)},
	 * leaving this method as the identity function, in order to avoid an unnecessary recomputation penalty.)
	 */
	abstract protected Approximation<T> simplify(Approximation<T> approximation);
	
	/**
	 * Updates an approximation given that the external context has changed, without resorting
	 * to information in sub computations.
	 * This class assumes that this is always possible as long as no simplification has been performed on this approximation,
	 * and that the sub-approximations used to calculate it are updateable-by-themselves as well.
	 * (if these conditions are not true, then the class automatically takes actions to them true in order to do the update).
	 * @param approximation
	 * @return
	 */
	abstract protected Approximation<T> computeUpdatedApproximationGivenThatExternalContextHasChangedByItself(Approximation<T> approximation);
	
	////////////////////// CONSTRUCTOR
	
	public AbstractAnytimeTreeComputationWithLossySimplification(TreeComputation<T> base, Approximation<T> totalIgnorance) {
		super(base, totalIgnorance);
	}

	////////////////////// SIMPLIFICATION MECHANISM
	
	// The strategy for this class is to "intercept" the setting of the current approximation
	// and make it the simplified version instead.
	// The unsimplified approximation is also kept if the need to update to a new external context arises.
	// When it is time to update, we check if the unsimplified approximation is updateable by itself.
	// If it is, we simply use the abstract method implementing that to obtain a new unsimplified approximation and simplify it again.
	// Otherwise, we update its subs, recompute it from them, and simplify it.

	////////////// SIMPLIFIED AND UNSIMPLIFIED APPROXIMATIONS ACCESS
	
	/**
	 * The unsimplified approximation computed based on sub-approximations.
	 * The current approximation for this anytime computation is possibly a simplification of this.
	 */
	private Approximation<T> unsimplified;
	
	/**
	 * Indicates whether the unsimplified approximation computed from the sub approximations
	 * is updateable by itself, which depends on whether the subs themselves are updateable by themselves.
	 * This is cached and a value of null indicates it needs to be re-assessed.
	 */
	private Boolean currentUnsimplifiedIsUpdateableByItself = null;
	private boolean getCurrentUnsimplifiedApproximationIsUpdateableByItself() {
		if (currentUnsimplifiedIsUpdateableByItself == null) {
			currentUnsimplifiedIsUpdateableByItself = forAll(getSubs(), currentApproximationIsUpdateableByItself);
		}
		return currentUnsimplifiedIsUpdateableByItself;
	}
	
	////////////// KEEPING TRACK OF WHETHER SIMPLIFIED AND UNSIMPLIFIED CURRENT APPROXIMATIONS ARE UPDATEABLE BY THEMSELVES
	
	private boolean currentApproximationIsUpdateableByItself() {
		// The current approximation is only updateable by itself if
		// the current unsimplified approximation is updateable by itself and there has been no simplification.
		// (simplified is the same as unsimplified).
		return  currentUnsimplifiedApproximationIsUpdateableByItself() && getCurrentApproximation() == unsimplified;
	}
	
	private boolean currentUnsimplifiedApproximationIsUpdateableByItself() {
		// The current unsimplified approximation is only updateable by itself if
		// it is total ignorance (assumed to be updateable by itself since it is not computed from sub-computations)
		// or the unsimplified approximation has been computed from subs that are updateable by themselves.
		return  subsHaveNotYetBeenMade() || getCurrentUnsimplifiedApproximationIsUpdateableByItself();
	}

	////////////// INTERSECTION OF setCurrentApproximation
	
	@Override
	public void setCurrentApproximation(Approximation<T> newCurrentApproximation) {
		unsimplified = newCurrentApproximation;
		var simplifiedCurrentApproximation = simplify(unsimplified);
		super.setCurrentApproximation(simplifiedCurrentApproximation);
	}

	////////////// UPDATING
	
	@Override
	public void refreshFromWithout() {
		
		Approximation<T> newCurrentApproximation;
		
		if (currentUnsimplifiedApproximationIsUpdateableByItself()) {
			newCurrentApproximation = 
					computeUpdatedApproximationGivenThatExternalContextHasChangedByItself(getCurrentApproximation());
			setCurrentApproximation(newCurrentApproximation);
		}
		else {
			computeUpdatedCurrentApproximationGivenThatExternalContextHasChangedWithoutIteratingItselfGivenThatCurrentApproximationIsNotUpdateableByItself();
		}
		
	}

	private 
	void
	computeUpdatedCurrentApproximationGivenThatExternalContextHasChangedWithoutIteratingItselfGivenThatCurrentApproximationIsNotUpdateableByItself() {
		forEach(getSubs(), updateCurrentApproximationGivenThatExternalContextHasChangedButWithoutIteratingItself);
		refreshFromWithin();
	}
	
	////////////////// CONVENIENCE
	
	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<? extends AbstractAnytimeTreeComputationWithLossySimplification<T>> getSubs() {
		return (ArrayList<? extends AbstractAnytimeTreeComputationWithLossySimplification<T>>) super.getSubs();
		// TODO: add template argument to these classes specifying sub-computation classes so that this is unnecessary
	}

	private final Predicate<? super AbstractAnytimeTreeComputationWithLossySimplification<T>> currentApproximationIsUpdateableByItself = AbstractAnytimeTreeComputationWithLossySimplification::currentApproximationIsUpdateableByItself;

	private final Procedure<AnytimeEagerTreeComputation<T>> updateCurrentApproximationGivenThatExternalContextHasChangedButWithoutIteratingItself = AnytimeEagerTreeComputation<T>::refreshFromWithout;

}
