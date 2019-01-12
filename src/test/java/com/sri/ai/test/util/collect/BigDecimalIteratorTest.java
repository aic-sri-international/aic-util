package com.sri.ai.test.util.collect;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;

import com.sri.ai.util.collect.BigDecimalIterator;

public class BigDecimalIteratorTest {
	
	@Test(expected = NullPointerException.class)
	public void testNullStartValue() {
		new BigDecimalIterator(null, BigDecimal.ONE, true);
	}

	@Test(expected = NullPointerException.class)
	public void testNullEndValue() {
		new BigDecimalIterator( BigDecimal.ONE, null, true);
	}


	@Test(expected = NullPointerException.class)
	public void testNullIncrementValue() {
		new BigDecimalIterator(BigDecimal.ONE, BigDecimal.TEN, true, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIncrementValueEqualZero() {
		new BigDecimalIterator(BigDecimal.ONE, BigDecimal.TEN, true, BigDecimal.ZERO);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIncrementValueLessThanZero() {
		new BigDecimalIterator(BigDecimal.ONE, BigDecimal.TEN, true, new BigDecimal("-1"));
	}

	@Test(expected = NullPointerException.class)
	public void testFromThisValueOnForeverNull() {
		BigDecimalIterator.fromThisValueOnForever(null);
	}

	@Test
	public void testFromThisValueOnForever100() {
		BigDecimal start = new BigDecimal("1.45");
		BigDecimal cur = null;
		final int ITERATIONS = 100;

		BigDecimalIterator iter = BigDecimalIterator.fromThisValueOnForever(start);
		for (int i = 0; iter.hasNext() && i < ITERATIONS; ++i) {
			cur = iter.next();
		}

		Assert.assertEquals(start.add(new BigDecimal(Integer.toString(ITERATIONS -1))), cur);
	}

	@Test
	public void testRangeDefaultIncrement() {
		BigDecimal start = new BigDecimal("1.45");
		BigDecimal end = new BigDecimal("10.45");
		BigDecimal cur = null;

		int count = 0;
		BigDecimalIterator iter = new BigDecimalIterator(start, end.add(BigDecimal.ONE), true);
		while (iter.hasNext()) {
			cur = iter.next();
			Assert.assertEquals(start.add(new BigDecimal(Integer.toString(count++))), cur);
		}

		Assert.assertEquals(end, cur);
	}

	@Test
	public void testRangeNonDefaultIncrement() {
		final BigDecimal start = new BigDecimal("1.45");
		final BigDecimal end = new BigDecimal("2.45");
		BigDecimal increment = new BigDecimal(".05");
		BigDecimal cur = null;

		int count = 0;
		BigDecimalIterator iter = new BigDecimalIterator(start, end, true, increment);
		while (iter.hasNext()) {
			cur = iter.next();
			BigDecimal bigDecimalCount = new BigDecimal(Integer.toString(count++));
			BigDecimal expected = start.add(increment.multiply(bigDecimalCount));
			Assert.assertEquals(expected, cur);
		}

		Assert.assertEquals(end.subtract(increment), cur);
	}

	@Test
	public void testStartEqualToExclusiveEnd() {
		final BigDecimal start = new BigDecimal("1.45");
		final BigDecimal end = new BigDecimal("1.45");
		BigDecimal increment = new BigDecimal(".05");

		int count = 0;
		BigDecimalIterator iter = new BigDecimalIterator(start, end, true, increment);
		while (iter.hasNext()) {
			count++;
		}

		Assert.assertEquals(0, count);
	}

	@Test
	public void testStartEqualToInclusiveEnd() {
		final BigDecimal start = new BigDecimal("1.45");
		final BigDecimal end = new BigDecimal("1.45");
		BigDecimal increment = new BigDecimal(".05");

		int count = 0;
		BigDecimalIterator iter = new BigDecimalIterator(start, end, false, increment);
		while (iter.hasNext()) {
			iter.next();
			count++;
		}

		Assert.assertEquals(1, count);
	}
}
