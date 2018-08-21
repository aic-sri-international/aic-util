package com.sri.ai.util.explanation.logging.core;

import static com.sri.ai.util.Util.myAssert;

import java.util.Collection;
import java.util.HashSet;
import java.util.Stack;

import com.sri.ai.util.explanation.logging.api.ExplanationConfiguration;
import com.sri.ai.util.explanation.logging.api.ExplanationFilter;
import com.sri.ai.util.explanation.logging.api.ExplanationHandler;
import com.sri.ai.util.explanation.logging.api.ExplanationLogger;
import com.sri.ai.util.explanation.logging.api.ExplanationRecord;

public class DefaultExplanationLogger implements ExplanationLogger {

	private boolean isActive;                       	// whether the logger is active or not
	private Number importanceThreshold; 				// user defined threshold above which explanations are recorded
	private Number importanceMultiplier;				// compounded importance weights at current nesting (calculated by multiplying "start" importance weights)
	private Stack<ExplanationRecord> startRecords;		// stores record from each relevant start() invocation. Each end() invocation pops a value.
	private int numberOfNestedIgnoredBlocks;	    	// tracks the number of currently nested ignored blocks
	private int nestingDepth;							// current level of nesting relative to"start"/"end" invocations deemed as important
	
	Collection<ExplanationHandler> handlers;			// collection of handlers to process recorded explanations
	ExplanationFilter filter;							// object that filters relevant records and passes them to explanationHandlers
	
	
	public DefaultExplanationLogger() {
		super();
		this.isActive = ExplanationConfiguration.WHETHER_EXPLANATION_LOGGERS_ARE_ACTIVE_BY_DEFAULT;
		this.importanceThreshold = 1.0;
		this.importanceMultiplier = 1.0;
		this.startRecords = new Stack<>();
		this.numberOfNestedIgnoredBlocks = 0;
		this.nestingDepth = 0;
		this.handlers = new HashSet<>();
		this.filter = null;
	}

	
	
	@Override
	public boolean isActive() {
		return isActive;
	}
	
	public void setIsActive(boolean newIsActive) {
		myAssert(startRecords.isEmpty(), () -> "Cannot change whether logger is active or not while inside explanation blocks");
		this.isActive = newIsActive;
	}

	
	
	@Override
	public Number getImportanceThreshold() {
		return importanceThreshold;
	}

	@Override
	public void setImportanceThreshold(Number threshold) {
		this.importanceThreshold = threshold;
	}

	@Override
	public ExplanationFilter getFilter() {
		return filter;
	}

	@Override
	public void setFilter(ExplanationFilter filter) {
		this.filter = filter;
	}

	@Override
	public void start(Number importance, Object... objects) {

		if (!isActive()) return;
		
		Number adjustedImportance = calculateAdjustedImportance(importance);
		ExplanationRecord record = makeRecord(importance, adjustedImportance, objects);
		if (blockMustBeIncluded(record)) {
			enterBlock(record);
		}
		else {
			increaseNumberOfNestedIgnoredBlocks();	    	
		}
	}

	private void enterBlock(ExplanationRecord record) {
		handleRecord(record);
		setParametersForNewBlock(record);
	}

	private void setParametersForNewBlock(ExplanationRecord record) {
		setImportanceMultiplier(record.getAdjustedImportance());
		pushNewStartRecord(record);
		increaseNestingDepth();
	}

	private boolean blockMustBeIncluded(ExplanationRecord record) {
		
		boolean result =
				!insideIgnoredBlock()
				&&
				isImportantEnough(record.getAdjustedImportance())
				&&
				testRecord(record);
		
		return result;
	}

	@Override
	public void explain(Number importance, Object... objects) {

		if (!isActive()) return;
		
		if (!insideIgnoredBlock()) {
			processExplainRecordRequest(importance, objects);
		}
	}


