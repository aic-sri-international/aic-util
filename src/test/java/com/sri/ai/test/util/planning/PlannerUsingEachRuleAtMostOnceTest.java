package com.sri.ai.test.util.planning;

import static com.sri.ai.test.util.planning.MyRule.rule;
import static com.sri.ai.util.Util.arrayList;
import static com.sri.ai.util.Util.containsAllCaseInsensitive;
import static com.sri.ai.util.Util.join;
import static com.sri.ai.util.Util.list;
import static com.sri.ai.util.Util.println;
import static com.sri.ai.util.planning.core.ContingentPlan.contingent;
import static com.sri.ai.util.planning.core.OrPlan.or;
import static com.sri.ai.util.planning.core.SequentialPlan.and;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.LinkedList;

import org.junit.Test;

import com.sri.ai.util.Util;
import com.sri.ai.util.planning.api.ContingentGoal;
import com.sri.ai.util.planning.api.Goal;
import com.sri.ai.util.planning.api.Plan;
import com.sri.ai.util.planning.api.Rule;
import com.sri.ai.util.planning.core.PlannerUsingEachRuleAtMostOnce;

public class PlannerUsingEachRuleAtMostOnceTest {
	
	Goal a = new MyGoal("a");
	Goal b = new MyGoal("b");
	Goal c = new MyGoal("c");
	Goal d = new MyGoal("d");
	Goal e = new MyGoal("e");
	Goal f = new MyGoal("f");
	
	ContingentGoal ca = new MyContingentGoal("ca");
	ContingentGoal cb = new MyContingentGoal("cb");
	ContingentGoal cc = new MyContingentGoal("cc");
	ContingentGoal cd = new MyContingentGoal("cd");
	ContingentGoal ce = new MyContingentGoal("ce");
	ContingentGoal cf = new MyContingentGoal("cf");
	
	ArrayList<? extends Rule<Goal>> allRules;
	
	LinkedList<? extends Goal> allRequiredGoals;
	
	LinkedList<? extends Goal> satisfiedGoals;
	
	Plan expected;
	
	PlannerUsingEachRuleAtMostOnce planner;
	
	Plan actual;

	@Test
	public void basicTest() {

		allRules = arrayList(
				rule(list(a), list()),
				rule(list(b), list(a)),
				rule(list(c), list(b)),
				rule(list(d), list(c))
				);

		allRequiredGoals = list(a,b);
		
		satisfiedGoals = list();
		
		expected = 
				and(
						rule(list(a), list()), 
						rule(list(b), list(a)));
		
		runTest();
	}

	@Test
	public void multipleAntecedentsTest() {

		allRules = arrayList(
				rule(list(a), list(b, c)),
				rule(list(b), list(d)),
				rule(list(b), list()),
				rule(list(c), list(b, d)),
				rule(list(d), list())
				);

		allRequiredGoals = list(a,b,c,d);
		
		satisfiedGoals = list();
		
		expected = 
				or(
						and(
								rule(list(b), list()),
								rule(list(d), list()),
								rule(list(c), list(b, d)),
								rule(list(a), list(b, c))),
						and(
								rule(list(d), list()),
								or(
										and(
												rule(list(b), list(d)),
												rule(list(c), list(b, d)),
												rule(list(a), list(b, c))),
										and(
												rule(list(b), list()),
												rule(list(c), list(b, d)),
												rule(list(a), list(b, c))))));
		
		runTest();
	}

	@Test
	public void alternativesWithCycleTest() {

		allRules = arrayList(
				rule(list(a), list()),
				rule(list(a), list(b)),
				rule(list(b), list(a))
				);

		allRequiredGoals = list(a,b);
		
		satisfiedGoals = list();
		
		expected = 
				and(
						rule(list(a), list()), 
						rule(list(b), list(a))); 

		runTest();
	}

