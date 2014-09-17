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
package com.sri.ai.util.experiment;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.common.annotations.Beta;
import com.sri.ai.util.Util;
import com.sri.ai.util.gnuplot.Gnuplot;
import com.sri.ai.util.gnuplot.DataSeries;
import com.sri.ai.util.rangeoperation.api.DAEFunction;
import com.sri.ai.util.rangeoperation.api.DependencyAwareEnvironment;
import com.sri.ai.util.rangeoperation.api.Range;
import com.sri.ai.util.rangeoperation.api.RangeOperation;
import com.sri.ai.util.rangeoperation.core.AbstractDAEFunction;
import com.sri.ai.util.rangeoperation.core.RangeOperationsInterpreter;
import com.sri.ai.util.rangeoperation.library.rangeoperations.Dimension;

@Beta
public class Experiment {

	public static String[] guaranteedPreCommands = {
		"set xlabel font 'Arial, 10",
		"set ylabel font 'Arial, 10'",
		"set title  font 'Arial, 20'"
	};

	/** 
	 * Runs an experiment and generates a gnuplot with its results.
	 * <p>
	 * The method works by generating a data matrix (according to {@link RangeOperationsInterpreter})
	 * with one or two {@link Dimension}s.
	 * The plot may contain one or more data series (shown as lines on the plot).
	 * A {@link DataSeriesSpec} object defines which {@link Dimension} is used to define multiple data series,
	 * as well as data series titles and format.
	 * <p>
	 * There is a commented example at the end of this documentation block.
	 * <p>
	 * Arguments to this method can be of three possible types:
	 * <ul>
	 * <li> String values representing variable names
	 *      followed by another argument representing the variable's value.
	 *      These variables can either be gnuplot parameters (see reference below),
	 *      or fixed parameters stored in a {@link DependencyAwareEnvironment} to be used
	 *      by the experiment (via {@link DAEFunction}s -- see below).
	 * <li> {@link RangeOperation}s, which work like for-loops (creating data matrix {@link Dimension}s)
	 *      or aggregate operations for a specified variable.
	 * <li> a single occurrence of a {@link DAEFunction}; this is the main argument since
	 *      this function is responsible for computing the experiment's reported results.
	 *      It can run whatever code one wishes, including calls to
	 *      {@link DependencyAwareEnvironment#get(String)},
	 *      {@link DependencyAwareEnvironment#getOrUseDefault(String, Object)} and
	 *      {@link DependencyAwareEnvironment#getResultOrRecompute(DAEFunction)}
	 *      to gain access to the variables defined by other arguments
	 *      (both fixed and iterated by range operations).
	 *      The interaction between range operations and a DAEFunction
	 *      produces a generalized (multidimensional) matrix (see {@link RangeOperationsInterpreter} for details).
	 *      In {@link Experiment}, the range operations and DAEFunction must be structured
	 *      so that the resulting matrix is a regular rows-and-columns matrix
	 *      in which each row is the data for an individual data series to appear in the plot.
	 * <li> a single {@link DataSeriesSpec}, which declares gnuplot directives for each of
	 *      the data series (the several graph lines) in the graph ({@link DataSeries}).
	 *      We obtain one data series per value of a variable specified by
	 *      the {@link DataSeriesSpec}.
	 * </ul>
	 * Arguments can be provided in any order, although the order of range operations does matter
	 * (their iterations are nested in the order they are given).
	 * <p>
	 * The variable on the graph's x-axis is the one specified by the first {@link Dimension} argument
	 * that is <i>not</i> the variable labeling the multiple data series.
	 * <p>
	 * The recognized gnuplot parameters are the following:
	 * <ul>
	 * <li> title: the graph's title
	 * <li> xlabel: the label of the x axis (default is variable name in x-{@link Dimension}).
	 * <li> ylabel: the label of the y axis (default is empty string).
	 * <li> filename: the name of a file (without an extension) to which to record the graph;
	 *                the extension ".ps" is automatically added.
	 * <li> file: boolean value indicating whether to record the graph, using title as filename.
	 * <li> print: same as file
	 * </ul>
	 * If file recording is disabled, gnuplot persists (keeps open) and shows the graph;
	 * otherwise, it closes as soon as the graph is recorded.
	 * <p>
	 * Consider the example:
	 * <pre>
	 * 		experiment(
	 *  		"file", "false",
	 *  		"title", "The more samples, the less variance",
	 *  		"xlabel", "Number of samples",
	 *  		"ylabel", "Average of Uniform[0,1] + mean - 0.5",
	 *  		"some unused variable that could be used if we wanted to", 10,
	 *  		new {@link Dimension}("mean", Util.list(2, 3)),
	 *  		new {@link Dimension}("numSamples", 1, 1000, 1),
	 *  		averageOfNumSamplesOfUniformPlusCurrentMeanMinusZeroPointFive,
	 *  		{@link DataSeriesSpec}("mean", Util.list(
	 *  				Util.list("title 'mean 2'", "w linespoints"),
	 *  				Util.list("title 'mean 3'", "w linespoints"))));
	 * </pre>
	 * Here, some gnuplot parameters and a (unused) variable are introduced.
	 * Then ranging operations {@link Dimension} is used to vary variables "mean" and "numSamples"
	 * across a range of values.
	 * The function averageOfNumSamplesOfUniformPlusCurrentMeanMinusZeroPointFive uses them to compute
	 * the elements of a matrix.
	 * The {@link DataSeriesSpec} specifies that the "mean" dimension is the one determining
     * the individual data series for the plot
	 * (and as a consequence the "numSamples" dimension is selected for the x-axis),
	 * and specifies their gnuplot labels and styles as well.
	 * Note that the plot's y-axis does not correspond to any dimensions of the matrix, but to its values
	 * (the ones computed by the {@link DAEFunction}).
	 * 
	 * @param arguments
	 *        the experiments arguments.
	 */ 
	public static void experiment(Object ... arguments) {

		List<Dimension<Object>> dimensions = getDimensions(arguments);
		
		DataSeriesSpec dataSeriesDimensionSpec = getDataSeriesDimensionSpec(arguments);

		Range<Object> xSeries = getXDimensionRange(dimensions, dataSeriesDimensionSpec);
		
		List data = (List) RangeOperationsInterpreter.apply(arguments);

		List<DataSeries<Object>> dataSeriesList = getDataSeriesList(data, dimensions, dataSeriesDimensionSpec);

		Map<String, Object> properties = Util.getMapWithStringKeys(arguments);
		
		if ( ! properties.containsKey("xlabel")) {
			final Dimension<Object> xDimension = getXDimension(dimensions, dataSeriesDimensionSpec);
			String name = xDimension == null? "x" : xDimension.getRange().getName();
			properties.put("xlabel", name);
		}

		LinkedList<String> preCommands = getPreCommands(properties);

		Gnuplot.plot(preCommands, xSeries, dataSeriesList);
	}

