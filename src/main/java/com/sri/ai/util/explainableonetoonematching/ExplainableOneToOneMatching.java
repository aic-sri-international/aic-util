package com.sri.ai.util.explainableonetoonematching;

import static com.sri.ai.util.Util.getFirstNonNullResultOrNull;
import static com.sri.ai.util.Util.set;
import static com.sri.ai.util.Util.setFrom;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * A generic class computing whether two collections have a one-to-one matching according to a {@link Matcher}.
 * Users must also provide a {@link UnmatchedElementMerger}, {@link LeftOverMerger}, and an explanation that indicates perfect matching.  
 * 
 * @author braz
 *
 * @param <T>
 * @param <E>
 */
public class ExplainableOneToOneMatching<T, E> {

	public static <T1, E1> E1 match(
			List<T1> c1,
			List<T1> c2,
			Matcher<? super T1, ? extends E1> matcher,
			UnmatchedElementMerger<? super T1, Set<E1>, ? extends E1> unmatchedElementMerger,
			LeftOverMerger<Set<T1>, ? extends E1> leftOverMerger,
			E1 correctMatchExplanation) {
		
		return new ExplainableOneToOneMatching<T1, E1>(c1, c2, matcher, unmatchedElementMerger, leftOverMerger, correctMatchExplanation).computeMatchingExplanation();
	}

	private List<T> c1;
	@SuppressWarnings("unused")
	private List<T> c2;
	private Matcher<? super T, ? extends E> matcher;
	private UnmatchedElementMerger<? super T, Set<E>, ? extends E> unmatchedElementMerger;
	private LeftOverMerger<Set<T>, ? extends E> leftOverMerger;
	private E correctMatchExplanation;

	/** Intermediary data member keeping track of which elements in second collection have not yet been matched. */
	private LinkedList<T> remainingC2;
	
	public ExplainableOneToOneMatching(
			List<T> c1,
			List<T> c2,
			Matcher<? super T, ? extends E> matcher,
			UnmatchedElementMerger<? super T, Set<E>, ? extends E> unmatchedElementMerger,
			LeftOverMerger<Set<T>, ? extends E> leftOverMerger,
			E correctMatchExplanation) {
		
		this.c1 = c1;
		this.c2 = c2;
		this.remainingC2 = new LinkedList<>(c2);
		this.matcher = matcher;
		this.unmatchedElementMerger = unmatchedElementMerger;
		this.leftOverMerger = leftOverMerger;
		this.correctMatchExplanation = correctMatchExplanation;
	}

	/**
	 * Performs the attempted matching and generates the explanation according to the constructor arguments.
	 */
	public E computeMatchingExplanation() {
		
		E explanationForSomeElementInFirstCollcetionNotMatching =
				getFirstNonNullResultOrNull(c1, this::getExplanationForUnmatchingOrNull);
		
		if (explanationForSomeElementInFirstCollcetionNotMatching != null) {
			return explanationForSomeElementInFirstCollcetionNotMatching;
		}
		
		if (remainingC2.isEmpty()) {
			return correctMatchExplanation;
		}
		
		var explanationOfLeftOverElements = leftOverMerger.apply(setFrom(remainingC2));
		
		return explanationOfLeftOverElements;
	}

	private E getExplanationForUnmatchingOrNull(T element) {
		var explanationsList = getExplanationsForUnmatchingOrNull(element);
		var explanation = mergeExplanationsIfAny(element, explanationsList);
		return explanation;
	}

	private Set<E> getExplanationsForUnmatchingOrNull(T element) {
		Set<E> explanationsForUnmatching = set();
		var remainingC2ListIterator = remainingC2.listIterator();
		while (remainingC2ListIterator.hasNext()) {
			T elementInRemainingC2 = remainingC2ListIterator.next();
			E explanationForUnmatching = matcher.apply(element, elementInRemainingC2);
			if (explanationForUnmatching == null) {
				// there was a match, so remove matched element in C2 and indicate match by returning null 
				remainingC2ListIterator.remove();
				return null;
			}
			else {
				explanationsForUnmatching.add(explanationForUnmatching);
			}
		}
		return explanationsForUnmatching;
	}

	public E mergeExplanationsIfAny(T element, Set<E> explanationsList) {
		return explanationsList == null? null : unmatchedElementMerger.apply(element, explanationsList);
	}

}
