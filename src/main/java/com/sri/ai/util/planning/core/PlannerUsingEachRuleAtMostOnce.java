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
import static com.sri.ai.util.planning.core.OrPlan.orPlan;
import static com.sri.ai.util.planning.core.SequentialPlan.and;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.sri.ai.util.explanation.logging.api.ThreadExplanationLogger;
import com.sri.ai.util.planning.api.ContingentGoal;
import com.sri.ai.util.planning.api.Goal;
import com.sri.ai.util.planning.api.Plan;
import com.sri.ai.util.planning.api.Planner;
import com.sri.ai.util.planning.api.PlanningState;
import com.sri.ai.util.planning.api.Rule;
import com.sri.ai.util.planning.util.PlanHierarchicalExplanation;

/**
 * A planner based on a set of rules that, given a set of <i>antecedent</i> goals,
 * achieves a set of <i>consequent</i> goals.
 * 
 * The planner creates a {@link Plan} in which each rule is used at most once in order
 * to guarantee achievement of a given set of <i>required</i> goals.
 * <p>
 * The antecedents of a rule can be of two different types:
 * <p>
 * A <i>contingent</i> goal (implementing interface {@link ContingentGoal})
 * may become true as a result to a change in the execution-time state
 * even if it were not in the consequents of any applied rule.
 * <p>
 * A goal is <i>static</i> (not implementing interface {@link ContingentGoal})
 * if its truth value can be perfectly known during static planning time,
 * that is, if it becomes satisfied only by action of a rule with it in its consequents.
 * <p>
 * The planner makes the assumption that once a contingent goal is either satisfied or fails during execution,
 * it stays that way permanently even after other rules are applied.
 * <p>
 * Here's a formalization:
 * 
 * <pre>
 * A goal is a logic proposition.
 * 
 * A state is a truth assignment to all goals.
 *
 * A plan is one of the following:
 * - a rule r with a set of antecedents ant(r) and a set of consequents cons(r)
 * - a contingent plan 'if C then p_t else p_e' with a condition C (a formula on goals) and a then-plan p_t and an else-plan p_e. 
 * - a sequence of plans p_1, ..., p_m (m may be 0, in which case we denote the sequence Epsilon)
 * - a disjunction of plans or(p_1, ..., p_n); if n = 0, the plan is called a "failed plan".
 * 
 * A rule is an atomic plan whereas the other types of plans are compound plans.
 * 
 * The application r x s of a rule r to a state s is:
 * - s union cons(r), if ant(r) is satisfied by s
 * - s, otherwise
 * 
 * A state space is a set of states. A truth assignment to a sub-set of goals defines the state space of all states consistent with it.
 * 
 * The application r x ss of a rule r to a state space ss is defined as the set { r x s : s in ss }.
 * 
 * The application p x ss of a compound plan p to a state space ss is defined as:
 * - { p_t x s : s in ss satisfies C } union { p_e x s : s in ss does not satisfy C }, if p is 'if C then p_t else p_e' 
 * - ss, if p is Epsilon
 * - p_n x ((p_1, ..., p_n-1) x ss), if p is a sequence p_1, ..., p_m for m > 0.
 * - Disjunction_i p_i x ss, if p is or(p_1, ..., p_n).
 * 
 * A rule sequence Seq is valid for a set of required goals G, and a state s
 * if and only if (Seq x s) implies G.
 * A valid rule sequence Seq is minimal if no subsequence of Seq is valid.
 * Seq is valid (minimal) for a state space ss if it is valid (minimal) for each s in ss.
 * (This implies that no rule appears more than once in the sequence, and
 * that no rule is applied without its antecedents being satisfied by the current state.)
 * 
 * The conditioning of a plan p given a state s is the result of replacing each contingent plan in p
 * by its then- or else-plan depending on whether s instantiates its condition.
 * A conditioned plan therefore does not contain contingent plans and is a disjunction/sequence tree plan.
 * 
 * A plan is valid for a state s if the set of all paths in its conditioning by s
 * is equal to the set of minimal valid paths for s.
 * 
 * A plan is valid for a state space ss if it is valid for all s in ss.
 * 
 * Given a partial truth assignment H, the state space induced by H, ss(H), is the set of states consistent with H.
 * 
 * ------------
 * 
 * The following algorithm receives a set of rules R, a set of required goals G,
 * a predicate isContingent on goals, and a state space ss(H) induced by a partial truth assignment (true or false for a subset of goals) H
 * and returns a valid plan using rules in R for ss(H) and G,
 * in which contingent plan conditions are always contingent goals (per isContingent).
 * 
 * Plan(R, H) (isContingent and G are fixed)
 *     Epsilon, if H implies G
 *     Disjunction_{r in R : cons(r) not satisfied by H (i.e., it's useful)} ConsiderAvailableAndUsefulRule(r, R \ {r}, H) otherwise
 *     
 *  
 * ConsiderAvailableAndUsefulRule(r, R, H)
 *     if not staticAntecendentsAllSatisfied(r, H) or thereIsContigentAntecedentNegatedBy(r, H)
 *        return or() (failed plan)
 *     else if there is C : isContigentAntecedentNotSatisfiedByH(r, C, H)
 *        return if C then ConsiderAvailableAndUsefulRule(r, H union C) else ConsiderAvailableAndUsefulRule(r, H union not C)
 *     else
 *        return sequence(r, Plan(R, H union cons(r)))
 * 
 * staticAntecendentsAllSatisfied(r, H) = for all a in ant(r) : !isContingent(a) => a is satisfied by H
 * thereIsContigentAntecedentNegatedBy(r, H) = there is a in ant(r) : isContingent(a) and H implies negation of a
 * isContigentAntecedentNotSatisfiedByH(r, C, H) = C in ant(r) and isContingent(C) and C isn't satisfied by H
 * sequence(r, p) = if p is failed return failed else return sequence r, p
 * </pre>
 * 
 * @author braz
 *
 * @param <R> the type of rules used
 * @param <G> the type of goals used
 */