	@Test
	public void alternativesTest() {

		allRules = arrayList(
				rule(list(a), list()),
				rule(list(b), list()),
				rule(list(a), list(b)),
				rule(list(b), list(a))
				);

		allRequiredGoals = list(a,b);
		
		satisfiedGoals = list();
		
		expected = 
				or(
						and(
								rule(list(a), list()), 
								or(
										rule(list(b), list()),
										rule(list(b), list(a)))),
						and(
								rule(list(b), list()),
								or(
										rule(list(a), list()),
										rule(list(a), list(b)))));				

		runTest();
	}


	@Test
	public void alternativesButNotReallyTest() {

		allRules = arrayList(
				rule(list(a), list()),
				rule(list(b), list(c)),
				rule(list(a), list(b)),
				rule(list(b), list(a)),
				rule(list(c), list())
				);

		allRequiredGoals = list(a,b,c);
		
		satisfiedGoals = list();
		
		expected = 
		or(
				and(
						rule(list(a), list()), 
						or(
								and(
										rule(list(b), list(a)),
										rule(list(c), list())),
								and(
										rule(list(c), list()),
										or(
												rule(list(b), list(c)),
												rule(list(b), list(a)))))),
				and(
						rule(list(c), list()), 
						or(
								and(
										rule(list(a), list()),
										or(
												rule(list(b), list(c)),
												rule(list(b), list(a)))),
								and(
										rule(list(b), list(c)),
										or(
												rule(list(a), list()),
												rule(list(a), list(b)))))));
														
		runTest();
	}

	@Test
	public void deepLinearTest() {
		
		allRules = arrayList(
				rule(list(f), list(e)),
				rule(list(e), list(d)),
				rule(list(d), list(c)),
				rule(list(c), list(b)),
				rule(list(b), list(a)),
				rule(list(a), list())
				);

		allRequiredGoals = list(a,b,c,d,e,f);
		
		satisfiedGoals = list();
		
		expected = 
				and(
						rule(list(a), list()),
						rule(list(b), list(a)),
						rule(list(c), list(b)),
						rule(list(d), list(c)),
						rule(list(e), list(d)),
						rule(list(f), list(e)));
														
		runTest();
	}

	@Test
	public void deepDespair() {
		
		allRules = arrayList(
				rule(list(f), list(e)),
				rule(list(e), list(d)),
				rule(list(d), list(c)),
				rule(list(c), list(b)),
				rule(list(b), list(a))
				// rule(list(a), list()) // the root cannot be created.
				);

		allRequiredGoals = list(a,b,c,d,e,f);
		
		satisfiedGoals = list();
		
		expected = or(); // empty plan
														
		runTest();
	}

	@Test
	public void cycleTest() {
		
		allRules = arrayList(
				rule(list(f), list(e)),
				rule(list(e), list(d)),
				rule(list(d), list(c)),
				rule(list(c), list(b)),
				rule(list(b), list(a)),
				rule(list(a), list(f)) // there we go again
				);

		allRequiredGoals = list(a,b,c,d,e,f);
		
		satisfiedGoals = list();
		
		expected = or(); // empty plan
														
		runTest();
	}

	@Test
	public void multipleContributorsTest() {
		
		allRules = arrayList(
				rule(list(f), list(e, d, c, b, a)),
				rule(list(e), list()),
				rule(list(d), list()),
				rule(list(c), list()),
				rule(list(b), list()),
				rule(list(a), list())
				);

		allRequiredGoals = list(a,b,c,d,e,f);
		
		satisfiedGoals = list();
		
		// gigantic all-permutations kind of plan, but the output looks correct
		// runTest();
	}

	@Test
	public void noRulesTest() {
		
		allRules = arrayList();

		allRequiredGoals = list(a,b,c,d,e,f);
		
		satisfiedGoals = list();
		
		expected = or(); // empty plan
														
		runTest();
	}

	@Test
	public void noGoalsTest() {
		
		allRules = arrayList(
				rule(list(f), list(e)),
				rule(list(e), list(d)),
				rule(list(d), list(c)),
				rule(list(c), list(b)),
				rule(list(b), list(a)),
				rule(list(a), list())
				);

		allRequiredGoals = list();
		
		satisfiedGoals = list();
		
		expected = and();
														
		runTest();
	}
	
