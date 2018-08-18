package com.sri.ai.util.explanation.logging.api;

import java.util.Collection;

/**
 * A class with static methods for accessing and using a thread-wide {@link ExplanationLogger}.
 * 
 * @author braz
 *
 */
public interface ThreadExplanationLogger {
	
	static ExplanationLogger getThreadExplanationLogger() {
		return null;
	}
	
	static Number getImportanceThreshold() {
		return getThreadExplanationLogger().getImportanceThreshold();
	}
	
	static void setImportanceThreshold(Double importanceThreshold) {
		getThreadExplanationLogger().setImportanceThreshold(importanceThreshold);
	}
	
	
	
	static ExplanationFilter getFilter() {
		return getThreadExplanationLogger().getFilter();
	}
	
	static void setFilter(ExplanationFilter filter) {
		getThreadExplanationLogger().setFilter(filter);
	}
	
	
	
	static <T> T explanationBlock(Object...objects) {
		return getThreadExplanationLogger().explanationBlock(objects);
	}
	

	
	static void start(Object... objects) {
		getThreadExplanationLogger().explain(objects);
	}
	
	static void explain(Object... objects) {
		getThreadExplanationLogger().explain(objects);
	}
	
	static void end(Object... objects) {
		getThreadExplanationLogger().end(objects);
	}
	
	
	 
	static void start(Number importance, Object... objects) {
		getThreadExplanationLogger().start(importance, objects);
	}
	
	static void explain(Number importance, Object... objects) {
		getThreadExplanationLogger().explain(importance, objects);
	}
	
	
	
	static Collection<? extends ExplanationHandler> getHandlers() {
		return getThreadExplanationLogger().getHandlers();
	}

	static void addHandler(ExplanationHandler handler) {
		getThreadExplanationLogger().addHandler(handler);
	}
	
	static boolean removeHandler(ExplanationHandler handler) {
		return getThreadExplanationLogger().removeHandler(handler);
	}

}
