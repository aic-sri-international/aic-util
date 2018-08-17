package com.sri.ai.util.explanation.logging.core;

import java.util.Collection;
import java.util.HashSet;

import com.sri.ai.util.base.Stack;
import com.sri.ai.util.explanation.logging.api.ExplanationFilter;
import com.sri.ai.util.explanation.logging.api.ExplanationHandler;
import com.sri.ai.util.explanation.logging.api.ExplanationLogger;
import com.sri.ai.util.explanation.logging.api.ExplanationRecord;

public class DefaultExplanationLogger implements ExplanationLogger {
	
	//TODO: ERROR CHECKING / HANDLING FOR TYPE CONVERSIONS
	
	private double importanceThreshold; 			// user defined threshold above which explanations are recorded
	
	private double importanceMultiplier;			// compounded importance weights at current nesting (calculated by multiplying "start" importance weights)
	private Stack<Double> importanceStack;			// stores raw importance weights from each start() invocation. Each end() invocation pops a value.
	private int numberOfNestedUnimportantBlocks;	// tracks the number of unimportant blocks the explanation is inside of
	private int nestingDepth;						// current level of nesting relative to"start"/"end" invocations deemed as important
	
	Collection<ExplanationHandler> handlers;		// collection of handlers to process recorded explanations
	ExplanationFilter filter;						// object that filters relevant records and passes them to explanationHandlers
	
	
	//TODO:  getTotalNexting(), etc.

	public DefaultExplanationLogger() {
		super();
		this.importanceThreshold = 1;
		this.importanceMultiplier = 1;
		this.importanceStack = new Stack<>();
		this.numberOfNestedUnimportantBlocks = 0;
		this.nestingDepth = 0;
		this.handlers = new HashSet<>();
		this.filter = null;
	}

	@Override
	public Number getImportanceThreshold() {
		return importanceThreshold;
	}

	@Override
	public void setImportanceThreshold(Number threshold) {
		this.importanceThreshold = threshold.doubleValue();
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
		
		if(!insideUnimportantBlock())
		{
			processStartRecordRequest(importance.doubleValue(), objects);
		}
	    else {
	    	increaseDepthInsideUnimportantBlock();	    	
	    }
	}

	private void processStartRecordRequest(double importance, Object[] objects) {
		double adjustedImportance = calculateCompoundedImportance(importance);
	    if (isImportantEnough(adjustedImportance)) {
	    	processRecordRequest(importance, adjustedImportance, objects);
			setImportanceMultiplier(adjustedImportance);
			pushImportance(importance);
		    increaseNestingDepthOfRecordings();
	    }
	    else {
	    	increaseDepthInsideUnimportantBlock();	    	
	    }
	}

	@Override
	public void explain(Number importance, Object... objects) {
		if (!insideUnimportantBlock()) {
			processExplainRecordRequest((double) importance, objects);
		}
	}


	private void processExplainRecordRequest(double importance, Object[] objects) {
		double adjustedImportance = calculateCompoundedImportance(importance);
	    if (isImportantEnough(adjustedImportance)) {
	    	processRecordRequest(importance, adjustedImportance, objects);
	    }		
	}

	@Override
	public void end(Object... objects) {
	    if (!insideUnimportantBlock()) {
	    	double importance = popImportance();
	    	double adjustedImportance = importanceMultiplier;
		    decreaseNestingDepthOfRecords();
	    	processRecordRequest(importance, adjustedImportance, objects);
			setImportanceMultiplier(adjustedImportance/importance);
	    }
	    else {
	    	decreaseDepthInsideUnimportantBlock();	    	
	    }
	}

	private void processRecordRequest(double importance, double adjustedImportance, Object[] objects) {
		ExplanationRecord record = makeRecord(importance, adjustedImportance, objects);
		if (filter != null && filter.test(record)) {
			sendRecordToHandlers(record);
		}
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
	
	private double calculateCompoundedImportance(double importance) {
		double compoundedImportance = importance * importanceMultiplier;
		return compoundedImportance;
	}
	
	private boolean insideUnimportantBlock() {
		return numberOfNestedUnimportantBlocks > 0;
	}

	private boolean isImportantEnough(double compoundedImportance) {
		return compoundedImportance >= importanceThreshold;
	}
	
	private ExplanationRecord makeRecord(double importance, double compoundedImportance, Object[] objects) {
		ExplanationRecord record = new DefaultExplanationRecord(importance, compoundedImportance, nestingDepth, objects);
		return record;
	}
	
	private void sendRecordToHandlers(ExplanationRecord record) {
		for (ExplanationHandler handler : handlers) {
			handler.handle(record);
		}
	}
	
	private void setImportanceMultiplier(double newImportanceMultiplier) {
		this.importanceMultiplier = newImportanceMultiplier;
	}
	
	private void pushImportance(double importanceWeight) {
		importanceStack.push(importanceWeight);
	}
	
	private double popImportance() {
		double importance = importanceStack.pop();
		return importance;
	}
	
	private void increaseNestingDepthOfRecordings() {
		++nestingDepth;
	}
	
	private void decreaseNestingDepthOfRecords() {
		--nestingDepth;		
	}
	
	private void increaseDepthInsideUnimportantBlock() {
		++numberOfNestedUnimportantBlocks;
	}
	
	private void decreaseDepthInsideUnimportantBlock() {
		--numberOfNestedUnimportantBlocks;
	}
}
