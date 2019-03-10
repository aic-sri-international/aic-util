package com.sri.ai.util.planning.core;

import static com.sri.ai.util.Util.arrayListFrom;
import static com.sri.ai.util.Util.collectThoseWhoseIndexSatisfyArrayList;
import static com.sri.ai.util.Util.forAll;
import static com.sri.ai.util.Util.getFirst;
import static com.sri.ai.util.Util.list;
import static com.sri.ai.util.Util.myAssert;
import static com.sri.ai.util.Util.subtract;
import static com.sri.ai.util.Util.thereExists;
import static com.sri.ai.util.explanation.logging.api.ThreadExplanationLogger.RESULT;
import static com.sri.ai.util.explanation.logging.api.ThreadExplanationLogger.code;
import static com.sri.ai.util.explanation.logging.api.ThreadExplanationLogger.explain;
import static com.sri.ai.util.explanation.logging.api.ThreadExplanationLogger.explanationBlock;
import static com.sri.ai.util.explanation.logging.api.ThreadExplanationLogger.lazy;
import static com.sri.ai.util.planning.core.SequentialPlan.and;
import static com.sri.ai.util.planning.util.PlanHierarchicalExplanation.explainResultingPlan;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import com.sri.ai.util.base.Wrapper;
import com.sri.ai.util.planning.api.ContingentGoal;
import com.sri.ai.util.planning.api.Goal;
import com.sri.ai.util.planning.api.Plan;
import com.sri.ai.util.planning.api.Planner;
import com.sri.ai.util.planning.api.PlanningState;
import com.sri.ai.util.planning.api.Rule;
import com.sri.ai.util.planning.util.PlanHierarchicalExplanation;

