package com.sri.ai.util.explanation.logging.core;

import java.util.Collection;

import javax.print.event.PrintJobAttributeListener;

import com.sri.ai.util.explanation.logging.api.ExplanationFilter;
import com.sri.ai.util.explanation.logging.api.ExplanationHandler;
import com.sri.ai.util.explanation.logging.api.ExplanationLogger;
import com.sri.ai.util.explanation.logging.api.ExplanationRecord;

public class RelativeWeightExplanationLogger implements ExplanationLogger {
	
	//TODO: ERROR CHECKING / HANDLING FOR TYPE CONVERSIONS
	
	double threshold;

	@Override
	public Number getImportanceThreshold() {
		return threshold;
	}

	@Override
	public void setImportanceThreshold(Number threshold) {
			this.threshold = (double) threshold;
	}

	@Override
	public ExplanationFilter getFilter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setFilter(ExplanationFilter filter) {
		// TODO Auto-generated method stub

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
