package com.sri.ai.util.explanation.logging;

@FunctionalInterface
public interface ExplanationFilter {
	
	boolean include(ExplanationRecord record);

}