/**
 * A planner based on a set of rules and a <code>isEffectivelyStaticGoal</code> predicate.
 * <p>
 * A goal is <i>effectively static</i> if its truth value can be perfectly known during static planning time,
 * that is, if it becomes satisfied only by action of a rule with it in its consequents.
 * <p>
 * A <i>contingent</i> goal may become true as a result to a change in the execution-time state
 * even if it were not in the consequents of any applied rule.
 * <p>
 * The interface {@link ContingentGoal} has been provided to help mark goals as contingent,
 * but this does not depend only on the interface a goal object implements, but also
 * on how it is used in the rules, so users must be careful about this.
 * <p>
 * For example, if a a goal is contingent but does not implement {@link ContingentGoal},
 * the planner and plans will not have a way of knowing whether it has become satisfied
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
 * Note also that this interface does not provide a method for checking whether a goal has been negated
 * because rules only depend on whether a goal is satisfied, not on whether its negation is satisfied.
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
			Collection<? extends G1> negatedEffectiveContingentGoals,
			Predicate<G1> isEffectivelyStaticGoal, 
			Collection<? extends R1> rules) {
		
		return planUsingEachRuleAtMostOnceAndIfSuccessfulApplyingSequelPlan(
				allRequiredGoals, 
				satisfiedGoals, 
				negatedEffectiveContingentGoals,
				isEffectivelyStaticGoal, 
				rules, 
				new PlannerThatDoesNothing<R1, G1>());
	}

	public static <R1 extends Rule<G1>, G1 extends Goal> 
	Plan 
	planUsingEachRuleAtMostOnceAndIfSuccessfulApplyingSequelPlan(
			Collection<? extends G1> allRequiredGoals, 
			Collection<? extends G1> satisfiedGoals, 
			Collection<? extends G1> negatedEffectiveContingentGoals,
			Predicate<G1> isEffectivelyStaticGoal, 
			Collection<? extends R1> rules, 
			Planner<R1, G1> sequel) {
		
		PlannerUsingEachRuleAtMostOnce planner = new PlannerUsingEachRuleAtMostOnce<R1, G1>(allRequiredGoals, satisfiedGoals, negatedEffectiveContingentGoals, isEffectivelyStaticGoal, rules, sequel);
		Plan plan = planner.plan();
		return plan;
	}

	private PlanningState<R, G> state;
	
	private Predicate<G> isEffectivelyStaticGoal;
	
	private Planner<R, G> sequel;
	
	public PlannerUsingEachRuleAtMostOnce(Collection<? extends G> allRequiredGoals, Collection<? extends G> satisfiedGoals, Collection<? extends G> negatedEffectiveContingentGoals, Predicate<G> isEffectivelyStaticGoal, Collection<? extends R> rules) {
		this(allRequiredGoals, satisfiedGoals, negatedEffectiveContingentGoals, isEffectivelyStaticGoal, rules, new PlannerThatDoesNothing<R, G>());
	}
	
	public PlannerUsingEachRuleAtMostOnce(Collection<? extends G> allRequiredGoals, Collection<? extends G> satisfiedGoals, Collection<? extends G> negatedEffectiveContingentGoals, Predicate<G> isEffectivelyStaticGoal, Collection<? extends R> rules, Planner<R, G> sequel) {
		ArrayList<? extends R> arrayListOfSamplingRules = (rules instanceof ArrayList)? (ArrayList<? extends R>) rules : arrayListFrom(rules);
		this.state = new PlanningState<>(allRequiredGoals, satisfiedGoals, negatedEffectiveContingentGoals, arrayListOfSamplingRules);
		this.isEffectivelyStaticGoal = isEffectivelyStaticGoal;
		this.sequel = sequel;
		myAssert(forAll(allRequiredGoals, g -> isEffectivelyStatic(g)), () -> "Required goals cannot contain contingent goals.");
		myAssert(forAll(rules, r -> forAll(r.getConsequents(), c -> isEffectivelyStatic(c))), () -> "Rules consequents cannot contain contingent goals.");
	}

	@Override
	public Plan plan(PlanningState<R, G> state) {
		this.state = state;
		Plan result = plan();
		return result;
	}
	
	/**
	 * Returns a plan that is guaranteed to achieve all goals given available rules,
	 * or an empty {@link OrPlan} if there is no such plan. 
	 * @return
	 */
	public Plan plan() {
		return explanationBlock(
				"Planning for ", lazy(() -> subtract(state.allRequiredGoals, state.satisfiedGoals)), 
				" with still unused rules ", lazy(() -> collectThoseWhoseIndexSatisfyArrayList(state.rules, state.ruleIsAvailable)),
				", out of total rule set ", lazy(() -> state.rules),
				", required goals ", state.allRequiredGoals,
				", satisfied goals ", state.satisfiedGoals,
				code(() -> {

					Plan result;
					
					if (allRequiredGoalsAreSatisfied()) {
						explain("All goals are satisfied, returning sequel plan provided by sequel planner " + sequel.getClass());
						result = sequel.plan(state);
					}
					else {
						result = planIfThereIsAtLeastOneUnsatisfiedRequiredGoal();
					}

					explainResultingPlan(result);

					return result;

				}));
	}

	private Plan planIfThereIsAtLeastOneUnsatisfiedRequiredGoal() {
		return explanationBlock("There are still unsatisfied required goals", code(() -> {
			
			explainUnsatisfiedRequiredGoals();
			
			List<Plan> alternativePlansStartingWithRule = list();
			for (int i = 0; i != state.rules.size(); i++) {
				attemptToUseRule(i, alternativePlansStartingWithRule);
			}
			Plan result = OrPlan.or(alternativePlansStartingWithRule);

			explainResultingPlan(result);

			return result;

		}));
	}

	private void explainUnsatisfiedRequiredGoals() {
		explanationBlock("Unsatisfied goals: ", code(() -> {
			for (Goal requiredGoal : state.allRequiredGoals) {
				if (!state.satisfiedGoals.contains(requiredGoal)) {
					explain(requiredGoal);
				}
			}
		}));
	}

	private void attemptToUseRule(int i, List<Plan> alternativePlansStartingWithRule) {
		explanationBlock("Attempting rule ", state.rules.get(i), code(() -> {

			if (state.ruleIsAvailable.get(i)) {
				R rule = state.rules.get(i);
				if (ruleIsUseful(rule)) {
					considerRule(rule, i, alternativePlansStartingWithRule);
				}
				else {
					explain("Rule is not useful (all consequents already satisfied)");
				}
			}
			else {
				explain("Rule has been used already");
			}
			
			explainCurrentAlternativePlans(alternativePlansStartingWithRule);

		}));
	}

	private void explainCurrentAlternativePlans(List<Plan> alternativePlansStartingWithRule) {
		explain("Alternative plans:");
		for (Plan alternativeRulePlan : alternativePlansStartingWithRule) {
			PlanHierarchicalExplanation.explain(alternativeRulePlan);
		}
	}

	private void considerRule(R rule, int i, List<Plan> alternativePlansStartingWithRule) {
		explanationBlock("Considering available rule ",  rule, code(() -> {

			Goal unsatisfiedEffectivelyStaticAntecedent = getUnsatisfiedEffectivelyStaticAntecedent(rule);
			if (unsatisfiedEffectivelyStaticAntecedent == null) {
				considerRuleWithAtLeastEffectivelyStaticAntecedentsAllSatisfied(rule, i, alternativePlansStartingWithRule);
			}
			else {
				explain("Rule does not apply because ", unsatisfiedEffectivelyStaticAntecedent, " is unsatisfied");
			}

		}));
	}

	private Goal getUnsatisfiedEffectivelyStaticAntecedent(R rule) {
		Goal unsatisfiedStaticAntecendent = getFirst(rule.getAntecendents(), a -> isEffectivelyStaticAndUnsatisfied(a));
		return unsatisfiedStaticAntecendent;
	}

	private boolean isEffectivelyStaticAndUnsatisfied(G goal) {
		boolean result = 
				isEffectivelyStatic(goal)
				&& 
				!state.satisfiedGoals.contains(goal);
		return result;
	}

	private Plan planIfThereIsAtLeastOneUnsatisfiedRequiredGoalStartingAtRuleWithIndexWhichIsKnownToHaveAtLeastAllEffectivelyStaticAntecedentsAllSatisfied(R rule, int i) {
		return explanationBlock("Finding plan using ", rule, " known to have all effectively static antecedents satisfied", code(() -> {

			List<Plan> alternativeRulePlansUnderThisCase = list();
			considerRuleWithAtLeastEffectivelyStaticAntecedentsAllSatisfied(rule, i, alternativeRulePlansUnderThisCase);
			Plan result = OrPlan.or(alternativeRulePlansUnderThisCase);

			explainResultingPlan(result);

			return result;

		}));
	}

	private void considerRuleWithAtLeastEffectivelyStaticAntecedentsAllSatisfied(R rule, int i, List<Plan> alternativePlansStartingWithRule) {
		explanationBlock("Considering rule with all static antecedents satisfied: ", rule, code(() -> {

			ContingentGoal contingentGoalNegatedByState = contingentGoalNegatedByState(rule);
			if (contingentGoalNegatedByState == null) {
				considerRuleWithAtLeastEffectivelyStaticAntecedentsAllSatisfiedAndNoContingentGoalsNegatedByState(rule, i, alternativePlansStartingWithRule);
			}
			else {
				explain("Rule does not apply because ", contingentGoalNegatedByState, " is already negated");
			}
			
	}));
}

	private void considerRuleWithAtLeastEffectivelyStaticAntecedentsAllSatisfiedAndNoContingentGoalsNegatedByState(R rule, int i, List<Plan> alternativePlansStartingWithRule) {
		explanationBlock("Considering rule with all static antecedents satisfied and no contingent goals negated by state: ", rule, code(() -> {

			ContingentGoal contingentGoalNotYetDefined = contingentGoalNotYetDefined(rule);
			if (contingentGoalNotYetDefined != null) {
				considerBothPossibilitiesForContingentGoal(rule, i, contingentGoalNotYetDefined, alternativePlansStartingWithRule);
			}
			else {
				findPlansStartingWithRule(rule, i, alternativePlansStartingWithRule);
			}

		}));
	}

	private ContingentGoal contingentGoalNegatedByState(R rule) {
		ContingentGoal effectivelyContingentAntecendentNegatedByState = 
				(ContingentGoal) 
				getFirst(
						rule.getAntecendents(), 
						a -> 
						!isEffectivelyStatic(a)
						&& 
						state.negatedEffectivelyContingentGoals.contains(a));
		return effectivelyContingentAntecendentNegatedByState;
	}

	private void considerBothPossibilitiesForContingentGoal(R rule, int i, ContingentGoal contingentGoal, List<Plan> alternativePlansStartingWithRule) {
		explanationBlock("Considering both possibilities for contingent ", contingentGoal, code(() -> {

			Plan thenBranch = considerContingentGoalSatisfied(rule, i, contingentGoal);
			Plan elseBranch = considerContingentGoalNegated(rule, contingentGoal);
			Plan result;
			if ( ! thenBranch.isFailedPlan() && ! elseBranch.isFailedPlan()) {
				result = new ContingentPlan(contingentGoal, thenBranch, elseBranch);
				alternativePlansStartingWithRule.add(result);
			}
			// both branches must be guaranteed to be successful since we cannot tell in advance which one will be used

		}));
	}
	
	@SuppressWarnings("unchecked")
	private Plan considerContingentGoalSatisfied(R rule, int i, ContingentGoal contingentGoal) {
		return explanationBlock("Assuming contingent ", contingentGoal, " is true", code(() -> {

			Set<G> satisfiedGoalsBeforeRule = new LinkedHashSet<>(state.satisfiedGoals);
			state.satisfiedGoals.add((G) contingentGoal);
			Plan thenBranch = planIfThereIsAtLeastOneUnsatisfiedRequiredGoalStartingAtRuleWithIndexWhichIsKnownToHaveAtLeastAllEffectivelyStaticAntecedentsAllSatisfied(rule, i);
			state.satisfiedGoals = satisfiedGoalsBeforeRule;
			return thenBranch;

		}), "Assuming contingent: ", contingentGoal, " is true, plan is ", RESULT);
	}

	@SuppressWarnings("unchecked")
	private Plan considerContingentGoalNegated(R rule, ContingentGoal contingentGoal) {
		return explanationBlock("Assuming contingent ", contingentGoal, " is false", code(() -> {

			Set<G> negatedGoalsBeforeRule = new LinkedHashSet<>(state.negatedEffectivelyContingentGoals);
			state.negatedEffectivelyContingentGoals.add((G) contingentGoal);
			Plan elseBranch = planIfThereIsAtLeastOneUnsatisfiedRequiredGoal();
			state.negatedEffectivelyContingentGoals = negatedGoalsBeforeRule;
			return elseBranch;

		}), "Assuming contingent: ", contingentGoal, " is false, plan is ", RESULT);
	}

	private void findPlansStartingWithRule(R rule, int i, List<Plan> alternativeRulePlans) {
		Plan planStartingWithRule = tryRule(rule, i);
		if ( ! planStartingWithRule.isFailedPlan()) {
			alternativeRulePlans.add(planStartingWithRule);
		}
	}

	private Plan tryRule(R rule, int i) {
		
		return explanationBlock("Finding plans starting with ", rule, code(() -> {

			Set<G> howThingsUsedToBe = applyRule(rule, i);

			Plan planStartingWithRule = planGivenWeJustAppliedThisRule(rule);

			revertRule(i, howThingsUsedToBe);

			return planStartingWithRule;

		}), "Plan starting with rule and solving remaining goals: ", RESULT);
	}

	private Plan planGivenWeJustAppliedThisRule(R rule) {
		Plan remainingPlan = plan();
		if (remainingPlan.isFailedPlan()) {
			return OrPlan.or();
		}
		else {
			return and(rule, remainingPlan);
		}
	}

	private ContingentGoal contingentGoalNotYetDefined(R rule) {
		return explanationBlock("Looking for contingent goal not yet defined in rule ", rule, code(() -> {
			
			ContingentGoal contingentGoalNotYetSatisfied = 
					(ContingentGoal) 
					getFirst(
							rule.getAntecendents(), 
							a -> isContingentGoalNotDefinedByPlanningState(a));
			return contingentGoalNotYetSatisfied;
			
		}), "Contingent goal not yet defined: ", RESULT);
	}

	private boolean isContingentGoalNotDefinedByPlanningState(G a) {
		Wrapper<Boolean> result = new Wrapper<>(true);
		return explanationBlock("Checking if antecendent is contingent goal not defined by planning state: ", a, code(() -> {
			
			explain("Is not effectively static       : ", !isEffectivelyStatic(a));
			explain("Is not in satisfied goals       : ", !state.satisfiedGoals.contains(a));
			explain("Is not negated by planning state: ", !state.negatedEffectivelyContingentGoals.contains(a));
			
			result.value =
					!isEffectivelyStatic(a) 
					&& 
					!state.satisfiedGoals.contains(a)
					&& 
					!state.negatedEffectivelyContingentGoals.contains(a);
			return result.value;

		}), "Antecedent ", (result.value? "IS" : "IS NOT"), " contingent goal not defined by planning state");
	}

	private boolean isEffectivelyStatic(G a) {
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
		return explanationBlock("Applying rule ", rule, code(() -> {
	
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
		return state.satisfiedGoals.containsAll(state.allRequiredGoals);
	}

}
