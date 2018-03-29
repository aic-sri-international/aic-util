package com.sri.ai.util.rplot;

import static com.sri.ai.util.Util.println;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

import com.sri.ai.util.Util;
import com.sri.ai.util.rplot.dataframe.DataFrame;
import com.sri.ai.util.rplot.dataframe.ListOfListDataFrame;

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
	public static void ggplotPlot(
			List<String> preProcessingCmds,
			List<String> ggplotAesArgs,
			List<String> listOfGgplotCmds, 
			DataFrame df) {
		
		ggplotPlot(preProcessingCmds,ggplotAesArgs,listOfGgplotCmds, df ,null);
	}
	
	/**
	 * Plot on file
	 * @param listOfGgplotCmds 	: The ggplot commands to be called
	 * @param dfNames			: The column names of the Data Frame
	 * @param dfColumns			: A list with the columns of the Data Frame
	 * @param ggplotArgs		: Ggplot (extra arguments) arguments 
	 * @param fileName 			: file name (HAS TO BE .pdf)
	 */
	public static void ggplotPlot(
			List<String> preProcessingCmds,
			List<String> ggplotAesArgs,
			List<String> listOfGgplotCmds, 
			DataFrame df,
			String fileName) {
		RConnection c = null;
		try {
			c = new RConnection();
			
			createDataFrameInR(listOfGgplotCmds, df, c);
			
			//Preprocessing
			for(String s : preProcessingCmds) {
				c.eval(s);
			}
			
			String aes = "aes(" + String.join(", ",ggplotAesArgs) + ")";
			String ggplotCmds = String.join(" + ",listOfGgplotCmds);
			c.eval("p <- ggplot(df," + aes + ") + " + ggplotCmds);
			
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

	private static void createDataFrameInR(List<String> listOfGgplotCmds, DataFrame df,
			RConnection c) throws REngineException, RserveException {
		
		ArrayList<String> listOfColIndexes = createColumnsInR(df, c);
		joinColunsIntoADataFrame(listOfGgplotCmds, df, c, listOfColIndexes);
	}

	private static ArrayList<String> createColumnsInR(DataFrame df, RConnection c) throws REngineException {
		int i = 0;
		ArrayList<String> s = new ArrayList<>();
		List<Object[]> cols = df.getColumns();
		for(Object[] col : cols) {
			if(col.length != 0) {
				if(col[0] instanceof Double) { //too messy! TODO compress it into a single command (if possible)
					double[] castedCol = new double[col.length];
					for(int j = 0; j<col.length;j++) castedCol[j] = (double)col[j]; 
					c.assign("l"+ i,castedCol);
				}
				else if(col[0] instanceof Integer) {
					int[] castedCol = new int[col.length];
					for(int j = 0; j<col.length;j++) castedCol[j] = (int)col[j]; 
					c.assign("l"+ i, castedCol);
				}
				else if(col[0] instanceof String) {
					String[] castedCol = new String[col.length];
					for(int j = 0; j<col.length;j++) castedCol[j] = (String)col[j]; 
					c.assign("l"+ i, castedCol);
				}
				s.add("l" + i);
			}
			i++;
		}
		return s;
	}
	
	private static void joinColunsIntoADataFrame(List<String> listOfGgplotCmds, DataFrame df,
			RConnection c, ArrayList<String> listOfColIndexes) throws RserveException {
		String indexesConcatenated = String.join(", ", listOfColIndexes);
		String colNames = "'"+ String.join("', '",df.getHeader()) + "'";
		
		c.eval("library(ggplot2)"); // importing library
		c.eval("df <- data.frame(" + indexesConcatenated + ")"); //creating a DataFrame in R
		c.eval("colnames(df) <- c(" + colNames + ")");
	}
	//Test code
	 public static void main(String a[]) {
		 // Start the server
		 println(StartRserve.checkLocalRserve());
		 // Import ggplot library (in R)
		 ggplotInstall();
		 
		 // Make dataFrame : 
		 Object[] x = {1.,2.,3.,4.,5.,6.};
		 Object[] y = {1.9,2.1,3.5,4.8,5.2,6.5};
		 DataFrame df = new ListOfListDataFrame(new ArrayList<>() , 0, 0, 0);
		 df.addColumn("x", Double.class, x);
		 df.addColumn("y", Double.class, y);
		 
		 // preProcessing (no pre processing in this example)
		 List<String> preProccesninCmds = new ArrayList<>();
		 
		 // ggplot parameters : one mandatory parameter is "aes", 
		 // which defines the axes (also colors among other things) 
		 List<String> ggplotAesArgs = Util.list("x = x", "y = y");
		 
		 // ggplot commands. those are the actual types of plots.
		 // by concatenating a list of commands, one plots many graphs
		 // one over the other.
		 List<String> ggplotCmds = Util.list("geom_point()","geom_smooth(method='loess')");
		 ggplotPlot(preProccesninCmds,ggplotAesArgs,ggplotCmds, df,"test.pdf");
		 println("-------------------------");
		 ggplotPlot(preProccesninCmds,ggplotAesArgs,ggplotCmds, df);
		 println("-------------------------");
		 
		 // The code above generates the following ggplot code in R:
		 // ggplot(df,aes(x = x,y = y)) + geom_point() + geom_smooth(method='loess')
		 //     ggplot setting            scatter plot         smooth line 
	 }
}
