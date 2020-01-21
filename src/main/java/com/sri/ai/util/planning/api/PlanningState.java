package com.sri.ai.util.planning.api;

import static com.sri.ai.util.Util.collectThoseWhoseIndexSatisfyArrayList;
import static com.sri.ai.util.Util.fill;
import static com.sri.ai.util.Util.join;

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
	
	public Set<G> negatedContingentGoals;
	
	public PlanningState(
			Collection<? extends G> allRequiredGoals, 
			Collection<? extends G> satisfiedGoals, 
			Collection<? extends G> negatedContingentGoals, 
			ArrayList<? extends R> rules) {
		
		this.allRequiredGoals = allRequiredGoals;
		this.rules = rules;
		this.satisfiedGoals = new LinkedHashSet<>(satisfiedGoals);
		this.negatedContingentGoals = new LinkedHashSet<>(negatedContingentGoals);
		this.ruleIsAvailable = fill(rules.size(), true);
	}

	/** Copy constructor. */
	private PlanningState(PlanningState<R, G> another) {
		
		this.allRequiredGoals = another.allRequiredGoals;
		this.rules = another.rules;
		this.satisfiedGoals = new LinkedHashSet<>(another.satisfiedGoals);
		this.negatedContingentGoals = new LinkedHashSet<>(another.negatedContingentGoals);
		this.ruleIsAvailable = new ArrayList<>(another.ruleIsAvailable);
	}

	public PlanningState<R, G> copy() {
		return new PlanningState<R, G>(this);
	}

	@Override
	public String toString() {
		String allRules = join("\n", rules);
		String availableRules = join("\n", collectThoseWhoseIndexSatisfyArrayList(rules, ruleIsAvailable));
		String result =
				"Planning state with goals " + allRequiredGoals + ", " 
				+ ", rules " + allRules
				+ ", available rules " + availableRules
				+ ", satisfied goals " + satisfiedGoals
				;
		return result;
	}
}
