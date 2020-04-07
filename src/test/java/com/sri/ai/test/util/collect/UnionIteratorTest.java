package com.sri.ai.test.util.collect;

import static com.sri.ai.util.Util.arrayList;
import static com.sri.ai.util.Util.list;
import static com.sri.ai.util.Util.listFrom;
import static com.sri.ai.util.Util.set;
import static com.sri.ai.util.collect.FunctionIterator.functionIterator;
import static org.junit.Assert.assertEquals;

import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import com.sri.ai.util.collect.UnionIterator;

public class UnionIteratorTest {

	@Test
	public void test() {
		
		Iterable<Iterable<Object>> iterableOfIterables;
		List<Object> expected;
		
		iterableOfIterables = list();
		expected = list();
		runTest(iterableOfIterables, expected);
		
		iterableOfIterables = list(set(), arrayList(2), list());
		expected = list(2);
		runTest(iterableOfIterables, expected);
		
		iterableOfIterables = list(set(1), arrayList(2), list(3));
		expected = list(1, 2, 3);
		runTest(iterableOfIterables, expected);
		
	}

	private void runTest(Iterable<Iterable<Object>> iterableOfIterables, List<Object> expected) {
		testIterableOfIterables(iterableOfIterables, expected);
		testIteratorOfIterators(iterableOfIterables, expected);
	}

	private void testIterableOfIterables(Iterable<Iterable<Object>> iterableOfIterables, List<Object> expected) {
		List<Object> actual = listFrom(new UnionIterator<Object>(iterableOfIterables));
		assertEquals(expected, actual);
	}

	private void testIteratorOfIterators(Iterable<Iterable<Object>> iterableOfIterables, List<Object> expected) {
		Iterator<Iterator<Object>> iteratorOfIterators = functionIterator(iterableOfIterables, i -> i.iterator());
		List<Object> actual = listFrom(new UnionIterator<Object>(iteratorOfIterators));
		assertEquals(expected, actual);
	}

}
