package com.sri.ai.util.explanation.logging.api;

import static com.sri.ai.util.Configuration.getString;

import com.sri.ai.util.Configuration;

public class ExplanationConfiguration {

	/**
	 * Indicates whether explanation loggers are active or not by default.
	 */
	public static boolean WHETHER_EXPLANATION_LOGGERS_ARE_ACTIVE_BY_DEFAULT = false;
	
	public static final String DEFAULT_NESTING_BLOCK = getString("explanation.nesting.block", "*");
	
	public static final String DEFAULT_NESTING_POSTFIX = getString("explanation.nesting.postfix", " ");
	
	public static final boolean DEFAULT_INCLUDE_TIMESTAMP = Configuration.getBoolean("explanation.include.timestamp", false);
	
	public static final boolean DEFAULT_INCLUDE_BLOCK_TIME = Configuration.getBoolean("explanation.include.block.time", true);

}
