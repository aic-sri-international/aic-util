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
import com.sri.ai.util.gnuplot.YSeries;
import com.sri.ai.util.rangeoperation.api.DAEFunction;
import com.sri.ai.util.rangeoperation.api.DependencyAwareEnvironment;
import com.sri.ai.util.rangeoperation.api.Range;
import com.sri.ai.util.rangeoperation.core.AbstractDAEFunction;
import com.sri.ai.util.rangeoperation.core.RangeOperationsApplication;
import com.sri.ai.util.rangeoperation.library.rangeoperations.Averaging;
import com.sri.ai.util.rangeoperation.library.rangeoperations.Axis;

@Beta
public class Experiment {

	public static String[] guaranteedPreCommands = {
		"set xlabel font 'Arial, 20",
		"set ylabel font 'Arial, 20'",
	"set title font 'Arial, 10'"};

	/** 
	 * Runs an experiment and generates a gnuplot with its results.
	 * <p>
	 * Arguments to this method can be of three possible types:
	 * <ul>
	 * <li> String values representing variable names
	 *      followed by another argument representing the variable's value.
	 *      These variables can either be gnuplot parameters (see reference below),
	 *      or fixed parameters stored in a {@link DependencyAwareEnvironment} to be used
	 *      by the experiment (via {@link AbstractDAEFunction}s -- see below).
	 * <li> {@link RangeOperation}s, which work like for-loops or aggregate operations
	 *      for a given variable.
	 * <li> a single occurrence of a {@link AbstractDAEFunction}; this is the main argument since
	 *      this function is responsible for computing the experiment's reported results.
	 *      It can run whatever code one wishes, including calls to
	 *      {@link DependencyAwareEnvironment#get(String)},
	 *      {@link DependencyAwareEnvironment#getOrUseDefault(String, Object)} and
	 *      {@link DependencyAwareEnvironment#getResultOrRecompute(AbstractDAEFunction)}
	 *      to gain access to the variables defined by other arguments
	 *      (both fixed and iterated by range operations).
	 * <li> a single {@link YSeriesSpec}, which declares gnuplot directives for each of
	 *      the "lines" in the graph ({@link YSeries}).
	 *      We obtain one "line" per value of a variable, which is also specified by
	 *      the {@link YSeriesSpec}.
	 * </ul>
	 * Arguments can be provided in any order, although the order of range operations does matter
	 * (their iterations are nested in the order they are given).
	 * <p>
	 * The recognized gnuplot parameters are the following:
	 * <ul>
	 * <li> title: the graph's title
	 * <li> xlabel: the label of the x axis.
	 * <li> ylabel: the label of the y axis.
	 * <li> filename: the name of a file (without an extension) to which to record the graph;
	 *                the extension ".ps" is automatically added.
	 * <li> file: boolean value indicating whether to record the graph, using title as filename.
	 * <li> print: same as file
	 * </ul>
	 * If file recording is disabled, gnuplot persists (keeps open) and shows the graph;
	 * otherwise, it closes as soon as the graph is recorded.
	 */ 
	public static void experiment(Object ... args) {

		List data = (List) RangeOperationsApplication.run(args);

		Map<String, Object> properties = Util.getMapWithStringKeys(args);

		LinkedList<String> preCommands = getPreCommands(properties);

		List<Axis> axes = getAxes(args);
		
		YSeriesSpec ySeriesSpec = getYSeriesSpec(args);

		Range xSeries = getXSeries(axes, ySeriesSpec);
		
		List<YSeries> ySeriesList = getYSeriesList(data, axes, ySeriesSpec);

		Gnuplot.plot(preCommands, xSeries, ySeriesList);
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
		if (properties.containsKey("filename")
				|| Util.getOrUseDefault(properties, "print", "false").equals("true")
				|| Util.getOrUseDefault(properties, "file", "false").equals("true")) {
			String filename = (String) Util.getOrUseDefault(properties, (String) properties.get("filename"), properties.get("title"));
			if (filename == null) {
				filename = "unnamed";
			}
			preCommands.add("set term postscript color");
			preCommands.add("set output '" + filename + ".ps'");
		} else {
			preCommands.add("persist");
		}
		return preCommands;
	}

	private static List<Axis> getAxes(Object ... args) {
		List<Axis> result = new LinkedList<Axis>();
		for (Object object : args) {
			if (object instanceof Axis) {
				result.add((Axis) object);
			}
		}
		return result;
	}

