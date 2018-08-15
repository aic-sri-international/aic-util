package com.sri.ai.util.explanation.logging.api;

import java.util.Collection;
import java.util.logging.Level;

import com.sri.ai.util.base.NullaryFunction;

/**
 * An {@link ExplanationLogger} receives indented information about an algorithm's execution and passes it on to {@link ExplanationHandler}s
 * with specific functionalities.
 * <p>
 * This is similar to regular logging (and handlers may resort to regular logging as one way to handle explanations),
 * but adds the abilities to:
 * <ul>
 * <li> indicate nesting (an unbounded non-negative integer), so that viewers can exhibit explanations in a top-down level and avoid overwhelming the user;
 * nesting is controlled by {@link #start(NullaryFunction)} and {@link #end(NullaryFunction)} methods;
 * <li> receive Java objects without necessarily converting them to text, so that viewers can manipulate those objects in any way they want.
 * </ul>
 * <p>
 * Explanations adopt the same notion of {@link Level} as used by Java's SDK logging.
 * Explanations are by default at the {@link Level#INFO} level.
 * 
 * @author braz
 *
 */
public interface ExplanationLogger {
	
	Level getLevel();
	void setLevel(Level level);
	
	
	
	ExplanationFilter getFilter();
	void setFilter(ExplanationFilter filter);
	
	
	
	void explain(ExplanationRecord record);
	
	
	
	void start(Level level, Object... objects);
	
	void explain(Level level, Object... objects);
	
	void end(Level level, Object... objects);
	
	
	
	Collection<? extends ExplanationHandler> getHandlers();

	void addHandler(ExplanationHandler handler);
	
	boolean removeHandler(ExplanationHandler handler);

	
	
	default void start(Object... objects) {
		start(Level.INFO, objects);
	}
	
	default void explain(Object... objects) {
		explain(Level.INFO, objects);
	}
	
	default void end(Object... objects) {
		end(Level.INFO, objects);
	}
	
}
