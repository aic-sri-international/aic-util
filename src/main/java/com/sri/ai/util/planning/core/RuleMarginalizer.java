package com.sri.ai.util.planning.core;

import static com.sri.ai.util.Util.list;
import static com.sri.ai.util.Util.set;

import java.util.Collection;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.sri.ai.util.base.BinaryFunction;
import com.sri.ai.util.planning.api.Goal;
import com.sri.ai.util.planning.api.IndexedRules;
import com.sri.ai.util.planning.api.Rule;
import com.sri.ai.util.planning.dnf.api.Conjunction;
import com.sri.ai.util.planning.dnf.api.DNF;
import com.sri.ai.util.planning.dnf.core.DefaultConjunction;
import com.sri.ai.util.planning.dnf.core.DefaultDNF;

/**
 * An algorithm that takes a set of rules and a set of <i>marginalized</i>
 * goals, and computes another set of rules indicating which <i>remaining</i>
 * (that is, not marginalized) goals can be obtained from which using the
 * original set of rules.
 * <p>
 * In other words, it produces a set of rules that assumes that marginalized
 * goals are never a given.
 * 
 * @author
 *
 */
public class RuleMarginalizer<R extends Rule<G>, G extends Goal> {

	private Deque<G> goalStack;

	private Collection<? extends G> marginalizedGoals;

	private Collection<? extends G> remainingGoals;

	private IndexedRules<R,G> indexedRules;

	private Set<R> marginalizedRules;

	private BinaryFunction<G, Set<? extends G>, R> ruleFactory;

	public RuleMarginalizer(
			List<? extends R> rules, 
			Collection<? extends G> marginalized,
			BinaryFunction<G, Set<? extends G>, R> ruleFactory) {
		this.goalStack = new LinkedList<>();
		this.marginalizedGoals = marginalized;
		this.indexedRules = new DefaultIndexedRules<R,G>(rules);
		this.remainingGoals = getAllRemainingGoalsFromRules(indexedRules);
		this.marginalizedRules = set();
		this.ruleFactory = ruleFactory;
	}

	public Set<? extends R> marginalize() {
		for (G remainingGoal : remainingGoals) {
			collectMarginalizedRulesFor(remainingGoal);
		}
		return marginalizedRules;
	}

	private void collectMarginalizedRulesFor(G remainingGoal) {
		DNF<G> dnf = conditionFor(remainingGoal);
		makeMarginalizedRules(remainingGoal, dnf);
	}

	public void makeMarginalizedRules(G remainingGoal, DNF<G> dnf) {
		List<? extends R> marginalizedRulesForRemainingGoal = makeRulesForGoalWithGivenCondition(remainingGoal, dnf);
		marginalizedRules.addAll(marginalizedRulesForRemainingGoal);
	}

	public DNF<G> conditionFor(G goal) {
		if (isBeingSearched(goal)) {
			return falseCondition();
		}
		else {
			DNF<G> result = isProvided(goal).or(conditionFromRulesFor(goal));
			return result;
		}
	}

	private DNF<G> isProvided(G goal) {
		if (cannotBeProvided(goal)) {
			return falseCondition();
		} else {
			return hasActuallyBeenProvided(goal);
		}
	}

	public boolean cannotBeProvided(G goal) {
		boolean result = marginalizedGoals.contains(goal)
				|| searchIsAtTopLevelButWeMustUseAtLeastOneRule();
		return result;
	}

	public boolean isBeingSearched(G goal) {
		return goalStack.contains(goal);
	}

	private boolean searchIsAtTopLevelButWeMustUseAtLeastOneRule() {
		return goalStack.size() == 0;
	}

	public DNF<G> hasActuallyBeenProvided(G goal) {
		return new DefaultDNF<G>(new DefaultConjunction<G>(goal));
	}

	public DNF<G> conditionFromRulesFor(G goal) {
		DNF<G> conditionFromPreviousRules = falseCondition();
		for (R rule : getOriginalRulesFor(goal)) {
			DNF<G> conditionForAntecedents = conditionForObtainingGoalWithRule(goal, rule);
			conditionFromPreviousRules.or(conditionForAntecedents);
			if (conditionFromPreviousRules.isTrue())
				break;
		}
		return conditionFromPreviousRules;
	}

	public DNF<G> conditionForObtainingGoalWithRule(G goal, R rule) {
		goalStack.push(goal);
		DNF<G> conditionForAntecedents = conditionForConjunctionOfGoals(rule.getAntecendents());
		goalStack.pop();
		return conditionForAntecedents;
	}

	private DNF<G> conditionForConjunctionOfGoals(Collection<? extends G> goals) {
		DNF<G> conditionsRequiredForPreviousGoals = falseCondition();
		for (G goal : goals) {
			DNF<G> conditionForGoal = conditionFor(goal);
			conditionsRequiredForPreviousGoals.conjoin(conditionForGoal);
			if (conditionsRequiredForPreviousGoals.isFalse())
				break;
		}
		return conditionsRequiredForPreviousGoals;
	}

	private List<R> makeRulesForGoalWithGivenCondition(G remainingGoal, DNF<G> dnf) {
		List<R> result = list();
		for (Conjunction<G> conjunction : dnf.getConjunctions()) {
			R conjunctionRule = makeRuleForGoalWithGivenCondition(remainingGoal, conjunction);
			result.add(conjunctionRule);
		}
		return result;
	}

	private R makeRuleForGoalWithGivenCondition(G remainingGoal, Conjunction<G> conjunction) {
		Set<? extends G> antecendents = new LinkedHashSet<>(conjunction.getGoals());
		R rule = ruleFactory.apply(remainingGoal, antecendents);
		return rule;
	}

	private Collection<? extends R> getOriginalRulesFor(G goal) {
		List<R> rulesForGoal = indexedRules.getRulesFor(goal);
		return rulesForGoal;
	}

	private Collection<? extends G> getAllRemainingGoalsFromRules(IndexedRules<R,G> indexedRules) {
		return indexedRules.getGoals();
	}

	public DNF<G> falseCondition() {
		return new DefaultDNF<G>();
	}

}
