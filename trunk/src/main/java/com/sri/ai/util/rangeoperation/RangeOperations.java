package com.sri.ai.util.rangeoperation;

import java.util.*;

import com.google.common.base.Predicate;
import com.sri.ai.util.Util;

/**
 * <code>RangeOperations</code> is a facility for
 * easily writing and efficiently computing expressions with nested cumulative operators
 * (for example, <code>\sum_i \prod_j f(i,j)</code>).
 * <p>
 * It allows easy writing of these expressions through its method {@link #run(Object...)},
 * best explained by example:
 * <p>
 * <code>
 * Object result = RangeOperations.run(Averaging("x", 0, 10), X());
 * </code>
 * <p>
 * computes the average of integers from 0 to 10.
 * <p>
 * {@link #run(Object...)} receives a variable number of arguments,
 * which can be, among other types (see below), {@link RangeOperation} and {@link DAEFunction}. 
 * {@link #Averaging()} is a method returning an instance of {@link #Averaging},
 * an extension of {@link RangeOperation}.
 * {@link #X()} is a method returning an instance of {@link #X}, an extension of {@link DAEFunction}
 * returning the value of variable <code>"x"</code> in an implicit environment (of type {@link DependencyAwareEnvironment})
 * which is passed to the {@link DAEFunction}. 
 * <p>
 * Therefore, <code>run(Averaging("x", 0, 10), X())</code> denotes an operation
 * setting variable <code>"x"</code> to values from 0 to 10 and averaging over them,
 * and returns that value.
 * <p>
 * Another example is:
 * <p>
 * <code>
 * Object result = RangeOperations.run(Axis("x", 0, 20), Averaging("y", 0, 10), new F());
 * </code>
 * <p>
 * computes a list with 21 elements,
 * where the x-th element contains the average, over values of <code>"y"</code> from 0 to 10,
 * of <code>F()</code> evaluated on an environment with current values for <code>"x"</code> and <code>"y"</code>. 
 * <p>
 * An arbitrary number of range operations can be used, and will be performed in the order they are given.
 * User-defined extensions of {@link RangeOperation} and {@link DAEFunction} can be used as well.
 * See the documentation on {@link RangeOperation} for details on how to use it.
 * <p>
 * An important feature of this framework is automatic caching.
 * Suppose <code>F</code> above only depends on x.
 * It would then be wasteful to recalculate it for every new value of y, which is being used as a counter only.
 * This does not happen, however, because {@link DependencyAwareEnvironment} keeps track of such dependencies
 * automatically.
 * IMPORTANT: for this automatic dependency management to occur even for sub-functions inside F,
 * they must be calculated with the {@link DependencyAwareEnvironment#getResultOrRecompute(DAEFunction)} method.
 * The function will always be recomputed if {@link DAEFunction#isRandom()} returns <code>true</code>,
 * or an ancestor function is random.
 * <p>
 * If {@link #run(Object...)} receives Strings as arguments, they are assumed to be variables to be
 * put in the environment with the object right after them as value.
 * If an {@link DependencyAwareEnvironment} is found, it replaces the default (initially empty) environment,
 * removing previous variable values. Subsequent variables are added to this environment.
 * <p>
 * As a convenience, this class already provides a few {@link RangeOperation} extensions:
 * {@link #Averaging(String, int, int, int)}, {@link #Axis(String, int, int, int)} and 
 * {@link #Summation(String, int, int)}.
 * @author Rodrigo
 */
public class RangeOperations {

	///////////////////// CLASS CORE ///////////////////

	protected ArrayList<? extends RangeOperation> rangeOperations;
	protected DAEFunction function;
	protected DependencyAwareEnvironment environment;

	/**
	 * See class ({@link RangeOperations}) documentation. 
	 */
	public static Object run(Object ... arguments) {
		List<RangeOperation> rangeOperations = new LinkedList<RangeOperation>();
		DependencyAwareEnvironment environment = new DependencyAwareEnvironment();
		DAEFunction function = null;
		for (int i = 0; i < arguments.length; i++) {
			Object argument = arguments[i];
			if (argument instanceof RangeOperation) {
				rangeOperations.add((RangeOperation) argument);
			}
			else if (argument instanceof DependencyAwareEnvironment) {
				environment = (DependencyAwareEnvironment) argument;
			}
			else if (argument instanceof String) {
				String variable = (String) argument;
				Object value = arguments[++i];
				environment.put(variable, value);
			}
			else if (argument instanceof DAEFunction) {
				function = (DAEFunction) argument;
			}
		}

		RangeOperations rangeOperationsObject = new RangeOperations(environment, rangeOperations, function);
		Object result = rangeOperationsObject.run();
		return result;
	}

	public static Object run(List<? extends RangeOperation> rangeOperations, DAEFunction function) {
		return (new RangeOperations(new DependencyAwareEnvironment(), rangeOperations, function)).run();
	}

