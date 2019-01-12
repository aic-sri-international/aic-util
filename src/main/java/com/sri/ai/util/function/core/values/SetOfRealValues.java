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

		Validate.isTrue(this.step.compareTo(BigDecimal.ZERO) > 0, "step must be greater than zero");
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
	public int getIndex(Value value) {
		double valueAsDouble = value.doubleValue();
		if (valueAsDouble < first.doubleValue() || valueAsDouble > last.doubleValue()) {
			return -1;
		}
		else {
			int index = numberOfSteps(valueAsDouble);
			return index;
		}
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
