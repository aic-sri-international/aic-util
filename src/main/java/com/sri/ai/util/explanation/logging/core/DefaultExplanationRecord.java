package com.sri.ai.util.explanation.logging.core;

import static com.sri.ai.util.Util.join;

import com.sri.ai.util.explanation.logging.api.ExplanationRecord;

public class DefaultExplanationRecord implements ExplanationRecord {
	
	final static long UNDEFINEDBLOCKTIME = -1;
	
	final Number importance;				// raw marked importance (relative to block it record was extracted from)
	final Number adjustedImportance;		// overall importance (importance values having been multiplied when entering each successive explanation block)
	final int nestingDepth;					// depth of nesting record was extracted from within nested start/end explanation blocks
	final long timestamp;                   // time the record was created to the closest millisecond
	final Object[] objects;					// objects that were recorded from the explanation
	final long blockTime;                   // -1 if this is not from an block end record, otherwise the difference in time between this record's timestamp and its block start timestamp
	final long recordId;                    // a global record id based on order of construction
	final boolean isEndOfBlock;             // whether this explanation is the ending of a block

	private String originalObjectsString;
	
	public DefaultExplanationRecord(Number importance, Number adjustedImportance, int nestingDepth, long timestamp, Object[] objects, long blockTime, boolean isEndOfBlock) {
		this.importance = importance;
		this.adjustedImportance = adjustedImportance;
		this.nestingDepth = nestingDepth;
		this.timestamp = timestamp;
		this.objects = objects;
		this.originalObjectsString = join("", objects); // DEBUGGING
		this.blockTime = blockTime;
		this.recordId = ExplanationRecord.counter.value++;
		this.isEndOfBlock = isEndOfBlock;
	}
	
	public DefaultExplanationRecord(Number importance, Number adjustedImportance, int nestingDepth, long timestamp, Object[] objects, boolean isEndOfBlock) {
		this(importance, adjustedImportance, nestingDepth, timestamp, objects, UNDEFINEDBLOCKTIME, isEndOfBlock);
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
	public String getOriginalObjectsString() {
		return originalObjectsString;
	}

	@Override
	public long getBlockTime() {
		return blockTime;
	}

	@Override
	public long getRecordId() {
		return recordId;
	}

	@Override
	public boolean isEndOfBlock() {
		return isEndOfBlock;
	}
	
	@Override
	public String toString() {
		return join("", getObjects());
	}

}
