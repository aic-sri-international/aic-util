package com.sri.ai.util.planning.api;

import static com.sri.ai.util.Util.collectThoseWhoseIndexSatisfyArrayList;
import static com.sri.ai.util.Util.fill;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public class PlanningState<R extends Rule<G>, G extends Goal> {
	
	// TODO: this should be made into an interface and default implementation.
	// Then manipulations of this data by other classes should be encapsulated into appropriate methods.
	
	public Collection<? extends G> allRequiredGoals;
	
	public ArrayList<? extends R> rules;

	public ArrayList<Boolean> ruleIsAvailable;
	
	public Set<G> satisfiedGoals;
	
	public Set<G> negatedEffectivelyContingentGoals;
	
	public PlanningState(Collection<? extends G> allRequiredGoals, Collection<? extends G> satisfiedGoals, Collection<? extends G> negatedEffectivelyContingentGoals, ArrayList<? extends R> rules) {
		this.allRequiredGoals = allRequiredGoals;
		this.rules = rules;
		this.satisfiedGoals = new LinkedHashSet<>(satisfiedGoals);
		this.negatedEffectivelyContingentGoals = new LinkedHashSet<>(negatedEffectivelyContingentGoals);
		this.ruleIsAvailable = fill(rules.size(), true);
	}

	@Override
	public String toString() {
		return 
				"Planning state with goals " + allRequiredGoals + ", " 
				+ ", rules " + rules
				+ ", available rules " + collectThoseWhoseIndexSatisfyArrayList(rules, ruleIsAvailable)
				+ ", satisfied goals " + satisfiedGoals;
	}
}
