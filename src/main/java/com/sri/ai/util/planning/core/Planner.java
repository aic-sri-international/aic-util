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

public class Planner {
	
	public static Plan plan(List<? extends Goal> allGoals, ArrayList<? extends Rule> rules) {
		Planner planner = new Planner(allGoals, rules);
		Plan plan = planner.plan();
		return plan;
	}

	Collection<? extends Goal> allGoals;
	
	private ArrayList<? extends Rule> rules;

	private ArrayList<Boolean> ruleIsAvailable;
	
	Set<Goal> satisfiedGoals;

	public Planner(Collection<? extends Goal> allGoals, ArrayList<? extends Rule> rules) {
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
				Rule rule = rules.get(i);
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

	private boolean ruleApplies(Rule rule) {
		boolean result = forAll(rule.getAntecendents(), a -> satisfiedGoals.contains(a));
		return result;
	}

	private boolean ruleIsUseful(Rule rule) {
		boolean result = thereExists(rule.getConsequents(), c -> !satisfiedGoals.contains(c));
		return result;
	}
	
	public void findPlansStartingWithRule(Rule rule, int i, List<Plan> alternativeRulePlans) {
		ThreadExplanationLogger.explanationBlock("Finding plans starting with ", rule, code(() -> {

			Set<Goal> satisfiedGoalsBeforeRule = new LinkedHashSet<>(satisfiedGoals);
			
			applyRule(rule, i);

			attempToFindPlanStartingWithRule(rule, alternativeRulePlans);

			revertRule(i, satisfiedGoalsBeforeRule);

		}), "Altervative plans now ", alternativeRulePlans);
	}

	public void applyRule(Rule rule, int i) {
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

	public void attempToFindPlanStartingWithRule(Rule rule, List<Plan> alternativeRulePlans) {
		
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

	public SequentialPlan attemptPlanStartingWithRuleWhenThereAreUnsatisfiedGoalsRemaining(Rule rule) {
		
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

	public void revertRule(int i, Set<Goal> satisfiedGoalsBeforeRule) {
		ruleIsAvailable.set(i, true);
		satisfiedGoals = satisfiedGoalsBeforeRule;
	}

//	public Plan plan(Iterable<? extends Goal> allGoals) {
//		LinkedList<Goal> unfulfilledGoals = Util.addAllToANewList(allGoals);
//		SequentialPlan plan = new SequentialPlan();
//		plan(unfulfilledGoals, plan);
//		return plan;
//	}
//
//	private void plan(LinkedList<Goal> unfulfilledGoals, SequentialPlan planSoFar) {
//		ThreadExplanationLogger.explanationBlock("Planning for ", unfulfilledGoals, " with rules ", getCompiledRules(), code(() -> {
//			
//			while ( ! unfulfilledGoals.isEmpty()) {
//				fulfillSomeGoal(unfulfilledGoals, planSoFar);
//			}
//
//		}), "Plan: ", planSoFar);
//	}
//
//	private void fulfillSomeGoal(LinkedList<Goal> unfulfilledGoals, SequentialPlan planSoFar) {
//		ThreadExplanationLogger.explanationBlock("Fulfilling some goal", code(() -> {
//			
//			Goal goal = chooseNextGoal(unfulfilledGoals);
//			Rule rule = chooseNextRule(goal, unfulfilledGoals);
//			apply(rule, unfulfilledGoals);
//			planSoFar.add(rule);
//			
//		}), "Plan so far: ", planSoFar, ", unfullfilled goals: ", unfulfilledGoals);
//	}
//
//	private Goal chooseNextGoal(LinkedList<Goal> unfulfilledGoals) {
//		return ThreadExplanationLogger.explanationBlock("Picking goal to fulfill", code(() -> {
//			
//		// TODO: need to use adaptive selection here
//		Goal goal = pickUniformly(unfulfilledGoals, getRandom());
//		return goal;
//
//		}), "Picked goal: ", RESULT);
//	}
//
//	private Rule chooseNextRule(Goal goal, LinkedList<Goal> unfulfilledGoals) {
//		return ThreadExplanationLogger.explanationBlock("Choosing rule for ", goal, " given unfulfilled goals ", unfulfilledGoals, code(() -> {
//
//			List<Rule> rulesForGoal = getCompiledRules().getRulesFor(goal);
//			myAssert(rulesForGoal != null, noRuleFoundFor(goal));
//			List<Rule> rulesForGoalThatApply = getRulesThatApply(rulesForGoal, unfulfilledGoals);
//			myAssert( ! rulesForGoalThatApply.isEmpty(), noRuleApplies(goal, rulesForGoal, unfulfilledGoals));
//			// TODO: need to use adaptive selection here
//			Rule rule = pickUniformly(rulesForGoalThatApply, random);
//			return rule;
//
//		}), "Picked ", RESULT);
//	}
//
//	private List<Rule> getRulesThatApply(List<Rule> rules, LinkedList<Goal> unfulfilledGoals) {
//		return ThreadExplanationLogger.explanationBlock("Getting those rules that apply ", unfulfilledGoals, code(() -> {
//
//			List<Rule> rulesThatApply = collectToList(rules, antecedentsAreSatisfiedAccordingTo(unfulfilledGoals));
//			return rulesThatApply;
//
//		}), "Picked ", RESULT);
//	}
//
//	private Predicate<Rule> antecedentsAreSatisfiedAccordingTo(LinkedList<Goal> unfulfilledGoals) {
//		return r -> forAll(r.getAntecendents(), a -> isSatisfied(a, unfulfilledGoals));
//	}
//
//	private boolean isSatisfied(Goal goal, LinkedList<Goal> unfulfilledGoals) {
//		return ThreadExplanationLogger.explanationBlock("Deciding if ", goal, " is satisfied given unfulfilled goals ", unfulfilledGoals, code(() -> {
//
//			boolean result = ! isUnsatisfied(goal, unfulfilledGoals);
//			return result;
//
//		}), "Satisfied ", RESULT);
//	}
//
//	private boolean isUnsatisfied(Goal goal, LinkedList<Goal> unfulfilledGoals) {
//		boolean result = unfulfilledGoals.contains(goal);
//		return result;
//	}
//
//	private void apply(Rule rule, LinkedList<Goal> unfulfilledGoals) {
//		unfulfilledGoals.removeAll(rule.getConsequents());
//	}
//
//	private NullaryFunction<String> noRuleFoundFor(Goal goal) {
//		return () -> "No rules found for goal " + goal + " in\n" + this;
//	}
//
//	private NullaryFunction<String> noRuleApplies(Goal goal, List<Rule> rulesForGoal, LinkedList<Goal> unfulfilledGoals) {
//		return () -> 
//		"Dead end: No rule for " + goal + " below applies to current unfullfilled goals because some antecedent in each of them is unfullfilled:\n" 
//		+ join("\n", rulesForGoal) + "\n"
//		+ "Unfulfilled goals:\n" + join("\n", unfulfilledGoals);
//	}

}
