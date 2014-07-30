package com.sri.ai.util.gnuplot;

import java.util.*;
import java.io.*;

import com.sri.ai.util.Util;
import com.sri.ai.util.base.NullaryFunction;

/**
 * A class for using gnuplot programmatically.
 * 
 * @author braz
 */
public class Gnuplot {

	/** Time in milliseconds that is enough for gnuplot to launch and read temp files safely, with default of 2s. */
	public static final long gnuplotLaunchTime = 2000;

	/**
	 * Generates a plot with gnuplot using the following data:
	 * <ul>
	 * <li> <code>xSeries</code> is a {@link List<? extends Number>} or <code>null</code>
	 * <li> <code>ySeries</code> is a list of {@link #YSeries}
	 * </ul>
	 * It also issues a list of precommands to gnuplot provided as Strings
	 * (see gnuplot documentation for such precommands).
	 * If one of the precommands is "persist", gnuplot persists after execution.
	 */
	static public void plot(Collection<String> precommands, NullaryFunction xSeries, List<YSeries> ySeriesList) {
		GnuplotPipe pipe = new GnuplotPipe(precommands.contains("persist"));
		GnuplotData data = new GnuplotData(xSeries, ySeriesList);
		try {
			sendPrecommands(precommands, pipe);
			pipe.send("plot " + data.getDescription());
			Util.waitOrThrowError(gnuplotLaunchTime);
		}
		catch (IOException e) {
			throw new Error("Could not pipe commands to gnuplot.", e);
		}
		data.cleanUp();
	}

	private static void sendPrecommands(Collection<String> precommands, GnuplotPipe pipe) throws IOException {
		for (String precommand : precommands) {
			if (precommand.equals("persist")) {
				continue;
			}
			pipe.send(precommand);
		}
	}

	public static void main(String[] args) {
		plot(
				Util.list( // precommands
						"persist",
						//"set term postscript color",
						//"set output 'test.ps'",
						"set xlabel 'Time'",
						"set ylabel 'Intensity'"
				),
				getIteratorNullaryFunction(Util.list(10,20,30,40)), // xSeries
				Util.list( // list of ySeries
						new YSeries(
								Util.list("title 'random sequence 1'", "w linespoints"),
								Util.list(1,2,4,3)
								),
						new YSeries(
								Util.list("title 'random sequence 2'", "w linespoints"),
								Util.list(5,4,3,2)
								)
						)
				);
	}

    /** 
     * Returns a {@link NullaryFunction} that returns a new iterator to the given collection
     * each time is it invoked. 
     */
    static public NullaryFunction getIteratorNullaryFunction(final Collection c) {
	return new NullaryFunction() { public Object apply() { return c.iterator(); }};
    }
}