public class PlannerUsingEachRuleAtMostOnce<R extends Rule<G>, G extends Goal> implements Planner<R, G> {
	
	public static <R1 extends Rule<G1>, G1 extends Goal> 
	Plan 
	planUsingEachRuleAtMostOnce(
			Collection<? extends G1> allRequiredGoals, 
			Collection<? extends G1> satisfiedGoals,
			Collection<? extends G1> negatedContingentGoals,
			Collection<? extends R1> rules) {
		
		return planUsingEachRuleAtMostOnceAndIfSuccessfulApplyingSequelPlan(
				allRequiredGoals, 
				satisfiedGoals, 
				negatedContingentGoals,
				rules, 
				new PlannerThatDoesNothing<R1, G1>());
	}

	public static <R1 extends Rule<G1>, G1 extends Goal> 
	Plan 
	planUsingEachRuleAtMostOnceAndIfSuccessfulApplyingSequelPlan(
			Collection<? extends G1> allRequiredGoals, 
			Collection<? extends G1> satisfiedGoals, 
			Collection<? extends G1> negatedContingentGoals,
			Collection<? extends R1> rules, 
			Planner<R1, G1> sequel) {
		
		PlannerUsingEachRuleAtMostOnce planner = new PlannerUsingEachRuleAtMostOnce<R1, G1>(allRequiredGoals, satisfiedGoals, negatedContingentGoals, rules, sequel);
		Plan plan = planner.plan();
		return plan;
	}

	private PlanningState<R, G> state;
	
	private Planner<R, G> sequel;
	
	public PlannerUsingEachRuleAtMostOnce(Collection<? extends G> allRequiredGoals, Collection<? extends G> satisfiedGoals, Collection<? extends G> negatedContingentGoals, Collection<? extends R> rules) {
		this(allRequiredGoals, satisfiedGoals, negatedContingentGoals, rules, new PlannerThatDoesNothing<R, G>());
	}
	
	public PlannerUsingEachRuleAtMostOnce(Collection<? extends G> allRequiredGoals, Collection<? extends G> satisfiedGoals, Collection<? extends G> negatedContingentGoals, Collection<? extends R> rules, Planner<R, G> sequel) {
		ArrayList<? extends R> arrayListOfSamplingRules = (rules instanceof ArrayList)? (ArrayList<? extends R>) rules : arrayListFrom(rules);
		this.state = new PlanningState<>(allRequiredGoals, satisfiedGoals, negatedContingentGoals, arrayListOfSamplingRules);
		this.sequel = sequel;
		myAssert(forAll(allRequiredGoals, g -> isStatic(g)), () -> "Required goals cannot contain contingent goals.");
		myAssert(forAll(rules, r -> forAll(r.getConsequents(), c -> isStatic(c))), () -> "Rules consequents cannot contain contingent goals.");
	}

