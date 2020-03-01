package com.sri.ai.test.util.collect;

import static com.sri.ai.util.Util.iterator;
import static com.sri.ai.util.Util.list;
import static com.sri.ai.util.Util.listFrom;
import static com.sri.ai.util.Util.set;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.sri.ai.util.base.NullaryFunction;
import com.sri.ai.util.collect.NestedIterator;

public class NestedIteratorTest {

	@SuppressWarnings("unchecked")
	@Test
	public void test() {
		
		List<Object> list, expected, actual;
		
		list = list();
		expected = list();
		actual = listFrom(new NestedIterator(list));
		assertEquals(expected, actual);
		
		list = list(1, 2, list(3));
		expected = list(1, 2, 3);
		actual = listFrom(new NestedIterator(list));
		assertEquals(expected, actual);
		
		list = 
				list(
						set(1),
						2,
						set(3, 4),
						list(5, 6).iterator(),
						(NullaryFunction) () -> list(
								7, 
								list(), 
								iterator(), 
								(NullaryFunction) ()-> list((NullaryFunction) ()-> 8)).iterator(),
						list()
						);
		expected = list(1, 2, 3, 4, 5, 6, 7, 8);
		actual = listFrom(new NestedIterator(list));
		assertEquals(expected, actual);
	}

}
