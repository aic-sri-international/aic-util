package com.sri.ai.util.explainableonetoonematching;

import com.sri.ai.util.base.BinaryFunction;

/**
 * An interface for objects comparing two elements and returning null if they match, or an explanation if they do not.
 * 
 * @author braz
 *
 * @param <T> the type of elements
 * @param <E> the type of explanations
 */
public interface Matcher<T, E> extends BinaryFunction<T, T, E> {

}
