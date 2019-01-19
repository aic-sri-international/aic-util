package com.sri.ai.util.planning.core;

import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

import com.sri.ai.util.planning.api.Goal;
import com.sri.ai.util.planning.api.IndexedSetOfRules;
import com.sri.ai.util.planning.api.Rule;
import com.sri.ai.util.planning.dnf.api.DNF;
import com.sri.ai.util.planning.dnf.core.DefaultDNF;
import com.sri.ai.util.planning.dnf.core.PositiveConjunctiveClause;

/**
 * An algorithm for providing a {@link DNF} encoding the conditions for being able to satisfy a goal
 * given a set of rules and a set of goals known to never be satisfied.
 * 
 * @author braz
 *
 */
public class DNFProjectorPlanner<R extends Rule<G>, G extends Goal> {

	private Predicate<G> isKnownToBeUnsatisfied;

	private IndexedSetOfRules<R,G> indexedRules;

	///////////////////////////////
	
	public DNFProjectorPlanner(IndexedSetOfRules<R,G> indexedRules, Predicate<G> isKnownToBeUnsatisfied) {
		
		this.indexedRules = indexedRules;
		this.isKnownToBeUnsatisfied = isKnownToBeUnsatisfied;
	}

	/////////////////////////////// Determining conditions for goal
	
	private Deque<G> goalStack;

	public DNF<G> plan(G goal) {
		this.goalStack = new LinkedList<>();
		return getConditionForObtainingUnsatisfiedGoalWithinSearch(goal);
	}
	
	private DNF<G> getConditionForObtainingUnsatisfiedGoalWithinSearch(G goal) {
		DNF<G> currentCondition = falseCondition();
		for (R rule : getOriginalRulesFor(goal)) {
			DNF<G> conditionForAntecedents = conditionForObtainingGoalWithRule(goal, rule);
			currentCondition = currentCondition.or(conditionForAntecedents);
			if (currentCondition.isTrue())
				break;
		}
		return currentCondition;
	}

	private DNF<G> conditionForObtainingGoalWithRule(G goal, R rule) {
		goalStack.push(goal);
		DNF<G> conditionForAntecedents = conditionForConjunctionOfGoals(rule.getAntecendents());
		goalStack.pop();
		return conditionForAntecedents;
	}

	private DNF<G> conditionForConjunctionOfGoals(Collection<? extends G> goals) {
		DNF<G> conditionsRequiredForPreviousGoals = trueCondition();
		for (G goal : goals) {
			DNF<G> conditionForGoal = conditionFor(goal);
			conditionsRequiredForPreviousGoals = conditionsRequiredForPreviousGoals.and(conditionForGoal);
			if (conditionsRequiredForPreviousGoals.isFalse())
				break;
		}
		return conditionsRequiredForPreviousGoals;
	}

	private DNF<G> conditionFor(G goal) {
		if (isBeingSearched(goal)) {
			return falseCondition();
		}
		else {
			DNF<G> result = conditionForBeingSatisfied(goal).or(getConditionForObtainingUnsatisfiedGoalWithinSearch(goal));
			return result;
		}
	}

	private DNF<G> conditionForBeingSatisfied(G goal) {
		if (isKnownToBeUnsatisfied(goal)) {
			return falseCondition();
		} else {
			return conditionForGoalNotKnownToBeUnsatisfiedBeingSatisfied(goal);
		}
	}

	/////////////////////////////// Search auxiliary methods
	
	private Collection<? extends R> getOriginalRulesFor(G goal) {
		List<R> rulesForGoal = indexedRules.getRulesFor(goal);
		return rulesForGoal;
	}
	
	private boolean isBeingSearched(G goal) {
		return goalStack.contains(goal);
	}

	private boolean isKnownToBeUnsatisfied(G goal) {
		return isKnownToBeUnsatisfied.test(goal);
	}

	private DNF<G> conditionForGoalNotKnownToBeUnsatisfiedBeingSatisfied(G goal) {
		return new DefaultDNF<G>(new PositiveConjunctiveClause<G>(goal));
	}

	////////////////////////////// Auxiliary

	private DNF<G> trueCondition() {
		return new DefaultDNF<G>(new PositiveConjunctiveClause<G>());
	}

	private DNF<G> falseCondition() {
		return new DefaultDNF<G>();
	}

}