	private static LinkedList<String> getPreCommands(Map<String, Object> properties) {
		LinkedList<String> preCommands = new LinkedList<String>();

		for (String preCommand : guaranteedPreCommands) {
			preCommands.add(preCommand);
		}

		if (properties.containsKey("title")) {
			preCommands.add("set title '" + properties.get("title") + "'");
		}
		if (properties.containsKey("xlabel")) {
			preCommands.add("set xlabel '" + properties.get("xlabel") + "'");
		}
		if (properties.containsKey("ylabel")) {
			preCommands.add("set ylabel '" + properties.get("ylabel") + "'");
		}
		if (writesToFile(properties)) {
			preCommands.add("set term postscript color");
			preCommands.add("set output '" + filename(properties) + ".ps'");
		} else {
			preCommands.add("persist");
		}
		return preCommands;
	}

	private static boolean writesToFile(Map<String, Object> properties) {
		boolean result = properties.containsKey("filename")
				|| Util.getOrUseDefault(properties, "print", "false").equals("true")
				|| Util.getOrUseDefault(properties, "file", "false").equals("true");
		return result;
	}

	private static String filename(Map<String, Object> properties) {
		String filename = (String) Util.getOrUseDefault(properties, (String) properties.get("filename"), properties.get("title"));
		if (filename == null) {
			filename = "unnamed";
		}
		return filename;
	}

	private static <T> List<Dimension<T>> getDimensions(Object ... arguments) {
		List<Dimension<T>> result = new LinkedList<Dimension<T>>();
		for (Object object : arguments) {
			if (object instanceof Dimension<?>) {
				@SuppressWarnings("unchecked")
				Dimension<T> dimension = (Dimension<T>) object;
				result.add(dimension);
			}
		}
		return result;
	}

	/** 
	 * A class indicating the variable corresponding to a data series in a graph,
	 * as well as its directives (see {@link Gnuplot}).
	 */
	public static class DataSeriesSpec {

		private String variable;
		private List<List<String>> directivesList;

		public DataSeriesSpec(String variable, List<List<String>> directivesList) {
			this.variable = variable;
			this.directivesList = directivesList;
		}
		
		public String getName() {
			return variable;
		}
	}

	private static DataSeriesSpec getDataSeriesDimensionSpec(Object... arguments) {
		return (DataSeriesSpec) Util.getObjectOfClass(DataSeriesSpec.class, arguments);
	}

	private static <T> Range<T> getXDimensionRange(List<Dimension<T>> dimensions, DataSeriesSpec dataSeriesDimensionSpec) {
		Dimension<T> xDimension = getXDimension(dimensions, dataSeriesDimensionSpec);
		final Range<T> result = xDimension != null ? xDimension.getRange() : null;
		return result;
	}

