package com.sri.ai.test.util.graph2d.api.variables;

import com.sri.ai.util.graph2d.api.variables.DefaultValue;
import java.math.BigDecimal;
import org.junit.Assert;
import org.junit.Test;

public class DefaultValueTest {
  private final static Double DOUBLE = -19.222;
  private final static int INTEGER = -19;
  private final static BigDecimal BIG_DECIMAL =  new BigDecimal("-19.222");

  private static class Wrapper {
    @Override
    public String toString() {
      return BIG_DECIMAL.toString();
    }
  }

  @Test
  public void objectConversion() {
    DefaultValue defaultValue = new DefaultValue(new Wrapper());
    Assert.assertEquals(DOUBLE, defaultValue.doubleValue(), .0001);
  }

  @Test
  public void doubleConversion() {
    DefaultValue defaultValue = new DefaultValue(DOUBLE);
    Assert.assertEquals(DOUBLE, defaultValue.doubleValue(), .0001);
  }

  @Test
  public void integerConversion() {
    DefaultValue defaultValue = new DefaultValue(INTEGER);
    Assert.assertEquals(INTEGER, defaultValue.intValue());
  }

  @Test
  public void integerForDoubleConversion() {
    DefaultValue defaultValue = new DefaultValue(DOUBLE);
    Assert.assertEquals( INTEGER, defaultValue.intValue());
  }

  @Test
  public void doubleForIntegerConversion() {
    DefaultValue defaultValue = new DefaultValue(INTEGER);
    Assert.assertEquals(INTEGER, (int) defaultValue.doubleValue());
  }

  @Test
  public void integerForBigDecimalConversion() {
    DefaultValue defaultValue = new DefaultValue(BIG_DECIMAL);
    Assert.assertEquals(INTEGER, defaultValue.intValue());
  }

  @Test
  public void doubleForBigDecimalConversion() {
    DefaultValue defaultValue = new DefaultValue(BIG_DECIMAL);
    Assert.assertEquals(DOUBLE, defaultValue.doubleValue(), .0001);
  }

  @Test(expected = NullPointerException.class)
  public void nullNumber() {
    DefaultValue defaultValue = new DefaultValue(null);
    defaultValue.doubleValue();
  }

  @Test(expected = IllegalArgumentException.class)
  public void emptyNumber() {
    DefaultValue defaultValue = new DefaultValue("  ");
    defaultValue.doubleValue();
  }

  @Test(expected = IllegalArgumentException.class)
  public void invalidNumber() {
    DefaultValue defaultValue = new DefaultValue("-");
    defaultValue.doubleValue();
  }

}
