package com.sri.ai.util.explanation.logging.core;

import java.util.Collection;

import javax.print.event.PrintJobAttributeListener;

import com.sri.ai.util.explanation.logging.api.ExplanationFilter;
import com.sri.ai.util.explanation.logging.api.ExplanationHandler;
import com.sri.ai.util.explanation.logging.api.ExplanationLogger;
import com.sri.ai.util.explanation.logging.api.ExplanationRecord;

public class DefaultExplanationLogger implements ExplanationLogger {
	
	//TODO: ERROR CHECKING / HANDLING FOR TYPE CONVERSIONS
	
	private double importanceThreshold; 					// user defined threshold above which explanations are recorded
	
	private double compundedImportanceWeight;				// cumulative importance weight at current nesting (calculated by multiplying "start" importance weights
	private int levelsInsideUnimportantBlock;				// tracks how far within an unimportant block (relative to the importanceThreshold) algorithm is currently processing
	private int nestingLevel;								// current level of nesting relative to "start"/"end" invocations
	
	Collection<ExplanationHandler> explanationHandlers;		// collection of handlers to process recorded explanations
	ExplanationFilter explanationFilter;				// object that processes explanations and relays important explanations to explanationHandlers
	

	@Override
	public Number getImportanceThreshold() {
		return importanceThreshold;
	}

	@Override
	public void setImportanceThreshold(Number newImportanceThreshold) {
		this.importanceThreshold = (double) newImportanceThreshold;
	}

	@Override
	public ExplanationFilter getFilter() {
		return explanationFilter;
	}

	@Override
	public void setFilter(ExplanationFilter newFilter) {
		this.explanationFilter = newFilter;
	}

	@Override
	public void explain(ExplanationRecord record) {
		// TODO Auto-generated method stub

	}

	@Override
	public void start(Number importanceWeight, Object... objects) {
		// TODO Auto-generated method stub

	}

	@Override
	public void explain(Number importanceWeight, Object... objects) {
		// TODO Auto-generated method stub

	}

	@Override
	public void end(Object... objects) {
		// TODO Auto-generated method stub

	}

	@Override
	public Collection<? extends ExplanationHandler> getHandlers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addHandler(ExplanationHandler handler) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean removeHandler(ExplanationHandler handler) {
		// TODO Auto-generated method stub
		return false;
	}

}
