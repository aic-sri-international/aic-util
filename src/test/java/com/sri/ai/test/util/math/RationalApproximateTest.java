package com.sri.ai.test.util.math;

import java.math.MathContext;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.sri.ai.util.math.Rational;

public class RationalApproximateTest {	

	@Before
	public void setUp() {
		Rational.resetApproximationConfiguration(true, MathContext.DECIMAL128.getPrecision(), MathContext.DECIMAL128.getRoundingMode());
	}
	
	@After
	public void tearDown() {
		Rational.resetApproximationConfigurationFromAICUtilConfiguration();
	}
	
	@Test(expected=ArithmeticException.class)
	public void testPowOverflow() {
		// Results that would end up with a scaling component > Integer.MAX_VALUE (2147483647)
		// are currently not supported by the approximation logic (i.e. limitation of 
		// the internally used BigDecimal).
		new Rational(3).pow(new Rational(5000000000L));
		// ^ should be approx:
		// 3.96562995550248277772152567382684267430164154838 × 10^2385606273                                                       
	}
	
	@Test
	public void testPowLargeRationalIntegerExponent() {	
		Rational pow; 
		pow = new Rational(3).pow(new Rational(Integer.MAX_VALUE));
		Assert.assertEquals("14.01511460528495155992833521026788E+1024610091", pow.getNumerator().toString());
		pow = new Rational(3).pow(new Rational(4000000000L));
		Assert.assertEquals("756.2227687355548530201943986629279E+1908485016", pow.getNumerator().toString());
	}
	
	@Ignore("TODO - currently fails due to inner limitation on int exponent size by big decimal (need to refactor).")
	@Test
	public void testPowLargeIntExponent() {	
		Rational pow = new Rational(3).pow(Integer.MAX_VALUE);
		Assert.assertEquals("14.01511460528495155992833521026788E+1024610091", pow.getNumerator().toString());
	}
	
	@Ignore("TODO - currently experimental")
	@Test
	public void testGeometricMeanV1() {
		Rational product = new Rational(3).pow(new Rational(4000));
		System.out.println("1");
		System.out.println("product="+product.getNumerator().toString());
		Rational geometricMean = product.pow(new Rational(1, 4000));
		System.out.println("2");
		System.out.println("geometricMean="+geometricMean.toStringExponent(47));
		Rational geometricMeanPow4000 = geometricMean.pow(4000);
		System.out.println("3");
		System.out.println("geometricMean^4000="+geometricMeanPow4000);
		Rational backToGeometricMean = geometricMeanPow4000.pow(new Rational(1, 4000));
		System.out.println("4");
		System.out.println("backToGeometricMean="+backToGeometricMean.toStringExponent(47));
	}
}