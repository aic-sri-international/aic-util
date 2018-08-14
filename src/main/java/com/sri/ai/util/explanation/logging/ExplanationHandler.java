package com.sri.ai.util.explanation.logging;

@FunctionalInterface
public interface ExplanationHandler {
	
	void handle(ExplanationRecord record);

}