	/** 
	 * A class indicating the variable corresponding to a Y series in a graph,
	 * as well as its directives (see {@link Gnuplot}).
	 */
	public static class YSeriesSpec {
		public YSeriesSpec(String variable, List<List<String>> directivesList) {
			this.variable = variable;
			this.directivesList = directivesList;
		}
		private String variable;
		private List<List<String>> directivesList;
	}

	/** Convenience method for constructing a {@link YSeriesSpec}. */
	public static YSeriesSpec YSeriesSpec(String variable, List<List<String>> directivesList) {
		return new YSeriesSpec(variable, directivesList);
	}

	private static YSeriesSpec getYSeriesSpec(Object... args) {
		return (YSeriesSpec) Util.getObjectOfClass(YSeriesSpec.class, args);
	}

	private static Range getXSeries(List<Axis> axes, YSeriesSpec ySeriesSpec) {
		for (Axis axis : axes) {
			if ( ! axis.getRange().getName().equals(ySeriesSpec.variable)) {
				return axis.getRange();
			}
		}
		return null;
	}

	private static Axis getSeriesAxis(List<Axis> axes, YSeriesSpec seriesSpec) {
		for (Axis axis : axes) {
			if (axis.getRange().getName().equals(seriesSpec.variable)) {
				return axis;
			}
		}
		return null;
	}

	private static List<YSeries> getYSeriesList(List data, List<Axis> axes, YSeriesSpec ySeriesSpec) {
		List<YSeries> ySeriesList = new LinkedList<YSeries>();
		Axis ySeriesAxis = getSeriesAxis(axes, ySeriesSpec);
		if (ySeriesAxis != null) {
			int dimension = axes.indexOf(ySeriesAxis);
			Iterator rangeIterator = (Iterator) ySeriesAxis.getRange().apply();
			Iterator<List<String>> directiveIterator = ySeriesSpec.directivesList.iterator();
			int sliceIndex = 0;
			while(rangeIterator.hasNext()) {
				rangeIterator.next();
				List<String> directives = (List<String>) directiveIterator.next();
				List<? extends Number> ySeriesData = matrixSlice(data, dimension, sliceIndex);
				ySeriesList.add(new YSeries(directives, ySeriesData));
				sliceIndex++;
			}
		}
		else {
			if (axes.size() > 1) {
				Util.fatalError("YSeriesSpec " + ySeriesSpec + " does not refer to any present axis and data is multidimensional.");
			}
			List<String> directives = Util.getFirst(ySeriesSpec.directivesList);
			ySeriesList.add(new YSeries(directives, data));
		}
		return ySeriesList;
	}

	/**
	 * An extension of {@link List} for keeping pre-commands for a {@link Gnuplot} graph.
	 */
	private static class PreCommands extends LinkedList {
		public PreCommands(String ... preCommands) {
			addAll(Arrays.asList(preCommands));
		}
	}

	public static PreCommands preCommands(String ... preCommands) {
		return new PreCommands(preCommands);
	}


	public static PreCommands getPreCommands(Object ... args) {
		PreCommands preCommands = (PreCommands) Util.getObjectOfClass(PreCommands.class, args);
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


	/**
	 * Takes a list of lists, a dimension (0 for rows, 1 for columns) and an index (either row or column index),
	 * and returns the corresponding slice (data[index,*] if dimension is 0, or data[*,index] if dimension is 1).
	 */
	public static List<Number> matrixSlice(List<List<Number>> data, int dimension, int index) {
		if (dimension == 0) {
			return (List<Number>) data.get(index);
		}
		List<Number> result = new LinkedList<Number>();
		for (Iterator<List<Number>> rowIt = data.iterator(); rowIt.hasNext();) {
			List<Number> row = rowIt.next();
			result.add(row.get(index));
		}
		return result;
	}

	private static DAEFunction getDistanceGivenTrueAndEstimatedDistributions = new AbstractDAEFunction() { @Override
	public Object apply(DependencyAwareEnvironment environment) {
		return environment.get("numSamples");
	}};

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		experiment(
				"file", "false",
				"title", "Test for experiments",
				"xlabel", "Number of samples",
				"ylabel", "Average distributions divergence",
				"numSamplesForTrueDistribution", 10,
				new Axis("inferenceEngineClassName", Util.list("blog.SamplingEngine", "blog.ParticleFilter")),
				new Axis("numSamples", 10, 200, 50),
				new Averaging("run", 1, 2),
				getDistanceGivenTrueAndEstimatedDistributions,
				YSeriesSpec("inferenceEngineClassName", Util.list(
						Util.list("title 'LW'", "w linespoints"),
						Util.list("title 'DBLOG PF'","w linespoints"))));

	}
}