	@Override
	public Plan plan(PlanningState<R, G> state) {
		this.state = state;
		Plan result = plan();
		return result;
	}
	
	/////////////////////// MAIN ALGORITHM - START 
	
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
			
			List<Plan> subPlans = list();
			for (int i = 0; i != state.rules.size(); i++) {
				if (ruleIsEligible(i)) {
					Plan subPlan = planContingentlyStartingWithRuleOfIndex(i);
					subPlans.add(subPlan);
				}
			}
			
			Plan result = orPlan(subPlans);

			explainResultingPlan(result);

			return result;

		}));
	}

	private boolean ruleIsEligible(int i) {
		return explanationBlock("Deciding if ", state.rules.get(i), " is eligible", code(() -> {
			if (state.ruleIsAvailable.get(i)) {
				explain("Rule has not yet been used");
				R rule = state.rules.get(i);
				if (ruleIsUseful(rule)) {
					Goal unsatisfiedStaticAntecedent = findUnsatisfiedStaticAntecedent(rule);
					if (unsatisfiedStaticAntecedent == null) {
						ContingentGoal contingentGoalNegatedByState = findContingentGoalNegatedByState(rule);
						if (contingentGoalNegatedByState == null) {
							return true;
						}
						else {
							explain("Rule has contingent antecedent already assumed to be false: " + contingentGoalNegatedByState);
							return false;
						}
					}
					else {
						explain("Rule is not satisfied (antecedent " + unsatisfiedStaticAntecedent + " is not satisfied by current planning state)");
						return false;
					}
				}
				else {
					explain("Rule is not useful (all consequents already satisfied)");
					return false;
				}
			}
			else {
				explain("Rule has been used already");
				return false;
			}
		}), "Rule is eligible: ", RESULT);
	}

	private Plan planContingentlyStartingWithRuleOfIndex(int i) {
		return explanationBlock("Starting plan with eligible rule ", state.rules.get(i), code(() -> {

			Plan result;

			R rule = state.rules.get(i);

			ContingentGoal contingentGoalNotYetDefined = findContingentGoalNotYetDefined(rule);
			if (contingentGoalNotYetDefined != null) {
				result = planStartingWithRuleOfIndexContingentlyOnUndefinedContingentAntecedent(rule, i, contingentGoalNotYetDefined);
			}
			else {
				result = planStartingWithRuleOfIndex(rule, i);
			}

			explainResultingPlan(result);

			return result;

		}));
	}

	private Plan planStartingWithRuleOfIndexContingentlyOnUndefinedContingentAntecedent(R rule, int i, ContingentGoal contingentGoal) {
		return explanationBlock("Considering both possibilities for contingent ", contingentGoal, code(() -> {
	
			Plan thenBranch = planStartingWithRuleOfIndexAssumingContingentAntecedentIsTrue(rule, i, contingentGoal);
			Plan elseBranch = planStartingWithRuleOfIndexAssumingContingentAntecedentIsFalse(rule, contingentGoal);
			Plan result;
			if ( ! thenBranch.isFailedPlan() && ! elseBranch.isFailedPlan()) {
				result = new ContingentPlan(contingentGoal, thenBranch, elseBranch);
			}
			else {
				// both branches must be guaranteed to be successful since we cannot tell in advance which one will be used
				result = OrPlan.orPlan();
			}
	
			return result;
			
		}));
	}

	@SuppressWarnings("unchecked")
	private Plan planStartingWithRuleOfIndexAssumingContingentAntecedentIsTrue(R rule, int i, ContingentGoal contingentGoal) {
		return explanationBlock("Assuming contingent ", contingentGoal, " is true", code(() -> {
	
			Set<G> satisfiedGoalsBeforeRule = new LinkedHashSet<>(state.satisfiedGoals);
			state.satisfiedGoals.add((G) contingentGoal);
			Plan thenBranch = planContingentlyStartingWithRuleOfIndex(i);
			state.satisfiedGoals = satisfiedGoalsBeforeRule;
			return thenBranch;
	
		}), "Assuming contingent: ", contingentGoal, " is true, plan is ", RESULT);
	}

	@SuppressWarnings("unchecked")
	private Plan planStartingWithRuleOfIndexAssumingContingentAntecedentIsFalse(R rule, ContingentGoal contingentGoal) {
		return explanationBlock("Assuming contingent ", contingentGoal, " is false", code(() -> {
	
			Set<G> negatedGoalsBeforeRule = new LinkedHashSet<>(state.negatedContingentGoals);
			state.negatedContingentGoals.add((G) contingentGoal);
			Plan elseBranch = planIfThereIsAtLeastOneUnsatisfiedRequiredGoal(); // we know there are unsatisfied goals because there was before considering this rule, and it has not been applied, so nothing changed
			state.negatedContingentGoals = negatedGoalsBeforeRule;
			return elseBranch;
	
		}), "Assuming contingent: ", contingentGoal, " is false, plan is ", RESULT);
	}

	private Plan planStartingWithRuleOfIndex(R rule, int i) {
		
		return explanationBlock("Finding plans starting with ", rule, code(() -> {
	
			Set<G> satisfiedGoalsBeforeRuleApplication = applyRuleAndReturnSatisfiedGoalsAsTheyWereBeforeItWasApplied(rule, i);
			
			Plan remainingPlan = plan();
	
			Plan planStartingWithRule = makeSequence(rule, remainingPlan);
	
			revertRule(i, satisfiedGoalsBeforeRuleApplication);
	
			return planStartingWithRule;
	
		}), "Plan starting with rule and solving remaining goals: ", RESULT);
	}

	/////////////////////// MAIN ALGORITHM - END
	
	/////////////////////// RULE APPLICATION - START
	
	private Set<G> applyRuleAndReturnSatisfiedGoalsAsTheyWereBeforeItWasApplied(R rule, int i) {
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

	/////////////////////// RULE APPLICATION - END
	
	/////////////////////// AUXILIARY METHODS - START
	
	private Goal findUnsatisfiedStaticAntecedent(R rule) {
		Goal unsatisfiedStaticAntecendent = getFirst(rule.getAntecendents(), a -> isStaticAndUnsatisfied(a));
		return unsatisfiedStaticAntecendent;
	}

	private boolean isStaticAndUnsatisfied(G goal) {
		boolean result = 
				isStatic(goal)
				&& 
				!state.satisfiedGoals.contains(goal);
		return result;
	}

	private ContingentGoal findContingentGoalNegatedByState(R rule) {
		ContingentGoal contingentAntecendentNegatedByState = 
				(ContingentGoal) 
				getFirst(
						rule.getAntecendents(), 
						a -> 
						!isStatic(a)
						&& 
						state.negatedContingentGoals.contains(a));
		return contingentAntecendentNegatedByState;
	}

	private ContingentGoal findContingentGoalNotYetDefined(R rule) {
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
		return explanationBlock("Checking if antecendent is contingent goal not defined by planning state: ", a, code(() -> {
			
			explain("Is not static                   : ", !isStatic(a));
			explain("Is not in satisfied goals       : ", !state.satisfiedGoals.contains(a));
			explain("Is not negated by planning state: ", !state.negatedContingentGoals.contains(a));
			
			boolean result =
					!isStatic(a) 
					&& 
					!state.satisfiedGoals.contains(a)
					&& 
					!state.negatedContingentGoals.contains(a);
			
			return result;

		}), "Antecedent ", a, " is contingent goal not defined by planning state: ", RESULT);
	}

	private boolean isStatic(G a) {
		boolean result = !(a instanceof ContingentGoal);
		return result;
	}

	private boolean ruleIsUseful(R rule) {
		boolean result = thereExists(rule.getConsequents(), c -> !state.satisfiedGoals.contains(c));
		return result;
	}

	private boolean allRequiredGoalsAreSatisfied() {
		return state.satisfiedGoals.containsAll(state.allRequiredGoals);
	}

	private Plan makeSequence(R firstRule, Plan remainingPlan) {
		if (remainingPlan.isFailedPlan()) {
			return OrPlan.orPlan();
		}
		else {
			return and(firstRule, remainingPlan);
		}
	}

	/////////////////////// AUXILIARY METHODS - END
	
	/////////////////////// EXPLANATION METHODS - START
	
	private static void explainResultingPlan(Plan plan) {
		ThreadExplanationLogger.explain("Resulting plan:");
		PlanHierarchicalExplanation.explain(plan);
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

	/////////////////////// EXPLANATION METHODS - END
	
}
