package com.sri.ai.util.graph2d.api;

import static com.sri.ai.util.Util.println;
import static com.sri.ai.util.graph2d.api.GraphSetMaker.graphSetMaker;

import java.io.File;
import java.util.List;

import com.sri.ai.util.base.Procedure;
import com.sri.ai.util.function.api.functions.Functions;
import com.sri.ai.util.function.api.variables.Variable;
import com.sri.ai.util.graph2d.core.DefaultGraphSet;
import com.sri.ai.util.graph2d.core.jfreechart.GraphSettings;

/**
 * An interface for collections of 2D graph plots generated from a {@link Functions} object
 * and a chosen variable,
 * as well as any other useful information is associated with them
 * (such as the {@link Functions} object from which it was generated).
 * 
 * @author braz
 *
 */
public interface GraphSet {

	Functions getFunctions();
	
	List<? extends GraphPlot> getGraphPlots();

	static GraphSet graphSet(Functions functions) {
		DefaultGraphSet defaultGraphSet = new DefaultGraphSet(functions);
		return defaultGraphSet;
	}

	void add(GraphPlot plot);
	
	/**
	 * Delete files of graph plots in this set, applying an action to each of them.
	 * @param actionOnDeletedFile
	 */
	default void deleteFiles(Procedure<File> actionOnDeletedFile) {
		for (GraphPlot graphPlot : getGraphPlots()) {
			if (graphPlot.getImageFile().delete()) {
				File imageFile = graphPlot.getImageFile();
				actionOnDeletedFile.apply(imageFile);
			}
		}
	}

	/**
	 * Delete files of graph plots in this set.
	 */
	default void deleteFiles() {
		deleteFiles(f -> {});
	}

	/**
	 * Convenience method for plotting a {@link Functions} object.
	 * @param functions
	 * @param xAxisVariable
	 * @param filePathnameBase
	 * @return the generated {@link GraphSet}
	 */
	static GraphSet plot(Functions functions, Variable xAxisVariable, String filePathnameBase) {
		GraphSetMaker graphSetMaker = graphSetMaker();
		graphSetMaker.setGraphSettings(new GraphSettings().setDotWidth(1f).setLineWidth(.5f));
		graphSetMaker.setFunctions(functions);
		graphSetMaker.setFilePathnameBase(filePathnameBase);
	
		println("Preparing plot...");
		GraphSet graphSet = graphSetMaker.make(xAxisVariable);
		
		println(graphSet);
		
		return graphSet;
	}

	/**
	 * Convenience method for plotting a {@link Functions} object to temp files.
	 * @param functions
	 * @param xAxisVariable
	 * @return the generated {@link GraphSet}
	 */
	static GraphSet plot(Functions functions, Variable xAxisVariable) {
		return plot(functions, xAxisVariable, "");
	}

	/**
	 * Convenience method for plotting a {@link Functions} object to temp files.
	 * @param functions
	 * @param xAxisIndex index of the x-axis variable in function's input variables.
	 * @param filePathnameBase
	 * @return the generated {@link GraphSet}
	 */
	static GraphSet plot(Functions functions, int xAxisIndex, String filePathnameBase) {
		return plot(functions, functions.getAllInputVariables().getVariables().get(xAxisIndex), filePathnameBase);
	}
}
