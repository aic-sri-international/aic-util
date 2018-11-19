package com.sri.ai.util.explanation.logging.example;

import static com.sri.ai.util.explanation.logging.api.ThreadExplanationLogger.end;
import static com.sri.ai.util.explanation.logging.api.ThreadExplanationLogger.explain;
import static com.sri.ai.util.explanation.logging.api.ThreadExplanationLogger.start;

public class Example {
	
	static void myDay(int day) {
		
		start("Starting a new day, day number ", day);
		
		for (int i = 0 ; i != 5; i++) {
			runErrand(i);
		}

		end("Calling it a day, day number ", day);
	}
	
	static void runErrand(int errand) {
		start("Running errand number ", errand);
		for (int i = 0; i != 10; i++) {
			explain("Doing task", i, " of errand ", errand);
		}
		end("Finished errand number ", errand);
	}

	public static void main(String[] args) {
		//ThreadExplanationLogger.getThreadExplanationLogger().addHandler(new FileHandler(filename));
		myDay(3);
		
	}

}
