package com.sri.ai.util.explainableonetoonematching;

import com.google.common.base.Function;

/**
 * An interface for functions combining left-over elements in a second collection to an explanation about them being left-over.
 * 
 * @author braz
 *
 * @param <T>
 * @param <C>
 * @param <E>
 */
public interface LeftOverMerger<C, E> extends Function<C, E>  {

}
