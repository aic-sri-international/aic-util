package com.sri.ai.util.explanation.logging.core.handler;

import java.io.IOException;
import java.io.Writer;
import java.sql.Timestamp;

import com.sri.ai.util.base.NullaryFunction;
import com.sri.ai.util.explanation.logging.api.ExplanationConfiguration;
import com.sri.ai.util.explanation.logging.api.ExplanationHandler;
import com.sri.ai.util.explanation.logging.api.ExplanationRecord;

public abstract class AbstractWriterExplanationHandler implements ExplanationHandler {
	
	private NullaryFunction<Writer> writerMaker;
	private Writer writer;
	private Nesting nesting;
	private boolean includeTimestamp;
	private boolean includeBlockTime;
	private boolean includeRecordId;
	
	public AbstractWriterExplanationHandler(NullaryFunction<Writer> writerMaker) {
		this.writerMaker = writerMaker;
		this.writer = null;
		this.nesting = new Nesting(ExplanationConfiguration.DEFAULT_NESTING_BLOCK, ExplanationConfiguration.DEFAULT_NESTING_POSTFIX);
		this.includeTimestamp = ExplanationConfiguration.DEFAULT_INCLUDE_TIMESTAMP;
		this.includeBlockTime = ExplanationConfiguration.DEFAULT_INCLUDE_BLOCK_TIME;
		this.includeRecordId = ExplanationConfiguration.DEFAULT_INCLUDE_RECORD_ID;
	}
	
	public AbstractWriterExplanationHandler(Writer writer) {
		this(() -> writer);
	}

	public boolean writerHasBeenCreated() {
		return writer != null;
	}
	
	private void makeSureWriterIsMade() {
		if ( ! writerHasBeenCreated()) {
			writer = writerMaker.apply();
		}
	}
	
	@Override
	public void handle(ExplanationRecord record) {
		makeSureWriterIsMade();
		StringBuilder builder = new StringBuilder();
		addNesting(builder, record);
		addRecordIdIfNeeded(builder, record);
		addObjects(builder, record);
		addTimestampIfNeeded(builder, record);
		addBlockTimeIfNeeded(builder, record);
		write(builder, record);
	}

	private void addNesting(StringBuilder builder, ExplanationRecord record) {
		builder.append(nesting.getNestingString(record.getNestingDepth()));
	}

	private void addTimestampIfNeeded(StringBuilder builder, ExplanationRecord record) {
		if (getIncludeTimestamp() && record.getTimestamp() != -1) {
			Timestamp timestamp = new Timestamp(record.getTimestamp());
			builder.append(" (" + timestamp + ")");
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
			builder.append(" (" + record.getBlockTime() + " ms)");
		}
	}

	private void addRecordIdIfNeeded(StringBuilder builder, ExplanationRecord record) {
		if (getIncludeRecordId() && record.getRecordId() != -1) {
			builder.append("Id: " + record.getRecordId() + " ");
		}
	}

	private void write(StringBuilder builder, ExplanationRecord record) throws Error {
		try {
			String string = builder.toString();
			if (!isEmptyEndOfBlock(string, record)) {
				writer.write(string + "\n");
				writer.flush();
			}
		} catch (IOException e) {
			throw new Error(e);
		}
	}

	private boolean isEmptyEndOfBlock(String string, ExplanationRecord record) {
		return 
				record.isEndOfBlock() 
				&& 
				string.equals(nesting.getNestingString(record.getNestingDepth()));
	}
	
	public Nesting getNesting() {
		return nesting;
	}
	
	public void setNesting(Nesting nesting) {
		this.nesting = nesting;
	}

	public String getNestingBlock() {
		return nesting.getNestingBlock();
	}

	public void setNestingStringBlock(String nestingString) {
		this.nesting = nesting.setNestingBlock(nestingString);
	}

	public String getNestingStringPostfix() {
		return nesting.getNestingPostfix();
	}

	public void setNestingPostfix(String nestingPostfixString) {
		this.nesting = nesting.setNestingPostfix(nestingPostfixString);
	}

	public void setWriterMaker(NullaryFunction<Writer> writerMaker) {
		this.writerMaker = writerMaker;
	}

	public void setWriter(Writer writer) {
		this.writer = writer;
	}

	public static String getDefaultNestingString() {
		return ExplanationConfiguration.DEFAULT_NESTING_BLOCK;
	}

	public NullaryFunction<Writer> getWriterMaker() {
		return writerMaker;
	}

	public Writer getWriter() {
		makeSureWriterIsMade();
		return writer;
	}

	@Override
	public boolean getIncludeTimestamp() {
		return includeTimestamp;
	}

	@Override
	public void setIncludeTimestamp(boolean includeTimestamp) {
		this.includeTimestamp = includeTimestamp;
	}

	@Override
	public boolean getIncludeBlockTime() {
		return includeBlockTime;
	}

	@Override
	public void setIncludeBlockTime(boolean includeBlockTime) {
		this.includeBlockTime = includeBlockTime;
	}

	@Override
	public boolean getIncludeRecordId() {
		return includeRecordId;
	}

	@Override
	public void setIncludeRecordId(boolean includeRecordId) {
		this.includeRecordId = includeRecordId;
	}
}
