/*
 * Copyright (c) 2013, SRI International
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
 * Neither the name of the aic-util nor the names of its
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
package com.sri.ai.util.ml.decisiontree.core;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.sri.ai.util.ml.decisiontree.api.DecisionTree;

/**
 * A default implementation of a decision tree.
 * 
 * @author braz
 *
 * @param <V>
 * @param <F>
 * @param <L>
 */
@Beta
public class DefaultDecisionTree<V,L> implements DecisionTree<V,L> {
	
	private boolean isLeaf;
	
	private L label; // label if leaf, null otherwise
	
	private Function<V, Integer> test; // test if not leaf, null otherwise
	private DecisionTree<V,L>[] subTrees; // sub-trees if not leaf, null otherwise
	
	@Override
	public boolean isLeaf() {
		return isLeaf;
	}

	@Override
	public void setLeaf(boolean isLeaf) {
		this.isLeaf = isLeaf;
	}

	@Override
	public L getLabel() {
		return label;
	}

	@Override
	public void setLabel(L label) {
		this.label = label;
	}

	@Override
	public Function<V, Integer> getTest() {
		return test;
	}

	@Override
	public void setTest(Function<V, Integer> test) {
		this.test = test;
	}

	@Override
	public DecisionTree<V,L>[] getSubTrees() {
		return subTrees;
	}

	@Override
	public void setSubTrees(DecisionTree<V,L>[] subTrees) {
		this.subTrees = subTrees;
	}

	@Override
	public L apply(V input) {
		L result;
		if (isLeaf()) {
			return getLabel();
		}
		else {
			int testResult = getTest().apply(input);
			result = getSubTrees()[testResult].apply(input);
		}
		return result;
	}
}
