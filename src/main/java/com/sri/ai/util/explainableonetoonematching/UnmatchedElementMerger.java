package com.sri.ai.util.explainableonetoonematching;

import com.sri.ai.util.base.BinaryFunction;

/**
 * An interface for binary functions taking an element and a list of explanations for why it did not match any elements in a collection,
 * and returning a combined explanation.
 * 
 * @author braz
 *
 * @param <T>
 * @param <C>
 * @param <E>
 */
public interface UnmatchedElementMerger<T, C, E> extends BinaryFunction<T, C, E> {

}
