package com.sri.ai.test.util.collect;

import com.sri.ai.util.collect.BigDecimalIterator;
import java.math.BigDecimal;
import org.junit.Assert;
import org.junit.Test;

public class BigDecimalIteratorTest {


  @Test(expected = NullPointerException.class)
  public void testNullStartValue() {
  new BigDecimalIterator(null, BigDecimal.ONE);
 }

  @Test(expected = NullPointerException.class)
  public void testNullEndValue() {
   new BigDecimalIterator( BigDecimal.ONE, null);
 }


  @Test(expected = NullPointerException.class)
  public void testNullIncrementValue() {
    new BigDecimalIterator(BigDecimal.ONE, BigDecimal.TEN, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testIncrementValueEqualZero() {
    new BigDecimalIterator(BigDecimal.ONE, BigDecimal.TEN, BigDecimal.ZERO);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testIncrementValueLessThanZero() {
    new BigDecimalIterator(BigDecimal.ONE, BigDecimal.TEN, new BigDecimal("-1"));
  }

  @Test(expected = IllegalArgumentException.class)
 public void testStartEqualEndValue() {
   new BigDecimalIterator(BigDecimal.ONE, BigDecimal.ONE);
 }

 @Test(expected = IllegalArgumentException.class)
 public void testStartGreaterThanEndValue() {
   new BigDecimalIterator(BigDecimal.TEN, BigDecimal.ONE);
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
    BigDecimalIterator iter = new BigDecimalIterator(start, end.add(BigDecimal.ONE));
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
    BigDecimalIterator iter = new BigDecimalIterator(start, end, increment);
    while (iter.hasNext()) {
      cur = iter.next();
      BigDecimal bigDecimalCount = new BigDecimal(Integer.toString(count++));
      BigDecimal expected = start.add(increment.multiply(bigDecimalCount));
      Assert.assertEquals(expected, cur);
    }

    Assert.assertEquals(end.subtract(increment), cur);
  }
}
