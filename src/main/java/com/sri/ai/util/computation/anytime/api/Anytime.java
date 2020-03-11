/*
 * Copyright (c) 2015, SRI International
 * All rights reserved.
 * Licensed under the The BSD 3-Clause License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * 
 * http://opensource.org/licenses/BSD-3-Clause
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
 * Neither the name of the aic-praise nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) 
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.sri.ai.util.computation.anytime.api;

import java.util.Iterator;

import com.sri.ai.util.base.NullaryFunction;


/**
 * An anytime computation is a computation that returns an approximation to some
 * other, exact, computation.
 * 
 * It extends the interface of an iterator to approximations of a value.
 * Ideally, the values of the iterator are better and better approximations,
 * hopefully converging to the exact value although that is not required at this level.
 * <p>
 * The current value of the iterator is available at {@link #getCurrentApproximation()},
 * which does not iterate to the next value.
 * <p>
 * It extends the interface of nullary function of an approximation.
 * When invoked as a nullary function, it returns the final approximation
 * (that is, the final element of the iterator's range). 
 * 
 * @author braz
 *
 * @param <T> the type of the values being approximated
 */
public interface Anytime<T> extends Iterator<Approximation<T>>, NullaryFunction<Approximation<T>> {
	
	Approximation<T> getCurrentApproximation();

	void setCurrentApproximation(Approximation<T> newCurrentApproximation); // TODO: remove from interface level

	@Override
	default Approximation<T> apply() {
		while (hasNext()) {
			next();
		}
		return getCurrentApproximation();
	}


	/**
	 * Must compute an updated current approximation given that the external context/input (which is assumed to be accessible to it somehow)
	 * has changed, <i>without</i> further advancing its own computation.
	 * By computing its updated current approximation, we mean determining it <i>as if</i> it had been computed
	 * based on the current external context in the first place.
	 * Naturally, a possible implementation is to just recompute its current approximation from scratch
	 * using the new external context, but hopefully there will be a more efficient incremental way of doing this.
	 * <p>
	 * The reason for the constraint on not advancing its own computation (that is, iterating itself)
	 * is that that might alter the external context also for other codependent computations, creating an infinite loop.
	 * An example at the time of this writing is two {@link Anytime}s that are siblings in a computation tree,
	 * with each sibling's external context depending on the computation of the other.
	 * In that case, when one of the siblings is iterated, the external context for the other is updated,
	 * so its current approximation might be needed to be updated too, but we do not want to iterate
	 * the second sibling because this would prompt the first to be notified and iterated as well,
	 * causing uncontrolled and exhaustive computation.
	 * <p>
	 * The initial motivation for this was from Anytime Exact Belief Propagation.
	 * A current approximation may be computed assuming that some variables are not external to the branch
	 * and may be summed out. After further expansion of external branches, these variables may be found,
	 * in which case they need to be un-summed out.
	 * In our particular case, the approximation contains all the information for this operation to be performed
	 * incrementally and cheaply, without having to re-compute the branch.
	 */
	void refreshFromWithout();
	
	
	/**
	 * Computes (or re-computes) the current approximation based on subsidiary computations' current approximations (or lack thereof).
	 */
	void refreshFromWithin();

}