	private static <T> Dimension<T> getXDimension(List<Dimension<T>> dimensions, DataSeriesSpec dataSeriesDimensionSpec) {
		for (Dimension<T> dimension : dimensions) {
			if ( ! dimension.getRange().getName().equals(dataSeriesDimensionSpec.variable)) {
				return dimension;
			}
		}
		return null;
	}

	private static <T> Dimension<T> getDataSeriesDimension(List<Dimension<T>> dimensions, DataSeriesSpec dataSeriesSpec) {
		for (Dimension<T> dimension : dimensions) {
			if (dimension.getRange().getName().equals(dataSeriesSpec.variable)) {
				return dimension;
			}
		}
		return null;
	}

	private static <T> List<DataSeries<T>> getDataSeriesList(List data, List<Dimension<T>> dimensions, DataSeriesSpec dataSeriesSpec) {
		List<DataSeries<T>> dataSeriesList = new LinkedList<DataSeries<T>>();
		Dimension<T> dataSeriesDimension = getDataSeriesDimension(dimensions, dataSeriesSpec);
		if (dataSeriesDimension != null) {
			int dimension = dimensions.indexOf(dataSeriesDimension);
			Iterator rangeIterator = (Iterator) dataSeriesDimension.getRange().apply();
			Iterator<List<String>> directiveIterator = dataSeriesSpec.directivesList.iterator();
			int sliceIndex = 0;
			while(rangeIterator.hasNext()) {
				rangeIterator.next();
				if ( ! directiveIterator.hasNext()) {
					throw new Error("DataSeriesSpec on '" + dataSeriesSpec.getName() + "' does not have enough directives (it needs one per value of '" + dataSeriesSpec.getName() + "')");
				}
				List<String> directives = (List<String>) directiveIterator.next();
				@SuppressWarnings("unchecked")
				List<T> dataSeriesData = Util.matrixSlice((List<List<T>>) data, dimension, sliceIndex);
				dataSeriesList.add(new DataSeries<T>(directives, dataSeriesData));
				sliceIndex++;
			}
		}
		else {
			if (dimensions.size() > 1) {
				Util.fatalError("DataSeriesSpec " + dataSeriesSpec + " does not refer to any present dimension and data is multidimensional.");
			}
			List<String> directives = Util.getFirst(dataSeriesSpec.directivesList);
			@SuppressWarnings("unchecked")
			List<T> dataList = (List<T>) data;
			dataSeriesList.add(new DataSeries<T>(directives, dataList));
		}
		return dataSeriesList;
	}

	/**
	 * An extension of {@link List<String>} for keeping pre-commands for a {@link Gnuplot} graph.
	 */
	@SuppressWarnings("serial")
	private static class PreCommands extends LinkedList<String> {
		public PreCommands(String ... preCommands) {
			addAll(Arrays.asList(preCommands));
		}
	}

	public static PreCommands preCommands(String ... preCommands) {
		return new PreCommands(preCommands);
	}


	public static PreCommands getPreCommands(Object ... arguments) {
		PreCommands preCommands = (PreCommands) Util.getObjectOfClass(PreCommands.class, arguments);
		if (preCommands == null) {
			preCommands = new PreCommands();
		}
		return preCommands;
	}

	private static class Title {
		public Title(String value) {
			buffer.append(value);
		}
		@Override
		public String toString() { return buffer.toString(); }
		private StringBuffer buffer = new StringBuffer();
	}

	public static Title Title(String value) {
		return new Title(value);
	}

	public static String getTitle(Object ... args) {
		return Util.getObjectOfClass(Title.class, args).toString();
	}


	private static DAEFunction averageOfNumSamplesOfUniformPlusCurrentMeanMinusZeroPointFive = new AbstractDAEFunction() {
		@Override
		public Object apply(DependencyAwareEnvironment environment) {
			int numberOfSamples = environment.getInt("numSamples");
			int mean = environment.getInt("mean");
			double sum = 0;
			for (int i = 0; i != numberOfSamples; i++) {
				sum += (mean - 0.5) + Math.random();
			}
			double result = sum/numberOfSamples;
			return result;
		}
	};

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		
		experiment(
				"file", "false",
				"title", "The more samples, the less variance",
				"xlabel", "Number of samples",
				"ylabel", "Average of Uniform[0,1] + mean - 0.5",
				"some unused variable that could be used if we wanted to", 10,
				
				new Dimension("mean", Util.list(2, 3)),
				new Dimension("numSamples", 1, 200, 1),
				
				averageOfNumSamplesOfUniformPlusCurrentMeanMinusZeroPointFive,
				
				new DataSeriesSpec("mean", Util.list(
						Util.list("title 'mean 2'", "w linespoints"),
						Util.list("title 'mean 3'", "w linespoints"))));

	}
}
