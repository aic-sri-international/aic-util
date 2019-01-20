package com.sri.ai.util.planning.core;

import static com.sri.ai.util.Util.collectThoseWhoseIndexSatisfyArrayList;
import static com.sri.ai.util.Util.forAll;
import static com.sri.ai.util.Util.getFirstSatisfyingPredicateOrNull;
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
import java.util.function.Predicate;

import com.sri.ai.util.Util;
import com.sri.ai.util.explanation.logging.api.ThreadExplanationLogger;
import com.sri.ai.util.planning.api.ContingentGoal;
import com.sri.ai.util.planning.api.Goal;
import com.sri.ai.util.planning.api.Plan;
import com.sri.ai.util.planning.api.Planner;
import com.sri.ai.util.planning.api.PlanningState;
import com.sri.ai.util.planning.api.Rule;

/**
 * A planner based on a set of rules and a <code>isEffectivelyStaticGoal</code> predicate.
 * <p>
 * A goal is <i>effective</i> if its truth value can be perfectly known during static planning time,
 * that is, if it becomes satisfied only by action of a rule with it in its consequences.
 * <p>
 * A <i>contingent</i> goal may become true as a result to a change in the execution-time state
 * even if it were not in the consequents of any applied rule.
 * <p>
 * The interface {@link ContingentGoal} has been provided to help mark goals as static or contingent,
 * but those properties do not depend only on the interface a goal object implements, but also
 * on how it is used in the rules, so users must be careful about this.
 * <p>
 * For example, if a a goal is contingent but does not implement {@link ContingentGoal},
 * the planner and plans will not have a way to know whether it has become satisfied
 * by means other than rule application.
 * <p>
 * Likewise, a goal implementing {@link ContingentGoal} that only changes as a result
 * of rule application is an <i>effectively static goal</i>.
 * If the planner does not know that such a goal is effectively static,
 * it will create larger plans than necessary that go through the trouble
 * of checking whether the goal has become satisfied even if no rule producing it has been applied.
 * For this reason, the planner's API provides a means for the user to specify that knowledge as one of its arguments.
 * <p>
 * The planner also makes the assumption that once a contingent goal is either satisfied or fails during execution,
 * it stays that way permanently.
 * <p>
 * Note that it would not make sense to check if goals <i>not</i> implementing {@link ContingentGoal}
 * are "effectively contingent", because the only way of checking for their satisfiability during
 * execution time is through the method {@link ContingentGoal#isSatisfied(com.sri.ai.util.planning.api.State)}.
 * Note also that this interface does not provide a method for checking whether a goal has failed
 * because rules only depend on whether a goal is satisfied, not on whether it has failed.
 * 
 * @author braz
 *
 * @param <R>
 * @param <G>
 */
public class PlannerUsingEachRuleAtMostOnce<R extends Rule<G>, G extends Goal> implements Planner<R, G> {
	
	public static <R1 extends Rule<G1>, G1 extends Goal> 
	Plan 
	planUsingEachRuleAtMostOnce(
			Collection<? extends G1> allRequiredGoals, 
			Collection<? extends G1> satisfiedGoals,
			Collection<? extends G1> failedGoals,
			Predicate<G1> isEffectivelyStaticGoal, 
			ArrayList<? extends R1> rules) {
		
		return planUsingEachRuleAtMostOnceAndIfSuccessfulApplyingSequelPlan(
				allRequiredGoals, 
				satisfiedGoals, 
				failedGoals,
				isEffectivelyStaticGoal, 
				rules, 
				new PlannerThatDoesNothing<R1, G1>());
	}

	public static <R1 extends Rule<G1>, G1 extends Goal> 
	Plan 
	planUsingEachRuleAtMostOnceAndIfSuccessfulApplyingSequelPlan(
			Collection<? extends G1> allRequiredGoals, 
			Collection<? extends G1> satisfiedGoals, 
			Collection<? extends G1> failedGoals,
			Predicate<G1> isEffectivelyStaticGoal, 
			ArrayList<? extends R1> rules, 
			Planner<R1, G1> sequel) {
		
		PlannerUsingEachRuleAtMostOnce planner = new PlannerUsingEachRuleAtMostOnce<R1, G1>(allRequiredGoals, satisfiedGoals, failedGoals, isEffectivelyStaticGoal, rules, sequel);
		Plan plan = planner.plan();
		return plan;
	}

	private Collection<? extends G> allRequiredGoals;

	private PlanningState<R, G> state;
	
	private Predicate<G> isEffectivelyStaticGoal;
	
	private Planner<R, G> sequel;
	
	public PlannerUsingEachRuleAtMostOnce(Collection<? extends G> allRequiredGoals, Collection<? extends G> satisfiedGoals, Collection<? extends G> failedGoals, Predicate<G> isEffectivelyStaticGoal, ArrayList<? extends R> rules) {
		this(allRequiredGoals, satisfiedGoals, failedGoals, isEffectivelyStaticGoal, rules, new PlannerThatDoesNothing<R, G>());
	}
	
