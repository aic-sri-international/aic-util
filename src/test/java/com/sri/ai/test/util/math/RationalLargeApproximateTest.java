package com.sri.ai.test.util.math;

import java.math.RoundingMode;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
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
	
// TODO	
	@Ignore("TODO - currently fails due to inner limitation on int exponent size by big decimal (need to refactor).")
	@Test
	public void testPowLargeIntExponent() {	
		Rational pow = new Rational(3).pow(Integer.MAX_VALUE);
		Assert.assertEquals("14.01511460528495155992833521026788E+1024610091", pow.getNumerator().toString());
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
		String result = rational.toStringExponent(PRECISION);
		return result;
	}
}