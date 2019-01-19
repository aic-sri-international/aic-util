package com.sri.ai.util.planning.core;

import static com.sri.ai.util.Util.mapIntoSet;
import static com.sri.ai.util.Util.set;
import static java.util.Collections.unmodifiableSet;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.sri.ai.util.base.BinaryFunction;
import com.sri.ai.util.planning.api.Goal;
import com.sri.ai.util.planning.api.IndexedSetOfRules;
import com.sri.ai.util.planning.api.Rule;
import com.sri.ai.util.planning.dnf.api.ConjunctiveClause;
import com.sri.ai.util.planning.dnf.api.DNF;

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
 * @author braz
 *
 */
public class ProjectionOfSetOfRules<R extends Rule<G>, G extends Goal> {

	private Collection<? extends G> projectedGoals;

	private IndexedSetOfRules<R,G> indexedRules;

	private Set<R> projectedRules;

	private BinaryFunction<G, Set<? extends G>, R> ruleFactory;

	private DNFProjectorPlanner<R, G> projector;
	
	///////////////////////////////
	
	public ProjectionOfSetOfRules(
			List<? extends R> rules, 
			Collection<? extends G> projectedGoals,
			BinaryFunction<G, Set<? extends G>, R> ruleFactory) {
		
		this.projectedGoals = projectedGoals;
		this.indexedRules = new DefaultIndexedRules<R,G>(rules);
		this.projectedRules = null;
		this.ruleFactory = ruleFactory;
		this.projector = new DNFProjectorPlanner<R, G>(indexedRules, g -> !projectedGoals.contains(g));
	}

	/////////////////////////////// Projection
	
	public Set<? extends R> getProjectedSetOfRules() {
		if (projectedRules == null) {
			computeProjectedRules();
		}
		return unmodifiableSet(projectedRules);
	}

	private void computeProjectedRules() {
		projectedRules = set();
		projectedGoals.forEach(this::collectProjectedRulesForGoal);
	}

	private void collectProjectedRulesForGoal(G projectedGoal) {
		DNF<G> dnf = projector.plan(projectedGoal);
		makeRulesForGoalWithGivenCondition(projectedGoal, dnf);
	}

	/////////////////////////////// Making rules from conditions
	
	private void makeRulesForGoalWithGivenCondition(G projectedGoal, DNF<G> dnf) {
		mapIntoSet(
				dnf.getConjunctiveClauses(), 
				c -> makeRuleForGoalWithGivenCondition(projectedGoal, c), 
				projectedRules);
	}

	private R makeRuleForGoalWithGivenCondition(G projectedGoal, ConjunctiveClause<G> conjunction) {
		Set<? extends G> antecendents = new LinkedHashSet<>(conjunction.getLiterals());
		R rule = ruleFactory.apply(projectedGoal, antecendents);
		return rule;
	}

}