	public PlannerUsingEachRuleAtMostOnce(Collection<? extends G> allRequiredGoals, Collection<? extends G> satisfiedGoals, Collection<? extends G> failedGoals, Predicate<G> isEffectivelyStaticGoal, ArrayList<? extends R> rules, Planner<R, G> sequel) {
		this.state = new PlanningState<>(allRequiredGoals, satisfiedGoals, failedGoals, rules);
		this.allRequiredGoals = allRequiredGoals;
		this.isEffectivelyStaticGoal = isEffectivelyStaticGoal;
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
				"Planning for ",  lazy(() -> subtract(allRequiredGoals, state.satisfiedGoals)), 
				" with available rules ", lazy(() -> collectThoseWhoseIndexSatisfyArrayList(state.rules, state.ruleIsAvailable)),
				", from total rule set ", lazy(() -> state.rules),
				" and total goals ", allRequiredGoals,
				code(() -> {

					Plan result;
					
					if (allRequiredGoalsAreSatisfied()) {
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
		return ThreadExplanationLogger.explanationBlock("There are still failed goals", code(() -> {
			
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
				considerRule(rule, i, alternativeRulePlans);
			}
			else {
				explain("Rule has been used already");
			}

		}), "Alternative plans now ", alternativeRulePlans);
	}

	private void considerRule(R rule, int i, List<Plan> alternativeRulePlans) {
		if (allStaticAntecedentsAreSatisfied(rule)) {
			considerRuleWithAtLeastEffectivelyAntecedentsAllSatisfied(rule, i, alternativeRulePlans);
		}
		else {
			explain("Rule does not apply");
		}
	}

	private void considerRuleWithAtLeastEffectivelyAntecedentsAllSatisfied(R rule, int i, List<Plan> alternativeRulePlans) {
		ContingentGoal contingentGoalNotYetDefined = contingentGoalNotYetDefined(rule);
		if (contingentGoalNotYetDefined == null) {
			considerApplicableRule(rule, i, alternativeRulePlans);
		}
		else {
			considerBothPossibilitiesForContingentGoal(rule, i, contingentGoalNotYetDefined, alternativeRulePlans);
		}
	}

	private void considerBothPossibilitiesForContingentGoal(R rule, int i, ContingentGoal contingentGoal, List<Plan> alternativeRulePlans) {
		Plan thenBranch = considerContingentGoalSatisfied(rule, i, contingentGoal);
		Plan elseBranch = considerContingentGoalUnsatisfied(rule, i, contingentGoal);
		ContingentPlan contingentPlan = new ContingentPlan(contingentGoal, thenBranch, elseBranch);
		alternativeRulePlans.add(contingentPlan);
	}

	@SuppressWarnings("unchecked")
	private Plan considerContingentGoalSatisfied(R rule, int i, ContingentGoal contingentGoal) {
		Set<G> satisfiedGoalsBeforeRule = new LinkedHashSet<>(state.satisfiedGoals);
		state.satisfiedGoals.add((G) contingentGoal);
		List<Plan> alternativeRulePlansUnderThisCase = list();
		considerRuleWithAtLeastEffectivelyAntecedentsAllSatisfied(rule, i, alternativeRulePlansUnderThisCase);
		Plan thenBranch = OrPlan.or(alternativeRulePlansUnderThisCase);
		state.satisfiedGoals = satisfiedGoalsBeforeRule;
		return thenBranch;
	}

	@SuppressWarnings("unchecked")
	private Plan considerContingentGoalUnsatisfied(R rule, int i, ContingentGoal contingentGoal) {
		Set<G> failedGoalsBeforeRule = new LinkedHashSet<>(state.failedGoals);
		state.failedGoals.add((G) contingentGoal);
		List<Plan> alternativeRulePlansUnderThisCase = list();
		considerRuleWithAtLeastEffectivelyAntecedentsAllSatisfied(rule, i, alternativeRulePlansUnderThisCase);
		Plan elseBranch = OrPlan.or(alternativeRulePlansUnderThisCase);
		state.failedGoals = failedGoalsBeforeRule;
		return elseBranch;
	}

	private void considerApplicableRule(R rule, int i, List<Plan> alternativeRulePlans) {
		if (ruleIsUseful(rule)) {
			findPlansStartingWithRule(rule, i, alternativeRulePlans);
		}
		else {
			explain("Rule is not useful");
		}
	}

	private void findPlansStartingWithRule(R rule, int i, List<Plan> alternativeRulePlans) {
		ThreadExplanationLogger.explanationBlock("Finding plans starting with ", rule, code(() -> {

			Plan planStartingWithRule = tryRule(rule, i);
			alternativeRulePlans.add(planStartingWithRule);
			
		}), "Alternative plans now ", alternativeRulePlans);
	}

	private Plan tryRule(R rule, int i) {
		
		return ThreadExplanationLogger.explanationBlock("There are failed goals yet: ", Util.subtract(allRequiredGoals, state.satisfiedGoals), code(() -> {

			Set<G> howThingsUsedToBe = applyRule(rule, i);

			Plan planStartingWithRule = planGivenWeJustAppliedThisRule(rule);

			revertRule(i, howThingsUsedToBe);

			return planStartingWithRule;

		}), "Plan starting with rule and solving remaining goals: ", RESULT);
	}

	private Plan planGivenWeJustAppliedThisRule(R rule) {
		return and(rule, plan());
	}

	private boolean allStaticAntecedentsAreSatisfied(R rule) {
		boolean result = forAll(rule.getAntecendents(), a -> !isStatic(a) || state.satisfiedGoals.contains(a));
		return result;
	}

	private ContingentGoal contingentGoalNotYetDefined(R rule) {
		ContingentGoal contingentGoalNotYetSatisfied = 
				(ContingentGoal) 
				getFirstSatisfyingPredicateOrNull(
						rule.getAntecendents(), 
						a -> isContingentGoalNotDefinedByPlanningState(a));
		return contingentGoalNotYetSatisfied;
	}

	private boolean isContingentGoalNotDefinedByPlanningState(G a) {
		boolean result = 
				!isStatic(a) 
				&& 
				!state.satisfiedGoals.contains(a)
				&& 
				!state.failedGoals.contains(a);
		return result;
	}

	private boolean isStatic(G a) {
		boolean result = 
				!(a instanceof ContingentGoal)
				||
				isEffectivelyStaticGoal.test(a);
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

	private boolean allRequiredGoalsAreSatisfied() {
		return state.satisfiedGoals.containsAll(allRequiredGoals);
	}

}
