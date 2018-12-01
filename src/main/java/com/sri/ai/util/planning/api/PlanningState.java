package com.sri.ai.util.planning.api;

import static com.sri.ai.util.Util.collectThoseWhoseIndexSatisfyArrayList;
import static com.sri.ai.util.Util.fill;
import static com.sri.ai.util.Util.set;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

public class PlanningState<R extends Rule<G>, G extends Goal> {
	
	public Collection<? extends G> allGoals;
	
	public ArrayList<? extends R> rules;

	public ArrayList<Boolean> ruleIsAvailable;
	
	public Set<G> satisfiedGoals;
	
	public PlanningState(Collection<? extends G> allGoals, ArrayList<? extends R> rules) {
		this.allGoals = allGoals;
		this.rules = rules;
		this.satisfiedGoals = set();
		this.ruleIsAvailable = fill(rules.size(), true);
	}

	@Override
	public String toString() {
		return 
				"Planning state with goals " + allGoals + ", " 
				+ ", rules " + rules
				+ ", available rules " + collectThoseWhoseIndexSatisfyArrayList(rules, ruleIsAvailable)
				+ ", satisfied goals " + satisfiedGoals;
	}
}
