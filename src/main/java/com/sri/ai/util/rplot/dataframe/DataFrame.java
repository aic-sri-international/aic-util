package com.sri.ai.util.rplot.dataframe;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
	public int getNumberOfRows();
	public int getNumberOfCols();
	public List<String> getHeader();
	
	public Object[] getRow(int i);
	public void addRow(Object... rowElements);
	public Object[] getColumn(int j);
	public List<Object[]> getColumns();
	public void addColumn(String name,Class<?> c,Object... elements);
	
	default public void printToCsv(String fileName) {
		//TODO: add a path, so that it prints in the right place
		// to do so, we should deal with differenc encodding for windows and linux/mac
        
		try {
			FileWriter fileWriter = new FileWriter(fileName);
			
			String header = String.join(",", getHeader()) + "\n";
			fileWriter.write(header);
			
			int nRows = getNumberOfRows();
            for(int i = 0; i < nRows; i++) {
            	Object[] row = getRow(i);
            	ArrayList<String> rowString = new ArrayList<>();
            	for (int j = 0; j < row.length;j++) {
            		rowString.add(row[j].toString());
            	}
            	
            	String s = String.join(",", rowString) + "\n";
            	fileWriter.write(s);
            }
			
            fileWriter.flush();
            fileWriter.close();			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			
		}

	}
	
}
