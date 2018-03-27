package com.sri.ai.util.rplot;

import static com.sri.ai.util.Util.println;

import java.io.IOException;
import java.util.List;

//import org.rosuda.REngine.Rserve.*;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

/**
 * Requirements: 
 * 		R
 * 		Rserve installed (in R, do: install.packages("Rserve"))
 * 		Include <YOUR_R_HOME>\library\Rserve\java\REngine.jar 
 * 				<YOUR_R_HOME>\library\Rserve\java\REngine.jarand Rserve.jar 
 * 			in the eclipse path. 
 * 			In my case it was "/home/gabriel/R/x86_64-pc-linux-gnu-library/3.4/Rserve/java/"...  
 * 		
 * @author gabriel
 *
 */
public class ggplot {
	
	//
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

	public static void ggplotPlot(String cmd, List<String> args) {
		//TODO
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
