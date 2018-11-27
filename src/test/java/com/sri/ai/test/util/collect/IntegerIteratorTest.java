package com.sri.ai.test.util.collect;

import com.sri.ai.util.collect.IntegerIterator;
import org.junit.Assert;
import org.junit.Test;

public class IntegerIteratorTest {

  @Test(expected = IllegalArgumentException.class)
  public void testNegativeIncrementValue() {
    new IntegerIterator(1, 10, 0);
  }

 @Test
  public void testFromThisValueOnForever100() {
    final int ITERATIONS = 100;
    final int start = 1;
    int cur = -1;

    IntegerIterator iter = IntegerIterator.fromThisValueOnForever(start);

   for (int i = 0; iter.hasNext() && i < ITERATIONS; ++i) {
     cur = iter.next();
   }

    Assert.assertEquals(ITERATIONS, cur);
  }

  @Test
  public void testRangeDefaultIncrement() {
    final int start = 1;
    final int end = 10;
    int cur = -1;

    int count = 0;
    IntegerIterator iter = new IntegerIterator(start, end + 1);
    while (iter.hasNext()) {
      cur = iter.next();
      Assert.assertEquals(start + count++, cur);
    }

    Assert.assertEquals(end, cur);
  }

  @Test
  public void testRangeNonDefaultIncrement() {
    final int start = 1;
    final int end = 10;
    int cur = -1;
    final int increment = 3;

    final int lastValue = 7;

    int count = 0;
    IntegerIterator iter = new IntegerIterator(start, end, increment);
    while (iter.hasNext()) {
      cur = iter.next();
      int expected = start + (count++ * increment);
      Assert.assertEquals(expected, cur);
    }

    Assert.assertEquals(lastValue, cur);
  }
}
