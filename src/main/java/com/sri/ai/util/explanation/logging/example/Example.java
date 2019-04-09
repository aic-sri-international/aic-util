package com.sri.ai.util.explanation.logging.example;

import static com.sri.ai.util.explanation.logging.api.ThreadExplanationLogger.code;
import static com.sri.ai.util.explanation.logging.api.ThreadExplanationLogger.explain;
import static com.sri.ai.util.explanation.logging.api.ThreadExplanationLogger.explanationBlock;

public class Example {
	
	static void myDay(int day) {

		explanationBlock("Starting a new day, day number ", day, code(() -> {

			for (int i = 0 ; i != 5; i++) {
				runErrand(i);
			}

		}), "Calling it a day, day number ", day);
	}
	
	static void runErrand(int errand) {
		explanationBlock("Running errand number ", errand, code(() -> {
			for (int i = 0; i != 10; i++) {
				explain("Doing task", i, " of errand ", errand);
			}
		}), "Finished errand number ", errand);
	}

	public static void main(String[] args) {
		//ThreadExplanationLogger.getThreadExplanationLogger().addHandler(new FileHandler(filename));
		myDay(3);
		
	}

}
