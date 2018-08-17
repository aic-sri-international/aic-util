package com.sri.ai.util.explanation.logging.core;

import java.util.Collection;

import com.sri.ai.util.base.Stack;

import javax.print.event.PrintJobAttributeListener;

import com.sri.ai.util.explanation.logging.api.ExplanationFilter;
import com.sri.ai.util.explanation.logging.api.ExplanationHandler;
import com.sri.ai.util.explanation.logging.api.ExplanationLogger;
import com.sri.ai.util.explanation.logging.api.ExplanationRecord;

public class DefaultExplanationLogger implements ExplanationLogger {
	
	//TODO: ERROR CHECKING / HANDLING FOR TYPE CONVERSIONS
	
	private double importanceThreshold; 					// user defined threshold above which explanations are recorded
	
	private double importanceMultiplier;					// compounded importance weights at current nesting (calculated by multiplying "start" importance weights
	private Stack<Double> storedImportanceWeights;			// stores raw importance weights from each start() invocation.  Each end() invocation pops a value.
	private int depthInsideUnimportantBlock;				// tracks how far within an unimportant block (relative to the importanceThreshold) algorithm is currently processing
	private int nestingDepth;								// current level of nesting relative to"start"/"end" invocations deemed as important
	
	Collection<ExplanationHandler> explanationHandlers;		// collection of handlers to process recorded explanations
	ExplanationFilter explanationFilter;					// object that filters relevant records and passes them to explanationHandlers
	
	
	//TODO:  getTotalNexting(), etc.

	@Override
	public Number getImportanceThreshold() {
		return importanceThreshold;
	}

	@Override
	public void setImportanceThreshold(Number threshold) {
		this.importanceThreshold = (double) threshold;
	}

	@Override
	public ExplanationFilter getFilter() {
		return explanationFilter;
	}

	@Override
	public void setFilter(ExplanationFilter filter) {
		this.explanationFilter = filter;
	}

	@Override
	public void start(Number importance, Object... objects) {
		
		if(!insideUnimportantBlock())
		{
			double importanceWeight = (double) importance;
			processStartRecordRequest(importanceWeight, objects);
		}
	    else {
	    	increseDepthInsideUnimportantBlock();	    	
	    }
	}

	private void processStartRecordRequest(double importanceWeight, Object[] objects) {
		double adjustedImportance = calculateCompoundedImportanceValue(importanceWeight);
	    if (isImportantEnough(adjustedImportance)) {
	    	processRecordRequest(importanceWeight, adjustedImportance, objects);
			setImportanceMultiplier(adjustedImportance);
			addImportanceWeightToStoredImportanceWeights(importanceWeight);
		    increaseNestingDepthOfRecordings();
	    }
	    else {
	    	increseDepthInsideUnimportantBlock();	    	
	    }
	}

	@Override
	public void explain(Number importance, Object... objects) {
		if(!insideUnimportantBlock())
		{
			double importanceWeight = (double) importance;
			processExplainRecordRequest(importanceWeight, objects);
		}
	}


	private void processExplainRecordRequest(double importanceWeight, Object[] objects) {
		double adjustedImportance = calculateCompoundedImportanceValue(importanceWeight);
	    if (isImportantEnough(adjustedImportance)) {
	    	processRecordRequest(importanceWeight, adjustedImportance, objects);
	    }		
	}

	@Override
	public void end(Object... objects) {
	    if (!insideUnimportantBlock()) {
	    	double importanceWeight = popImportanceWeightCorrespondingToTheLastStart();
	    	double adjustedImportance = importanceMultiplier;
		    decreaseNestingDepthOfRecordings();
	    	processRecordRequest(importanceWeight, adjustedImportance, objects);
			setImportanceMultiplier(adjustedImportance/importanceWeight);
	    }
	    else {
	    	decreaseDepthInsideUnimportantBlock();	    	
	    }
	}

	private void processRecordRequest(double importanceWeight, double adjustedImportance, Object[] objects) {
		ExplanationRecord record = makeRecord(importanceWeight, adjustedImportance, objects);
		if (explanationFilter.test(record)) {
			sendRecordToHandlers(record);
		}
	}

	@Override
	public Collection<? extends ExplanationHandler> getHandlers() {
		return explanationHandlers;
	}

	@Override
	public void addHandler(ExplanationHandler handler) {
		explanationHandlers.add(handler);
	}

	@Override
	public boolean removeHandler(ExplanationHandler handler) {
		//Note, this looks for the EXACT SAME object
		return explanationHandlers.remove(handler);
	}
	
	private double calculateCompoundedImportanceValue(double importanceWeight) {
		double compoundedImportanceValue = importanceWeight * importanceMultiplier;
		return compoundedImportanceValue;
	}
	
	private boolean insideUnimportantBlock() {
		return depthInsideUnimportantBlock > 0;
	}

	private boolean isImportantEnough(double compoundedImportance) {
		boolean isImportant = true;
		if(compoundedImportance > importanceThreshold)	{
			isImportant = false;
		}
		return isImportant;
	}
	
	private ExplanationRecord makeRecord(double importance, double compoundedImportance, Object[] objects) {
		ExplanationRecord record = new DefaultExplanationRecord(importance, compoundedImportance, objects);
		return record;
	}
	
	private void sendRecordToHandlers(ExplanationRecord record) {
		for(ExplanationHandler handler : explanationHandlers)
		{
			handler.handle(record);
		}
	}
	
	private void setImportanceMultiplier(double newImportanceMultiplier) {
		this.importanceMultiplier = newImportanceMultiplier;
	}
	
	private void addImportanceWeightToStoredImportanceWeights(double importanceWeight) {
		storedImportanceWeights.push(importanceWeight);
	}
	
	private double popImportanceWeightCorrespondingToTheLastStart() {
		double importanceWeight = storedImportanceWeights.pop();
		return importanceWeight;
	}
	
	private void increaseNestingDepthOfRecordings() {
		++nestingDepth;
	}
	
	private void decreaseNestingDepthOfRecordings() {
		--nestingDepth;		
	}
	
	private void increseDepthInsideUnimportantBlock() {
		++depthInsideUnimportantBlock;
	}
	
	private void decreaseDepthInsideUnimportantBlock() {
		--depthInsideUnimportantBlock;
	}
}
