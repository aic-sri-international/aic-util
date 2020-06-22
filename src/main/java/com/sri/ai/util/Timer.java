package com.sri.ai.util;

import static com.sri.ai.util.Util.round;

import com.sri.ai.util.base.NullaryFunction;
import com.sri.ai.util.base.Pair;

public class Timer {

	public static <T> long time(NullaryFunction<T> procedure) {
		long initialTime = System.currentTimeMillis();
		procedure.apply();
		long finalTime = System.currentTimeMillis();
		long result = finalTime - initialTime;
		return result;
	}
	
	public static <T> Pair<T,Long> getResultAndTime(NullaryFunction<T> procedure) {
		long initialTime = System.currentTimeMillis();
		T result = procedure.apply();
		long finalTime = System.currentTimeMillis();
		long time = finalTime - initialTime;
		return new Pair<T,Long>(result,time);
	}
	
	public static <T> String timeStringInSeconds(Pair<T,Long> resultAndTime, int decimalPlaces) {
		return round(resultAndTime.second/1000., decimalPlaces) + " seconds";
	}
}
