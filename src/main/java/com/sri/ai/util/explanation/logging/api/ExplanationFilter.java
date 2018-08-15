package com.sri.ai.util.explanation.logging.api;

@FunctionalInterface
public interface ExplanationFilter {
	
	boolean include(ExplanationRecord record);

}
