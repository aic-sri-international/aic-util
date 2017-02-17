package com.sri.ai.test.util.math;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.sri.ai.util.base.Pair;
import com.sri.ai.util.math.Rational;

public class AlternativeComputeQuotientPerformanceTest {
	
	public interface ComputeQuotient {
		void compute(int dividend, int divisor);
	}
	
	@Test
	public void testZeroLeastSignificant() {
		Rational[] inputValues    = new Rational[] {new Rational(999999), new Rational(99999), new Rational(9999), new Rational(999), new Rational(99),
				                                    new Rational(9), new Rational(1), new Rational(0), new Rational(-1), new Rational(-9),
				                                    new Rational(-99), new Rational(-999), new Rational(-9999), new Rational(-99999), new Rational(-999999)};
		Rational[] expectedValues = new Rational[] {new Rational(990000), new Rational(90000), new Rational(9000), new Rational(900), new Rational(90),
                									new Rational(9), new Rational(1), new Rational(0), new Rational(-1), new Rational(-9),
                									new Rational(-90), new Rational(-900), new Rational(-9000), new Rational(-90000), new Rational(-990000)};
        for (int i = 0; i < inputValues.length; i++) {
        	Assert.assertEquals(expectedValues[i], zeroLeastSignificant(inputValues[i]));
        }
	}

	@Ignore
	@Test
	public void testPerformance() {		
		List<Pair<String, ComputeQuotient>> computeQuotientMethods = Arrays.asList(
					new Pair<>("Nothing Computed             :", (ComputeQuotient) AlternativeComputeQuotientPerformanceTest::nothingComputed),
					new Pair<>("Double Division              :", (ComputeQuotient) AlternativeComputeQuotientPerformanceTest::doubleDivision),
					new Pair<>("Instantiated Rationals Only  :", (ComputeQuotient) AlternativeComputeQuotientPerformanceTest::instantiateRationalsOnly),
					new Pair<>("Rational.division()          :", (ComputeQuotient) AlternativeComputeQuotientPerformanceTest::rationalDivision),
					new Pair<>("Rational Loss of Precision   :", (ComputeQuotient) AlternativeComputeQuotientPerformanceTest::rationalLossOfPrecisionAtThreshold)
				);
		
		quickBurnIn(computeQuotientMethods);
		
		final int RUNS_TO_AVERAGE_OVER = 3;
		final int MAX_COMPUTE_CALLS    = 100000000;
		Map<String, Long> methodTimings = new LinkedHashMap<>();
		System.out.println("#Calls to compute per method = "+MAX_COMPUTE_CALLS);
		System.out.println("#Runs averaged over          = "+RUNS_TO_AVERAGE_OVER);
		for (int r = 0; r < RUNS_TO_AVERAGE_OVER; r++) {
			for (Pair<String, ComputeQuotient> nameMethod : computeQuotientMethods) {
				String methodName       = nameMethod.first;
				ComputeQuotient method  = nameMethod.second;
				Random random = new Random(1);
				long start = System.currentTimeMillis();		
				for (int i = 0; i < MAX_COMPUTE_CALLS; i++) {	
					method.compute(random.nextInt(), random.nextInt());
				}
				long took = System.currentTimeMillis() - start;		
				methodTimings.put(methodName, methodTimings.getOrDefault(methodName, new Long(0))+took);
			}
		}
		for (Map.Entry<String, Long> methodTotalTime : methodTimings.entrySet()) {
			System.out.println(methodTotalTime.getKey()+" Took "+String.format("%08d", methodTotalTime.getValue()/RUNS_TO_AVERAGE_OVER)+"ms.");
		}
	}
	
	public static void quickBurnIn(List<Pair<String, ComputeQuotient>> computeQuotientMethods) {
		for (Pair<String, ComputeQuotient> nameMethod : computeQuotientMethods) {
			ComputeQuotient method  = nameMethod.second;
			for (int i = 1; i < 1000000; i++) {
				method.compute(i, i);
			}
		}
	}
	
	public static void nothingComputed(int dividend, int divisor) {
		// NOTHING: Just the compute call overhead
	}
	
	public static void doubleDivision(int dividend, int divisor) {
		Double dDividend = new Double(dividend);
		Double dDivisor  = new Double(divisor);
		double quotient  = dDividend / dDivisor;
	}
	
	public static void instantiateRationalsOnly(int dividend, int divisor) {
		new Rational(dividend); new Rational(divisor);
	}
	
	public static void rationalDivision(int dividend, int divisor) {
		Rational quotient = new Rational(dividend).divide(new Rational(divisor));
	}
	
	public static void rationalLossOfPrecisionAtThreshold(int dividend, int divisor) {
		Rational rationalDividend = new Rational(dividend);
		Rational rationalDivisor  = new Rational(divisor);
		if (rationalDividend.isInteger() && rationalDivisor.isInteger()) {
			Rational quotient;
			if (moreThanNDigits(rationalDividend) || moreThanNDigits(rationalDivisor)) {
				Rational zeroedLeastSignificantDividend = zeroLeastSignificant(rationalDividend);
				Rational zeroedLeastSignificantDivisor  = zeroLeastSignificant(rationalDivisor);
				quotient = zeroedLeastSignificantDividend.divide(zeroedLeastSignificantDivisor);
			}
			else {
				quotient = rationalDividend.divide(rationalDivisor);
			}
							
		}
	}
	
	static final int N_THRESHOLD = 8;
	static final int K_ZEROS     = 4;
	
	static final Rational N_MAX_VALUE = new Rational(10).pow(N_THRESHOLD);
	static final Rational N_MIN_VALUE = new Rational(10).pow(N_THRESHOLD).negate();
	static final Rational[] K_MAX_VALUES = new Rational[K_ZEROS+1];
	static final Rational[] K_MIN_VALUES = new Rational[K_ZEROS+1];
	static {
		for (int i = 0; i < K_ZEROS+1; i++) {
			Rational kValue = new Rational(10).pow(i);		
			K_MAX_VALUES[i] = kValue;
			K_MIN_VALUES[i] = kValue.negate();		
		}
	}
	
	public static boolean moreThanNDigits(Rational rational) {
		boolean result = rational.compareTo(N_MAX_VALUE) > 0 || rational.compareTo(N_MIN_VALUE) < 0;
		return result;
	}
	
	public static Rational zeroLeastSignificant(Rational rational) {
		Rational result = rational.subtract(rational.mod(mValue(rational)));
		
		return result;
	}
	
	public static Rational mValue(Rational rational) {
		if (rational.isZero()) {
			return rational.ONE;
		} 
		else if (rational.isPositive()) {
			for (int i = K_MAX_VALUES.length-1; i >=0; i--) {
				if (i == 0 || rational.compareTo(K_MAX_VALUES[i]) > 0) {
					return K_MAX_VALUES[i];
				}
			}
		}
		else {
			for (int i = K_MIN_VALUES.length-1; i >=0; i--) {
				if (i == 0 || rational.compareTo(K_MIN_VALUES[i]) < 0) {
					return K_MIN_VALUES[i];
				}
			}
		}
		
		throw new IllegalStateException("Can't find mValue for "+rational);
	}
}
