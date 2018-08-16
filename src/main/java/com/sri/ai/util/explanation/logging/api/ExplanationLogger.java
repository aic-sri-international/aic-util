package com.sri.ai.util.explanation.logging.api;

import java.util.Collection;

/**
 * An {@link ExplanationLogger} receives indented information about an algorithm's execution and passes it on to {@link ExplanationHandler}s
 * with specific functionalities.
 * <p>
 * Each invocation of {@link #start(Object...)}, {@link #explain(Object...)} and {@link #end(Object...)} create an {@link ExplanationRecord}
 * passed on to {@link #explain(ExplanationRecord)}.
 * <p>
 * {@link ExplanationLogger} is similar to regular logging (and handlers may resort to regular logging as one way to handle explanations),
 * but adds the abilities to:
 * <ul>
 * <li> indicate nesting (an unbounded non-negative integer), so that viewers can exhibit explanations in a top-down level and avoid overwhelming the user;
 * nesting is controlled by {@link #start(Object...)} and {@link #end(Object...)} methods;
 * <li> receive Java objects without necessarily converting them to text, so that viewers can manipulate those objects in any way they want.
 * <li> exhibit or omit amounts of detail according to the user specifications. This is done as follows:
 * explanations records have an importance weight (with default 1). 
 * An {@link ExplanationLogger} has an importance weight threshold (also default 1)
 * and only records with importance equal or greater than this threshold are used.
 * The importance of entire nested blocks can be affected by invoking {@link #start(Number, Object...)} ({@link #start(Object...)} uses default 1),
 * which provides an importance weight that multiplies all importance weights within its block.
 * Therefore, multiple nested blocks compose the importance weights provided by their {@link #start(Number, Object...)} methods by multiplication,
 * for greater or smaller importance weights.
 * If the explanation record coming from a {@link #start(Object...)} invocation is not used, then neither are the explanation records enclosed in its corresponding block,
 * regardless of their importance weights.
 * </ul>
 * <p>
 * 
 * @author braz
 *
 */
public interface ExplanationLogger {
	
	Number getImportanceThreshold();
	void setImportanceThreshold(Number importanceThreshold);
	
	
	
	ExplanationFilter getFilter();
	void setFilter(ExplanationFilter filter);
	
	
	
	void explain(ExplanationRecord record);
	
	
	
	void start(Number importance, Object... objects);
	
	void explain(Number importance, Object... objects);
	
	void end(Object... objects);
	
	
	
	Collection<? extends ExplanationHandler> getHandlers();

	void addHandler(ExplanationHandler handler);
	
	boolean removeHandler(ExplanationHandler handler);

	
	
	default void start(Object... objects) {
		start(1, objects);
	}
	
	default void explain(Object... objects) {
		explain(1, objects);
	}
	
}
