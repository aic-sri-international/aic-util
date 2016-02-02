package com.sri.ai.util.ml.decisiontree.api;

import com.google.common.base.Function;
import com.sri.ai.util.ml.api.DataSet;

/**
 * An interface for classes providing a split analysis given a data collection.
 * @author braz
 *
 * @param <V> type of data items
 * @param <L> labels
 */
public interface SplitAnalyzer<V,L> extends Function<DataSet<V>, SplitAnalysis<V,L>> {
}