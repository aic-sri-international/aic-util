package com.sri.ai.util.planning.api;

import static com.sri.ai.util.Util.collectThoseWhoseIndexSatisfyArrayList;
import static com.sri.ai.util.Util.fill;
import static com.sri.ai.util.Util.myAssert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import com.sri.ai.util.Util;

public class PlanningState<R extends Rule<G>, G extends Goal> {
	
	public Collection<? extends G> allGoals;
	
	public ArrayList<? extends R> rules;

	public ArrayList<Boolean> ruleIsAvailable;
	
	public Set<G> satisfiedGoals;
	
	public PlanningState(Collection<? extends G> allGoals, Collection<? extends G> satisfiedGoals, ArrayList<? extends R> rules) {
		myAssert(allGoals.containsAll(satisfiedGoals), () -> "Planning requires 'all goals' to include indeed all goals, even the already satisfied ones, but " + Util.subtract(satisfiedGoals, allGoals) + " are satisfied goals not included in 'all goals'");
		this.allGoals = allGoals;
		this.rules = rules;
		this.satisfiedGoals = new LinkedHashSet<>(satisfiedGoals);
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
