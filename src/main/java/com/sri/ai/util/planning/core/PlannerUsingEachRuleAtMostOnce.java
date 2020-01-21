package com.sri.ai.util.planning.core;

import static com.sri.ai.util.Util.arrayListFrom;
import static com.sri.ai.util.Util.collectIntegers;
import static com.sri.ai.util.Util.collectThoseWhoseIndexSatisfyArrayList;
import static com.sri.ai.util.Util.count;
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
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.sri.ai.util.Enclosing;
import com.sri.ai.util.Util;
import com.sri.ai.util.collect.StackedLinkedList;
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
 *     PlanIfThereAreUnsatisfiedRequiredGoals(R, H), otherwise
 *     
 * PlanIfThereAreUnsatisfiedRequiredGoals(R, H)
 *     Disjunction_{r in R : isEligibleRule(r, H)} PlanContingentlyStartingWithRuleOfIndex(r, R, H)
 *  
 * PlanContingentlyStartingWithRuleOfIndex(r, R, H)
 *     if there is C : isContigentAntecedentNotYetDefinedByH(r, C, H)
 *        return if C then PlanContingentlyStartingWithRuleOfIndex(r, R, H union C) else PlanIfThereAreUnsatisfiedRequiredGoals(R \ {r}, H union not C)
 *     else
 *        return sequence(r, Plan(R, H union cons(r)))
 * 
 * isEligibleRule(r, H) = isUseful(r, H) and staticAntecendentsAllSatisfied(r, H) and not thereIsContigentAntecedentNegatedBy(r, H) 
 * staticAntecendentsAllSatisfied(r, H) = for all a in ant(r) : !isContingent(a) => a is satisfied by H
 * thereIsContigentAntecedentNegatedBy(r, H) = there is a in ant(r) : isContingent(a) and H implies negation of a
 * isContigentAntecedentNotYetDefinedByH(r, C, H) = C in ant(r) and isContingent(C) and C isn't satisfied by H
 * sequence(r, p) = if p is failed return failed else return sequence r, p
 * 
 * ======================
 * 
 * Theorem: In PlanIfThereAreUnsatisfiedRequiredGoals(R, H),
 * if there is r in R : isEligibleRule(r, H) and PlanContingentlyStartingWithRuleOfIndex(r, R, H) is the failed plan,
 * then for all r in R : isEligibleRule(r, H) => PlanContingentlyStartingWithRuleOfIndex(r, R, H) is the failed plan.
 * 
 * Proof: Assume there are r1, r2 in R such that isEligibleRule(r1, H) and isEligibleRule(r2, H),
 * but PlanContingentlyStartingWithRuleOfIndex(r1, R, H) is the failed plan
 * while PlanContingentlyStartingWithRuleOfIndex(r2, R, H) is a successful plan.
 * 
 * If PlanContingentlyStartingWithRuleOfIndex(r2, R, H) is a successful plan,
 * it may or may not involve r1.
 * If it does not involve r1, then attempting r1 first would not prevent later finding a successful PlanContingentlyStartingWithRuleOfIndex(r2, R, H)),
 * because attempting r1 in advance does not prevent any other rules from being used
 * since the only thing it does is satisfying some goals and we assume all rule antecedents are positive.
 * Even if r1 has contingent antecedents, attempting it first does limit the planning,
 * because in the branches that r1 is applied, again the explanation above applies, and it does not limit planning,
 * and in the branches in which r1 is not applied (because of negated contingent antecedents),
 * the planner is still able to find PlanContingentlyStartingWithRuleOfIndex(r2, R, H).
 * 
 * If PlanContingentlyStartingWithRuleOfIndex(r2, R, H) does involve r1,
 * we can simply move it first in the plan and again it would be a successful plan
 * because now at every step we would have the same state spaces with extra goals satisfied (the goals of r1)
 * in the branches in which it applies (that is, its contingent antecedents were satisfied)
 * and in the branches in which it does not apply the state space would be identical.
 *
 * Therefore, the initial assumption in the proof is false by reductio ad absurdum and the theorem is proven.
 * 
 * </pre>
 * 
 * @author braz
 *
 * @param <R> the type of rules used
 * @param <G> the type of goals used
 */
public class PlannerUsingEachRuleAtMostOnce<R extends Rule<G>, G extends Goal> implements Planner<R, G> {
	
