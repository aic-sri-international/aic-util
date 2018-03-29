package com.sri.ai.util.rplot.dataframe;

import static com.sri.ai.util.Util.println;

import java.util.ArrayList;
import java.util.List;


/**
 * In The order of the columns comprises, first, the double values columns, and next the
 * strig values ones
 * rows and columns are numbered starting from 1, just because it's the R way
 *      IDX|int   |...   | int  |double|double|double| ...  |double|string|string| ...  |string|
 *     ----+------+------+------+------+------+------+------+------+------+------+------+------+
 *        1|      |      |      |      |      |      |      |      |      |      |      |	   |
 *        2|      |      |      |      |      |      |      |      |	  |	  	 |	  	|	   |
 *        3|      |      |      |      |      |      |      |      |	  |	  	 |	  	|	   |
 *      ...|      |      |      |      |      |      |      |      |	  |	  	 |	  	|	   |
 * 
 * @author gabriel
 *
 */

public class ListOfListDataFrame implements DataFrame{

	String dfName;
	ArrayList<String> colNames;
	ArrayList<ArrayList<Integer>> integerCols;
	ArrayList<ArrayList<Double>>  doubleCols;
	ArrayList<ArrayList<String>>  factorCols;
	int nRows;
	int nCols;
	
	public ListOfListDataFrame(List<String> colNames, 
			int numberOfDoubleTypedColumns, 
			int numberOfIntegerTypedColumns,
			int numberOfStringTypedColumns) {
		
		if(colNames.size() != numberOfIntegerTypedColumns + 
				numberOfDoubleTypedColumns + numberOfStringTypedColumns) {
			//TODO error message
			return;
		}
		
		this.colNames= new ArrayList<>(colNames);
		this.nRows = 0;
		this.nCols = colNames.size();
		
		this.integerCols = new ArrayList<>();
		this.doubleCols = new ArrayList<>();
		this.factorCols = new ArrayList<>();
				
		for (int i = 0; i < numberOfIntegerTypedColumns; i++) {
			this.integerCols.add(new ArrayList<>());			
		}

		for (int i = 0; i < numberOfDoubleTypedColumns; i++) {
			this.doubleCols.add(new ArrayList<>());			
		}

		for (int i = 0; i < numberOfStringTypedColumns; i++) {
			this.factorCols.add(new ArrayList<>());			
		}
		
	}
	
	@Override
	public Object[] getRow(int i) {
		if(nRows <= i) {
			//TODO : deal better with the error 
			//(throw exception/ better error message/ better returning object)
			println("error - print i > nrRows");
			return null;
		}
		
		Object[] res = new Object[this.nCols];
		int j = 0;
		int k = 0;
		while(j < this.integerCols.size()) {
			res[j] = this.integerCols.get(k).get(i);
			j++;
			k++;
		}
		k = 0;
		while(j < this.doubleCols.size()) {
			res[j] = this.doubleCols.get(k).get(i);
			j++;
			k++;
		}
		k=0;
		while(j < this.nCols) {
			res[j] = this.factorCols.get(j).get(i);
			j++;
			k++;
		}
		return res;
	}

	@Override
	public void addRow(Object[] row) {
		if (row.length != this.nCols) {
			//TODO error message
			return;
		}
		int k = 0;
		int j = 0;
		while(j < this.integerCols.size()) {
			ArrayList<Integer> l = this.integerCols.get(k);
			l.add((Integer) row[j]);
			k++;j++;
		}
		k = 0;
		while(j < this.doubleCols.size()) {
			ArrayList<Double> l = this.doubleCols.get(k);
			l.add((Double) row[j]);
			k++;j++;
		}
		k = 0;
		while(j < this.factorCols.size()) {
			ArrayList<String> l = this.factorCols.get(k);
			l.add((String) row[j]);
			k++;j++;
		}
		this.nRows ++;
	}

	@Override
	public Object[] getColumn(int j) {
		if (j > this.nCols) {
			println("error - print j > nrRows");
			return null;
		}
		
		if(j < this.integerCols.size()) {
			Object[] res = this.integerCols.get(j).toArray();
			return res;
		}
		
		j = j - this.integerCols.size();
		
		if(j < this.doubleCols.size()) {
			Object[] res = this.doubleCols.get(j).toArray();
			return res;
		}

		j = j - this.doubleCols.size();
		
		if(j < this.factorCols.size()) {
			Object[] res = this.factorCols.get(j).toArray();
			return res;
		}
		return null;
	}

}
