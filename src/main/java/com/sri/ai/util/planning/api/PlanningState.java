package com.sri.ai.util.planning.api;

import static com.sri.ai.util.Util.collectThoseWhoseIndexSatisfyArrayList;
import static com.sri.ai.util.Util.fill;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public class PlanningState<R extends Rule<G>, G extends Goal> {
	
	public ArrayList<? extends R> rules;

	public ArrayList<Boolean> ruleIsAvailable;
	
	public Set<G> satisfiedGoals;
	
	public Set<G> failedGoals;
	
	public PlanningState(Collection<? extends G> allRequiredGoals, Collection<? extends G> satisfiedGoals, Collection<? extends G> failedGoals, ArrayList<? extends R> rules) {
		this.rules = rules;
		this.satisfiedGoals = new LinkedHashSet<>(satisfiedGoals);
		this.failedGoals = new LinkedHashSet<>(failedGoals);
		this.ruleIsAvailable = fill(rules.size(), true);
	}

	@Override
	public String toString() {
		return 
				"Planning state with rules " + rules
				+ ", available rules " + collectThoseWhoseIndexSatisfyArrayList(rules, ruleIsAvailable)
				+ ", satisfied goals " + satisfiedGoals;
	}
}
