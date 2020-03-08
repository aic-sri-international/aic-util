package com.sri.ai.test.util.explainableonetoonematching;

import static com.sri.ai.util.Util.join;
import static com.sri.ai.util.Util.list;
import static com.sri.ai.util.explainableonetoonematching.ExplainableOneToOneMatching.match;
import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.List;

import org.junit.Test;

import com.sri.ai.util.explainableonetoonematching.LeftOverMerger;
import com.sri.ai.util.explainableonetoonematching.Matcher;
import com.sri.ai.util.explainableonetoonematching.UnmatchedElementMerger;

public class ExplainableOneToOneMatchingTest {
	
	@Test
	public void test() {
		
		Matcher<Integer, String> matcher = (s1, s2) -> s1.equals(s2)? null : s1 + " != " + s2;
		UnmatchedElementMerger<Integer, Collection<String>, String> unmatchedElementMerger = (element, explanations) -> element + " could not be matched: " + join(explanations);
		LeftOverMerger<Collection<Integer>, String> leftOverMerger = list -> "Left over: " + join(list);
		String perfectMatch = "Perfect match!!!";
		Matcher<List<Integer>, String> correctMatchMaker = (l1, l2) -> perfectMatch;
		
		List<Integer> list1;
		List<Integer> list2;
		String expected;
		String actual;
		
		list1 = list();
		list2 = list();
		expected = perfectMatch;
		actual = match(list1, list2, matcher, unmatchedElementMerger, leftOverMerger, correctMatchMaker);
		assertEquals(expected, actual);
		
		list1 = list(1, 2, 3);
		list2 = list(3, 2, 1);
		expected = perfectMatch;
		actual = match(list1, list2, matcher, unmatchedElementMerger, leftOverMerger, correctMatchMaker);
		assertEquals(expected, actual);
		
		list1 = list(1, 2, 2, 3, 3, 3);
		list2 = list(3, 2, 1, 3, 2, 3);
		expected = perfectMatch;
		actual = match(list1, list2, matcher, unmatchedElementMerger, leftOverMerger, correctMatchMaker);
		assertEquals(expected, actual);
		
		list1 = list(1);
		list2 = list(2, 3);
		expected = "1 could not be matched: 1 != 2, 1 != 3";
		actual = match(list1, list2, matcher, unmatchedElementMerger, leftOverMerger, correctMatchMaker);
		assertEquals(expected, actual);
		
		list1 = list(1);
		list2 = list();
		expected = "1 could not be matched: ";
		actual = match(list1, list2, matcher, unmatchedElementMerger, leftOverMerger, correctMatchMaker);
		assertEquals(expected, actual);
		
		list1 = list(2, 1);
		list2 = list(2, 3);
		expected = "1 could not be matched: 1 != 3";
		actual = match(list1, list2, matcher, unmatchedElementMerger, leftOverMerger, correctMatchMaker);
		assertEquals(expected, actual);
		
		list1 = list(2, 1, 1);
		list2 = list(1, 2, 3);
		expected = "1 could not be matched: 1 != 3";
		actual = match(list1, list2, matcher, unmatchedElementMerger, leftOverMerger, correctMatchMaker);
		assertEquals(expected, actual);
		
		list1 = list(2, 1, 1);
		list2 = list();
		expected = "2 could not be matched: ";
		actual = match(list1, list2, matcher, unmatchedElementMerger, leftOverMerger, correctMatchMaker);
		assertEquals(expected, actual);
		
		list1 = list(2, 1, 1);
		list2 = list(2, 1);
		expected = "1 could not be matched: ";
		actual = match(list1, list2, matcher, unmatchedElementMerger, leftOverMerger, correctMatchMaker);
		assertEquals(expected, actual);
		
		list1 = list(2, 1, 1);
		list2 = list(2, 1, 1, 1);
		expected = "Left over: 1";
		actual = match(list1, list2, matcher, unmatchedElementMerger, leftOverMerger, correctMatchMaker);
		assertEquals(expected, actual);
		
		list1 = list(1, 2, 3);
		list2 = list(1, 2, 3, 4);
		expected = "Left over: 4";
		actual = match(list1, list2, matcher, unmatchedElementMerger, leftOverMerger, correctMatchMaker);
		assertEquals(expected, actual);
		
		list1 = list();
		list2 = list(1, 2, 3, 4);
		expected = "Left over: 1, 2, 3, 4";
		actual = match(list1, list2, matcher, unmatchedElementMerger, leftOverMerger, correctMatchMaker);
		assertEquals(expected, actual);
		
		list1 = list(1, 4);
		list2 = list(1, 2, 3, 4);
		expected = "Left over: 2, 3";
		actual = match(list1, list2, matcher, unmatchedElementMerger, leftOverMerger, correctMatchMaker);
		assertEquals(expected, actual);
		
	}

}
