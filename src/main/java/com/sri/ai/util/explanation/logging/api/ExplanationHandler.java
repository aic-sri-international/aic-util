package com.sri.ai.util.explanation.logging.api;

@FunctionalInterface
public interface ExplanationHandler {
	
	void handle(ExplanationRecord record);

}
