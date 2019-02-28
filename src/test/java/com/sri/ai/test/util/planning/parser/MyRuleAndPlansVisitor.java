package com.sri.ai.test.util.planning.parser;

import static com.sri.ai.util.Util.list;
import static com.sri.ai.util.Util.mapIntoList;

import java.util.List;

import com.sri.ai.test.util.planning.JoinLinkedList;
import com.sri.ai.test.util.planning.MyContingentGoal;
import com.sri.ai.test.util.planning.MyGoal;
import com.sri.ai.test.util.planning.MyRule;
import com.sri.ai.util.planning.api.ContingentGoal;
import com.sri.ai.util.planning.api.Plan;
import com.sri.ai.util.planning.core.ContingentPlan;
import com.sri.ai.util.planning.core.OrPlan;
import com.sri.ai.util.planning.core.SequentialPlan;
import com.sri.ai.util.planning.test.MyRuleAndPlansParser.AndContext;
import com.sri.ai.util.planning.test.MyRuleAndPlansParser.ContingentGoalContext;
import com.sri.ai.util.planning.test.MyRuleAndPlansParser.ContingentPlanContext;
import com.sri.ai.util.planning.test.MyRuleAndPlansParser.GoalContext;
import com.sri.ai.util.planning.test.MyRuleAndPlansParser.GoalListContext;
import com.sri.ai.util.planning.test.MyRuleAndPlansParser.GoalListPlusContext;
import com.sri.ai.util.planning.test.MyRuleAndPlansParser.MyRuleContext;
import com.sri.ai.util.planning.test.MyRuleAndPlansParser.MyRuleListContext;
import com.sri.ai.util.planning.test.MyRuleAndPlansParser.OrContext;
import com.sri.ai.util.planning.test.MyRuleAndPlansParser.PlainGoalContext;
import com.sri.ai.util.planning.test.MyRuleAndPlansParser.PlanContext;
import com.sri.ai.util.planning.test.MyRuleAndPlansParser.PlanListContext;

public class MyRuleAndPlansVisitor extends com.sri.ai.util.planning.test.MyRuleAndPlansBaseVisitor<Object> {

	@Override
	public Plan visitPlan(PlanContext ctx) {
		return (Plan) super.visitPlan(ctx);
	}

	@Override
	public Plan visitAnd(AndContext ctx) {
		return new SequentialPlan(visitPlanList(ctx.planList()));
	}

	@Override
	public Plan visitOr(OrContext ctx) {
		return new OrPlan(visitPlanList(ctx.planList()));
	}

	@Override
	public Object visitContingentPlan(ContingentPlanContext ctx) {
		return 
				new ContingentPlan(
						(ContingentGoal) visitGoal(ctx.goal()), 
						visitPlan(ctx.plan(0)), 
						visitPlan(ctx.plan(1)));
	}

	@Override
	public List<Plan> visitPlanList(PlanListContext ctx) {
		List<PlanContext> plans = ctx == null? list() : ctx.plan();
		return mapIntoList(plans, p -> visitPlan(p));
	}

	@Override
	public JoinLinkedList<MyRule> visitMyRuleList(MyRuleListContext ctx) {
		List<MyRuleContext> rules = ctx == null? list() : ctx.myRule();
		return new JoinLinkedList<MyRule>("; ", mapIntoList(rules, r -> visitMyRule(r)));
	}

	@Override
	public Object visitMyRule(MyRuleContext ctx) {
		return new MyRule(visitGoalListPlus(ctx.goalListPlus()), visitGoalList(ctx.goalList()));
	}

	@Override
	public JoinLinkedList<MyGoal> visitGoalListPlus(GoalListPlusContext ctx) {
		List<GoalContext> goals = ctx == null? list() : ctx.goal();
		return new JoinLinkedList<MyGoal>(", ", mapIntoList(goals, g -> (MyGoal) visitGoal(g)));
	}

	@Override
	public JoinLinkedList<MyGoal> visitGoalList(GoalListContext ctx) {
		List<GoalContext> goals = ctx == null? list() : ctx.goal();
		return new JoinLinkedList<MyGoal>(", ", mapIntoList(goals, g -> (MyGoal) visitGoal(g)));
	}

	@Override
	public Object visitContingentGoal(ContingentGoalContext ctx) {
		return new MyContingentGoal(ctx.Identifier().getText());
	}

	@Override
	public Object visitPlainGoal(PlainGoalContext ctx) {
		return new MyGoal(ctx.getText());
	}

}
