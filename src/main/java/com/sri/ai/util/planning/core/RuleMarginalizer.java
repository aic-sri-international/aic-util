package com.sri.ai.util.planning.core;

import static com.sri.ai.util.Util.fill;
import static com.sri.ai.util.Util.list;
import static com.sri.ai.util.Util.set;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.sri.ai.util.base.BinaryFunction;
import com.sri.ai.util.planning.api.Conjunction;
import com.sri.ai.util.planning.api.DNF;
import com.sri.ai.util.planning.api.Goal;
import com.sri.ai.util.planning.api.IndexedRules;
import com.sri.ai.util.planning.api.Rule;

/**
 * An algorithm that takes a set of rules and a set of <i>marginalized</i> goals,
 * and computes another set of rules indicating which <i>remaining</i> (that is, not marginalized)
 * goals can be obtained from which using the original set of rules.
 * <p>
 * In other words, it produces a set of rules that assumes that marginalized goals are never a given.
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
	
	public RuleMarginalizer(ArrayList<? extends Rule> rules, Collection<? extends Goal> marginalized, BinaryFunction<Goal, Set<? extends Goal>, Rule> ruleFactory) {
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
		DNF dnf = conditionFor(remainingGoal);
		collectMarginalizedRules(remainingGoal, dnf);
	}

	public void collectMarginalizedRules(Goal remainingGoal, DNF dnf) {
		List<? extends Rule> marginalizedRulesForRemainingGoal = makeRulesForGoalWithGivenCondition(remainingGoal, dnf);
		marginalizedRules.addAll(marginalizedRulesForRemainingGoal);
	}

	public DNF conditionFor(Goal goal) {
		DNF result = isProvided(goal).or(conditionFromRulesFor(goal));
		return result;
	}

	private DNF isProvided(Goal goal) {
		if (cannotBeProvided(goal)) {
			return falseCondition();
		}
		else {
			return hasActuallyBeenProvided(goal);
		}
	}

	public boolean cannotBeProvided(Goal goal) {
		boolean result = 
				marginalizedGoals.contains(goal) 
				|| cannotBeConsideredPossiblyProvidedBecauseWeAreSearchingHowToObtainIt(goal) 
				|| isTopLevelRemainingGoal();
		return result;
	}

	public boolean cannotBeConsideredPossiblyProvidedBecauseWeAreSearchingHowToObtainIt(Goal goal) {
		return goalStack.contains(goal);
	}

	private boolean isTopLevelRemainingGoal() {
		return goalStack.size() == 0;
	}

	public DefaultDNF hasActuallyBeenProvided(Goal goal) {
		return new DefaultDNF(new DefaultConjunction(goal));
	}

	public DNF conditionFromRulesFor(Goal goal) {
		DNF conditionFromPreviousRules = falseCondition();
		for (Rule rule : getOriginalRulesFor(goal)) {
			goalStack.push(goal);
			DNF conditionForAntecedents = conditionForConjunctionOfGoals(rule.getAntecendents());
			conditionFromPreviousRules.or(conditionForAntecedents);
			goalStack.pop();
			if (conditionFromPreviousRules.isTrue()) break;
		}
		return conditionFromPreviousRules;
	}

	private DNF conditionForConjunctionOfGoals(Collection<? extends Goal> goals) {
		DNF conditionsRequiredForPreviousGoals = falseCondition();
		for (Goal goal : goals) {
			DNF conditionForGoal = conditionFor(goal);
			conditionsRequiredForPreviousGoals.conjoin(conditionForGoal);
			if (conditionsRequiredForPreviousGoals.isFalse()) break;
		}
		return conditionsRequiredForPreviousGoals;
	}

	private List<Rule> makeRulesForGoalWithGivenCondition(Goal remainingGoal, DNF dnf) {
		List<Rule> result = list();
		for (Conjunction conjunction : dnf.getConjunctions()) {
			Rule conjunctionRule = makeRuleForGoalWithGivenCondition(remainingGoal, conjunction);
			result.add(conjunctionRule);
		}
		return result;
	}

	private Rule makeRuleForGoalWithGivenCondition(Goal remainingGoal, Conjunction conjunction) {
		Set<? extends Goal> antecendents = new LinkedHashSet<>(conjunction.getGoals());
		Rule rule = ruleFactory.apply(remainingGoal, antecendents);
		return rule;
	}

	private Collection<? extends Rule> getOriginalRulesFor(Goal goal) {
		return indexedRules.getRulesFor(goal);
	}

	private Collection<? extends Goal> getAllRemainingGoalsFromRules(IndexedRules indexedRules) {
		return indexedRules.getGoals();
	}

	public DefaultDNF falseCondition() {
		return new DefaultDNF();
	}

}
