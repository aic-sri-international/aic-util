package com.sri.ai.util.explanation.logging.core.handler;

import java.io.IOException;
import java.io.Writer;
import java.sql.Timestamp;

import com.sri.ai.util.base.NullaryFunction;
import com.sri.ai.util.explanation.logging.api.ExplanationHandler;
import com.sri.ai.util.explanation.logging.api.ExplanationRecord;

public abstract class AbstractWriterExplanationHandler implements ExplanationHandler {
	
	private static final String DEFAULT_NESTING_STRING = "*";

	private static final boolean DEFAULT_INCLUDE_TIMESTAMP = false;

	private static final boolean DEFAULT_INCLUDE_BLOCK_TIME = false;
	
	private Writer writer;
	private String nestingString;
	
	private boolean includeTimestamp;
	private boolean includeBlockTime;
	
	public AbstractWriterExplanationHandler(Writer explanationStream) {
		this.writer = explanationStream;
		this.nestingString = DEFAULT_NESTING_STRING;
		this.includeTimestamp = DEFAULT_INCLUDE_TIMESTAMP;
		this.includeBlockTime = DEFAULT_INCLUDE_BLOCK_TIME;
	}
	
	@Override
	public void handle(ExplanationRecord record) {
		StringBuilder builder = new StringBuilder();
		addNesting(builder, record);
		addTimestampIfNeeded(builder, record);
		addObjects(builder, record);
		addBlockTimeIfNeeded(builder, record);
		write(builder);
	}

	private void addNesting(StringBuilder builder, ExplanationRecord record) {
		for (int i = 0; i != record.getNestingDepth() + 1; i++) {
			builder.append(nestingString);
		}
		builder.append(" ");
	}

	private void addTimestampIfNeeded(StringBuilder builder, ExplanationRecord record) {
		if (getIncludeTimestamp() && record.getTimestamp() != -1) {
			Timestamp timestamp = new Timestamp(record.getTimestamp());
			builder.append(" " + timestamp);
		}
	}

	private void addObjects(StringBuilder builder, ExplanationRecord record) {
		for (Object object : record.getObjects()) {
			addObject(builder, object);
		}
	}

	private void addObject(StringBuilder builder, Object object) {
		if (object instanceof NullaryFunction) {
			NullaryFunction function = (NullaryFunction) object;
			Object functionResult = function.apply();
			addObject(builder, functionResult);
		}
		else {
			builder.append(object);
		}
	}

	private void addBlockTimeIfNeeded(StringBuilder builder, ExplanationRecord record) {
		if (getIncludeBlockTime() && record.getBlockTime() != -1) {
			builder.append(" (total time " + record.getBlockTime() + " ms)");
		}
	}

	private void write(StringBuilder builder) throws Error {
		try {
			writer.write(builder.toString() + "\n");
		} catch (IOException e) {
			throw new Error(e);
		}
	}

	public String getNestingString(String nestingString) {
		return nestingString;
	}

	public void setNestingString(String nestingString) {
		this.nestingString = nestingString;
	}

	public Writer getWriter() {
		return writer;
	}

	public boolean getIncludeTimestamp() {
		return includeTimestamp;
	}

	public void setIncludeTimestamp(boolean includeTimestamp) {
		this.includeTimestamp = includeTimestamp;
	}

	public boolean getIncludeBlockTime() {
		return includeBlockTime;
	}

	public void setIncludeBlockTime(boolean includeBlockTime) {
		this.includeBlockTime = includeBlockTime;
	}

}