	@Test
	public void allRequiredGoalsAlreadySatisfiedTest() {
		
		allRules = arrayList(
				rule(list(f), list(e)),
				rule(list(e), list(d)),
				rule(list(d), list(c)),
				rule(list(c), list(b)),
				rule(list(b), list(a)),
				rule(list(a), list())
				);

		allRequiredGoals = list();
		
		satisfiedGoals = list();
		
		expected = and();
														
		runTest();
	}

	@Test
	public void satisfiedGoalsTest() {
		
		allRules = arrayList(
				rule(list(f), list(e)),
				rule(list(e), list(d)),
				rule(list(d), list(c)),
				rule(list(c), list(b)),
				rule(list(b), list(a)),
				rule(list(a), list())
				);

		allRequiredGoals = list(a,b,c,d,e,f);
		
		satisfiedGoals = list(b, c, d);
		
		expected = 
				or(
						and(
								rule(list(e), list(d)),
								or(
										and(
												rule(list(f), list(e)),
												rule(list(a), list())),
										and(
												rule(list(a), list()),
												rule(list(f), list(e))))),
						and(
								rule(list(a), list()),
								rule(list(e), list(d)),
								rule(list(f), list(e))));
														
		runTest();
	}

	@Test
	public void basicContingentGoalsTest() {
		
		allRules = arrayList(
				rule(list(b), list(ca)),
				rule(list(b), list(a)),
				rule(list(a), list())
				);

		allRequiredGoals = list(b);
		
		satisfiedGoals = list();
		
		expected = 
				or(
						contingent(
								ca, 
								rule(list(b), list(ca)), 
								and(
										rule(list(a), list()), 
										rule(list(b), list(a)))),
						and(
								rule(list(a), list()),
								or(
										rule(list(b), list(a)))));
						
														
		runTest();
		
	}


	@Test
	public void overallSuccessDependsOnContingentGoalsTest() {
		
		allRules = arrayList(
				rule(list(b), list(ca))
				);

		allRequiredGoals = list(b);
		
		satisfiedGoals = list();
		
		expected = or(); 
														
		runTest();
		
	}

	@Test
	public void twoLevelContingentGoalsTest() {
		
		allRules = arrayList(
				rule(list(b), list(a, ca)),
				rule(list(a), list(ca)),
				rule(list(b), list())
				);

		allRequiredGoals = list(b);
		
		satisfiedGoals = list();
		
		expected = 
				or(
						contingent(
								ca, 
								and(
										rule(list(a), list(ca)),
										or(
												rule(list(b), list(a, ca)),
												rule(list(b), list()))),
								rule(list(b), list())),
						rule(list(b), list()));
														
		runTest();
		
	}

	@Test
	public void threeLevelContingentGoalsTest() {
		
		allRules = arrayList(
				rule(list(c), list(b, ca)),
				rule(list(b), list(ca)),
				rule(list(b), list(a)),
				rule(list(a), list())
				);

		allRequiredGoals = list(b);
		
		satisfiedGoals = list();
		
		expected = 
				or(
						contingent(
								ca,
								rule(list(b), list(ca)),
								and(
										rule(list(a), list()), 
										rule(list(b), list(a)))),
						and(
								rule(list(a), list()),
								rule(list(b), list(a))));
														
		runTest();
		
	}

	@Test
	public void twoContingentGoalsInDifferentRulesTest() {
		
		allRules = arrayList(
				rule(list(b), list(ca)),
				rule(list(b), list(cb)),
				rule(list(b), list())
				);

		allRequiredGoals = list(b);
		
		satisfiedGoals = list();
		
		expected = 
				or(
						contingent(
								ca, 
								rule(list(b), list(ca)),
								or(
										contingent(
												cb,
												rule(list(b), list(cb)),
												rule(list(b), list())),
										rule(list(b), list()))),
						rule(list(b), list()));
														
		runTest();
		
	}

	@Test
	public void twoContingentGoalsInTheSameRuleAndNoWayToGuaranteedPlanTest() {
		
		allRules = arrayList(
				rule(list(c), list(a, ca, cb)),
				rule(list(a), list()) // there is no guaranteed way of reaching requires goal
				);

		allRequiredGoals = list(c);
		
		satisfiedGoals = list();
		
		expected = or();
														
		runTest();
		
	}

