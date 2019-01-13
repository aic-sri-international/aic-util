package com.sri.ai.util.function.core.values;

import static com.sri.ai.util.collect.FunctionIterator.functionIterator;

import java.math.BigDecimal;
import java.util.Iterator;

import org.apache.commons.lang3.Validate;

import com.sri.ai.util.collect.BigDecimalIterator;
import com.sri.ai.util.function.api.values.Value;
import com.sri.ai.util.function.api.variables.SetOfValues;

public class SetOfRealValues implements SetOfValues {

	private BigDecimal first;
	private BigDecimal step;
	private BigDecimal last;

	/**
	 * Lower bound of values for which we determine an index -- default is first
	 */
	private double lowerBoundForDiscretizedValue;

	/**
	 * Upper bound of values for which we determine an index -- default is last
	 */
	private double upperBoundForDiscretizedValue;
	
	/**
	 * Construct a set of real values
	 * <p>
	 * The set starts with <code>first</code>, increments by <code>step</code>, and will not exceed
	 *  {@link Integer#MAX_VALUE}
	 *
	 * @param first starting value
	 * @param step increment
	 */
	public SetOfRealValues(int first, BigDecimal step) {
		this(first, step, Integer.MAX_VALUE);
	}

	/**
	 * Construct a set of real values
	 * <p>
	 * The set starts with <code>first</code>, increments by <code>step</code>, and will not exceed
	 *  {@link Integer#MAX_VALUE}
	 *
	 * @param first starting value
	 * @param step increment
	 */
	public SetOfRealValues(String first, String step) {
		this(first, step, Integer.toString(Integer.MAX_VALUE));
	}

	/**
	 * Construct a set of real values
	 * <p>
	 * The set starts with <code>first</code>, increments by <code>step</code>, and will not exceed
	 *  <code>last</code>
	 *
	 * @param first starting value
	 * @param step increment
	 * @param last maximum value
	 */
	public SetOfRealValues(int first, BigDecimal step, int last) {
		Validate.notNull(step, "step cannot be null");
		Validate.isTrue(step.compareTo(BigDecimal.ZERO) > 0, "step must be greater than zero");

		this.first = new BigDecimal(first);
		this.step = step;
		this.last = new BigDecimal(last);
		this.lowerBoundForDiscretizedValue = this.first.doubleValue();
		this.upperBoundForDiscretizedValue = this.last.doubleValue();
	}

	/**
	 * Construct a set of real values
	 * <p>
	 * The set starts with <code>first</code>, increments by <code>step</code>, and will not exceed
	 *  <code>last</code>
	 *
	 * @param first starting value
	 * @param step increment
	 * @param last maximum value
	 */
	public SetOfRealValues(String first, String step, String last) {
		Validate.notNull(first, "first cannot be null");
		Validate.notNull(step, "step cannot be null");
		Validate.notNull(last, "last cannot be null");

		this.first = new BigDecimal(first);
		this.step = new BigDecimal(step);
		this.last = new BigDecimal(last);
		this.lowerBoundForDiscretizedValue = this.first.doubleValue();
		this.upperBoundForDiscretizedValue = this.last.doubleValue();

		Validate.isTrue(this.step.compareTo(BigDecimal.ZERO) > 0, "step must be greater than zero");
	}

	/**
	 * Construct a set of real values
	 * <p>
	 * The set starts with <code>first</code>, increments by <code>step</code>, and will not exceed
	 *  <code>last</code>
	 *
	 * @param first starting value
	 * @param step increment
	 * @param last maximum value
	 */
	public SetOfRealValues(BigDecimal first, BigDecimal step, BigDecimal last) {
		Validate.notNull(first, "first cannot be null");
		Validate.notNull(step, "step cannot be null");
		Validate.notNull(last, "last cannot be null");
		Validate.isTrue(this.step.compareTo(BigDecimal.ZERO) > 0, "step must be greater than zero");

		this.first = first;
		this.step = step;
		this.last = last;
		this.lowerBoundForDiscretizedValue = this.first.doubleValue();
		this.upperBoundForDiscretizedValue = this.last.doubleValue();
	}

	/**
	 * Get the lower bound of values for which we determine an index -- default is first
	 */
	public double getLowerBoundForDiscretizedValue() {
		return lowerBoundForDiscretizedValue;
	}

	/**
	 * Set the lower bound of values for which we determine an index
	 */
	public void setLowerBoundForDiscretizedValue(double lowerBoundForDiscretizedValue) {
		this.lowerBoundForDiscretizedValue = lowerBoundForDiscretizedValue;
	}

	/**
	 * Get the upper bound of values for which we determine an index -- default is last
	 */
	public double getUpperBoundForDiscretizedValue() {
		return upperBoundForDiscretizedValue;
	}

	/**
	 * Set the upper bound of values for which we determine an index
	 */
	public void setUpperBoundForDiscretizedValue(double upperBoundForDiscretizedValue) {
		this.upperBoundForDiscretizedValue = upperBoundForDiscretizedValue;
	}

	public BigDecimal getFirst() {
		return first;
	}

	public BigDecimal getStep() {
		return step;
	}

	public BigDecimal getLast() {
		return last;
	}

	@Override
	public Iterator<Value> iterator() {
		return functionIterator(new BigDecimalIterator(first, last, /* exclusive = */ false, step), Value::value);
	}

	@Override
	public int getIndexOf(Value value) {
		double valueAsDouble = value.doubleValue();
		if (valueAsDouble < lowerBoundForDiscretizedValue || valueAsDouble > upperBoundForDiscretizedValue) {
			return -1;
		}

		double firstDoubleValue = first.doubleValue();
		if (valueAsDouble < firstDoubleValue) {
			valueAsDouble = firstDoubleValue;
		} else {
			double lastDoubleValue = last.doubleValue();
			if (valueAsDouble > lastDoubleValue) {
				valueAsDouble = lastDoubleValue;
			}
		}
		
		int index = numberOfSteps(valueAsDouble);
		return index;
	}

	public int numberOfSteps(double valueAsDouble) {
		double numberOfStepsAsDouble = (valueAsDouble - first.doubleValue())/step.doubleValue();
		long numberOfStepsAsLong = Math.round(numberOfStepsAsDouble);
		int numberOfStepsAsInt = Math.toIntExact(numberOfStepsAsLong);
		return numberOfStepsAsInt;
	}

	@Override
	public int size() {
		return numberOfSteps(last.doubleValue());
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " from " + first + " to " + last + ", step " + step;
	}

}
