/*
 * Copyright (c) 2013, SRI International
 * All rights reserved.
 * Licensed under the The BSD 3-Clause License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * 
 * http://opensource.org/licenses/BSD-3-Clause
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
 * Neither the name of the aic-util nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) 
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.sri.ai.util.gnuplot;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.google.common.annotations.Beta;
import com.sri.ai.util.Util;
import com.sri.ai.util.base.NullaryFunction;

/**
 * A class aggregating all data for a gnuplot plot.
 * 
 * @author braz
 */
@Beta
public class GnuplotData {
	
	/** A list of string descriptions for each data series. */
	private List<String> commandLineDescriptions;

	/** A list of File objects to be deleted at clean up. */
	private List<File> filesToDelete = new LinkedList<File>();

	/** Index used to name data files. */
	private int dataFileIndex = 0;

	public <T> GnuplotData(NullaryFunction<Iterator<T>> xSeries, List<DataSeries<T>> dataSeriesList) {
		try {
			commandLineDescriptions = new LinkedList<String>();
			for (DataSeries<T> dataSeries : dataSeriesList) {
				commandLineDescriptions.add(getGnuplotCommandLineDescription(dataSeries, xSeries));
			}
		}
		catch (IOException e) {
			Util.fatalError("Could not generate data files for gnuplot.", e);
		}
	}

	/** @return a comma-separated string of descriptions. */
	public String getGnuplotCommandLinePlotArguments() {
		return Util.join(",", commandLineDescriptions);
	}

	/** 
	 * Get gnuplot command line description for a y-series to be plotted, including x-series data if available
	 * (that is, <code>xSeries</code> is not <code>null</code>).
	 */
	private <T> String getGnuplotCommandLineDescription(DataSeries<T> dataSeries, NullaryFunction<Iterator<T>> xSeriesIteratorMaker) throws IOException {
		String titleClause = getClauseValueOrEmptyString(dataSeries.directives, "title ", "t ");
		if (titleClause.equals("")) {
			titleClause = getClauseValueOrEmptyString(dataSeries.directives, "notitle", "notitle");
		}
		
		String withClause = getClauseValueOrEmptyString(dataSeries.directives, "with ", "w ");
		
		boolean xSeriesIsAvailable = xSeriesIteratorMaker != null;
		
		Iterator<T> xSeriesIterator = xSeriesIsAvailable? xSeriesIteratorMaker.apply() : null;
		
		String path = storeDataAndReturnPath(xSeriesIterator, dataSeries.dataIteratorMaker.apply());
		
		StringBuffer command = new StringBuffer();
		command.append("'" + path + "'");
		command.append(xSeriesIsAvailable? " using 1:2" : " using 1");
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

	private <T> String storeDataAndReturnPath(Iterator<T> xSeriesDataIterator, Iterator<T> dataSeriesDataIterator) throws IOException {
		File tempFile = new File("gnuplot" + dataFileIndex++ + ".dat");
		BufferedWriter w = new BufferedWriter(new FileWriter(tempFile));
		
		if (xSeriesDataIterator != null) {
			writeDataWithXSeries(xSeriesDataIterator, dataSeriesDataIterator, w);
		}
		else {
			writeDataWithoutXSeries(dataSeriesDataIterator, w);
		}
		w.close();
		filesToDelete.add(tempFile);
		return tempFile.getPath();
	}

	private <T> void writeDataWithoutXSeries(Iterator<T> dataSeriesDataIterator, BufferedWriter w) throws IOException {
		while (dataSeriesDataIterator.hasNext()) {
			Object y = dataSeriesDataIterator.next();
			w.write(y.toString());
			w.newLine();
		}
	}

	private <T> void writeDataWithXSeries(
			Iterator<T> xSeriesDataIterator, Iterator<T> dataSeriesDataIterator, BufferedWriter w) throws IOException {
		while (xSeriesDataIterator.hasNext()) {
			w.write(xSeriesDataIterator.next() + " " + dataSeriesDataIterator.next());
			w.newLine();
		}
	}

	public void cleanUp() {
//		for (File file : filesToDelete) {
//			file.deleteOnExit();
//		}
	}
}