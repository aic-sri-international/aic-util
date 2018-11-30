package com.sri.ai.util.planning.core;

import static com.sri.ai.util.Util.fill;
import static com.sri.ai.util.Util.forAll;
import static com.sri.ai.util.Util.list;
import static com.sri.ai.util.Util.set;
import static com.sri.ai.util.Util.thereExists;
import static com.sri.ai.util.explanation.logging.api.ThreadExplanationLogger.RESULT;
import static com.sri.ai.util.explanation.logging.api.ThreadExplanationLogger.code;
import static com.sri.ai.util.explanation.logging.api.ThreadExplanationLogger.explain;
import static com.sri.ai.util.planning.core.OrPlan.or;
import static com.sri.ai.util.planning.core.SequentialPlan.and;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.sri.ai.util.Util;
import com.sri.ai.util.explanation.logging.api.ThreadExplanationLogger;
import com.sri.ai.util.planning.api.Goal;
import com.sri.ai.util.planning.api.Plan;
import com.sri.ai.util.planning.api.Rule;

public class Planner<R extends Rule<G>, G extends Goal> {
	
	public static <R1 extends Rule<G1>, G1 extends Goal> Plan plan(List<G1> allGoals, ArrayList<R1> rules) {
		Planner planner = new Planner<R1, G1>(allGoals, rules);
		Plan plan = planner.plan();
		return plan;
	}

	Collection<? extends G> allGoals;
	
	private ArrayList<? extends R> rules;

	private ArrayList<Boolean> ruleIsAvailable;
	
	Set<G> satisfiedGoals;

	public Planner(Collection<? extends G> allGoals, ArrayList<? extends R> rules) {
		this.allGoals = allGoals;
		this.rules = rules;
	}

	/**
	 * Returns a plan that is guaranteed to achieve all goals given available rules,
	 * or null if there is no such plan. 
	 * @return
	 */
	public Plan plan() {
		return ThreadExplanationLogger.explanationBlock("Planning for ", allGoals, " with rules ", rules, code(() -> {

			satisfiedGoals = set();
			ruleIsAvailable = fill(rules.size(), true);
			Plan result = subPlanGivenSatisfiedGoalsAndAvailableRules();
			return result;

		}), "Plan: ", RESULT);
	}
	
	private Plan subPlanGivenSatisfiedGoalsAndAvailableRules() {
		return ThreadExplanationLogger.explanationBlock("Planning given satisfied goals ", satisfiedGoals, " with available rules ", ruleIsAvailable, code(() -> {

			List<Plan> alternativeRulePlans = list();
			for (int i = 0; i != rules.size(); i++) {
				attemptToUseRule(i, alternativeRulePlans);
			}

			Plan result = or(alternativeRulePlans);

			return result;

		}), "Plan: ", RESULT);
	}

	public void attemptToUseRule(int i, List<Plan> alternativeRulePlans) {
		ThreadExplanationLogger.explanationBlock("Attempting rule ", rules.get(i), code(() -> {

			if (ruleIsAvailable.get(i)) {
				R rule = rules.get(i);
				if (ruleApplies(rule)) {
					if (ruleIsUseful(rule)) {
						findPlansStartingWithRule(rule, i, alternativeRulePlans);
					}
					else {
						explain("Rule is not useful");
					}
				}
				else {
					explain("Rule does not apply");
				}
			}
			else {
				explain("Rule has been used already");
			}

		}), "Altervative plans now ", alternativeRulePlans);
	}

	private boolean ruleApplies(R rule) {
		boolean result = forAll(rule.getAntecendents(), a -> satisfiedGoals.contains(a));
		return result;
	}

	private boolean ruleIsUseful(R rule) {
		boolean result = thereExists(rule.getConsequents(), c -> !satisfiedGoals.contains(c));
		return result;
	}
	
	public void findPlansStartingWithRule(R rule, int i, List<Plan> alternativeRulePlans) {
		ThreadExplanationLogger.explanationBlock("Finding plans starting with ", rule, code(() -> {

			Set<G> satisfiedGoalsBeforeRule = new LinkedHashSet<>(satisfiedGoals);
			
			applyRule(rule, i);

			attempToFindPlanStartingWithRule(rule, alternativeRulePlans);

			revertRule(i, satisfiedGoalsBeforeRule);

		}), "Altervative plans now ", alternativeRulePlans);
	}

	public void applyRule(R rule, int i) {
		ThreadExplanationLogger.explanationBlock("Applying rule ", rule, code(() -> {

			ruleIsAvailable.set(i, false);
			satisfiedGoals.addAll(rule.getConsequents());
			// Note: copying satisfiedGoals is not desirable and one may wonder why not treat it like we treat ruleIsAvailable.
			// That would not work
			// because adding the rule's consequents to satisfiedGoals would be ok,
			// but reverting it would not be easy since some of the consequents might have already been in satisfiedGoals
			// while others not, so it would not be clear which ones to remove.

		}), "Satisfied goals after application are: ", satisfiedGoals);
		
	}

	public void attempToFindPlanStartingWithRule(R rule, List<Plan> alternativeRulePlans) {
		
		ThreadExplanationLogger.explanationBlock("Solving any remaining goals after applying ", rule, code(() -> {

			boolean allGoalsAreSatisfied = satisfiedGoals.size() == allGoals.size();

			Plan planStartingWithRule;

			if (allGoalsAreSatisfied) {
				planStartingWithRule = and(rule);
			}
			else {
				planStartingWithRule = attemptPlanStartingWithRuleWhenThereAreUnsatisfiedGoalsRemaining(rule);
			}

			alternativeRulePlans.add(planStartingWithRule);

		}), "Altervative plans now ", alternativeRulePlans);
	}

	public SequentialPlan attemptPlanStartingWithRuleWhenThereAreUnsatisfiedGoalsRemaining(R rule) {
		
		return ThreadExplanationLogger.explanationBlock("There are unsatisfied goals yet: ", Util.subtract(allGoals, satisfiedGoals), code(() -> {

			Plan planStartingWithRule;
			Plan subPlanAfterRule = subPlanGivenSatisfiedGoalsAndAvailableRules();
			if (subPlanAfterRule == null) {
				explain("Could not find sub-plan after applying rule to solve remaining goals.");
				planStartingWithRule = null;
			}
			else {
				explain("Found sub-plan for remaining goals: " + subPlanAfterRule);
				planStartingWithRule = and(rule, subPlanAfterRule);
			}
			return planStartingWithRule;

		}), "Plan starting with rule and solving remaining goals: ", RESULT);
	}

	public void revertRule(int i, Set<G> satisfiedGoalsBeforeRule) {
		ruleIsAvailable.set(i, true);
		satisfiedGoals = satisfiedGoalsBeforeRule;
	}

}
