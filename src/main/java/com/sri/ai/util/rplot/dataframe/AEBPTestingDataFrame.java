package com.sri.ai.util.rplot.dataframe;

import java.util.List;

import com.sri.ai.util.Util;

public class AEBPTestingDataFrame {
	DataFrame df;
	
	public AEBPTestingDataFrame() {
		List<String> colNames = Util.list("Iteration","Number of runs",
				"Max P(V=True)","Min P(V=True)","Iteration time",
				"Total time", "Method used");

		int nInt = 2;
		int nDouble = 4;
		int nString = 1;
		
		this.df = new ListOfListDataFrame(colNames, nInt, nDouble, nString);
	}

}
