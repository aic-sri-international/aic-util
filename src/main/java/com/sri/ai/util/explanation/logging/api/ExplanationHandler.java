package com.sri.ai.util.explanation.logging.api;

public interface ExplanationHandler {
	
	void handle(ExplanationRecord record);

	void setIncludeRecordId(boolean includeRecordId);

	boolean getIncludeRecordId();

	void setIncludeBlockTime(boolean includeBlockTime);

	boolean getIncludeBlockTime();

	void setIncludeTimestamp(boolean includeTimestamp);

	boolean getIncludeTimestamp();

}
