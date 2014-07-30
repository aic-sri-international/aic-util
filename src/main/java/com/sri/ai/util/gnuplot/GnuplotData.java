package com.sri.ai.util.gnuplot;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.sri.ai.util.Util;
import com.sri.ai.util.base.NullaryFunction;

/**
 * A class aggregating all data for a gnuplot plot.
 * 
 * @author braz
 */
public class GnuplotData {
	
	/** A list of string descriptions for each data series. */
	private List<String> descriptions;

	/** A list of File objects to be deleted at clean up. */
	private List<File> filesToDelete = new LinkedList<File>();

	/** Index used to name data files. */
	private int dataFileIndex = 0;

	public GnuplotData(NullaryFunction xSeries, List<YSeries> ySeriesList) {
		try {
			descriptions = new LinkedList<String>();
			for (YSeries ySeries : ySeriesList) {
				descriptions.add(getDescription(xSeries, ySeries));
			}
		}
		catch (IOException e) { Util.fatalError("Could not generate data files for gnuplot.", e); }
	}

	/** Returns a comma-separated string of descriptions. */
	public String getDescription() {
		return Util.join(",", descriptions);
	}

	/** Get data series description for a particular list of data. */
	private String getDescription(NullaryFunction xSeries, YSeries ySeries) throws IOException {
		String titleClause = getClauseValueOrEmptyString(ySeries.directives, "title ", "t ");
		if (titleClause.equals("")) titleClause = getClauseValueOrEmptyString(ySeries.directives, "notitle", "notitle");
		String withClause = getClauseValueOrEmptyString(ySeries.directives, "with ", "w ");
		@SuppressWarnings("unchecked")
		String path = storeDataAndReturnPath(xSeries == null? null : (Iterator<? extends Number>) xSeries.apply(), (Iterator<? extends Number>) ySeries.data.apply());
		StringBuffer command = new StringBuffer();
		command.append("'" + path + "'");
		command.append(xSeries != null? " using 1:2" : " using 1");
		command.append(" " + titleClause);
		command.append(" " + withClause);
		return command.toString();
	}

	private String getClauseValueOrEmptyString(List<String> directives, String fullName, String abbreviation) {
		String clause = "";
		for (String directive : directives) {
			if (directive.startsWith(fullName) || directive.startsWith(abbreviation)) {
				clause = directive;
			}
		}
		return clause;
	}

	private String storeDataAndReturnPath(Iterator<? extends Number> xSeries, Iterator<? extends Number> ySeries) throws IOException {
		File tempFile = new File("gnuplot" + dataFileIndex++ + ".dat");
		BufferedWriter w = new BufferedWriter(new FileWriter(tempFile));
		if (xSeries != null) {
			writeDataWithXSeries(xSeries, ySeries, w);
		}
		else {
			writeDataWithoutXSeries(ySeries, w);
		}
		w.close();
		filesToDelete.add(tempFile);
		return tempFile.getPath();
	}

	private void writeDataWithoutXSeries(Iterator<? extends Number> ySeries, BufferedWriter w) throws IOException {
		while (ySeries.hasNext()) {
			Object y = ySeries.next();
			w.write(y.toString());
			w.newLine();
		}
	}

	private void writeDataWithXSeries(
			Iterator<? extends Number> xSeries, Iterator<? extends Number> ySeries, BufferedWriter w) throws IOException {
		while (xSeries.hasNext()) {
			w.write(xSeries.next() + " " + ySeries.next());
			w.newLine();
		}
	}

	public void cleanUp() {
//		for (File file : filesToDelete) {
//			file.deleteOnExit();
//		}
	}
}