package com.sri.ai.util.rplot;

import static com.sri.ai.util.Util.println;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.rosuda.REngine.REngineException;
//import org.rosuda.REngine.Rserve.*;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

/**
 * Requirements: 
 * 		R
 * 		
 * @author gabriel
 *
 */
public class Ggplot {
	
	public static void ggplotInit()  {
    	RConnection connection = null;
    
		try {        	
        	//Install ggplot2 if not installed
        	Runtime.getRuntime().exec("Rscript installggplot2.R");
        	
            connection = new RConnection();
            connection.eval("library(ggplot2)");
        } catch (RserveException e) {
            e.printStackTrace();
        } catch (IOException e) {
        e.printStackTrace();
        }
        finally{
            connection.close();
        }
	}

	public static void ggplotPlot(String ggplotCmd, 
			List<String> dfNames,List<double[]> dfColumns,List<String> ggplotArgs) {
		RConnection c = null;
		try {
			c = new RConnection();
			
			ArrayList<String> ListOfColNames = createColumnsInR(dfColumns, c);
			
			String listsIndexesConcatenated = parseToOneString(ListOfColNames);
			String args = parseToOneString(ggplotArgs);
			
			c.eval("df<-data.frame(" + listsIndexesConcatenated + ")");
			c.eval("p <-" + ggplotCmd + "(df," + args + ")");
		} catch (RserveException e) {
			e.printStackTrace();
		} catch (REngineException e) {
			e.printStackTrace();
		}finally {
			c.close();
		}
	}

	private static String parseToOneString(List<String> listOfColNames) {
		String res = "";
		
		boolean first = true;
		for(String s : listOfColNames) {
			if(first) {
				res = s;
				first = false;
			}
			else{
				res = res + "," + s;
			}
		}
		return res;
	}

	private static ArrayList<String> createColumnsInR(List<double[]> dfColumns, RConnection c) throws REngineException {
		int i = 0;
		ArrayList<String> s = new ArrayList<>();
		for(double[] col : dfColumns) {
			c.assign("l"+i, col);
			s.add(s + "l");
			i++;
		}
		return s;
	}
	
	//Test code
	 public static void main(String a[]) {
		 // Start the server
		 println(StartRserve.checkLocalRserve());
		 // Import ggplot library (in R)
		 ggplotInit();
		 // Plot something
		 //TODO
		 
		 println("-------------------------");
		 

	 }
}
