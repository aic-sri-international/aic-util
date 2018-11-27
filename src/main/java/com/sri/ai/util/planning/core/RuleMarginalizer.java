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
public class RuleMarginalizer {

	private Deque<Goal> goalStack;

	private Collection<? extends Goal> marginalizedGoals;

	private Collection<? extends Goal> remainingGoals;

	private IndexedRules indexedRules;

	private Set<Rule> marginalizedRules;

	private BinaryFunction<Goal, Set<? extends Goal>, Rule> ruleFactory;

	public RuleMarginalizer(List<? extends Rule> rules, Collection<? extends Goal> marginalized,
			BinaryFunction<Goal, Set<? extends Goal>, Rule> ruleFactory) {
		this.goalStack = new LinkedList<>();
		this.marginalizedGoals = marginalized;
		this.indexedRules = new DefaultIndexedRules(rules);
		this.remainingGoals = getAllRemainingGoalsFromRules(indexedRules);
		this.marginalizedRules = set();
		this.ruleFactory = ruleFactory;
	}

	public Set<? extends Rule> marginalize() {
		for (Goal remainingGoal : remainingGoals) {
			collectMarginalizedRulesFor(remainingGoal);
		}
		return marginalizedRules;
	}

	private void collectMarginalizedRulesFor(Goal remainingGoal) {
		DNF<Goal> dnf = conditionFor(remainingGoal);
		makeMarginalizedRules(remainingGoal, dnf);
	}

	public void makeMarginalizedRules(Goal remainingGoal, DNF<Goal> dnf) {
		List<? extends Rule> marginalizedRulesForRemainingGoal = makeRulesForGoalWithGivenCondition(remainingGoal, dnf);
		marginalizedRules.addAll(marginalizedRulesForRemainingGoal);
	}

	public DNF<Goal> conditionFor(Goal goal) {
		if (isBeingSearched(goal)) {
			return falseCondition();
		}
		else {
			DNF<Goal> result = isProvided(goal).or(conditionFromRulesFor(goal));
			return result;
		}
	}

	private DNF<Goal> isProvided(Goal goal) {
		if (cannotBeProvided(goal)) {
			return falseCondition();
		} else {
			return hasActuallyBeenProvided(goal);
		}
	}

	public boolean cannotBeProvided(Goal goal) {
		boolean result = marginalizedGoals.contains(goal)
				|| searchIsAtTopLevelButWeMustUseAtLeastOneRule();
		return result;
	}

	public boolean isBeingSearched(Goal goal) {
		return goalStack.contains(goal);
	}

	private boolean searchIsAtTopLevelButWeMustUseAtLeastOneRule() {
		return goalStack.size() == 0;
	}

	public DNF<Goal> hasActuallyBeenProvided(Goal goal) {
		return new DefaultDNF<Goal>(new DefaultConjunction<Goal>(goal));
	}

	public DNF<Goal> conditionFromRulesFor(Goal goal) {
		DNF<Goal> conditionFromPreviousRules = falseCondition();
		for (Rule rule : getOriginalRulesFor(goal)) {
			DNF<Goal> conditionForAntecedents = conditionForObtainingGoalWithRule(goal, rule);
			conditionFromPreviousRules.or(conditionForAntecedents);
			if (conditionFromPreviousRules.isTrue())
				break;
		}
		return conditionFromPreviousRules;
	}

	public DNF<Goal> conditionForObtainingGoalWithRule(Goal goal, Rule rule) {
		goalStack.push(goal);
		DNF<Goal> conditionForAntecedents = conditionForConjunctionOfGoals(rule.getAntecendents());
		goalStack.pop();
		return conditionForAntecedents;
	}

	private DNF<Goal> conditionForConjunctionOfGoals(Collection<? extends Goal> goals) {
		DNF<Goal> conditionsRequiredForPreviousGoals = falseCondition();
		for (Goal goal : goals) {
			DNF<Goal> conditionForGoal = conditionFor(goal);
			conditionsRequiredForPreviousGoals.conjoin(conditionForGoal);
			if (conditionsRequiredForPreviousGoals.isFalse())
				break;
		}
		return conditionsRequiredForPreviousGoals;
	}

	private List<Rule> makeRulesForGoalWithGivenCondition(Goal remainingGoal, DNF<Goal> dnf) {
		List<Rule> result = list();
		for (Conjunction<Goal> conjunction : dnf.getConjunctions()) {
			Rule conjunctionRule = makeRuleForGoalWithGivenCondition(remainingGoal, conjunction);
			result.add(conjunctionRule);
		}
		return result;
	}

	private Rule makeRuleForGoalWithGivenCondition(Goal remainingGoal, Conjunction<Goal> conjunction) {
		Set<? extends Goal> antecendents = new LinkedHashSet<>(conjunction.getGoals());
		Rule rule = ruleFactory.apply(remainingGoal, antecendents);
		return rule;
	}

	private Collection<? extends Rule> getOriginalRulesFor(Goal goal) {
		List<Rule> rulesForGoal = indexedRules.getRulesFor(goal);
		return rulesForGoal;
	}

	private Collection<? extends Goal> getAllRemainingGoalsFromRules(IndexedRules indexedRules) {
		return indexedRules.getGoals();
	}

	public DNF<Goal> falseCondition() {
		return new DefaultDNF<Goal>();
	}

}