	public <T extends RangeOperation> RangeOperations(DependencyAwareEnvironment environment, List<T> rangeOperations, DAEFunction function) {
		this.environment = environment;
		this.rangeOperations = new ArrayList<T>(rangeOperations);
		this.function = function;
		for (RangeOperation range : rangeOperations) {
			range.getRange().setEnvironment(environment);
		}
	}

	protected Object run() {
		return run(0);
	}

	protected Object run(int i) {
		if (i == rangeOperations.size()) {
			return environment.getResultOrRecompute(function);
		}

		RangeOperation rangeOp = rangeOperations.get(i);
		rangeOp.getOperator().initialize();
		for (rangeOp.initialize(); rangeOp.hasNext(); ) {
			rangeOp.next();
			Object subresult = run(i+1); 
			rangeOp.getOperator().increment(subresult);
		}
		return rangeOp.getOperator().getResult();
	}

	//////////////////// END OF CLASS CORE ///////////////////

	//////////////////// CONVENIENCE CLASSES AND METHODS ///////////////////

	/** Convenience method for constructing an {@link Axis}. */
	public static Axis Axis(Range range) {
		return new Axis(range);
	}

	/** Convenience method for constructing an {@link Axis}. */
	public static Axis Axis(String name, int first, int last, int step) {
		return new Axis(name, first, last, step);
	}

	/** Convenience method for constructing an {@link Axis}. */
	public static Axis Axis(String name, int first, int last) {
		return new Axis(name, first, last);
	}

	/** Convenience method for constructing an {@link Axis}. */
	public static Axis Axis(String name, int first, int last, float rate) {
		return new Axis(name, first, last, rate);
	}

	/** Convenience method for constructing an {@link Axis}. */
	public static Axis Axis(String name, Collection collection) {
		return new Axis(name, collection);
	}

	/** Convenience method for constructing a {@link Summation}. */
	public static Summation Summation(String name, int first, int last, int step) {
		return new Summation(name, first, last, step);
	}

	/** Convenience method for constructing a {@link Summation}. */
	public static Summation Summation(String name, int first, int last) {
		return new Summation(name, first, last);
	}

	/** Convenience method for constructing an {@link Averaging}. */
	public static Averaging Averaging(Range range) {
		return new Averaging(range);
	}

	/** Convenience method for constructing an {@link Averaging}. */
	public static Averaging Averaging(String name, int first, int last, int step) {
		return new Averaging(name, first, last, step);
	}

	/** Convenience method for constructing an {@link Averaging}. */
	public static Averaging Averaging(String name, int first, int last) {
		return new Averaging(name, first, last);
	}

	/** Convenience method for constructing an {@link PredicatedAveraging}. */
	public static PredicatedAveraging PredicatedAveraging(String name, Predicate<Object> predicate, int first, int last) {
		return new PredicatedAveraging(name, predicate, first, last);
	}

	//////////////////// END OF CONVENIENCE CLASSES AND METHODS ///////////////////

	//////////////////// BEGINNING OF UTIL METHODS ///////////////////

	/**
	 * Incrementally calculates component-wise averages, given previously calculated averages
	 * (out of n numbers) and a list of new numbers.
	 * The average list is filled with the appropriate number of zeros if it is empty.
	 * The result is stored in-place, destroying the previous average list. 
	 */
	static public List incrementalComputationOfComponentWiseAverage(List<Number> average, int n, List newItems) {
		if (average == null) {
			Util.fatalError("Util.incrementalComputationOfComponentWiseAverage must receive a non-null List");
		}

		if (average.size() == 0) {
			for (int i = 0; i != newItems.size(); i++) {
				average.add(new Double(0));
			}
		}

		for (int i = 0; i != newItems.size(); i++) {
			double currentAverage = ((Double) average.get(i)).doubleValue();
			double newItem        = ((Double) newItems.get(i)).doubleValue();
			double newAverage     = (currentAverage * n + newItem) / (n + 1); 
			average.set(i, new Double(newAverage));
		}

		return average;
	}

	/**
	 * A more general version of {@link #incrementalComputationOfComponentWiseAverage(List, int, List)}
	 * that operates on lists of lists of arbitrary depth, including depth 0, that is, on {@link Number}s.
	 * It is in-place and returns <code>average</code> if given objects are lists, or returns a new Number otherwise.
	 */
	public static Object incrementalComponentWiseAverageArbitraryDepth(Object average, int n, Object newItems) {
		if (average instanceof Number) {
			return (((Number)average).doubleValue()*n + ((Number)newItems).doubleValue())/(n + 1);
		}
		@SuppressWarnings("unchecked")
		ListIterator<Number> averageIterator = ((List<Number>)average).listIterator();
		ListIterator newItemsIt = ((List)newItems).listIterator();
		while (averageIterator.hasNext()) {
			Object averageElement = averageIterator.next();
			Object newItemsElement = newItemsIt.next();
			Number newAverageElement = (Number) incrementalComponentWiseAverageArbitraryDepth(averageElement, n, newItemsElement);
			if (newAverageElement != averageElement) {
				averageIterator.set(newAverageElement);
			}
		}
		return average;
	}

	//////////////////// END OF UTIL METHODS ///////////////////
}
