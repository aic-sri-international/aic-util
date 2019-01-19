package com.sri.ai.util.planning.core;

import static com.sri.ai.util.Util.set;
import static java.util.Collections.unmodifiableSet;

import java.util.Collection;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.sri.ai.util.base.BinaryFunction;
import com.sri.ai.util.planning.api.Goal;
import com.sri.ai.util.planning.api.IndexedSetOfRules;
import com.sri.ai.util.planning.api.Rule;
import com.sri.ai.util.planning.dnf.api.ConjunctiveClause;
import com.sri.ai.util.planning.dnf.api.DNF;
import com.sri.ai.util.planning.dnf.core.DefaultDNF;
import com.sri.ai.util.planning.dnf.core.PositiveConjunctiveClause;

/**
 * An algorithm that takes an original set of rules and a set of goals to be <i>projected</i>,
 * and computes a <code>projected</code> set of rules for obtaining the <i>projected</i> goals
 * and whose antecedents are projected goals themselves.
 * <p>
 * The relationship between the original and projected set of rules is as follows:
 * a rule is a projected rule if and only if
 * its consequents and antecedents are all projected goals,
 * and it is possible to obtain its consequents by applying the original set of rules to its antecedents.
 * <p>
 * Note that, since a projected rule's antecedents are all projected goals,
 * it is implicitly assumed that all other original goals are not satisfied (since they are never in the projected rule's antecedents).
 * <p>
 * For example, suppose I have one rule for obtaining flour and another for using the flour to bake a cake.
 * If I project these rules into just baking a cake, I will get a single rule for baking a cake without any antecedents
 * (because I can use the original rule for obtaining flour and the other original rule to bake a cake from the flour).
 * If I project these rules into obtaining flour, I get a projected rule for obtaining flour without any antecedents.
 * 
 * @author
 *
 */
public class ProjectionOfSetOfRules<R extends Rule<G>, G extends Goal> {

	private Deque<G> goalStack;

	private Collection<? extends G> projectedGoals;

	private IndexedSetOfRules<R,G> indexedRules;

	private Set<R> projectedRules;

	private BinaryFunction<G, Set<? extends G>, R> ruleFactory;

	///////////////////////////////
	
	public ProjectionOfSetOfRules(
			List<? extends R> rules, 
			Collection<? extends G> projectedGoals,
			BinaryFunction<G, Set<? extends G>, R> ruleFactory) {
		
		this.projectedGoals = projectedGoals;
		this.indexedRules = new DefaultIndexedRules<R,G>(rules);
		this.projectedRules = null;
		this.ruleFactory = ruleFactory;
	}

	/////////////////////////////// Projection
	
	public Set<? extends R> getProjectedSetOfRules() {
		if (projectedRules == null) {
			project();
		}
		return unmodifiableSet(projectedRules);
	}

	private void project() {
		projectedRules = set();
		for (G projectedGoal : projectedGoals) {
			collectProjectedRulesFor(projectedGoal);
		}
	}

	private void collectProjectedRulesFor(G projectedGoal) {
		resetSearch();
		DNF<G> dnf = conditionFor(projectedGoal);
		makeRulesForGoalWithGivenCondition(projectedGoal, dnf);
	}

	/////////////////////////////// Determining conditions for goal
	
	private void resetSearch() {
		goalStack = new LinkedList<>();
	}

	public DNF<G> conditionFor(G goal) {
		if (isBeingSearched(goal)) {
			return falseCondition();
		}
		else {
			DNF<G> result = isProvided(goal).or(conditionObtaining(goal));
			return result;
		}
	}

	private boolean isBeingSearched(G goal) {
		return goalStack.contains(goal);
	}

	private DNF<G> isProvided(G goal) {
		if (cannotBeProvided(goal)) {
			return falseCondition();
		} else {
			return hasActuallyBeenProvided(goal);
		}
	}

	private boolean cannotBeProvided(G goal) {
		boolean result = 
				isEliminatedGoal(goal)
				|| 
				searchIsAtTopLevelButWeMustUseAtLeastOneRule();
		return result;
	}

	private boolean isEliminatedGoal(G goal) {
		return !projectedGoals.contains(goal);
	}

	private boolean searchIsAtTopLevelButWeMustUseAtLeastOneRule() {
		return goalStack.size() == 0;
	}

	public DNF<G> hasActuallyBeenProvided(G goal) {
		return new DefaultDNF<G>(new PositiveConjunctiveClause<G>(goal));
	}

	public DNF<G> conditionObtaining(G goal) {
		DNF<G> conditionFromPreviousRules = falseCondition();
		for (R rule : getOriginalRulesFor(goal)) {
			DNF<G> conditionForAntecedents = conditionForObtainingGoalWithRule(goal, rule);
			conditionFromPreviousRules = conditionFromPreviousRules.or(conditionForAntecedents);
			if (conditionFromPreviousRules.isTrue())
				break;
		}
		return conditionFromPreviousRules;
	}

	private Collection<? extends R> getOriginalRulesFor(G goal) {
		List<R> rulesForGoal = indexedRules.getRulesFor(goal);
		return rulesForGoal;
	}
	
	public DNF<G> conditionForObtainingGoalWithRule(G goal, R rule) {
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

	/////////////////////////////// Making rules from conditions
	
	private void makeRulesForGoalWithGivenCondition(G projectedGoal, DNF<G> dnf) {
		for (ConjunctiveClause<G> conjunction : dnf.getConjunctiveClauses()) {
			R conjunctionRule = makeRuleForGoalWithGivenCondition(projectedGoal, conjunction);
			projectedRules.add(conjunctionRule);
		}
	}

	private R makeRuleForGoalWithGivenCondition(G projectedGoal, ConjunctiveClause<G> conjunction) {
		Set<? extends G> antecendents = new LinkedHashSet<>(conjunction.getLiterals());
		R rule = ruleFactory.apply(projectedGoal, antecendents);
		return rule;
	}

	////////////////////////////// Auxiliary

	private DNF<G> trueCondition() {
		return new DefaultDNF<G>(new PositiveConjunctiveClause<G>());
	}

	public DNF<G> falseCondition() {
		return new DefaultDNF<G>();
	}

}
