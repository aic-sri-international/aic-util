package com.sri.ai.util.explanation.logging.api;

import static com.sri.ai.util.Util.getOrMakeAndPut;
import static com.sri.ai.util.Util.map;

import java.util.Collection;
import java.util.Map;

import com.sri.ai.util.base.NullaryFunction;
import com.sri.ai.util.base.NullaryProcedure;
import com.sri.ai.util.explanation.logging.core.ExplanationBlock;
import com.sri.ai.util.explanation.logging.core.ExplanationBlock.Code;
import com.sri.ai.util.explanation.logging.core.handler.FileExplanationHandler;
import com.sri.ai.util.explanation.logging.helper.ExplanationLoggerForFileForThisThread;
import com.sri.ai.util.explanation.logging.helper.ExplanationLoggerToConsole;
import com.sri.ai.util.explanation.logging.helper.ExplanationLoggerToFile;
import com.sri.ai.util.tree.Tree;

/**
 * A class with public static methods for accessing and using a thread-wide {@link ExplanationLogger}.
 * 
 * @author braz
 *
 */
public class ThreadExplanationLogger {

	/** 
	 * Convenience constant redirecting to {@link ExplanationBlock#RESULT}.
	 */
	public static final Object RESULT = ExplanationBlock.RESULT;

	/**
	 * Convenience redirection to {@link ExplanationBlock#code(NullaryFunction)}.
	 */
	public static <T> Code<T> code(NullaryFunction<T> code) {
		return ExplanationBlock.code(code);
	}

	/**
	 * Convenience redirection to {@link ExplanationBlock#code(NullaryProcedure)}.
	 */
	public static Code<Void> code(NullaryProcedure code) {
		return ExplanationBlock.code(code);
	}

	/** Convenience redirecting to {@link ExplanationLogger#lazy(NullaryFunction)}. */
	public static NullaryFunction<Object> lazy(NullaryFunction<Object> code) {
		return ExplanationLogger.lazy(code);
	}

	
	
	/**
	 * Convenience method that sets this thread's default explanations logger to an instance of
	 * {@link ExplanationLoggerForFileForThisThread} linked to a file with a given name,
	 * also arranging for this file to be automatically closed in the case a throwable is thrown,
	 * and invokes {@link #explanationBlock(Object...)} with its remaining arguments.
	 */
	public static void explanationBlockToFile(Class<? extends FileExplanationHandler> fileExplanationHandlerClass, String fileName, Object... objects) {
		try (ExplanationLoggerForFileForThisThread threadLogger = new ExplanationLoggerForFileForThisThread(fileExplanationHandlerClass, fileName);) {
			ThreadExplanationLogger.explanationBlock(objects);
		}
	}
	
	/**
	 * Same as {@link #explanationBlockToFile(Class, String, Object...)} for the default file explanation handler class.
	 */
	public static void explanationBlockToFile(String fileName, Object... objects) {
		try (ExplanationLoggerForFileForThisThread threadLogger = new ExplanationLoggerForFileForThisThread(fileName);) {
			ThreadExplanationLogger.explanationBlock(objects);
		}
	}
	
	private final static Map<Thread, ExplanationLogger> fromThreadToExplanationLogger = map();
	
	public static ExplanationLogger getThreadExplanationLogger() {
		ExplanationLogger logger = 
				getOrMakeAndPut(
						fromThreadToExplanationLogger, 
						Thread.currentThread(),
						() -> getDefaultThreadExplanationLogger());
		return logger;
	}

	/**
	 * Returns the explanation logger to be used by default for the thread logger
	 * (that is, a logger to be used if one has not been explicitly provided by {@link #setThreadExplanationLogger(ExplanationLogger)}.
	 * If a file handler has been specified in the system properties, it is used to create a file logger to the file "explanations.txt"
	 * in the current directory.
	 * Otherwise, returns a console logger.
	 * @return
	 */
	public static ExplanationLogger getDefaultThreadExplanationLogger() {
		if (ExplanationConfiguration.explanationFileHasBeenRequested()) {
			return new ExplanationLoggerToFile(ExplanationConfiguration.DEFAULT_FILE_EXPLANATION_HANDLER_CLASS, "explanations.txt");
		}
		else {
			return new ExplanationLoggerToConsole();
		}
	}
	
	public static void setThreadExplanationLogger(ExplanationLogger logger) {
		fromThreadToExplanationLogger.put(Thread.currentThread(), logger);
	}
	
	
	
	public static boolean isActive() {
		return getThreadExplanationLogger().isActive();
	}
	
	public static void setIsActive(boolean newIsActive) {
		getThreadExplanationLogger().setIsActive(newIsActive);
	}

	
	
	public static Number getImportanceThreshold() {
		return getThreadExplanationLogger().getImportanceThreshold();
	}
	
	public static void setImportanceThreshold(Double importanceThreshold) {
		getThreadExplanationLogger().setImportanceThreshold(importanceThreshold);
	}
	
	
	
	public static ExplanationFilter getFilter() {
		return getThreadExplanationLogger().getFilter();
	}
	
	public static void setFilter(ExplanationFilter filter) {
		getThreadExplanationLogger().setFilter(filter);
	}
	
	
	
	public static <T> T explanationBlock(Object...objects) {
		return getThreadExplanationLogger().explanationBlock(objects);
	}
	

	
//	public static void start(Object... objects) {
//		getThreadExplanationLogger().start(objects);
//	}
	
	public static void explain(Object... objects) {
		getThreadExplanationLogger().explain(objects);
	}
	
//	public static void end(Object... objects) {
//		getThreadExplanationLogger().end(objects);
//	}
	
	
	 
	public static void explainTree(Tree<?> tree) {
		getThreadExplanationLogger().explainTree(tree);
	}
	
	public static void explainList(Object header, Collection<?> collection) {
		getThreadExplanationLogger().explainList(header, collection);
	}

	
	
	public static void start(Number importance, Object... objects) {
		getThreadExplanationLogger().start(importance, objects);
	}
	
	public static void explain(Number importance, Object... objects) {
		getThreadExplanationLogger().explain(importance, objects);
	}
	
	
	
	public static Collection<? extends ExplanationHandler> getHandlers() {
		return getThreadExplanationLogger().getHandlers();
	}

	public static void addHandler(ExplanationHandler handler) {
		getThreadExplanationLogger().addHandler(handler);
	}
	
	public static boolean removeHandler(ExplanationHandler handler) {
		return getThreadExplanationLogger().removeHandler(handler);
	}

}
