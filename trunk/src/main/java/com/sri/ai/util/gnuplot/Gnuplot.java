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

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.google.common.annotations.Beta;
import com.sri.ai.util.Util;
import com.sri.ai.util.base.NullaryFunction;

/**
 * A class for using gnuplot programmatically.
 * It requires gnuplot to be installed, with its directory in the PATH.
 * 
 * @author braz
 */
@Beta
public class Gnuplot {

	/** Time in milliseconds that is enough for gnuplot to launch and read temporary files safely, with default of 2 seconds. */
	public static long gnuplotLaunchTime = 2000;

	/**
	 * Generates a plot with gnuplot using the following data:
	 * <ul>
	 * <li> <code>xSeries</code> is a {@link NullaryFunction<Iterator<Number>>} providing
	 *      iterators over the numbers to appear on the x axis, or <code>null</code>
	 *      (in which case a simple integer enumeration matching the ySeries is used instead)
	 * <li> <code>ySeries</code> is a list of {@link YSeries}, each describing a data series to be plotted
	 *      and gnuplot command line directives for the series.
	 * </ul>
	 * The method also issues a list of precommands to gnuplot provided as Strings
	 * (see gnuplot documentation for such precommands).
	 * If one of the precommands is the non-gnuplot "persist", gnuplot persists after execution.
	 */
	static public <T extends Number> void plot(
			List<String> precommands,
			NullaryFunction<Iterator<T>> xSeries,
			List<YSeries<T>> ySeriesList) {
		
		GnuplotPipe pipe = new GnuplotPipe(precommands.contains("persist"));
		GnuplotData data = new GnuplotData(xSeries, ySeriesList);
		try {
			sendPrecommands(precommands, pipe);
			pipe.send("plot " + data.getGnuplotCommandLinePlotArguments());
			Util.waitOrThrowError(gnuplotLaunchTime);
		}
		catch (IOException e) {
			throw new Error("Could not pipe commands to gnuplot.", e);
		}
		data.cleanUp();
	}

	private static void sendPrecommands(Collection<String> precommands, GnuplotPipe pipe) throws IOException {
		for (String precommand : precommands) {
			if ( ! precommand.equals("persist")) {
				pipe.send(precommand);
			}
		}
	}

	/** A test. */
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		plot(
				Util.list( // precommands
						"persist",
						//"set term postscript color",
						//"set output 'test.ps'",
						"set xlabel 'Time'",
						"set ylabel 'Intensity'"
				),
				Util.getIteratorNullaryFunction(Util.list(10,20,30,40)), // xSeries
				Util.list( // list of ySeries
						new YSeries<Integer>(
								Util.list("title 'random sequence 1'", "w linespoints"),
								Util.list(1,2,4,3)
								),
						new YSeries<Integer>(
								Util.list("title 'random sequence 2'", "w linespoints"),
								Util.list(5,4,3,2)
								)
						)
				);
	}
}
