package com.sri.ai.util.graph2d.api;

import com.sri.ai.util.graph2d.core.DefaultBarGraphPlotter;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.function.Function;

import com.sri.ai.util.function.api.functions.SingleInputFunctions;
import com.sri.ai.util.function.api.variables.SetOfValues;
import com.sri.ai.util.function.api.variables.Variable;
import com.sri.ai.util.graph2d.core.AbstractExternalGraphPlotter;
import com.sri.ai.util.graph2d.core.DefaultLineGraphPlotter;
import com.sri.ai.util.graph2d.core.jfreechart.GraphSettings;
import org.apache.commons.lang3.StringUtils;

/** 
 * Interface for external libraries making images of graph plots.
 * Implementations of this interface must dispatch the task to those libraries.
 * Different types of graphs (bar graphs, line graphs) will be different implementations of this interface.
 * 
 * @author braz
 *
 */
public interface ExternalGraphPlotter {
	static ExternalGraphPlotter externalLineGraphMaker(Function<Variable, SetOfValues> setOfValuesForVariable) {
		AbstractExternalGraphPlotter abstractExternalGraphPlotter = new DefaultLineGraphPlotter();
		abstractExternalGraphPlotter.setSetOfValuesForVariable(setOfValuesForVariable);
		return abstractExternalGraphPlotter;
	}
	static ExternalGraphPlotter externalBarGraphMaker(Function<Variable, SetOfValues> setOfValuesForVariable) {
		AbstractExternalGraphPlotter abstractExternalGraphPlotter = new DefaultBarGraphPlotter();
		abstractExternalGraphPlotter.setSetOfValuesForVariable(setOfValuesForVariable);
		return abstractExternalGraphPlotter;
	}

	GraphSettings getGraphSettings();

	void setGraphSettings(GraphSettings graphSettings);

	/** The functions to be plotted. They must be single-input because the graphs are 2D. */
	SingleInputFunctions getFunctions();
	void setFunctions(SingleInputFunctions functions);

	String getTitle();
	void setTitle(String title);

	/** The file pathname to use (minus the extension); if not provided, a temp file will be created. */
	String getFilePathname();
	void setFilePathname(String filePathname);

	/**
	 * Receives the values to be used for the plot.
	 */
	void setFromVariableToSetOfValues(Map<Variable, SetOfValues> fromVariableToSetOfValues);

	GraphPlot plot();

	default File createFileForImage() {
		File imageFile;
		if (StringUtils.trimToNull(getFilePathname()) == null) {
			try {
				imageFile = File.createTempFile("graph2d-", ".png");
			} catch (IOException e) {
				throw new RuntimeException("Cannot create tmpfile for Image", e);
			}
		}
		else {
			imageFile = new File(getFilePathname() + ".png");
		}
		return imageFile;
	}

}
