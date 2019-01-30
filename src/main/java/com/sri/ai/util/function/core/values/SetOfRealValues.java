package com.sri.ai.util.function.core.values;

import static com.sri.ai.util.collect.FunctionIterator.functionIterator;
import static com.sri.ai.util.function.api.values.Value.value;

import java.math.BigDecimal;
import java.util.Iterator;

import org.apache.commons.lang3.Validate;

import com.sri.ai.util.base.Pair;
import com.sri.ai.util.collect.BigDecimalIterator;
import com.sri.ai.util.function.api.values.Value;
import com.sri.ai.util.function.api.variables.SetOfValues;

public class SetOfRealValues implements SetOfValues {

	private BigDecimal first;
	private BigDecimal step;
	private BigDecimal halfStep;
	private BigDecimal last;

	/**
	 * Lower bound of values for which we determine an index -- default is first
	 */
	private BigDecimal lowerBoundForDiscretizedValue;

	/**
	 * Upper bound of values for which we determine an index -- default is last
	 */
	private BigDecimal upperBoundForDiscretizedValue;
	
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

		this.first = new BigDecimal(first);
		setStep(step);
		this.last = new BigDecimal(last);
		this.lowerBoundForDiscretizedValue = this.first;
		this.upperBoundForDiscretizedValue = this.last;

