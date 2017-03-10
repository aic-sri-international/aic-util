package com.sri.ai.test.util.math;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import com.sri.ai.util.math.BigIntegerNumber;
import com.sri.ai.util.math.BigIntegerNumberApproximate;

public class BigIntegerNumberApproximateTest {

	@Test
	public void testGCD() {		
		// Use precision=2 in order to ensure scaling taken
		// into account in gcd computations properly.
		MathContext mathContext = new MathContext(2, RoundingMode.HALF_UP);
		
		// gcd(a, b) = result
		long[][] tests = new long[][] {
			// Zero cases
			{0, 0, 0},
			{10, 0, 10},
			
			// Same Case
			{13, 13, 13},
			
			// One Cases
			{7, 13, 1},
			
			// One arg is a prime
			{37, 600, 1},
			
			// One is multiplum of other
			{20, 100, 20},
			{15, 180, 15},
			
			// Precision digits do not have a GCD
			// but their scaled values do
			{25, 190, 5},
			
			// Other
			{6, 50, 2},
			{18, 15, 3},
			{18, 150, 6},
			{2, 100, 2},
			{2, 110, 2},
			{28, 36, 4},
				
			// Rounding
			{255, 190, 10},
			{6, 2250, 2},
			{195, 995, 200},
			{2061517, 624129, 20000}
		};
		
		for (int i = 0; i < tests.length; i++) {
			assertGCD(tests[i][0], tests[i][1], tests[i][2], mathContext);
		}
	}
	
	@Test
	public void testGCDExhaustive() {
		int exhaustiveTill = 1000;
		for (int precision = 0; precision <= 4; precision++) {
			MathContext mathContext = new MathContext(precision, RoundingMode.HALF_UP);
			for (int i = 0; i < exhaustiveTill; i++) {
				// Note: start from i as the call to assertCGD makes 2 calls with #s switched.
				for (int j = i; j < exhaustiveTill; j++) {
					// We calculate the expected GCD by using BigInteger directly
					// taking into account rounding expectations
					BigDecimal a = new BigDecimal(i, mathContext);
					BigDecimal b = new BigDecimal(j, mathContext);
					long expectedGCD = a.toBigIntegerExact().gcd(b.toBigIntegerExact()).longValueExact();
					assertGCD(i, j, expectedGCD, mathContext);
				}
			}
		}
	}
	
	@Test 
	public void testBitLengthExhaustive() {
		int exhaustiveTill = 65536; // i.e. 2^16
		for (int precision = 0; precision <= 5; precision++) {
			MathContext mathContext = new MathContext(precision, RoundingMode.HALF_UP);
			for (int i = 0; i < exhaustiveTill; i++) {
				BigDecimal                  bd = new BigDecimal(i, mathContext);
				BigIntegerNumberApproximate ba = new BigIntegerNumberApproximate(i, mathContext);
				Assert.assertEquals(i+".bitLength(), precision="+precision, bd.toBigIntegerExact().bitLength(), ba.bitLength());				
				Assert.assertEquals((-i)+".bitLength(), precision="+precision, bd.toBigIntegerExact().negate().bitLength(), ba.negate().bitLength());
			}
		}
	} 
	
	@Test
	public void testBitLengthLargePrecision() {
		// i.e. > The # digits representable by Double.MAX_VALUE is 1.7976931348623157e+308
		for (int precision = 300; precision <= 400; precision++) {
			String strNumber = StringUtils.repeat("9", precision); // i.e. > than
			MathContext mathContext = new MathContext(precision, RoundingMode.HALF_UP);
			BigDecimal                  bd = new BigDecimal(strNumber, mathContext);
			BigIntegerNumberApproximate ba = new BigIntegerNumberApproximate(strNumber, 10, mathContext);
			Assert.assertTrue("bd.bitLength() >= ba.bitLength(), precision="+precision, bd.toBigIntegerExact().bitLength() >= ba.bitLength());				
			Assert.assertTrue("bd.bitLength() >= ba.bitLength(), precision="+precision, bd.toBigIntegerExact().negate().bitLength() >= ba.negate().bitLength());
		}
	}
	
	private long gcd(long a, long b, MathContext mathContext) {
		BigIntegerNumberApproximate aApprox = new BigIntegerNumberApproximate(a, mathContext);
		BigIntegerNumberApproximate bApprox = new BigIntegerNumberApproximate(b, mathContext);
		
		BigIntegerNumber gcd = aApprox.gcd(bApprox);
		
		long result = gcd.longValue();
		
		return result;
	}
	
	private void assertGCD(long a, long b, long expectedGCD, MathContext mathContext) {
		// To ensure no ordering defects, want to test:
		// a.gcd(b)
		Assert.assertEquals("gcd("+a+", "+b+") precision = "+mathContext.getPrecision(), expectedGCD, gcd(a, b, mathContext));
		// b.gcd(a)
		Assert.assertEquals("gcd("+b+", "+a+") precision = "+mathContext.getPrecision(), expectedGCD, gcd(b, a, mathContext));
	}
}
