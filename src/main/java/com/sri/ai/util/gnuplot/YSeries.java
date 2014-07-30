package com.sri.ai.util.gnuplot;

import java.util.List;

import com.sri.ai.util.Util;
import com.sri.ai.util.base.NullaryFunction;

/**
 * Class representing a y series in gnuplot. 
 * If a list <code>directives</code> of Strings is given, each of them
 * is interpreted in the following way: if it starts with either "title " or "t ",
 * it is considered a 'title' directive for the series;
 * if it starts with either "with " or "w ", it is considered a 'with' directive for the series
 * (consult the gnuplot documentation for further details).
 * Their order does not matter; they are correctly placed in the gnuplot command.
 */
public class YSeries {
	public List<String> directives;
	public NullaryFunction data;

	public YSeries(List<? extends Number> data) {
		this.directives = Util.list();
		this.data = Gnuplot.getIteratorNullaryFunction(data);
	}
	
	public YSeries(List<String> directives, List<? extends Number> data) {
		this.directives = directives;
		this.data = Gnuplot.getIteratorNullaryFunction(data);
	}
}