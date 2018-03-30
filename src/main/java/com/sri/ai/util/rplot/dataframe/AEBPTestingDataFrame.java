package com.sri.ai.util.rplot.dataframe;

import static com.sri.ai.util.Util.list;
/**
 * TODO: this should probably go on praise - test - anytime EBP
 * 
 * This class defines a special case of data frame, useful to store information 
 * from tests on Anytime Exact BP
 * 
 * @author gabriel
 *
 */
public class AEBPTestingDataFrame extends ListOfListDataFrame {
	
	public AEBPTestingDataFrame() {
		super(list( "runNumber",	"Iteration",
					"Min.P.V.True", "Max.P.V.True","IterationTime","TotalTime", 	
					"GraphicalModelName","InferenceMethodUsed"),
				2, 
				4, 
				2);		
	}
	
}
