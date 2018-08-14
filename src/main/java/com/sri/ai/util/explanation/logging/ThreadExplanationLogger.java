package com.sri.ai.util.explanation.logging;

import java.util.Collection;
import java.util.logging.Level;

import com.sri.ai.util.base.NullaryFunction;

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
	
	static Level getLevel() {
		return getThreadExplanationLogger().getLevel();
	}
	
	static void setLevel(Level level) {
		getThreadExplanationLogger().setLevel(level);
	}
	
	
	
	static ExplanationFilter getFilter() {
		return getThreadExplanationLogger().getFilter();
	}
	
	static void setFilter(ExplanationFilter filter) {
		getThreadExplanationLogger().setFilter(filter);
	}
	
	
	
	static void explain(ExplanationRecord record) {
		getThreadExplanationLogger().explain(record);
	}
	
	
	
	static void start(NullaryFunction<String> explanation) {
		getThreadExplanationLogger().start(explanation);
	}
	
	static void start(Object... objects) {
		getThreadExplanationLogger().start(objects);
	}
	
	
	
	static void explain(NullaryFunction<String> explanation) {
		getThreadExplanationLogger().explain(explanation);
	}
	
	static void explain(Object... objects) {
		getThreadExplanationLogger().explain(objects);
	}
	
	
	
	static void end(NullaryFunction<String> explanation) {
		getThreadExplanationLogger().end(explanation);
	}
	
	static void end(Object... objects) {
		getThreadExplanationLogger().end(objects);
	}
	
	
	
	
	static void start(Level level, NullaryFunction<String> explanation) {
		getThreadExplanationLogger().start(level, explanation);
	}
	
	static void start(Level level, Object... objects) {
		getThreadExplanationLogger().start(level, objects);
	}
	
	
	static void explain(Level level, NullaryFunction<String> explanation) {
		getThreadExplanationLogger().explain(level, explanation);
	}
	
	static void explain(Level level, Object... objects) {
		getThreadExplanationLogger().explain(level, objects);
	}
	
	
	static void end(Level level, NullaryFunction<String> explanation) {
		getThreadExplanationLogger().end(level, explanation);
	}
	
	static void end(Level level, Object... objects) {
		getThreadExplanationLogger().end(level, objects);
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
