package com.sri.ai.util.planning.util;

import static com.sri.ai.util.explanation.logging.api.ThreadExplanationLogger.code;

import com.sri.ai.util.explanation.logging.api.ThreadExplanationLogger;
import com.sri.ai.util.planning.api.Plan;
import com.sri.ai.util.planning.core.AbstractCompoundPlan;

public class PlanHierarchicalExplanation {

	public static void explain(Plan plan) {
		if (ThreadExplanationLogger.getThreadExplanationLogger().isActive()) {
			explainIfActive(plan);
		}
	}

	private static void explainIfActive(Plan plan) {
		if (plan instanceof AbstractCompoundPlan) {
			ThreadExplanationLogger.explanationBlock(((AbstractCompoundPlan) plan).operatorName(), code(() -> {
				for (Plan subPlan : ((AbstractCompoundPlan) plan).getSubPlans()) {
					explainIfActive(subPlan);
				}
			}));
		}
		else {
			ThreadExplanationLogger.explain(plan);
		}
	}

	public static void explainResultingPlan(Plan plan) {
		ThreadExplanationLogger.explain("Resulting plan:");
		explain(plan);
	}
}
