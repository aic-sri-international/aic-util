package com.sri.ai.util.explanation.logging.helper;

import com.sri.ai.util.explanation.logging.api.ThreadExplanationLogger;

/** 
 * A convenience class that creates a {@link ExplanationLoggerToFile} and sets it as this thread's explanation logger;
 * the reason to make it a class (as opposed to a static method in {@link ThreadExplanationLogger}, for example,
 * is that it can be created inside a try-with-resources statement and therefore automatically close the file even
 * if there is an exception.
 * 
 * @author braz
 *
 */
public class ExplanationLoggerForFileForThisThread extends ExplanationLoggerToFile implements AutoCloseable {

	public ExplanationLoggerForFileForThisThread(String fileName) {
		super(fileName);
		ThreadExplanationLogger.setThreadExplanationLogger(this);
	}

}
