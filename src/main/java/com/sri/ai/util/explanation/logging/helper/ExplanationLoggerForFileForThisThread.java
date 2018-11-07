package com.sri.ai.util.explanation.logging.helper;

import com.sri.ai.util.explanation.logging.api.ExplanationConfiguration;
import com.sri.ai.util.explanation.logging.api.ExplanationLogger;
import com.sri.ai.util.explanation.logging.api.ThreadExplanationLogger;
import com.sri.ai.util.explanation.logging.core.handler.FileExplanationHandler;

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
	
	private ExplanationLogger oldThreadExplanationLogger;

	public ExplanationLoggerForFileForThisThread(Class<? extends FileExplanationHandler> fileExplanationHandlerClass, String fileName) {
		super(fileExplanationHandlerClass, fileName);
		this.oldThreadExplanationLogger = ThreadExplanationLogger.getThreadExplanationLogger();
		ThreadExplanationLogger.setThreadExplanationLogger(this);
	}
	
	public ExplanationLoggerForFileForThisThread(String fileName) {
		this(ExplanationConfiguration.DEFAULT_FILE_EXPLANATION_HANDLER_CLASS, fileName);
	}

	@Override
	public void close() {
		ThreadExplanationLogger.setThreadExplanationLogger(oldThreadExplanationLogger);
		super.close();
	}

}
