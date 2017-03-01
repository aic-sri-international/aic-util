package com.sri.ai.test.util.math;

import java.math.RoundingMode;

import org.junit.After;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.sri.ai.util.math.Rational;

public class RationalApproximateTest {		
	@After
	public void tearDown() {
		Rational.resetApproximationConfigurationFromAICUtilConfiguration();
	}
	
	@Ignore("TODO - implement based on Rational using approximate big integers internally.")
	@Test
	public void testNumerator127_p5_half_up() {
		Rational.resetApproximationConfiguration(true, 5, RoundingMode.HALF_UP);

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
}