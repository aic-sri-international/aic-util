package com.sri.ai.util.ml.decisiontree.api;

import com.google.common.base.Function;
import com.sri.ai.util.ml.api.DataSet;

public interface SplitAnalysis<V,L> {

	public abstract boolean isSplit();

	public abstract void setSplit(boolean isSplit);

	public abstract L getLabel();

	public abstract void setLabel(L label);

	public abstract DataSet<V>[] getDataBins();

	public abstract void setDataBins(DataSet<V>[] dataBins);

	public abstract Function<V, Integer> getTest();

	public abstract void setTest(Function<V, Integer> test);

	public abstract DataSet<V>[] getSplits();

	public abstract void setSplits(DataSet<V>[] splits);

}