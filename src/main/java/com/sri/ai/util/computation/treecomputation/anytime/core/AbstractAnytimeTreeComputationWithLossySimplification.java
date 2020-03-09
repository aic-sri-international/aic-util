package com.sri.ai.util.computation.treecomputation.anytime.core;

import static com.sri.ai.util.Util.forAll;
import static com.sri.ai.util.Util.forEach;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Predicate;
import com.sri.ai.util.base.NullaryFunction;
import com.sri.ai.util.base.Procedure;
import com.sri.ai.util.computation.anytime.api.Approximation;
import com.sri.ai.util.computation.treecomputation.anytime.api.AnytimeTreeComputation;
import com.sri.ai.util.computation.treecomputation.api.TreeComputation;

/**
 * A specialization of {@link AbstractAnytimeTreeComputation}
 * providing mechanisms for dealing with the situation in which simplifications are possible but "taint"
 * approximations so that updating them to new external contexts require recomputing their own tainted sub-computations.
 * This class keeps track of where simplifications happened so far,
 * so that necessary recomputations are automatically performed without burdening concrete implementations with such details.
 * <p>
 * More precisely, this class provides a base for classes that can simplify an approximation to a more efficient one,
 * under the penalty that the simplified approximation, as well as any other approximations computed from it up the tree,
 * cannot be <i>updated by themselves</i>.
 * We define an approximation to be <i>updateable by itself</i> if it can be modified to the correct approximation
 * given the new external context (as per {@link #updateCurrentApproximationGivenThatExternalContextHasChangedButWithoutIteratingItself()})
 * without any modification or information from their sub-computations.
 * <p>
 * To perform this, this class requires implementations of several abstract methods of its own,
 * besides those inherited from its parent class.
 * Please refer to their documentations for requirements on their implementation. 
 * <p>
 * Note that, unlike its super class, this class requires that its sub-computations are also instances of this same class.
 * If this is not followed, class cast exceptions will be thrown.
 * 
 * @author braz
 */
public abstract class AbstractAnytimeTreeComputationWithLossySimplification<T> extends AbstractAnytimeTreeComputation<T> {

	////////////////////// ABSTRACT METHODS
	
	@Override
	abstract protected AbstractAnytimeTreeComputation<T> makeAnytimeVersion(NullaryFunction<T> baseSub);

	@Override
	abstract protected AbstractAnytimeTreeComputation<T> pickNextSubToIterate();

	@Override
	abstract protected boolean evenOneSubWithTotalIgnoranceRendersApproximationEqualToTotalIgnorance();

	/**
	 * Implements the computation of the unsimplified approximation given sub approximations.
	 */
	abstract protected Approximation<T> functionWithoutSimplification(List<Approximation<T>> subsApproximations);

	/**
	 * Simplifies an approximation to a new one that may not be updateable by itself
	 * (as indicated by the invocation of {@link #updateCurrentApproximationGivenThatExternalContextHasChangedButWithoutIteratingItself()}).
	 * Note that if we know simplified approximations are always updateable by themselves,
	 * then this class should not be used at all and {@link AbstractAnytimeTreeComputation} should be used instead. 
	 * (For clarity and further elaboration, assuming this class were to be used
	 * then the simplification should simply be made part of {@link #functionWithoutSimplification(List)},
	 * leaving this method as the identity function, in order to avoid an unnecessary recomputation penalty.)
	 */
	abstract protected Approximation<T> simplify(Approximation<T> approximation);
	
	/**
	 * Updates an approximation given that the external context has changed, without resorting
	 * to information in sub computations.
	 * This class assumes that this is also possible as long as no simplifications has been performed on this approximation
	 * and that the approximations used to calculate it (coming from its subs)
	 * are such that they could also be updated without resorting to their own subs.
	 * @param approximation
	 * @return
	 */
	abstract protected Approximation<T> computeUpdatedApproximationGivenThatExternalContextHasChangedByItself(Approximation<T> approximation);
	
	////////////////////// CONSTRUCTOR
	
	public AbstractAnytimeTreeComputationWithLossySimplification(TreeComputation<T> base, Approximation<T> totalIgnorance) {
		super(base, totalIgnorance);
	}

	////////////////////// SIMPLIFICATION MECHANISM
	
	// The strategy for this class is to implement the method 'function' so that it keeps track of
	// both unsimplified and simplified approximations, as well as whether the unsimplified approximation
	// is updateable by itself.
	// When it is time to updateCurrentApproximationGivenThatExternalContextHasChangedButWithoutIteratingItself, we check if the unsimplified approximation is updateable by itself.
	// If it is, we simply updateCurrentApproximationGivenThatExternalContextHasChangedButWithoutIteratingItself and simplify it again.
	// Otherwise, we updateCurrentApproximationGivenThatExternalContextHasChangedButWithoutIteratingItself its subs, recompute it from them, and simplify it.

	/**
	 * The unsimplified approximation computed based on sub-approximations.
	 * The current approximation for this anytime computation is possibly a simplification of this.
	 */
	private Approximation<T> unsimplified;
	
