package com.sri.ai.util.explanation.logging.core;

import com.sri.ai.util.explanation.logging.api.ExplanationRecord;

public class DefaultExplanationRecord implements ExplanationRecord {
	
	final Number importance;				// raw marked importance (relative to block it record was extracted from)
	final Number adjustedImportance;		// overall importance (importance values having been multiplied when entering each successive explanation block)
	final int nestingDepth;					// depth of nesting record was extracted from within nested start/end explanation blocks
	final long timestamp;                   // time the record was created to the closest millisecond
	final Object[] objects;					// objects that were recorded from the explanation
	final long blockTime;                   // -1 if this is not from an block end record, otherwise the difference in time between this record's timestamp and its block start timestamp
	
	public DefaultExplanationRecord(Number importance, Number adjustedImportance, int nestingDepth, long timestamp, Object[] objects, long blockTime) {
		this.importance = importance;
		this.adjustedImportance = adjustedImportance;
		this.nestingDepth = nestingDepth;
		this.timestamp = timestamp;
		this.objects = objects;
		this.blockTime = blockTime;
	}

	@Override
	public Number getImportance() {
		return importance;
	}

	@Override
	public Number getAdjustedImportance() {
		return adjustedImportance;
	}

	@Override
	public int getNestingDepth() {
		return nestingDepth;
	}

	@Override
	public long getTimestamp() {
		return timestamp;
	}

	@Override
	public Object[] getObjects() {
		return objects;
	}

	@Override
	public long getBlockTime() {
		return blockTime;
	}

}
