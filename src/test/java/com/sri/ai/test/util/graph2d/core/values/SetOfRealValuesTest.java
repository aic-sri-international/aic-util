package com.sri.ai.test.util.graph2d.core.values;

import com.sri.ai.util.graph2d.api.variables.Value;
import com.sri.ai.util.graph2d.core.values.SetOfRealValues;
import java.math.BigDecimal;
import java.util.Iterator;
import org.junit.Assert;
import org.junit.Test;

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

  @Test(expected = IllegalArgumentException.class)
  public void testFirstGreaterThanLast() {
    new SetOfRealValues(2, BigDecimal.ONE, 1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testStringFirstGreaterThanLast() {
    new SetOfRealValues("2", "1", "1");
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
  public void testIterator() {
    int first = 2;
    final String last = "6";
    final int step = 2;

    SetOfRealValues setOfRealValues = new SetOfRealValues(Integer.toString(first), Integer.toString(step), last);

    Value value = null;

    Iterator<Value> iter = setOfRealValues.iterator();
    while (iter.hasNext()) {
      value = iter.next();
      Assert.assertEquals(first, value.intValue());
      first += step;
    }
    Assert.assertEquals(last, value == null ? "<null>" : value.stringValue());
  }
}
