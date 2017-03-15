package com.sri.ai.test.util.math;

import java.math.RoundingMode;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.sri.ai.util.AICUtilConfiguration;
import com.sri.ai.util.math.Rational;

// NOTE: The #s used here are so large that the exact representation of Rational takes a long time to compute.
// We only plan to work with these kinds of large #s when Rational is in approximation mode.
public class RationalLargeApproximateTest {	

	private static final int          PRECISION     = AICUtilConfiguration.getRationalApproximationPrecision();
	private static final RoundingMode ROUNDING_MODE = AICUtilConfiguration.getRationalApproximationRoundingMode();
	
	@Before
	public void setUp() {
		Rational.resetApproximationConfiguration(true, PRECISION, ROUNDING_MODE);
	}
	
	@After
	public void tearDown() {
		Rational.resetApproximationConfigurationFromAICUtilConfiguration();
	}
	
	//@Test
	public void testBitLengthDependencies() {
		Rational r = new Rational(2).pow(Integer.MAX_VALUE).multiply(2);
		System.out.println(r.getNumerator().toString());
		System.out.println("doubleValue="+r.doubleValue());
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
	public void testPowLargePositiveRationalIntegerExponent() {	
		Rational pow; 
		pow = new Rational(3).pow(new Rational(Integer.MAX_VALUE));
		Assert.assertEquals("1.401511460528495155992833521026788E+1024610092", toString(pow));
		pow = new Rational(3).pow(new Rational(4000000000L));
		Assert.assertEquals("7.562227687355548530201943986629280E+1908485018", toString(pow));
	}
	
	@Test
	public void testPowLargeNegativeRationalIntegerExponent() {	
		Rational pow; 
		pow = new Rational(3).pow(new Rational(-Integer.MAX_VALUE));
		Assert.assertEquals("1/1.401511460528495155992833521026788E+1024610092", toString(pow));
		pow = new Rational(3).pow(new Rational(-4000000000L));
		Assert.assertEquals("1/7.562227687355548530201943986629280E+1908485018", toString(pow));
	}
	
	@Test
	public void testPowLargePositiveIntExponent() {	
		Rational pow = new Rational(3).pow(Integer.MAX_VALUE);
		Assert.assertEquals("1.401511460528495155992833521026788E+1024610092", toString(pow));
	}
	
	@Test
	public void testPowLargeNegativeIntExponent() {
		Rational pow = new Rational(3).pow(-Integer.MAX_VALUE);
		Assert.assertEquals("1/1.401511460528495155992833521026788E+1024610092", toString(pow));
	}
	
	@Test
	public void testGeometricMean() {
		int [] exponents = new int[] {4000, 10000, 12345};
		for (int i = 0; i < exponents.length; i++) {
			int exponent = exponents[i];
			Rational product = new Rational(3).pow(new Rational(exponent));
			Rational geometricMean = product.pow(new Rational(1, exponent));
			Rational geometricMeanBackToProduct = geometricMean.pow(new Rational(exponent));
			Rational backAgainToGeometricMean = geometricMeanBackToProduct.pow(new Rational(1, exponent));
			
//			System.out.println("exponent="+exponent+", precision="+PRECISION);
//			System.out.println("product                     ="+product);
//			System.out.println("product                    n="+product.getNumerator().toString());
//			System.out.println("product                    d="+product.getDenominator().toString());
//			System.out.println("geometricMeanBackToProduct  ="+geometricMeanBackToProduct);
//			System.out.println("geometricMeanBackToProduct n="+geometricMeanBackToProduct.getNumerator().toString());
//			System.out.println("geometricMeanBackToProduct d="+geometricMeanBackToProduct.getDenominator().toString());
//			System.out.println("geometricMean               ="+geometricMean);
//			System.out.println("backAgainToGeometricMean    ="+backAgainToGeometricMean);
			
			Assert.assertEquals("product^"+exponent+", precision="+PRECISION, toString(product), toString(geometricMeanBackToProduct));
			Assert.assertEquals("geometricMean^"+exponent+", precision="+PRECISION, toString(geometricMean), toString(backAgainToGeometricMean));
		}
	}
	
	private String toString(Rational rational) {
		String result;		
		if (rational.isInteger()) {
			result = rational.getNumerator().toString();
		}
		else {
			result = rational.getNumerator().toString()+"/"+rational.getDenominator().toString();
		}
		return result;
	}
}