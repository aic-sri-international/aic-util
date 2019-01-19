package com.sri.ai.util.planning.core;

import static com.sri.ai.util.Util.collectThoseWhoseIndexSatisfyArrayList;
import static com.sri.ai.util.Util.forAll;
import static com.sri.ai.util.Util.list;
import static com.sri.ai.util.Util.subtract;
import static com.sri.ai.util.Util.thereExists;
import static com.sri.ai.util.explanation.logging.api.ThreadExplanationLogger.RESULT;
import static com.sri.ai.util.explanation.logging.api.ThreadExplanationLogger.code;
import static com.sri.ai.util.explanation.logging.api.ThreadExplanationLogger.explain;
import static com.sri.ai.util.explanation.logging.api.ThreadExplanationLogger.lazy;
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
import com.sri.ai.util.planning.api.Planner;
import com.sri.ai.util.planning.api.PlanningState;
import com.sri.ai.util.planning.api.Rule;

public class PlannerUsingEachRuleAtMostOnce<R extends Rule<G>, G extends Goal> implements Planner<R, G> {
	
	public static <R1 extends Rule<G1>, G1 extends Goal> 
	Plan 
	planUsingEachRuleAtMostOnce(List<G1> allGoals, Collection<? extends G1> satisfiedGoals, ArrayList<? extends R1> rules) {
		
		return planUsingEachRuleAtMostOnceAndIfSuccessfulApplyingSequelPlan(allGoals, satisfiedGoals, rules, new PlannerThatDoesNothing<R1, G1>());
	}

	public static <R1 extends Rule<G1>, G1 extends Goal> 
	Plan 
	planUsingEachRuleAtMostOnceAndIfSuccessfulApplyingSequelPlan(
			List<G1> allGoals, 
			Collection<? extends G1> satisfiedGoals, 
			ArrayList<? extends R1> rules, 
			Planner<R1, G1> sequel) {
		
		PlannerUsingEachRuleAtMostOnce planner = new PlannerUsingEachRuleAtMostOnce<R1, G1>(allGoals, satisfiedGoals, rules, sequel);
		Plan plan = planner.plan();
		return plan;
	}

	private PlanningState<R, G> state;
	
	private Planner<R, G> sequel;
	
	public PlannerUsingEachRuleAtMostOnce(Collection<? extends G> allGoals, Collection<? extends G> satisfiedGoals, ArrayList<? extends R> rules) {
		this(allGoals, satisfiedGoals, rules, new PlannerThatDoesNothing<R, G>());
	}
	
	public PlannerUsingEachRuleAtMostOnce(Collection<? extends G> allGoals, Collection<? extends G> satisfiedGoals, ArrayList<? extends R> rules, Planner<R, G> sequel) {
		this.state = new PlanningState<>(allGoals, satisfiedGoals, rules);
		this.sequel = sequel;
	}

	@Override
	public Plan plan(PlanningState<R, G> state) {
		this.state = state;
		Plan result = plan();
		return result;
	}
	
	/**
	 * Returns a plan that is guaranteed to achieve all goals given available rules,
	 * or null if there is no such plan. 
	 * @return
	 */
	public Plan plan() {
		return ThreadExplanationLogger.explanationBlock(
				"Planning for ",  lazy(() -> subtract(state.allGoals, state.satisfiedGoals)), 
				" with available rules ", lazy(() -> collectThoseWhoseIndexSatisfyArrayList(state.rules, state.ruleIsAvailable)),
				", from total rule set ", lazy(() -> state.rules),
				" and total goals ", state.allGoals,
				code(() -> {

					Plan result;
					
					if (allGoalsAreSatisfied()) {
						explain("All goals are satisfied, returning sequel plan provided by sequel planner " + sequel.getClass());
						result = sequel.plan(state);
					}
					else {
						result = planIfThereIsAtLeastOneUnsatisfiedGoal();
					}

					return result;

				}), "Plan: ", RESULT);
	}

	private Plan planIfThereIsAtLeastOneUnsatisfiedGoal() {
		return ThreadExplanationLogger.explanationBlock("There are still unsatisfied goals", code(() -> {
			
			Plan result;
			List<Plan> alternativeRulePlans = list();
			for (int i = 0; i != state.rules.size(); i++) {
				attemptToUseRule(i, alternativeRulePlans);
			}
			result = OrPlan.or(alternativeRulePlans);
			return result;
			
		}), "Plan is: ", RESULT);
	}

	private void attemptToUseRule(int i, List<Plan> alternativeRulePlans) {
		ThreadExplanationLogger.explanationBlock("Attempting rule ", state.rules.get(i), code(() -> {

			if (state.ruleIsAvailable.get(i)) {
				R rule = state.rules.get(i);
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

		}), "Alternative plans now ", alternativeRulePlans);
	}

	private void findPlansStartingWithRule(R rule, int i, List<Plan> alternativeRulePlans) {
		ThreadExplanationLogger.explanationBlock("Finding plans starting with ", rule, code(() -> {

			Plan planStartingWithRule = tryRule(rule, i);
			alternativeRulePlans.add(planStartingWithRule);
			
		}), "Alternative plans now ", alternativeRulePlans);
	}

	private Plan tryRule(R rule, int i) {
		
		return ThreadExplanationLogger.explanationBlock("There are unsatisfied goals yet: ", Util.subtract(state.allGoals, state.satisfiedGoals), code(() -> {

			Set<G> howThingsUsedToBe = applyRule(rule, i);

			Plan planStartingWithRule = planGivenWeJustAppliedThisRule(rule);

			revertRule(i, howThingsUsedToBe);

			return planStartingWithRule;

		}), "Plan starting with rule and solving remaining goals: ", RESULT);
	}

	private Plan planGivenWeJustAppliedThisRule(R rule) {
		return and(rule, plan());
	}

	private boolean ruleApplies(R rule) {
		boolean result = forAll(rule.getAntecendents(), a -> state.satisfiedGoals.contains(a));
		return result;
	}

	private boolean ruleIsUseful(R rule) {
		boolean result = thereExists(rule.getConsequents(), c -> !state.satisfiedGoals.contains(c));
		return result;
	}
	
	private Set<G> applyRule(R rule, int i) {
		return ThreadExplanationLogger.explanationBlock("Applying rule ", rule, code(() -> {
	
			Set<G> satisfiedGoalsBeforeRule = new LinkedHashSet<>(state.satisfiedGoals);

			state.ruleIsAvailable.set(i, false);
			state.satisfiedGoals.addAll(rule.getConsequents());
			// Note: copying satisfiedGoals is not desirable and one may wonder why not treat it like we treat ruleIsAvailable.
			// That would not work
			// because adding the rule's consequents to satisfiedGoals would be ok,
			// but reverting it would not be easy since some of the consequents might have already been in satisfiedGoals
			// while others not, so it would not be clear which ones to remove.
			
			return satisfiedGoalsBeforeRule;
	
		}), "Satisfied goals after application are: ", state.satisfiedGoals);
		
	}

	private void revertRule(int i, Set<G> satisfiedGoalsBeforeRule) {
		state.ruleIsAvailable.set(i, true);
		state.satisfiedGoals = satisfiedGoalsBeforeRule;
	}

	private boolean allGoalsAreSatisfied() {
		return state.satisfiedGoals.size() == state.allGoals.size();
	}

}