	@Test
	public void twoContingentGoalsInTheSameRuleAndNoWayToGuaranteedPlanButContingentPlansAreGivenTest() {
		
		allRules = arrayList(
				rule(list(c), list(a, ca, cb)),
				rule(list(a), list()) // there is no guaranteed way of reaching requires goal
				);

		allRequiredGoals = list(c);
		
		satisfiedGoals = list(ca, cb);
		
		expected = and(
				rule(list(a), list()),
				rule(list(c), list(a, ca, cb))
				);
														
		runTest();
		
	}

	@Test
	public void twoContingentGoalsInTheSameRuleTest() {
		
		allRules = arrayList(
				rule(list(c), list(a, ca, cb)),
				rule(list(a), list()),
				rule(list(c), list()) // must always have a guaranteed way of reaching requires goals
				);

		allRequiredGoals = list(c);
		
		satisfiedGoals = list();
		
		expected = 
				or(
						and(
								rule(list(a), list()),
								or(
										contingent(
												ca, 
												contingent(
														cb, 
														rule(list(c), list(a, ca, cb)),
														rule(list(c), list())),
												rule(list(c), list())),
										rule(list(c), list()))),
						rule(list(c), list()));

		runTest();
		
	}

	@Test
	public void repeatedContingentGoalsTest() {
		
		allRules = arrayList(
				rule(list(c), list(ca, a, ca, cb, ca, cb, ca)),
				rule(list(a), list(ca, cb)),
				rule(list(c), list()) // must always have a guaranteed way of reaching requires goals
				);

		allRequiredGoals = list(c);
		
		satisfiedGoals = list();
		
		expected = 
				or(
						contingent(
								ca, 
								contingent(
										cb, 
										and(
												rule(list(a), list(ca, cb)),
												or(
														rule(list(c), list(ca, a, ca, cb, ca, cb, ca)),
														rule(list(c), list()))),
										rule(list(c), list())),
								rule(list(c), list())),
						rule(list(c), list()));

		runTest();
		
	}

	@Test
	public void contingentGoalsCannotBeRequiredTest() {
		
		allRules = arrayList(
				rule(list(c), list(a, ca, cb))
				);

		allRequiredGoals = list(ca);
		
		satisfiedGoals = list();
		
		try {
			new PlannerUsingEachRuleAtMostOnce<Rule<Goal>, Goal>(allRequiredGoals, satisfiedGoals, list(), g -> false, allRules);
		} catch (Error e) {
			if (containsAllCaseInsensitive(e.getMessage(), "required", "contingent")) {
				println(e.getMessage());
				return;
			}
			else {
				throw e;
			}
		}
		fail("Should have throw error about contingent goals not being allowed to be required");
	}

	@Test
	public void contingentGoalsCannotBeInConsequentsTest() {
		
		allRules = arrayList(
				rule(list(ca), list(a, ca, cb))
				);

		allRequiredGoals = list();
		
		satisfiedGoals = list();
		
		try {
			new PlannerUsingEachRuleAtMostOnce<Rule<Goal>, Goal>(allRequiredGoals, satisfiedGoals, list(), g -> false, allRules);
		} catch (Error e) {
			if (containsAllCaseInsensitive(e.getMessage(), "consequents", "contingent")) {
				println(e.getMessage());
				return;
			}
			else {
				throw e;
			}
		}
		fail("Should have throw error about contingent goals not being allowed to be required");
	}

	public void runTest() {
		planner = new PlannerUsingEachRuleAtMostOnce<Rule<Goal>, Goal>(allRequiredGoals, satisfiedGoals, list(), g -> false, allRules);
		actual = planner.plan();
		println("Goals: " + allRequiredGoals);
		println("Rules:\n" + join("\n", allRules));
		println("Plan: " + actual);
		if (!Util.equals(expected, actual)) {
			println("Failure!");
			println("Expected:\n" + expected.nestedString());
			println("Actual:\n" + (actual == null? null : actual.nestedString()));
		}
		assertEquals(expected, actual);
	}
}
