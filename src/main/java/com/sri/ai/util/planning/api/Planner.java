package com.sri.ai.util.planning.api;

public interface Planner<R extends Rule<G>, G extends Goal> {
	
	Plan plan(PlanningState<R, G> state);

}
