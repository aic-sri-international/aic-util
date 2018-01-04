package com.sri.ai.test.util.rangeoperation;

import org.junit.Assert;
import org.junit.Test;

import com.sri.ai.util.Util;
import com.sri.ai.util.rangeoperation.api.DependencyAwareEnvironment;
import com.sri.ai.util.rangeoperation.core.AbstractDAEFunction;
import com.sri.ai.util.rangeoperation.core.RangeOperationsInterpreter;
import com.sri.ai.util.rangeoperation.library.rangeoperations.Averaging;
import com.sri.ai.util.rangeoperation.library.rangeoperations.Dimension;
import com.sri.ai.util.rangeoperation.library.rangeoperations.Summation;

public class RangeOperationsInterpreterTest {

	public class GetX extends AbstractDAEFunction {
		int counter = 0;
		@Override
		public Object apply(DependencyAwareEnvironment environment) {
			counter++;
			return environment.get("x");
		}
	}

	public class XPlusY extends AbstractDAEFunction {
		@Override
		public Object apply(DependencyAwareEnvironment environment) {
			return environment.getInt("x") + environment.getInt("y");
		}
	}

	@Test
	public void test() {
		Object expected;
		Object arguments[];
		
		arguments = new Object[]{ new Dimension("x", 1, 10), new GetX() };
		expected = Util.list(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
		test(expected, arguments);
		
		arguments = new Object[]{ new Averaging(10), new Dimension("x", 1, 10), new GetX() };
		expected = Util.list(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0);
		// averages each matrix position independently
		test(expected, arguments);
		
		arguments = new Object[]{ new Dimension("x", 1, 2), new Dimension("y", 1, 3), new XPlusY() };
		expected = Util.list(Util.list(2, 3, 4), Util.list(3, 4, 5));
		test(expected, arguments);
		
		arguments = new Object[]{ new Averaging(10), new Dimension("x", 1, 2), new Dimension("y", 1, 3), new XPlusY() };
		expected = Util.list(Util.list(2.0, 3.0, 4.0), Util.list(3.0, 4.0, 5.0));
		// averages each matrix position independently
		test(expected, arguments);
		
		GetX getX = new GetX();
		arguments = new Object[]{ new Dimension("x", 1, 5), new Summation("i", 1, 10), getX };
		expected = Util.list(10.0, 20.0, 30.0, 40.0, 50.0);
		test(expected, arguments);
		Assert.assertEquals(5, getX.counter); // function is computed only once per value of x.
	}

	private void test(Object expected, Object[] arguments) {
		Object actual;
		actual = RangeOperationsInterpreter.apply(arguments);
		// System.out.println(actual + "\n");	
		Assert.assertEquals(expected, actual);
	}

}
