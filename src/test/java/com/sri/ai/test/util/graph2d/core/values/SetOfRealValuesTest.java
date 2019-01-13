package com.sri.ai.test.util.graph2d.core.values;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;

import com.sri.ai.util.function.api.values.Value;
import com.sri.ai.util.function.core.values.DefaultValue;
import com.sri.ai.util.function.core.values.SetOfRealValues;

public class SetOfRealValuesTest {
	@Test(expected = NullPointerException.class)
	public void testNullStep() {
		new SetOfRealValues(0, null);
	}

	@Test(expected = NullPointerException.class)
	public void testNullStepString() {
		new SetOfRealValues("0", null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testStepZero() {
		new SetOfRealValues(0, BigDecimal.ZERO);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testStepStringZero() {
		new SetOfRealValues("0", "0");
	}

	@Test
	public void testFirstEqualLast() {
		new SetOfRealValues(1, BigDecimal.ONE, 1);
	}

	@Test
	public void testStringFirstEqualLast() {
		new SetOfRealValues("1", "1", "1");
	}

	@Test
	public void testRange() {
		SetOfRealValues setOfRealValues = new SetOfRealValues(1, BigDecimal.ONE, 3);
		Assert.assertEquals(setOfRealValues.getIndexOf(new DefaultValue(BigDecimal.TEN)), -1);
	}

	@Test
	public void testIteratorOverEmptyRange() {
		SetOfRealValues setOfRealValues = new SetOfRealValues(5, new BigDecimal(".5"), 4);
		int counter = 0;
		for (@SuppressWarnings("unused") Value setOfRealValue : setOfRealValues) {
			counter++;
		}
		Assert.assertEquals(0, counter);
	}

	@Test
	public void testIterator1() {
		double last = 0;

		SetOfRealValues setOfRealValues = new SetOfRealValues(1, new BigDecimal(".5"), 3);
		for (Value setOfRealValue : setOfRealValues) {
			last = setOfRealValue.doubleValue();
		}
		Assert.assertEquals(3., last, .0000001);
	}

	@Test
	public void testIterator() {
		int first = 2;
		final String last = "6";
		final int step = 2;

		SetOfRealValues setOfRealValues = new SetOfRealValues(Integer.toString(first), Integer.toString(step), last);

		Value value = null;

		for (Value setOfRealValue : setOfRealValues) {
			value = setOfRealValue;
			Assert.assertEquals(first, value.intValue());
			first += step;
		}
		Assert.assertEquals(last, value == null ? "<null>" : value.stringValue());
	}
	
	@Test
	public void testGetIndexOfWithDefaultBounds() {
		int first = 2;
		final String last = "6";
		final int step = 2;

		SetOfRealValues setOfRealValues = new SetOfRealValues(Integer.toString(first), Integer.toString(step), last);

		Assert.assertEquals(-1, setOfRealValues.getIndexOf(Value.value(1.9)));
		Assert.assertEquals(0, setOfRealValues.getIndexOf(Value.value(2.0)));
		Assert.assertEquals(1, setOfRealValues.getIndexOf(Value.value(4.0)));
		Assert.assertEquals(2, setOfRealValues.getIndexOf(Value.value(6.0)));
		Assert.assertEquals(-1, setOfRealValues.getIndexOf(Value.value(7.0)));
	}
	
	@Test
	public void testGetIndexOfWithExtraBounds() {
		int first = 2;
		final String last = "6";
		final int step = 2;

		SetOfRealValues setOfRealValues = new SetOfRealValues(Integer.toString(first), Integer.toString(step), last);
		setOfRealValues.setLowerBoundForDiscretizedValue(1.0);
		setOfRealValues.setUpperBoundForDiscretizedValue(7.0);
		
		Assert.assertEquals(-1, setOfRealValues.getIndexOf(Value.value(0.9)));
		Assert.assertEquals(0, setOfRealValues.getIndexOf(Value.value(1.0)));
		Assert.assertEquals(0, setOfRealValues.getIndexOf(Value.value(1.9)));
		Assert.assertEquals(0, setOfRealValues.getIndexOf(Value.value(2.0)));
		Assert.assertEquals(1, setOfRealValues.getIndexOf(Value.value(4.0)));
		Assert.assertEquals(2, setOfRealValues.getIndexOf(Value.value(6.0)));
		Assert.assertEquals(2, setOfRealValues.getIndexOf(Value.value(7.0)));
		Assert.assertEquals(-1, setOfRealValues.getIndexOf(Value.value(7.1)));
	}
}