	private void processExplainRecordRequest(Number importance, Object[] objects) {
		Number adjustedImportance = calculateAdjustedImportance(importance);
	    if (isImportantEnough(adjustedImportance)) {
	    	handleRecordIfNeeded(importance, adjustedImportance, objects);
	    }		
	}

	@Override
	public void end(Object... objects) {

		if (!isActive()) return;
		
	    if (!insideIgnoredBlock()) {
	    	exitBlock(objects);
	    }
	    else {
	    	decreaseNumberOfNestedIgnoredBlocks();	    	
	    }
	}

	private void exitBlock(Object... objects) {
		myAssert(!startRecords.isEmpty(), () -> "Attempt to end explanation block but there are no current explanation blocks opened.");
		Number importance = getLastStartImportance();
		Number adjustedImportance = importanceMultiplier;
		restoreParametersForPreviousBlock(importance, adjustedImportance);
		handleRecordIfNeeded(importance, adjustedImportance, objects);
	}

	private Number getLastStartImportance() {
		ExplanationRecord blockStartRecord = startRecords.peek();
		Number lastStartImportance = blockStartRecord.getImportance();
		return lastStartImportance;
	}



	private void restoreParametersForPreviousBlock(Number importance, Number adjustedImportance) {
		decreaseNestingDepth();
		popStartRecord();
		setImportanceMultiplier(adjustedImportance.doubleValue()/importance.doubleValue());
	}

	
	private void handleRecordIfNeeded(Number importance, Number adjustedImportance, Object[] objects) {
		ExplanationRecord record = makeRecord(importance, adjustedImportance, objects);
		if (testRecord(record)) {
			handleRecord(record);
		}
	}

	private boolean testRecord(ExplanationRecord record) {
		boolean result = 
				filter == null 
				|| 
				filter.test(record);
		return result;
	}

	@Override
	public Collection<? extends ExplanationHandler> getHandlers() {
		return handlers;
	}

	@Override
	public void addHandler(ExplanationHandler handler) {
		handlers.add(handler);
	}

	@Override
	public boolean removeHandler(ExplanationHandler handler) {
		// Note, this looks for the EXACT SAME object
		return handlers.remove(handler);
	}
	
	private Number calculateAdjustedImportance(Number importance) {
		Number adjustedImportance = importance.doubleValue() * importanceMultiplier.doubleValue();
		return adjustedImportance;
	}
	
	private boolean insideIgnoredBlock() {
		return numberOfNestedIgnoredBlocks > 0;
	}

	private boolean isImportantEnough(Number adjustedImportance) {
		return adjustedImportance.doubleValue() >= importanceThreshold.doubleValue();
	}
	
	private ExplanationRecord makeRecord(Number importance, Number adjustedImportance, Object[] objects) {
		long timestamp = System.currentTimeMillis();
		ExplanationRecord record = new DefaultExplanationRecord(importance, adjustedImportance, nestingDepth, timestamp, objects, -1);
		return record;
	}
	
	private void handleRecord(ExplanationRecord record) {
		for (ExplanationHandler handler : handlers) {
			handler.handle(record);
		}
	}
	
	private void setImportanceMultiplier(Number newImportanceMultiplier) {
		this.importanceMultiplier = newImportanceMultiplier;
	}
	
	private void pushNewStartRecord(ExplanationRecord record) {
		startRecords.push(record);
	}

	private ExplanationRecord popStartRecord() {
		myAssert(!startRecords.isEmpty(), () -> "Trying to pop an explanation level but we are at the top level already.");
		ExplanationRecord lastStartRecord = startRecords.pop();
		return lastStartRecord;
	}
	
	private void increaseNestingDepth() {
		++nestingDepth;
	}
	
	private void decreaseNestingDepth() {
		--nestingDepth;		
	}
	
	private void increaseNumberOfNestedIgnoredBlocks() {
		++numberOfNestedIgnoredBlocks;
	}
	
	private void decreaseNumberOfNestedIgnoredBlocks() {
		--numberOfNestedIgnoredBlocks;
	}
}
