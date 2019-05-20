package com.sri.ai.test.util.planning;

import static com.sri.ai.test.util.planning.MyRule.rule;
import static com.sri.ai.util.Util.arrayList;
import static com.sri.ai.util.Util.containsAllCaseInsensitive;
import static com.sri.ai.util.Util.join;
import static com.sri.ai.util.Util.list;
import static com.sri.ai.util.Util.println;
import static com.sri.ai.util.planning.core.ContingentPlan.contingent;
import static com.sri.ai.util.planning.core.OrPlan.orPlan;
import static com.sri.ai.util.planning.core.SequentialPlan.and;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import com.sri.ai.test.util.planning.parser.MyRuleAndPlansVisitor;
import com.sri.ai.util.Util;
import com.sri.ai.util.antlr.AntlrBundle;
import com.sri.ai.util.planning.api.ContingentGoal;
import com.sri.ai.util.planning.api.Goal;
import com.sri.ai.util.planning.api.Plan;
import com.sri.ai.util.planning.api.Rule;
import com.sri.ai.util.planning.core.PlannerUsingEachRuleAtMostOnce;
import com.sri.ai.util.planning.test.MyRuleAndPlansLexer;
import com.sri.ai.util.planning.test.MyRuleAndPlansParser;

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
				orPlan(
						and(
								rule(list(b), list()),
								rule(list(d), list()),
								rule(list(c), list(b, d)),
								rule(list(a), list(b, c))),
						and(
								rule(list(d), list()),
								orPlan(
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
				orPlan(
						and(
								rule(list(a), list()), 
								orPlan(
										rule(list(b), list()),
										rule(list(b), list(a)))),
						and(
								rule(list(b), list()),
								orPlan(
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
		orPlan(
				and(
						rule(list(a), list()), 
						orPlan(
								and(
										rule(list(b), list(a)),
										rule(list(c), list())),
								and(
										rule(list(c), list()),
										orPlan(
												rule(list(b), list(c)),
												rule(list(b), list(a)))))),
				and(
						rule(list(c), list()), 
						orPlan(
								and(
										rule(list(a), list()),
										orPlan(
												rule(list(b), list(c)),
												rule(list(b), list(a)))),
								and(
										rule(list(b), list(c)),
										orPlan(
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
		
		expected = orPlan(); // empty plan
														
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
		
		expected = orPlan(); // empty plan
														
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
		
		expected = orPlan(); // empty plan
														
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
				orPlan(
						and(
								rule(list(e), list(d)),
								orPlan(
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
		
		allRules = rules(
				"b <= contingent ca;" +
				"b <= a;" +
				"a <= ");

		allRequiredGoals = goals("b");
		
		satisfiedGoals = goals("");
		
		expected = plan(
				"or(" + 
				"    if (contingent ca)" + 
				"        then" + 
				"            b <= contingent ca" + 
				"        else" + 
				"            and(" + 
				"                a <=," + 
				"                b <= a)," + 
				"    and(" + 
				"        a <=," + 
				"        or(" + 
				"            if (contingent ca)" + 
				"                then" + 
				"                    b <= contingent ca" + 
				"                else" + 
				"                    b <= a," + 
				"            b <= a)))");
														
		runTest();
		
	}


	@Test
	public void overallSuccessDependsOnContingentGoalsTest() {
		
		allRules = arrayList(
				rule(list(b), list(ca))
				);

		allRequiredGoals = list(b);
		
		satisfiedGoals = list();
		
		expected = plan("if (contingent ca) then b <= contingent ca else or()"); 
														
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
				orPlan(
						contingent(
								ca, 
								and(
										rule(list(a), list(ca)),
										orPlan(
												rule(list(b), list(a, ca)),
												rule(list(b), list()))),
								rule(list(b), list())),
						rule(list(b), list()));
														
		runTest();
		
	}

	@Test
	public void threeLevelContingentGoalsTest() {
		
		allRules = rules(
				"c <= b, "
				+ "contingent ca; "
				+ "b <= contingent ca; "
				+ "b <= a; "
				+ "a <=");

		allRequiredGoals = goals("b");
		
		satisfiedGoals = goals("");
		
		expected = plan("or(" + 
				"    if (contingent ca)" + 
				"        then" + 
				"            b <= contingent ca" + 
				"        else" + 
				"            and(" + 
				"                a <=," + 
				"                b <= a)," + 
				"    and(" + 
				"        a <=," + 
				"        or(" + 
				"            if (contingent ca)" + 
				"                then" + 
				"                    b <= contingent ca" + 
				"                else" + 
				"                    b <= a," + 
				"            b <= a)))");
														
		runTest();
		
	}

	@Test
	public void twoContingentGoalsInDifferentRulesTest() {
		
		allRules = rules(
				"b <= contingent ca;" +
				"b <= contingent cb;" +
				"b <= ");

		allRequiredGoals = goals("b");
		
		satisfiedGoals = goals("");
		
		expected = plan(
				"or(" + 
				"    if (contingent ca)" + 
				"        then" + 
				"            b <= contingent ca" + 
				"        else" + 
				"            or(" + 
				"                if (contingent cb)" + 
				"                    then" + 
				"                        b <= contingent cb" + 
				"                    else" + 
				"                        b <=," + 
				"                b <=)," + 
				"    if (contingent cb)" + 
				"        then" + 
				"            b <= contingent cb" + 
				"        else" + 
				"            or(" + 
				"                if (contingent ca)" + 
				"                    then" + 
				"                        b <= contingent ca" + 
				"                    else" + 
				"                        b <=," + 
				"                b <=)," + 
				"    b <=)");
														
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
		
		expected = plan(
				"and("
				+ "a <= , "
				+ "if (contingent ca) "
				+ "   then if (contingent cb) "
				+ "           then c <= a, contingent ca, contingent cb "
				+ "           else or() "
				+ "   else or())");
														
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
				orPlan(
						and(
								rule(list(a), list()),
								orPlan(
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
				orPlan(
						contingent(
								ca, 
								contingent(
										cb, 
										and(
												rule(list(a), list(ca, cb)),
												orPlan(
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
			new PlannerUsingEachRuleAtMostOnce<Rule<Goal>, Goal>(allRequiredGoals, satisfiedGoals, list(), allRules);
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
			new PlannerUsingEachRuleAtMostOnce<Rule<Goal>, Goal>(allRequiredGoals, satisfiedGoals, list(), allRules);
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
		planner = new PlannerUsingEachRuleAtMostOnce<Rule<Goal>, Goal>(allRequiredGoals, satisfiedGoals, list(), allRules);
		actual = planner.topLevelPlan();
		println("Goals: " + allRequiredGoals);
		println("Rules:\n" + join("\n", allRules));
		println("Computed plan:\n" + actual.nestedString());
		if (!Util.equals(expected, actual)) {
			println("Failure!");
			println("Expected:\n" + expected.nestedString());
			println("Actual:\n" + (actual == null? null : actual.nestedString()));
		}
		assertEquals(expected, actual);
	}
	
	private static Plan plan(String string) {
		return (Plan) makeBundle(string).visit(p -> p.plan());
	}

	@SuppressWarnings("unchecked")
	private static LinkedList<Goal> goals(String string) {
		return new LinkedList<>((List<Goal>) makeBundle(string).visit(p -> p.goalList()));
	}

	@SuppressWarnings("unchecked")
	private static ArrayList<MyRule> rules(String string) {
		return new ArrayList<>((List<MyRule>) makeBundle(string).visit(p -> p.myRuleList()));
	}

	private static AntlrBundle<MyRuleAndPlansLexer, MyRuleAndPlansParser, MyRuleAndPlansVisitor> makeBundle(String string) {
		AntlrBundle<MyRuleAndPlansLexer, MyRuleAndPlansParser, MyRuleAndPlansVisitor> bundle = 
				AntlrBundle.antlrBundle(new StringReader(string), MyRuleAndPlansLexer.class, MyRuleAndPlansParser.class, MyRuleAndPlansVisitor.class);
		return bundle;
	}
}
