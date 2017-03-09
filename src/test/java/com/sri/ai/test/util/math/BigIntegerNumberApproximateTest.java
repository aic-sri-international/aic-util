package com.sri.ai.test.util.math;

import java.math.MathContext;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.sri.ai.util.math.BigIntegerNumber;
import com.sri.ai.util.math.BigIntegerNumberApproximate;

public class BigIntegerNumberApproximateTest {
	
	private MathContext mathContext;
	
	@Before
	public void setUp() {
		// Use precision=2 in order to ensure scaling taken
		// into account in gcd computations properly.
		mathContext = new MathContext(2);
	}

	@Test
	public void testGCD() {
		
		long[][] tests = new long[][] {
			// Zero cases
			{0, 0, 0},
			{0, 10, 0},
			
			// Same Case
			{13, 13, 13},
			
			// One Cases
			{1, 7, 13},
			
			// Other
			{2, 6, 50},
			{3, 18, 15},
			{15, 180, 15},
			{6, 18, 150}
		};
		
		for (int i = 0; i < tests.length; i++) {
			// To ensure no ordering defects, want to test:
			for (int j = 0; j < 2; j++) {
				if (j == 0) {
					// a.gcd(b)
					Assert.assertEquals("gcd("+tests[i][1]+", "+tests[i][2]+") = "+tests[i][0], tests[i][0], gcd(tests[i][1], tests[i][2]));
				}
				else {
					// b.gcd(a)
					Assert.assertEquals("gcd("+tests[i][2]+", "+tests[i][1]+") = "+tests[i][0], tests[i][0], gcd(tests[i][2], tests[i][1]));
				}
			}
		}
	}
	
	private long gcd(long a, long b) {
		BigIntegerNumberApproximate aApprox = new BigIntegerNumberApproximate(a, mathContext);
		BigIntegerNumberApproximate bApprox = new BigIntegerNumberApproximate(b, mathContext);
		
		BigIntegerNumber gcd = aApprox.gcd(bApprox);
		
		long result = gcd.longValue();
		
		return result;
	}
}
