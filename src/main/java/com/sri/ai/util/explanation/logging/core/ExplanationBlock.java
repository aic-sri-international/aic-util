package com.sri.ai.util.explanation.logging.core;

import static com.sri.ai.util.Util.getIndexOfFirstSatisfyingPredicateOrMinusOne;
import static com.sri.ai.util.Util.myAssert;
import static java.util.Arrays.copyOfRange;

import com.sri.ai.util.base.NullaryFunction;
import com.sri.ai.util.explanation.logging.api.ExplanationLogger;

public class ExplanationBlock<T> {

	private ExplanationLogger logger;
	private Number importance;
	private Object[] startArguments;
	private Code<T> code;
	private Object[] endArguments;

	public static final Object RESULT = new Object();
	
	private interface Code<T> extends NullaryFunction<T> {}
	
	/**
	 * A method to be used to indicate the {@link NullaryFunction} corresponding to the block's code;
	 * it is necessary to differentiate it from other {@link NullaryFunction} that are just lazy arguments to the start or end explanations.
	 * @param code
	 * @return
	 */
	public static <T> Code<T> CODE(NullaryFunction<T> code) {
		return () -> code.apply();
	}

	public ExplanationBlock(ExplanationLogger logger, Number importance, Object...objects) {
		
		this.logger = logger;
		this.importance = importance;
		setFieldsDependingOnCodePosition(objects);
	}

	@SuppressWarnings("unchecked")
	private void setFieldsDependingOnCodePosition(Object... objects) {
		int indexOfCode = getIndexOfCodeArgument(objects);
		startArguments = copyOfRange(objects, 0, indexOfCode);
		code = (Code<T>) objects[indexOfCode];
		endArguments = copyOfRange(objects, indexOfCode + 1, objects.length);
	}

	private int getIndexOfCodeArgument(Object... objects) {
		
		myAssert(objects.length != 0, () -> ExplanationBlock.class + " instantiated without any arguments besides logger and maybe importance.");

		int indexOfCode = getIndexOfFirstSatisfyingPredicateOrMinusOne(objects, o -> o instanceof Code);
		
		myAssert(indexOfCode != -1, () -> ExplanationBlock.class + " should have received an argument formed by 'CODE(NullaryFunction)' but received arguments " + objects);
		
		return indexOfCode;
	}

	public T execute() {
		
		T result;
		
		logger.start(importance, startArguments);
		
		try {
			result = code.apply();
			Object[] endArgumentsWithActualResult = replaceRESULTByResult(endArguments, result);
			logger.end(endArgumentsWithActualResult);
		}
		catch (Throwable throwable) {
			logger.end("Throwable thrown: ", throwable);
			throw throwable;
		}
		
		return result;
	}

	private Object[] replaceRESULTByResult(Object[] objects, T actualResult) {
		Object[] objectsWithActualResult = new Object[objects.length];
		for (int i = 0; i != objects.length; i++) {
			objectsWithActualResult[i] = objects[i] == RESULT? actualResult : objects[i];
		}
		return objectsWithActualResult;
	}
}
