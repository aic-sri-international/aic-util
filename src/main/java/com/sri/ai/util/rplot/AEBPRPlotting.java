package com.sri.ai.util.rplot;

import static com.sri.ai.util.Util.println;

import java.util.List;

import com.sri.ai.util.Util;
import com.sri.ai.util.rplot.dataframe.AEBPTestingDataFrame;

/**
 * 
 * This class provides static functions that plots results of a benchmark over AEBP
 * The input of the functions is a AnytimeEBP-Dataframe, i.e. a AEBPTestingDataFrame object
 * 
 * @author gabriel
 *
 */



public class AEBPRPlotting {
	
	public static void plottingSizeOfTheInterval(AEBPTestingDataFrame df) {
		plottingSizeOfTheInterval(df,false,null);
	}
	public static void plottingSizeOfTheInterval(AEBPTestingDataFrame df,boolean printDataFrame,String fileName) {
		//for each iteration i Mean times at i (multiple runs lead to different times...)
		List<String> preProcessing = Util.list("df<- aggregate(. ~ Iteration + InferenceMethodUsed, data = df, FUN = mean)");
		
		if(printDataFrame) {
			preProcessing.add("View(df)");
		}
		
		List<String> aes = Util.list("x = TotalTime", 
									  "y = Max.P.V.True - Min.P.V.True",
									  "colour = InferenceMethodUsed");
		List<String> cmds = Util.list("geom_point()","geom_line()",
				"labs(y = 'Distance from maximum and minimum probabilities of being true')");//Size sets thickness, width sets the the extremes length

		println("starting server = " + StartRserve.checkLocalRserve());
		Ggplot.ggplotPlot(preProcessing, aes, cmds, df, fileName);	
	}
	

	public static void plottingTheInterval(AEBPTestingDataFrame df) {
		plottingTheInterval(df,false,null);
	}
	public static void plottingTheInterval(AEBPTestingDataFrame df,boolean printDataFrame,String fileName) {
		//for each iteration i Mean times at i (multiple runs lead to different times...)
		List<String> preProcessing = Util.list(
				"title <- df$GraphicalModelName[1]"
				,"df<- aggregate(. ~ Iteration + InferenceMethodUsed, data = df, FUN = mean)"
				);
	
		Double scale = (Double) df.getRow(df.getNumberOfRows()-1)[5];
		
		if(printDataFrame) {
			preProcessing.add("View(df)");
		}
		
		List<String> aes = Util.list("x = TotalTime", 
									  "ymin = Min.P.V.True", 
									  "ymax = Max.P.V.True",
									  "colour = InferenceMethodUsed");
		List<String> cmds = Util.list("geom_errorbar(size = " + scale * .5 + ",width = .00)","ggtitle(title)");//Size sets thickness, width sets the the extremes length

		println("starting server = " + StartRserve.checkLocalRserve());
		Ggplot.ggplotPlot(preProcessing, aes, cmds, df,fileName);	
	}
	
	public static void main(String[] args) {
		//Testing print to file:
		AEBPTestingDataFrame fakeDF = new AEBPTestingDataFrame();
		//Running AnytimeExactBP
		fakeDF.addRow(1,1,.4, .8, .1,.1  ,"Anytime Exact BP", "Grid Model");// running the algorithm for the first time
		fakeDF.addRow(1,2,.5, .7, .2,.3  ,"Anytime Exact BP", "Grid Model");
		fakeDF.addRow(1,3,.52,.6, .4,.7  ,"Anytime Exact BP", "Grid Model");
		fakeDF.addRow(1,4,.55,.55,.8,1.5 ,"Anytime Exact BP", "Grid Model");
		fakeDF.addRow(2,1,.4 ,.8, .1,.1  ,"Anytime Exact BP", "Grid Model");// running the algorithm for the second time
		fakeDF.addRow(2,2,.5 ,.7, .3,.4  ,"Anytime Exact BP", "Grid Model");
		fakeDF.addRow(2,3,.52,.6, .3,.7  ,"Anytime Exact BP", "Grid Model");
		fakeDF.addRow(2,4,.55,.55,.9,1.6 ,"Anytime Exact BP", "Grid Model");
		
		//Running another incremental solution (maybe boxes instead of simplexes later on...)
		fakeDF.addRow(1,1,.3, .84, .11,.11  ,"Anytime Exact BP-V2", "Grid Model");// running the algorithm for the first time
		fakeDF.addRow(1,2,.45,.75, .21,.32  ,"Anytime Exact BP-V2", "Grid Model");
		fakeDF.addRow(1,3,.5 ,.64, .41,.73  ,"Anytime Exact BP-V2", "Grid Model");
		fakeDF.addRow(1,4,.55,.55, .81,1.54 ,"Anytime Exact BP-V2", "Grid Model");
				
		fakeDF.printToCsv( System.getProperty("user.home") +"/test.csv");
		//TODO: come up with a standard folder for dropping those files
		
		plottingTheInterval(fakeDF,true,null);
		
		plottingSizeOfTheInterval(fakeDF);
		
		
	}
}
