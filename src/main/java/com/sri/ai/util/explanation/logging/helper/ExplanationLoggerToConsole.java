package com.sri.ai.util.explanation.logging.helper;

import com.sri.ai.util.explanation.logging.api.ExplanationHandler;
import com.sri.ai.util.explanation.logging.core.DefaultExplanationLogger;
import com.sri.ai.util.explanation.logging.core.handler.ConsoleExplanationHandler;

/**
 * A convenience class that extends {@link DefaultExplanationLogger} by automatically
 * adding a {@link ConsoleExplanationHandler} to it.
 * 
 * @author braz
 *
 */
public class ExplanationLoggerToConsole extends DefaultExplanationLogger {
	
	public ExplanationLoggerToConsole() {
		super();
		ExplanationHandler handler = new ConsoleExplanationHandler();
		addHandler(handler);
	}

}
