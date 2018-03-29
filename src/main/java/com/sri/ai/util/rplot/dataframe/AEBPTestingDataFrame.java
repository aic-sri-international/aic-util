package com.sri.ai.util.rplot.dataframe;

import static com.sri.ai.util.Util.list;
/**
 * TODO: this should probably go on praise - test - anytime EBBP
 * @author gabriel
 *
 */
public class AEBPTestingDataFrame extends ListOfListDataFrame {
	
	public AEBPTestingDataFrame() {
		super(list( "runNumber",	"Iteration",
					"MaxP(V=True)", "MinP(V=True)","IterationTime","TotalTime", 	
					"InferenceMethodUsed","GraphicalModelName"),
				2, 
				4, 
				2);		
	}
	
}
