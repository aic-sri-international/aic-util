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
import com.sri.ai.util.ml.api.DataSet;
import com.sri.ai.util.ml.decisiontree.api.DecisionTree;
import com.sri.ai.util.ml.decisiontree.api.DecisionTreeFactory;
import com.sri.ai.util.ml.decisiontree.api.DecisionTreeLearner;
import com.sri.ai.util.ml.decisiontree.api.SplitAnalysis;
import com.sri.ai.util.ml.decisiontree.api.SplitAnalyzer;

/**
 * A default implementation of a {@link DecisionTreeLearner}.
 * 
 * @author braz
 *
 * @param <V> type of data items
 * @param <L> labels
 */
@Beta
public class DefaultDecisionTreeLearner<V,L> implements DecisionTreeLearner<V,L> {

	private DecisionTreeFactory<V,L> decisionTreeFactory;
	private SplitAnalyzer<V,L> splitAnalyzer;
	
	public DefaultDecisionTreeLearner(DecisionTreeFactory<V, L> decisionTreeFactory, SplitAnalyzer<V, L> splitAnalyzer) {
		super();
		this.decisionTreeFactory = decisionTreeFactory;
		this.splitAnalyzer = splitAnalyzer;
	}

	public DecisionTreeFactory<V, L> getDecisionTreeFactory() {
		return decisionTreeFactory;
	}

	public SplitAnalyzer<V, L> getSplitAnalyzer() {
		return splitAnalyzer;
	}

	@Override
	public DecisionTree<V,L> apply(DataSet<V> data) {
		DecisionTree<V,L> result;
		SplitAnalysis<V, L> maximumScoreSplitAnalysis = splitAnalyzer.apply(data);
		if (maximumScoreSplitAnalysis.isSplit()) {
			result = decisionTreeFactory.makeTestTree(maximumScoreSplitAnalysis);
		}
		else {
			result = decisionTreeFactory.makeLeaf(maximumScoreSplitAnalysis.getLabel());
		}
		return result;
	}
}
