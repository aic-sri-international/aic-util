package com.sri.ai.util.planning.util;

import static com.sri.ai.util.Util.collectProperties;
import static com.sri.ai.util.Util.restoreProperties;
import static com.sri.ai.util.explanation.logging.api.ThreadExplanationLogger.getThreadExplanationLogger;

import java.util.Collection;
import java.util.IdentityHashMap;

import com.sri.ai.util.explanation.logging.api.ExplanationHandler;
import com.sri.ai.util.planning.api.Plan;

public class PlanHierarchicalExplanation {

	public static void explain(Plan plan) {
		if (getThreadExplanationLogger().isActive()) {
			
			Collection<? extends ExplanationHandler> handlers = getThreadExplanationLogger().getHandlers();

			IdentityHashMap<? extends ExplanationHandler, Boolean> blockTimeProperty = 
					collectProperties(handlers, ExplanationHandler::getIncludeBlockTime);
			IdentityHashMap<? extends ExplanationHandler, Boolean> recordIdProperty = 
					collectProperties(handlers, ExplanationHandler::getIncludeRecordId);
			IdentityHashMap<? extends ExplanationHandler, Boolean> timestampProperty = 
					collectProperties(handlers, ExplanationHandler::getIncludeTimestamp);
			
			for (ExplanationHandler handler : handlers) {
				handler.setIncludeBlockTime(false);
				handler.setIncludeRecordId(false);
				handler.setIncludeTimestamp(false);
			}
			
			getThreadExplanationLogger().explainTree(plan.stringTree());
			
			restoreProperties(blockTimeProperty, (h, v) -> h.setIncludeBlockTime(v));
			restoreProperties(recordIdProperty, (h, v) -> h.setIncludeRecordId(v));
			restoreProperties(timestampProperty, (h, v) -> h.setIncludeTimestamp(v));
		}
	}
}
