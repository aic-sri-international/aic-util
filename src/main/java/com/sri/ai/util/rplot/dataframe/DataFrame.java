package com.sri.ai.util.rplot.dataframe;

/**
 * 
 * 
 * In The order of the columns comprises, first, the double values columns, and next the
 * strig values ones
 * rows and columns are numbered starting from 1, just because it's the R way
 *      IDX|double|double|double| ...  |double|string|string| ...  |string|
 *     ---------------------------------------------------------------------
 *        1|      |      |      |      |      |      |      |      |	  |
 *        2|      |      |      |      |      |      |      |      |	  |
 *        3|      |      |      |      |      |      |      |      |	  |
 *      ...|      |      |      |      |      |      |      |      |	  |
 * 
 * @author gabriel
 *
 */
public interface DataFrame {
	public Object[] getRow(int i);
	public void addRow(Object[] row);
	public Object[] getColumn(int j);
}