	private static final int MAX_LEAVES = 10;

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
		Plan plan = planner.topLevelPlan();
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
		Plan result = topLevelPlan();
		return result;
	}
	
	/////////////////////// MAIN ALGORITHM - START 
	
	int visitingOrder = 0;
	int leafOrder = 0;
	
	public Plan topLevelPlan() {
		
//		Util.print(".");
		
		// println("PlannerUsingEachRuleAtMostOnce: Planning from " + state.rules.size() + " rules");
//		state.rules.stream().forEach(r -> println(r));

//		println("PlannerUsingEachRuleAtMostOnce: Required goals : " + state.allRequiredGoals);
//		println("PlannerUsingEachRuleAtMostOnce: Satisfied goals: " + state.satisfiedGoals);
		
		if (state.rules.size() > 20) {
			// println("Maximum number of alternative plans is huge");
		}
		else {
			// println("Maximum number of alternative plans is: " + CombinatoricsUtils.factorial(state.rules.size()));
		}
		
		Plan result = plan(MAX_LEAVES, 0, Util.list());
		
		// println("Managed to complete planning");

		return result;
	}
	
	static private class Node {
		public String info;
		public Node(String info) {
			this.info = info;
		}
		@Override
		public String toString() {
			return info;
		}
	}
	
	/**
	 * Returns a plan that is guaranteed to achieve all goals given available rules,
	 * or an empty {@link OrPlan} if there is no such plan.
	 * @param maxLeaves determines the maximum number of leaf plans allowed (modulo contingent forking)
	 * @param level the current level of recursion
	 * @return
	 */
	public Plan plan(int maxLeaves, int level, List<Node> path) {
		return explanationBlock(
				"Planning for ", lazy(() -> subtract(state.allRequiredGoals, state.satisfiedGoals)), 
				" with still unused rules ", lazy(() -> collectThoseWhoseIndexSatisfyArrayList(state.rules, state.ruleIsAvailable)),
				", out of total rule set ", lazy(() -> state.rules),
				", required goals ", state.allRequiredGoals,
				", satisfied goals ", state.satisfiedGoals,
				code(() -> {

					Plan result;
					
					explain("Planner: level " + level + ", # available rules: " + numberOfAvailableRules() + ", # satisfied req. goals: " + numberOfSatisfiedRequiredGoals());
					// println("Planner: level " + level + ", # available rules: " + numberOfAvailableRules() + ", # satisfied req. goals: " + numberOfSatisfiedRequiredGoals());
					// println(join(path));
					
					if (allRequiredGoalsAreSatisfied()) {
						explain("All goals are satisfied, returning sequel plan provided by sequel planner " + sequel.getClass());
						result = sequel.plan(state);
						indentedPrintln(level, "Leaf " + leafOrder++);
					}
					else {
						result = planIfThereIsAtLeastOneUnsatisfiedRequiredGoal(maxLeaves, level, path);
					}

					explainResultingPlan(result);

					return result;

				}));
	}

	private String numberOfAvailableRules() {
		return Integer.toString(count(state.ruleIsAvailable, b -> b));
	}

	private String numberOfSatisfiedRequiredGoals() {
		return Integer.toString(subtract(state.allRequiredGoals, state.satisfiedGoals).size());
	}

	private Plan planIfThereIsAtLeastOneUnsatisfiedRequiredGoal(int maxLeaves, int level, List<Node> path) {
		return explanationBlock("There are still unsatisfied required goals", code(() -> {
			
			explainUnsatisfiedRequiredGoals();

			Plan result;
			
			indentedPrintln(level, "Max leaves: " + maxLeaves);
			
			List<Integer> selectedEligibleRulesToUse = selectEligibleRulesToUse(maxLeaves);
			// the above method call ensures the post-condition selectedEligibleRulesToUse.size() <= maxLeaves
			
			if (selectedEligibleRulesToUse.isEmpty()) {
				explain("No more eligible rules available and not all required goals are satisfied, so planning has failed.");
				result = failedPlan();
			}
			else {
				// Note that the following method requires selectedEligibleRulesToUse.size() <= maxLeaves, which was ensured by the call to selectedEligibleRulesToUse.
				result = planIfThereAreEligibleRules(selectedEligibleRulesToUse, maxLeaves, level, path);
			}

			return result;

		}));
	}

	private List<Integer> selectEligibleRulesToUse(int maxLeaves) {
		List<Integer> sortedEligibleRuleIndices = makeSortedEligibleRuleIndicesToUse();
		List<Integer> selectedEligibleRuleIndicesToUse = selectEligibleRulesToUse(sortedEligibleRuleIndices, maxLeaves);
		return selectedEligibleRuleIndicesToUse;
	}

	private List<Integer> makeSortedEligibleRuleIndicesToUse() {
		List<Integer> eligibleRuleIndices = collectIntegers(state.rules.size(), this::isIndexOfEligibleRule);
		eligibleRuleIndices.sort(descendingRuleIndexEstimatedSuccessWeightComparator);
		return eligibleRuleIndices;
	}

	private boolean isIndexOfEligibleRule(int i) {
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

	private Plan planIfThereAreEligibleRules(List<Integer> selectedEligibleRulesToUse, int maxLeaves, int level, List<Node> path) {
		
		myAssert(selectedEligibleRulesToUse.size() <= maxLeaves, () -> (new Enclosing(){}).methodName() + " requires the number of selected eligible rules to be at most the maximum number of leaves, but these are " + selectedEligibleRulesToUse.size() + " and " + maxLeaves + " respectively.");
		
		Plan result;
		
		// We must compute the limit on leaves for sub-plans by dividing the current maximum of leaves by the number of selected eligible rules.
		// However, if there is a remainder, it must also be distributed. This is explained later in the method.
		int baseSubMaxLeaves = maxLeaves / selectedEligibleRulesToUse.size();
		int remainder = maxLeaves - baseSubMaxLeaves * selectedEligibleRulesToUse.size();
		// Note that baseSubMaxLeaves is never 0 because selectedEligibleRulesToUse.size() <= maxLeaves.
		
		List<Plan> subPlans = list();
		int optionNumber = 1;
		for (int i : selectedEligibleRulesToUse) {
			
			indentedPrintln(
					level, 
					"Node " + visitingOrder++ + 
					": option " + optionNumber + 
					" out of " + selectedEligibleRulesToUse.size() + 
					" at level " + level);
			
			// We distribute the remainder by adding 1 to the sub maximum of leaves to the first <remainder> sub plans.
			int subMaxLeaves = baseSubMaxLeaves + (optionNumber <= remainder? 1 : 0);
			
			List<Node> newPath = new StackedLinkedList<>(new Node(optionNumber + "/" + selectedEligibleRulesToUse.size()), path);
			
			Plan subPlan = planContingentlyStartingWithRuleOfIndex(i, subMaxLeaves, level, newPath);
			if (subPlan.isFailedPlan()) {
				break; // see corresponding theorem in documentation
			}
			else {
				subPlans.add(subPlan);
			}
			optionNumber++;
		}

		result = orPlan(subPlans);

		explainResultingPlan(result);
		return result;
	}

	private List<Integer> selectEligibleRulesToUse(List<Integer> sortedEligibleRuleIndices, int maxLeaves) {
		return explanationBlock("Selecting from eligible rules", code(() -> {

			List<Integer> result;

			List<Integer> singletonListWithFirstRuleIfDeterministicOrNull = 
					singletonListWithFirstRuleIfDeterministicOrNull(sortedEligibleRuleIndices);

			if (singletonListWithFirstRuleIfDeterministicOrNull != null) {
				explain("First rule is deterministic, so selecting singleton with it: " + state.rules.get(singletonListWithFirstRuleIfDeterministicOrNull.get(0)));
				result = singletonListWithFirstRuleIfDeterministicOrNull;
			}
			else {
				result = selectUpToMaxLeavesSortedEligibleRuleIndices(sortedEligibleRuleIndices, maxLeaves);
			}

			return result;

		}));
	}
	
	private List<Integer> singletonListWithFirstRuleIfDeterministicOrNull(List<Integer> sortedEligibleRuleIndices) {
		List<Integer> result;
		
		if ( ! sortedEligibleRuleIndices.isEmpty()) {
			var firstRuleIndex = getFirst(sortedEligibleRuleIndices);
			R firstRule = state.rules.get(firstRuleIndex);
			if (firstRule.isDeterministic()) {
				result = list(firstRuleIndex);
			}
			else {
				result = null;
			}
		}
		else {
			result = null;
		}
		
		return result;
	}

	private
	List<Integer>
	selectUpToMaxLeavesSortedEligibleRuleIndices(List<Integer> sortedEligibleRuleIndices, int maxLeaves) {
		return explanationBlock("Selecting maximum of ", maxLeaves, " from eligible rules", code(() -> {
			if (sortedEligibleRuleIndices.size() > maxLeaves) {
				return sortedEligibleRuleIndices.subList(0, maxLeaves);
			}
			else {
				return sortedEligibleRuleIndices;
			}
		}));
	}

	private Plan planContingentlyStartingWithRuleOfIndex(int i, int maxLeaves, int level, List<Node> path) {
		return explanationBlock("Starting plan with eligible rule ", state.rules.get(i), code(() -> {

			Plan result;

			R rule = state.rules.get(i);

			ContingentGoal contingentGoalNotYetDefined = findContingentGoalNotYetDefined(rule);
			if (contingentGoalNotYetDefined != null) {
				result = planStartingWithRuleOfIndexContingentlyOnUndefinedContingentAntecedent(rule, i, contingentGoalNotYetDefined, maxLeaves, level, path);
			}
			else {
				result = planStartingWithRuleOfIndex(rule, i, maxLeaves, level, path);
			}

			explainResultingPlan(result);

			return result;

		}));
	}

	private Plan planStartingWithRuleOfIndexContingentlyOnUndefinedContingentAntecedent(R rule, int i, ContingentGoal contingentGoal, int maxLeaves, int level, List<Node> path) {
		return explanationBlock("Considering both possibilities for contingent ", contingentGoal, code(() -> {

			Plan thenBranch = planStartingWithRuleOfIndexAssumingContingentAntecedentIsTrue(rule, i, contingentGoal, maxLeaves, level, path);
			Plan elseBranch = planStartingWithRuleOfIndexAssumingContingentAntecedentIsFalse(rule, i, contingentGoal, maxLeaves, level, path);
			Plan result;
			if ( thenBranch.isFailedPlan() && elseBranch.isFailedPlan()) {
				result = failedPlan();
			}
			else {
				result = new ContingentPlan(contingentGoal, thenBranch, elseBranch);
			}
	
			return result;
			
		}));
	}

	@SuppressWarnings("unchecked")
	private Plan planStartingWithRuleOfIndexAssumingContingentAntecedentIsTrue(R rule, int i, ContingentGoal contingentGoal, int maxLeaves, int level, List<Node> path) {
		return explanationBlock("Assuming contingent ", contingentGoal, " is true", code(() -> {
	
			Set<G> satisfiedGoalsBeforeRule = new LinkedHashSet<>(state.satisfiedGoals);
			state.satisfiedGoals.add((G) contingentGoal);
			List<Node> newPath = new StackedLinkedList<>(new Node("g. " + i + " then"), path);
			Plan thenBranch = planContingentlyStartingWithRuleOfIndex(i, maxLeaves, level, newPath);
			state.satisfiedGoals = satisfiedGoalsBeforeRule;
			return thenBranch;
	
		}), "Assuming contingent: ", contingentGoal, " is true, plan is ", RESULT);
	}

	@SuppressWarnings("unchecked")
	private Plan planStartingWithRuleOfIndexAssumingContingentAntecedentIsFalse(R rule, int i, ContingentGoal contingentGoal, int maxLeaves, int level, List<Node> path) {
		return explanationBlock("Assuming contingent ", contingentGoal, " is false", code(() -> {
	
			Set<G> negatedGoalsBeforeRule = new LinkedHashSet<>(state.negatedContingentGoals);
			state.negatedContingentGoals.add((G) contingentGoal);
			
			List<Node> newPath = new StackedLinkedList<>(new Node("g. " + i + " else"), path);
			Plan elseBranch = planIfThereIsAtLeastOneUnsatisfiedRequiredGoal(maxLeaves, level, newPath);
			// we know there are unsatisfied goals because there was before considering this rule, and it has not been applied, so nothing changed
			// Previously, I was passing level + 1 as an argument because of a new recursion level
			// but later I realized that even though there is recursion, this is not a new *planning level* -- we are still trying to decide
			// whether to use the same rule or not.
			// This is important if we want to use level in an inductive termination proof,
			// for example saying that each planning level always contains one less available rule.
			
			state.negatedContingentGoals = negatedGoalsBeforeRule;
			return elseBranch;
	
		}), "Assuming contingent: ", contingentGoal, " is false, plan is ", RESULT);
	}

	private Plan planStartingWithRuleOfIndex(R rule, int i, int maxLeaves, int level, List<Node> path) {
		
		return explanationBlock("Finding plans starting with ", rule, code(() -> {
	
			Set<G> satisfiedGoalsBeforeRuleApplication = applyRuleAndReturnSatisfiedGoalsAsTheyWereBeforeItWasApplied(rule, i);
			
			Plan remainingPlan = plan(maxLeaves, level + 1, path);
	
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
			return failedPlan();
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

	private Plan failedPlan() {
		return orPlan();
	}

	private void indentedPrintln(int level, String message) {
		// println(indentationString(level) + message);
		// TODO: this should really be a separate explanation logger
	}

//	private String indentationString(int level) {
//		return fill(4*level, ' ');
//	}

	/////////////////////// EXPLANATION METHODS - END
	

	public final Comparator<? super Integer> descendingRuleIndexEstimatedSuccessWeightComparator = new Comparator<Integer>() {
		
		@Override
		public int compare(Integer ruleIndex1, Integer ruleIndex2) {
			R rule1 = state.rules.get(ruleIndex1);
			R rule2 = state.rules.get(ruleIndex2);
			return Double.compare(rule2.getEstimatedSuccessWeight(), rule1.getEstimatedSuccessWeight());
			// note the inversion, as we want descending order
		}
		
	};

}
