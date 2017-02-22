package com.sri.ai.test.util.math;

import java.math.BigInteger;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import com.sri.ai.util.math.Rational;

public class RationalApproximateTest {		
	@After
	public void tearDown() {
		Rational.resetApproximationConfigurationFromAICUtilConfiguration();
	}
	
	@Test
	public void testNumerator127_n5_k3() {
		Rational.resetApproximationConfiguration(true, 5, 3);

		// 01111111(127:7)/01111111(127:7) = 1
	    // ---->
	    // 00001111( 15:4)/00001111( 15:4) = 1
		Assert.assertEquals("1", new Rational(127, 127).toString());	
	    // 01111111(127:7)/01111110(126:7) = 1
	    // ---->
	    // 00001111( 15:4)/00001111( 15:4) = 1
		Assert.assertEquals("1", new Rational(127, 126).toString());
	    // 01111111(127:7)/01110111(119:7) = 1
	    // ---->
	    // 00001111( 15:4)/00001110( 14:4) = 1
		Assert.assertEquals("15/14", new Rational(127, 119).toString());
		// 01111111(127:7)/01010011( 83:7) = 1
	    // ---->
	    // 00001111( 15:4)/00001010( 10:4) = 1
		Assert.assertEquals("3/2", new Rational(127, 83).toString());
		
		// 01111111(127:7)/00001111(15:4) = 8
	    // ---->
	    // 00001111( 15:4)/00000001( 1:1) = 15
		Assert.assertEquals("15", new Rational(127, 15).toString());
		// 01111111(127:7)/00000111(7:3) = 18
	    // ---->
	    // 00011110( 30:5)/00000001(1:1) = 30
		Assert.assertEquals("30", new Rational(127, 7).toString());
		// 01111111(127:7)/00000001(1:1) = 127
	    // ---->
	    // 01111000(120:7)/00000001(1:1) = 120
		Assert.assertEquals("120", new Rational(127, 1).toString());
		
	    // 01111111(127:7)/11111111(-1:0) = -127
	    // ---->
	    // 10000000(-128:7)/00000001(1:1) = -128
		Assert.assertEquals("-128", new Rational(127, -1).toString());
	    // 01111111(127:7)/11111110(-2:1) = -63
	    // ---->
	    // 11000000(-64:6)/00000001(1:1) = -64
		Assert.assertEquals("-64", new Rational(127, -2).toString());
	}
	
	@Test
	public void testNumerator_neg127_n5_k3() {
		Rational.resetApproximationConfiguration(true, 5, 3);
		
		Assert.assertEquals("-128", new Rational(-127, 1).toString());
		Assert.assertEquals("120",  new Rational(-127, -1).toString());
	}
	
	
	// NOTE: Some Experimentation Code
	public static void main(String[] args) {
		BigInteger[] bis = new BigInteger[256];
		for (int i = 0; i < bis.length; i++) {
			bis[i] = new BigInteger(""+((bis.length/2 -1)-i));
		}
		for (int i = 0; i < bis.length; i++) {
			BigInteger bi = bis[i];
			System.out.println(String.format("%6s %s", bi.toString(), formatBits(bi)) 					
					+", bitCount="+bi.bitCount()+", bitLength="+bi.bitLength()+", lowestSetBit="+bi.getLowestSetBit());
			
			for (int j = 0; j < bis.length; j++) {
				BigInteger bj = bis[j];
				BigInteger numerator   = bi;
				BigInteger denominator = bj;
				if (denominator.signum() < 0) {
					numerator = numerator.negate();
					denominator = denominator.negate();
				}
				
				int dNumerator   = numerator.bitLength();
				int dDenominator = denominator.bitLength();
				if (dNumerator > N || dDenominator > N) {
					int d = Math.min(dNumerator, dDenominator);
					if (d > 0) {
						BigInteger newNumerator   = simplify(numerator, dNumerator, d);
						BigInteger newDenominator = simplify(denominator, dDenominator, d);
						
						System.out.println(String.format("      %s(%s:%s)/%s(%s:%s) = %s", formatBits(bi), bi, bi.bitLength(), formatBits(bj), bj, bj.bitLength(), bi.divide(bj)));
						System.out.println("      ---->");
						System.out.println(String.format("      %s(%s:%s)/%s(%s:%s) = %s", formatBits(newNumerator), newNumerator, newNumerator.bitLength(), formatBits(newDenominator), newDenominator, newDenominator.bitLength(), newNumerator.divide(newDenominator)));
						System.out.println("------");
					}
					else {
						System.out.println(bi.toString()+"/"+bj.toString()+" "+formatBits(bi)+"/"+formatBits(bj)+", d="+d+", bi.bitLength="+bi.bitLength()+", bj.bitLength="+bj.bitLength());
					}
				}
			}
		}
	}
	
	private static final int N = 5;
	private static final int K = 3;
	
	public static String formatBits(BigInteger bi) {
		String result = String.format("%s%s%s%s%s%s%s%s", 
				bi.testBit(7) ? 1 : 0, bi.testBit(6) ? 1 : 0, bi.testBit(5) ? 1 : 0, bi.testBit(4) ? 1 : 0, 
				bi.testBit(3) ? 1 : 0, bi.testBit(2) ? 1 : 0, bi.testBit(1) ? 1 : 0, bi.testBit(0) ? 1 : 0
				);
		return result;
	}
	
	public static BigInteger simplify(BigInteger bi, int bitLength, int d) {
		BigInteger result;
		
		if (d > K) {
			result = bi.shiftRight(K);
		}
		else if (bitLength <= K) {
			result = bi.shiftRight(d-1);
		}
		else {
			result = bi.shiftRight(K).shiftLeft(K-d+1);
		}
				
		return result;
	}
}