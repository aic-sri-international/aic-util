package com.sri.ai.util.explanation.logging.api;

import java.util.Collection;

import com.sri.ai.util.base.NullaryFunction;
import com.sri.ai.util.base.NullaryProcedure;
import com.sri.ai.util.explanation.logging.core.ExplanationBlock;
import com.sri.ai.util.explanation.logging.core.ExplanationBlock.Code;

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

	/** Convenience method for creating lazy arguments to explanations without having to write an ugly cast to {@link NullaryFunction}. */
	public static NullaryFunction<Object> lazy(NullaryFunction<Object> code) {
		return code;
	}
	
	
	
	boolean isActive();
	
	void setIsActive(boolean newIsActive);
	
	
	
	Number getImportanceThreshold();
	void setImportanceThreshold(Number importanceThreshold);
	
	
	
	ExplanationFilter getFilter();
	void setFilter(ExplanationFilter filter);
	
	
	
	/**
	 * A method for declaring an explanation start...end block that recovers gracefully from exceptions,
	 * that is, automatically generates an end explanation indicating an exception before leaving the method,
	 * as a way to keep nesting levels and importance weights consistent.
	 * <p>
	 * The method takes the arguments for the start and end explanations, but also a {@link NullaryFunction} object that
	 * represents the block code (that is, the code in between start and end).
	 * <p>
	 * It assumes all arguments before the block code to be the arguments of the start explanation,
	 * and all arguments after the block code to be the arguments of the end explanation
	 * (but see later for a more general form that allows {@link NullaryFunction} arguments for the
	 * start and end explanations as well).
	 * <p>
	 * It generates the start explanation and then executes the block code.
	 * If a {@link Throwable} object is thrown by the block code, then an end explanation indicating that,
	 * and containing the throwable, is automatically generated, and the throwable is re-thrown.
	 * Otherwise, it executes the end explanation specified by the user.
	 * 
	 * @param objects
	 */
	default <T> T explanationBlock(Number importance, Object...objects) {
		ExplanationBlock<T> block = new ExplanationBlock<T>(this, importance, objects);
		return block.execute();
	}

	/**
	 * Same as {@link explanationBlock(Number, Object...)} with default importance 1.0.
	 * 
	 * @param objects
	 */
	default <T> T explanationBlock(Object...objects) {
		ExplanationBlock<T> block = new ExplanationBlock<T>(this, 1.0, objects);
		return block.execute();
	}

	
	
	void start(Number importance, Object... objects);
	
	void explain(Number importance, Object... objects);
	
	void end(Object... objects);
	
	
	
	Collection<? extends ExplanationHandler> getHandlers();

	void addHandler(ExplanationHandler handler);
	
	boolean removeHandler(ExplanationHandler handler);

	
	
	default void start(Object... objects) {
		start(1.0, objects);
	}
	
	default void explain(Object... objects) {
		explain(1.0, objects);
	}
	
}
