package com.sri.ai.util;

import com.sri.ai.util.base.NullaryFunction;

public class Timer {

	public static <T> long time(NullaryFunction<T> procedure) {
		long initialTime = System.currentTimeMillis();
		procedure.apply();
		long finalTime = System.currentTimeMillis();
		long result = finalTime - initialTime;
		return result;
	}
}
