package com.sri.ai.util.planning.core;

import com.sri.ai.util.planning.api.Goal;
import com.sri.ai.util.planning.api.Plan;
import com.sri.ai.util.planning.api.Planner;
import com.sri.ai.util.planning.api.PlanningState;
import com.sri.ai.util.planning.api.Rule;

public class PlannerThatDoesNothing<R extends Rule<G>, G extends Goal> implements Planner<R, G> {
	
	public PlannerThatDoesNothing() {
	}

	@Override
	public Plan plan(PlanningState<R, G> state) {
		return new SequentialPlan();
	}
}