		Validate.isTrue(!isGreaterThanAPoint() || step.compareTo(BigDecimal.ZERO) > 0, "step must be greater than zero if set of real values is greater than a point");
	}

	private void setStep(BigDecimal step) {
		this.step = step;
		this.halfStep = step.divide(new BigDecimal(2));
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
		setStep(new BigDecimal(step));
		this.last = new BigDecimal(last);
		this.lowerBoundForDiscretizedValue = this.first;
		this.upperBoundForDiscretizedValue = this.last;

		Validate.isTrue(!isGreaterThanAPoint() || this.step.compareTo(BigDecimal.ZERO) > 0, "step must be greater than zero if set of real values is greater than a point");
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

		this.first = first;
		setStep(step);
		this.last = last;
		this.lowerBoundForDiscretizedValue = this.first;
		this.upperBoundForDiscretizedValue = this.last;

		Validate.isTrue(!isGreaterThanAPoint() || step.compareTo(BigDecimal.ZERO) > 0, "step must be greater than zero if set of real values is greater than a point");
	}

	public boolean isEmpty() {
		boolean result = last.compareTo(first) < 0;
		return result;
	}

	private boolean isSingleton() {
		boolean result = last.compareTo(first) == 0;
		return result;
	}

	private boolean isGreaterThanAPoint() {
		boolean result = last.compareTo(first) > 0;
		return result;
	}

	/**
	 * Get the lower bound of values for which we determine an index -- default is first
	 */
	public BigDecimal getLowerBoundForDiscretizedValue() {
		return lowerBoundForDiscretizedValue;
	}

	/**
	 * Set the lower bound of values for which we determine an index
	 */
	public void setLowerBoundForDiscretizedValue(BigDecimal lowerBoundForDiscretizedValue) {
		this.lowerBoundForDiscretizedValue = lowerBoundForDiscretizedValue;
	}

	/**
	 * Get the upper bound of values for which we determine an index -- default is last
	 */
	public BigDecimal getUpperBoundForDiscretizedValue() {
		return upperBoundForDiscretizedValue;
	}

	/**
	 * Set the upper bound of values for which we determine an index
	 */
	public void setUpperBoundForDiscretizedValue(BigDecimal upperBoundForDiscretizedValue) {
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
	public Value get(int i) {
		if (!isEmpty()) {
			BigDecimal projectedValue = first.add(step.multiply(new BigDecimal(i)));
			if (projectedValue.compareTo(first) >= 0 && projectedValue.compareTo(last) <= 0) {
				return value(projectedValue);
			}
		}
		throw new IndexOutOfBoundsException("Set of real values has size " + size() + " but index was " + i);
	}

	@Override
	public int getIndexOf(Value value) {
		BigDecimal valueAsBigDecimal = new BigDecimal(value.doubleValue());
		if (isEmpty() || valueAsBigDecimal.compareTo(lowerBoundForDiscretizedValue) < 0 || valueAsBigDecimal.compareTo(upperBoundForDiscretizedValue) > 0) {
			return -1;
		}
		
		if(size() == 1) {
			return 0;
		}

		if (valueAsBigDecimal.compareTo(first) < 0) {
			valueAsBigDecimal = first;
		} else {
			if (valueAsBigDecimal.compareTo(last) > 0) {
				valueAsBigDecimal = last;
			}
		}
		
		int index = numberOfSteps(valueAsBigDecimal);
		return index;
	}

	/** Returns the number of steps needed to take to reach a value, or -1 if set has empty range. */
	public int numberOfSteps(BigDecimal valueAsBigDecimal) {
		if (isEmpty()) {
			return -1;
		}
		if (valueAsBigDecimal.equals(first)) {
			return 0;
		}
		int numberOfSteps = valueAsBigDecimal.add(halfStep).subtract(first).divideToIntegralValue(step).intValue();
		return numberOfSteps;
	}

	/**
	 * Returns a pair containing the lower (inclusive) and upper (exclusive) bounds for values mapped to the <code>valueIndex</code>-th value,
	 * or null if set is empty.
	 * Note that the lower bound for the first value's index (0) is given by {@link #getLowerBoundForDiscretizedValue()}
	 * and the upper bound for the last value's index ({@link #size()}) is given by {@link #getUpperBoundForDiscretizedValue()}.
	 * @param valueIndex
	 * @return
	 */
	public Pair<BigDecimal, BigDecimal> getBoundsForIndex(int valueIndex) {
		if (isEmpty() || valueIndex < 0 || valueIndex > size() - 1) {
			return null;
		}
		else {
			BigDecimal value = (BigDecimal) get(valueIndex).objectValue();
			BigDecimal indexLowerBound = valueIndex == 0? getLowerBoundForDiscretizedValue() : value.subtract(halfStep);
			BigDecimal indexUpperBound = valueIndex == size() - 1? getUpperBoundForDiscretizedValue() : value.add(halfStep);
			return Pair.make(indexLowerBound, indexUpperBound);
		}
	}

	@Override
	public int size() {
		return numberOfSteps(last) + 1;
	}

	@Override
	public String toString() {
		return 
				isEmpty()
				? "Empty set of real values"
						: isSingleton()
						? "Singleton set of real " + first
						: getClass().getSimpleName() + " from " + first + " to " + last + ", step " + step + ", lower bound " + lowerBoundForDiscretizedValue + ", upperBoundForDiscretizedValue " + upperBoundForDiscretizedValue;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((first == null) ? 0 : first.hashCode());
		result = prime * result + ((halfStep == null) ? 0 : halfStep.hashCode());
		result = prime * result + ((last == null) ? 0 : last.hashCode());
		result = prime * result
				+ ((lowerBoundForDiscretizedValue == null) ? 0 : lowerBoundForDiscretizedValue.hashCode());
		result = prime * result + ((step == null) ? 0 : step.hashCode());
		result = prime * result
				+ ((upperBoundForDiscretizedValue == null) ? 0 : upperBoundForDiscretizedValue.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SetOfRealValues other = (SetOfRealValues) obj;
		if (first == null) {
			if (other.first != null)
				return false;
		} else if (!first.equals(other.first))
			return false;
		if (halfStep == null) {
			if (other.halfStep != null)
				return false;
		} else if (!halfStep.equals(other.halfStep))
			return false;
		if (last == null) {
			if (other.last != null)
				return false;
		} else if (!last.equals(other.last))
			return false;
		if (lowerBoundForDiscretizedValue == null) {
			if (other.lowerBoundForDiscretizedValue != null)
				return false;
		} else if (!lowerBoundForDiscretizedValue.equals(other.lowerBoundForDiscretizedValue))
			return false;
		if (step == null) {
			if (other.step != null)
				return false;
		} else if (!step.equals(other.step))
			return false;
		if (upperBoundForDiscretizedValue == null) {
			if (other.upperBoundForDiscretizedValue != null)
				return false;
		} else if (!upperBoundForDiscretizedValue.equals(other.upperBoundForDiscretizedValue))
			return false;
		return true;
	}

}