	/**
	 * Indicates whether the unsimplified approximation computed from the sub approximations
	 * is updateable by itself,
	 * which depends on whether the subs themselves are updateable by themselves.
	 */
	private boolean unsimplifiedIsUpdateableByItself;
	
	// Invariant:
	// 'unsimplified' contains the unsimplified result of applying 'functionWithoutSimplification' to sub's current approximations.
	// result of function is a simplification of 'unsimplified'
	// 'unsimplifiedIsUpdateableByItself' indicates whether 'unsimplified' has been computed from subs that are updateable by themselves
	
	@Override
	public Approximation<T> function(List<Approximation<T>> subsApproximations) {

//		println();
//		println("AbstractAnytimeTreeComputationWithLossySimplification");
//		println("Entering function for " + this);
//		if (!forAll(getSubs(), currentApproximationIsUpdateableByItself)) {
//			var sub = Util.findFirst(getSubs(), s -> ! s.currentApproximationIsUpdateableByItself());
//			println("Sub not updateable by itself: " + sub);
//			println("Sub's current approximation:", sub.getCurrentApproximation());
//			println("Sub's          unsimplified:", sub.unsimplified);
//			System.exit(-1);
//		}
		
		unsimplified = functionWithoutSimplification(subsApproximations);
		unsimplifiedIsUpdateableByItself = forAll(getSubs(), currentApproximationIsUpdateableByItself);
		var simplified = simplify(unsimplified);
		
//		println();
//		println("AbstractAnytimeTreeComputationWithLossySimplification");
//		println("unsimplified:", unsimplified);
//		println("simplified  :", simplified);
//		println("unsimplifiedIsUpdateableByItself:", unsimplifiedIsUpdateableByItself);
		
		return simplified;
		// Note: we do not invoke setCurrentApproximation here because the super class takes care of doing that with the result of function.
	}

	public boolean currentApproximationIsUpdateableByItself() {
		// The current approximation is only updateable by itself if
		// there has been no simplification (which is assumed to always removes that property)
		// and the unsimplified approximation has been computed from subs that are updateable by themselves.
		return  subsHaveNotYetBeenMade() 
				||
				(
				getCurrentApproximation() == unsimplified
				&&
				unsimplifiedIsUpdateableByItself);
	}
	
	@Override
	public Approximation<T> computeUpdatedCurrentApproximationGivenThatExternalContextHasChangedWithoutIteratingItself() {
//		var canonical = computeUpdatedApproximationGivenThatExternalContextHasChangedByItself(getCurrentApproximation());
//		return canonical;
		
		// This function assumes 'function' has already been executed. This will only have happened if subs have been made yet.
		// If they have not, just return the current approximation without change (since it is a simplex).
		if (subsHaveNotYetBeenMade()) {
			return getCurrentApproximation();
		}
		
		checkInvariant("Checking invariant at the beginning of updating");

		Approximation<T> newVersion;
		
		if (currentApproximationIsUpdateableByItself()) {
			var newUnsimplified = computeUpdatedApproximationGivenThatExternalContextHasChangedByItself(unsimplified);
			if (newUnsimplified != unsimplified) {
				unsimplified = newUnsimplified;
				var newSimplified = simplify(newUnsimplified);
				newVersion = newSimplified;
			}
			else {
				// No changes, which means there was no need for updates
				newVersion = getCurrentApproximation();
			}
		}
		else {
			forEach(getSubs(), updateCurrentApproximationGivenThatExternalContextHasChangedButWithoutIteratingItself);
			var newSimplified = computeApproximationBasedOnSubsCurrentCollectiveApproximation();
			// Note that the above line updates unsimplified and unsimplifiedIsUpdateableByItself as side effects
			newVersion = newSimplified;
		}
		
		return newVersion;
		
	}
	
	@Override
	public void setCurrentApproximation(Approximation<T> newCurrentApproximation) {
		super.setCurrentApproximation(newCurrentApproximation);
		checkInvariant("Checking invariant right at the end of setCurrentApproximation at Lossy level");
	}
	
	private void checkInvariant(String message) {
//		if (!currentApproximationIsUpdateableByItself()) {
//			println("AbstractAnytimeTreeComputationWithLossySimplification");
//			println(message);
//			println("Current approximation not updateable by itself at node", this);
//			System.exit(-1);
//		}
	}

	////////////////// CONVENIENCE
	
	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<? extends AbstractAnytimeTreeComputationWithLossySimplification<T>> getSubs() {
		return (ArrayList<? extends AbstractAnytimeTreeComputationWithLossySimplification<T>>) super.getSubs();
		// TODO: add template argument to these classes specifying sub-computation classes so that this is unnecessary
	}

	private final Predicate<? super AbstractAnytimeTreeComputationWithLossySimplification<T>> currentApproximationIsUpdateableByItself = AbstractAnytimeTreeComputationWithLossySimplification::currentApproximationIsUpdateableByItself;

	private final Procedure<AnytimeTreeComputation<T>> updateCurrentApproximationGivenThatExternalContextHasChangedButWithoutIteratingItself = AnytimeTreeComputation<T>::updateCurrentApproximationGivenThatExternalContextHasChangedButWithoutIteratingItself;

}
