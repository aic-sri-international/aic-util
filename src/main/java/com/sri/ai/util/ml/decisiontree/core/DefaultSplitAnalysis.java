package com.sri.ai.util.ml.decisiontree.core;

import com.google.common.base.Function;
import com.sri.ai.util.ml.api.DataSet;
import com.sri.ai.util.ml.decisiontree.api.SplitAnalysis;

public class DefaultSplitAnalysis<V,L> implements SplitAnalysis<V, L> {
	private boolean isSplit;
	
	private L label; // resulting label of data if not a split 
	
	private Function<V,Integer> test; // test and resulting data bins if it is a split
	private DataSet<V>[] dataBins; //
	
	@Override
	public boolean isSplit() {
		return isSplit;
	}

	@Override
	public void setSplit(boolean isSplit) {
		this.isSplit = isSplit;
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
	public DataSet<V>[] getDataBins() {
		return dataBins;
	}

	@Override
	public void setDataBins(DataSet<V>[] dataBins) {
		this.dataBins = dataBins;
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
	public DataSet<V>[] getSplits() {
		return dataBins;
	}

	@Override
	public void setSplits(DataSet<V>[] splits) {
		this.dataBins = splits;
	}
}