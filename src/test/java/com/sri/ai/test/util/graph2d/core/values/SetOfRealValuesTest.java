package com.sri.ai.test.util.graph2d.core.values;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;

import com.sri.ai.util.base.Pair;
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
		Assert.assertEquals(-1, setOfRealValues.getIndexOf(new DefaultValue(BigDecimal.TEN)));
	}

	@Test
	public void testStepIrrelevancyInSingleton() {
		new SetOfRealValues(1, new BigDecimal(-1), 1);
		new SetOfRealValues(1, new BigDecimal(0), 1);
		new SetOfRealValues(1, new BigDecimal(1), 1);
	}

	@Test
	public void testIteratorOverEmptyRange() {
		// step does not need to be positive if range is empty; testing this here too.
		SetOfRealValues setOfRealValues = new SetOfRealValues(5, new BigDecimal("-1"), 4);
		int counter = 0;
		for (@SuppressWarnings("unused") Value setOfRealValue : setOfRealValues) {
			counter++;
		}
		Assert.assertEquals(0, counter);
	}

	@Test
	public void testIteratorOverSingleton() {
		// step does not need to be positive if range is empty; testing this here too.
		SetOfRealValues setOfRealValues = new SetOfRealValues(5, new BigDecimal("-1"), 5);
		int counter = 0;
		for (@SuppressWarnings("unused") Value setOfRealValue : setOfRealValues) {
			counter++;
		}
		Assert.assertEquals(1, counter);
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
		Assert.assertEquals(0, setOfRealValues.getIndexOf(Value.value(2.999)));
		Assert.assertEquals(1, setOfRealValues.getIndexOf(Value.value(3.0)));
		Assert.assertEquals(1, setOfRealValues.getIndexOf(Value.value(3.9)));
		Assert.assertEquals(1, setOfRealValues.getIndexOf(Value.value(4.0)));
		Assert.assertEquals(1, setOfRealValues.getIndexOf(Value.value(4.1)));
		Assert.assertEquals(2, setOfRealValues.getIndexOf(Value.value(6.0)));
		Assert.assertEquals(-1, setOfRealValues.getIndexOf(Value.value(7.0)));
	}
	
	@Test
	public void testGetIndexOfWithExtraBounds() {
		int first = 2;
		final String last = "6";
		final int step = 2;

		SetOfRealValues setOfRealValues = new SetOfRealValues(Integer.toString(first), Integer.toString(step), last);
		setOfRealValues.setLowerBoundForDiscretizedValue(new BigDecimal(1));
		setOfRealValues.setUpperBoundForDiscretizedValue(new BigDecimal(7));
		
		Assert.assertEquals(-1, setOfRealValues.getIndexOf(Value.value(0.9)));
		Assert.assertEquals(0, setOfRealValues.getIndexOf(Value.value(1.0)));
		Assert.assertEquals(0, setOfRealValues.getIndexOf(Value.value(1.9)));
		Assert.assertEquals(0, setOfRealValues.getIndexOf(Value.value(2.0)));
		Assert.assertEquals(1, setOfRealValues.getIndexOf(Value.value(4.0)));
		Assert.assertEquals(2, setOfRealValues.getIndexOf(Value.value(6.0)));
		Assert.assertEquals(2, setOfRealValues.getIndexOf(Value.value(7.0)));
		Assert.assertEquals(-1, setOfRealValues.getIndexOf(Value.value(7.1)));
	}

	@Test
	public void testGetIndexOfWithEmptyRange() {
		int first = 3;
		final String last = "2";
		final int step = -1;

		SetOfRealValues setOfRealValues = new SetOfRealValues(Integer.toString(first), Integer.toString(step), last);
		setOfRealValues.setLowerBoundForDiscretizedValue(new BigDecimal(1));
		setOfRealValues.setUpperBoundForDiscretizedValue(new BigDecimal(7));
		
		Assert.assertEquals(-1, setOfRealValues.getIndexOf(Value.value(0.9)));
		Assert.assertEquals(-1, setOfRealValues.getIndexOf(Value.value(1.0)));
		Assert.assertEquals(-1, setOfRealValues.getIndexOf(Value.value(1.9)));
		Assert.assertEquals(-1, setOfRealValues.getIndexOf(Value.value(2.0)));
		Assert.assertEquals(-1, setOfRealValues.getIndexOf(Value.value(4.0)));
		Assert.assertEquals(-1, setOfRealValues.getIndexOf(Value.value(6.0)));
		Assert.assertEquals(-1, setOfRealValues.getIndexOf(Value.value(7.0)));
		Assert.assertEquals(-1, setOfRealValues.getIndexOf(Value.value(7.1)));
	}

	@Test
	public void testGetIndexOfWithSingleton() {
		int first = 3;
		final String last = "3";
		final int step = -1;

		SetOfRealValues setOfRealValues = new SetOfRealValues(Integer.toString(first), Integer.toString(step), last);
		setOfRealValues.setLowerBoundForDiscretizedValue(new BigDecimal(1));
		setOfRealValues.setUpperBoundForDiscretizedValue(new BigDecimal(7));
		
		Assert.assertEquals(-1, setOfRealValues.getIndexOf(Value.value(0.9)));
		Assert.assertEquals(0, setOfRealValues.getIndexOf(Value.value(1.0)));
		Assert.assertEquals(0, setOfRealValues.getIndexOf(Value.value(1.9)));
		Assert.assertEquals(0, setOfRealValues.getIndexOf(Value.value(2.0)));
		Assert.assertEquals(0, setOfRealValues.getIndexOf(Value.value(4.0)));
		Assert.assertEquals(0, setOfRealValues.getIndexOf(Value.value(6.0)));
		Assert.assertEquals(0, setOfRealValues.getIndexOf(Value.value(7.0)));
		Assert.assertEquals(-1, setOfRealValues.getIndexOf(Value.value(7.1)));
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testGetOnEmptyRange() {
		SetOfRealValues setOfRealValues = new SetOfRealValues(5, new BigDecimal("-1"), 4);
		setOfRealValues.get(0);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testGetWithIndexMappingToANumberLessThanFirst() {
		SetOfRealValues setOfRealValues = new SetOfRealValues(1, BigDecimal.ONE, 3);
		setOfRealValues.get(-1);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testGetWithIndexMappingToANumberGreaterThanLast() {
		SetOfRealValues setOfRealValues = new SetOfRealValues(1, BigDecimal.ONE, 3);
		setOfRealValues.get(3);
	}

	@Test
	public void testGet() {
		SetOfRealValues setOfRealValues = new SetOfRealValues(1, BigDecimal.ONE, 3);
		assertEquals(Value.value(new BigDecimal(1)), setOfRealValues.get(0));
		assertEquals(Value.value(new BigDecimal(2)), setOfRealValues.get(1));
		assertEquals(Value.value(new BigDecimal(3)), setOfRealValues.get(2));
	}

	@Test
	public void testGetOnSingleton() {
		SetOfRealValues setOfRealValues = new SetOfRealValues(1, BigDecimal.ZERO, 1);
		assertEquals(Value.value(new BigDecimal(1)), setOfRealValues.get(0));
	}
	
	@Test
	public void testGetBoundsForIndex() {
		SetOfRealValues setOfRealValues = new SetOfRealValues(1, BigDecimal.ONE, 3);
		setOfRealValues.setLowerBoundForDiscretizedValue(BigDecimal.ZERO);
		setOfRealValues.setUpperBoundForDiscretizedValue(new BigDecimal(4));

		assertEquals(null, setOfRealValues.getBoundsForIndex(-1));
		assertEquals(Pair.make(new BigDecimal(0), new BigDecimal(1.5)), setOfRealValues.getBoundsForIndex(0));
		assertEquals(Pair.make(new BigDecimal(1.5), new BigDecimal(2.5)), setOfRealValues.getBoundsForIndex(1));
		assertEquals(Pair.make(new BigDecimal(2.5), new BigDecimal(4)), setOfRealValues.getBoundsForIndex(2));
		assertEquals(null, setOfRealValues.getBoundsForIndex(3));
	}

	@Test
	public void testGetBoundsForIndexForSingleton() {
		SetOfRealValues setOfRealValues = new SetOfRealValues(1, BigDecimal.ZERO, 1);
		setOfRealValues.setLowerBoundForDiscretizedValue(BigDecimal.ZERO);
		setOfRealValues.setUpperBoundForDiscretizedValue(new BigDecimal(4));
		
		assertEquals(null, setOfRealValues.getBoundsForIndex(-1));
		assertEquals(Pair.make(new BigDecimal(0), new BigDecimal(4)), setOfRealValues.getBoundsForIndex(0));
		assertEquals(null, setOfRealValues.getBoundsForIndex(1));
		assertEquals(null, setOfRealValues.getBoundsForIndex(2));
	}
	
	@Test
	public void testGetBoundsForIndexForEmptyRange() {
		SetOfRealValues setOfRealValues = new SetOfRealValues(5, new BigDecimal("-1"), 4);
		setOfRealValues.setLowerBoundForDiscretizedValue(BigDecimal.ZERO);
		setOfRealValues.setUpperBoundForDiscretizedValue(new BigDecimal(4));
		
		assertEquals(null, setOfRealValues.getBoundsForIndex(-1));
		assertEquals(null, setOfRealValues.getBoundsForIndex(0));
		assertEquals(null, setOfRealValues.getBoundsForIndex(1));
		assertEquals(null, setOfRealValues.getBoundsForIndex(2));
	}

	
	@Test
	public void testSize() {
		SetOfRealValues setOfRealValues = new SetOfRealValues(1, BigDecimal.ONE, 3);
		assertEquals(3, setOfRealValues.size());
	}

	@Test
	public void testSizeForSingleton() {
		SetOfRealValues setOfRealValues = new SetOfRealValues(1, BigDecimal.ZERO, 1);
		assertEquals(1, setOfRealValues.size());
	}
	
	@Test
	public void testSizeForEmptyRange() {
		SetOfRealValues setOfRealValues = new SetOfRealValues(5, new BigDecimal("-1"), 4);
		assertEquals(0, setOfRealValues.size());
	}
}
