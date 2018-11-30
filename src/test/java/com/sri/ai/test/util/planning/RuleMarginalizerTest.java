package com.sri.ai.test.util.planning;

import static com.sri.ai.test.util.planning.MyRule.rule;
import static com.sri.ai.util.Util.arrayList;
import static com.sri.ai.util.Util.join;
import static com.sri.ai.util.Util.list;
import static com.sri.ai.util.Util.println;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.sri.ai.util.base.BinaryFunction;
import com.sri.ai.util.planning.api.Goal;
import com.sri.ai.util.planning.api.Rule;
import com.sri.ai.util.planning.core.PlannerUsingEachRuleAtMostOnce;
import com.sri.ai.util.planning.core.RuleMarginalizer;

public class RuleMarginalizerTest {
	
	Goal a = new MyGoal("a");
	Goal b = new MyGoal("b");
	Goal c = new MyGoal("c");
	Goal d = new MyGoal("d");
	Goal e = new MyGoal("e");
	Goal f = new MyGoal("f");
	
	ArrayList<? extends Rule<Goal>> allRules;
	
	LinkedList<Goal> allGoals;

	LinkedList<Goal> marginalizedGoals;
	
	List<? extends Rule> expected;
	
	PlannerUsingEachRuleAtMostOnce planner;
	
	Set<? extends Rule> actual;

	BinaryFunction<Goal, Set<? extends Goal>, Rule<Goal>> 
	ruleFactory = 
	(consequent, antecendents) -> rule(list(consequent), new LinkedList<>(antecendents));

	@Test
	public void basicTest() {

		allRules = arrayList(
				rule(list(b), list(a)),
				rule(list(a), list()));

		marginalizedGoals = list(a);
		
		expected = list(rule(list(a), list()));
		
		runTest();
	}

	@Test
	public void cycleTest() {

		allRules = arrayList(
				rule(list(b), list(a)),
				rule(list(a), list(b)));

		marginalizedGoals = list(a);
		
		expected = list();
		
		runTest();
	}

	@Test
	public void remainingVariableIsIndependentOfMarginalizedOneTest() {

		allRules = arrayList(
				rule(list(b), list(a)),
				rule(list(a), list(b)),
				rule(list(b), list()));

		marginalizedGoals = list(a);
		
		expected = list(rule(list(b), list()));
		
		runTest();
	}

	@Test
	public void remainingVariableIsDependentOfMarginalizedOneTest() {

		allRules = arrayList(
				rule(list(b), list(a)),
				rule(list(a), list()));

		marginalizedGoals = list(a);
		
		expected = list(rule(list(b), list()));
		
		runTest();
	}

	@Test
	public void multipleAntecedentsTest() {

		allRules = arrayList(
				rule(list(c), list(a, b)),
				rule(list(b), list(a, c)));

		marginalizedGoals = list(c);
		
		expected = list();
		
		runTest();

		allRules = arrayList(
				rule(list(c), list(a, b)),
				rule(list(b), list(a, c)),
				rule(list(c), list())
				);

		marginalizedGoals = list(c);
		
		expected = list(rule(list(b), list(a)));
		
		runTest();
	}

	@Test
	public void longChainTest() {

		allRules = arrayList(
				rule(list(f), list(e)),
				rule(list(e), list(d)),
				rule(list(d), list(c)),
				rule(list(c), list(b)),
				rule(list(b), list(a)),
				rule(list(a), list())
				);

		marginalizedGoals = list(e, c, a);
		
		expected = list(
				rule(list(f), list(d)),
				rule(list(d), list(b))
				);
		
		runTest();
	}

	@Test
	public void noRulesTest() {

		allRules = arrayList();

		marginalizedGoals = list(a);
		
		expected = list();
		
		runTest();
	}

	public void runTest() {
		RuleMarginalizer<Rule<Goal>, Goal> marginalizer = new RuleMarginalizer<>(allRules, marginalizedGoals, ruleFactory);

		actual = marginalizer.marginalize();
		
		println(join("\n", actual));
	}
}
