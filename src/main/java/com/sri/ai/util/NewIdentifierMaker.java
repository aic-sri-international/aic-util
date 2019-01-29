package com.sri.ai.util;

import static com.sri.ai.util.Util.set;

import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A function that takes a base string and returns the first string
 * of the form <code>base + i</code> for <code>i</code> an integer greater than 0
 * that has not been generated before and that satisfies a given predicate.
 * 
 * @author braz
 *
 */
public class NewIdentifierMaker implements Function<String, String> {
	
	private Set<String> history;
	private Predicate<String> predicate;
	
	public NewIdentifierMaker(Predicate<String> predicate) {
		this.history = set();
		this.predicate = predicate;
	}

	@Override
	public String apply(String base) {
		String newIdentifier = Util.makeNewIdentifier(base, s -> !history.contains(s) && predicate.test(s));
		history.add(newIdentifier);
		return newIdentifier;
	}
	
	

}
