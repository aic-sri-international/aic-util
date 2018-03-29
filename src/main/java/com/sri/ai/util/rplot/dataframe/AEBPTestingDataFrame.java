package com.sri.ai.util.rplot.dataframe;

import static com.sri.ai.util.Util.list;

public class AEBPTestingDataFrame extends ListOfListDataFrame {
	
	
	public AEBPTestingDataFrame() {
		super(list( "Iteration",	"Number of runs",
					"Max P(V=True)","Min P(V=True)","Iteration time","Total time", 	
					"Method used"),
				2, 
				4, 
				1);		
	}
	
}
