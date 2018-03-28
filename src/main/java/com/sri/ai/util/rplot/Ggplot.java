package com.sri.ai.util.rplot;

import static com.sri.ai.util.Util.println;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

import com.sri.ai.util.Util;

/**
 * Requirements: 
 * 		R
 * 		
 * This class allows the creation of ggplot graphs.
 * 
 * @author gabriel
 *
 */
public class Ggplot {
	
	/**
	 * Intall ggplot, if not installed yet;
	 */
	public static void ggplotInstall()  {
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

	/**
	 * Plot on the screen
	 * @param listOfGgplotCmds 	: The ggplot commands to be called
	 * @param dfNames			: The column names of the Data Frame
	 * @param dfColumns			: A list with the columns of the Data Frame
	 * @param ggplotArgs		: Ggplot (extra arguments) arguments 
	 */
	public static void ggplotPlot(List<String> listOfGgplotCmds, 
			List<String> dfNames,List<double[]> dfColumns,List<String> ggplotArgs) {
		
		ggplotPlot(listOfGgplotCmds, dfNames,dfColumns,ggplotArgs,null);
	}
	
	/**
	 * Plot on file
	 * @param listOfGgplotCmds 	: The ggplot commands to be called
	 * @param dfNames			: The column names of the Data Frame
	 * @param dfColumns			: A list with the columns of the Data Frame
	 * @param ggplotArgs		: Ggplot (extra arguments) arguments 
	 * @param fileName 			: file name (HAS TO BE .pdf)
	 */
	public static void ggplotPlot(List<String> listOfGgplotCmds, 
			List<String> dfNames,List<double[]> dfColumns,List<String> ggplotArgs,
			String fileName) {
		RConnection c = null;
		try {
			c = new RConnection();
			
			ArrayList<String> listOfColIndexes = createColumnsInR(dfColumns, c);
			
			String indexesConcatenated = String.join(", ", listOfColIndexes);
			String args = String.join(", ",ggplotArgs);
			String colNames = "'"+ String.join("', '",dfNames) + "'";
			String ggplotCmds = String.join(" + ",listOfGgplotCmds);
			
            c.eval("library(ggplot2)");
			c.eval("df <- data.frame(" + indexesConcatenated + ")");
			c.eval("colnames(df) <- c(" + colNames + ")");
			c.eval("p <- ggplot(df," + args + ") + " + ggplotCmds);
			if(fileName != null) {
				c.eval("pdf( \"~/"+fileName+"\" );print(p);dev.off()");
			}
			else{
				c.eval("print(p)");
				println("Please press Enter");
				System.in.read();
			}
			//c.eval("dev.off()");
		} catch (RserveException e) {
			e.printStackTrace();
		} catch (REngineException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
	}


	private static ArrayList<String> createColumnsInR(List<double[]> dfColumns, RConnection c) throws REngineException {
		int i = 0;
		ArrayList<String> s = new ArrayList<>();
		for(double[] col : dfColumns) {
			c.assign("l"+i, col);
			s.add("l" + i);
			i++;
		}
		return s;
	}
	
	//Test code
	 public static void main(String a[]) {
		 // Start the server
		 println(StartRserve.checkLocalRserve());
		 // Import ggplot library (in R)
		 ggplotInstall();
		 // Plot something
		 
		 // Make dataFrame : we pass a list of the data-frame columns
		 double[] x = {1.,2.,3.,4.,5.,6.};
		 double[] y = {1.9,2.1,3.5,4.8,5.2,6.5};
		 List<double[]> dfColumns = Util.list(x,y);
		 
		 // We pass the names of the columns (ggplot) plot them automatically
		 List<String> dfNames = Util.list("x","y");
		 
		 // ggplot parameters : one mandatory parameter is "aes", 
		 // which defines the axes (also colors among other things) 
		 List<String> ggplotArgs = Util.list("aes(x = x, y = y)");
		 
		 // ggplot commands. those are the actual types of plots.
		 // by concatenating a list of commands, one plots many graphs
		 // one over the other.
		 List<String> ggplotCmds = Util.list("geom_point()","geom_smooth(method='loess')");
		 ggplotPlot(ggplotCmds, dfNames , dfColumns , ggplotArgs,"test.pdf");
		 println("-------------------------");
		 ggplotPlot(ggplotCmds, dfNames , dfColumns , ggplotArgs);
		 println("-------------------------");
		 
		 // The code above generates the following ggplot code in R:
		 // ggplot(df,aes(x = x,y = y)) + geom_point() + geom_smooth(method='loess')
		 //     ggplot setting            scatter plot         smooth line 
	 }
}
