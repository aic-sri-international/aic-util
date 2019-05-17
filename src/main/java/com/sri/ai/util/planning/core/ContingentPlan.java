package com.sri.ai.util.planning.core;

import static com.sri.ai.util.Util.list;

import com.sri.ai.util.base.Pair;
import com.sri.ai.util.planning.api.ContingentGoal;
import com.sri.ai.util.planning.api.Plan;
import com.sri.ai.util.planning.api.State;
import com.sri.ai.util.tree.DefaultTree;
import com.sri.ai.util.tree.Tree;

/**
 * A {@link Plan} that tests, during execution, whether a {@link ContingentGoal} is satisfied
 * by the current {@link State}, and executes one or the other sub-plan as a result.
 * 
 * @author braz
 *
 */
public class ContingentPlan extends AbstractCompoundPlan {

	private ContingentGoal contingentGoal;
	private Plan thenBranch;
	private Plan elseBranch;
	
	public ContingentPlan(ContingentGoal contingentGoal, Plan thenBranch, Plan elseBranch) {
		super(list(thenBranch, elseBranch));
		this.contingentGoal = contingentGoal;
		this.thenBranch = thenBranch;
		this.elseBranch = elseBranch;
	}
	
	public static ContingentPlan contingent(ContingentGoal contingentGoal, Plan thenBranch, Plan elseBranch) {
		return new ContingentPlan(contingentGoal, thenBranch, elseBranch);
	}

	@Override
	public double computeEstimatedSuccessWeight() {
		if (thenBranch.isDeterministic() && elseBranch.isDeterministic()) {
			return Plan.MAXIMUM_ESTIMATED_SUCCESS_WEIGHT;
		}
		else {
			// TODO can we do better? I did not use max because we do not want
			// the plan to indicate absolute success if only one of its branches can do it.
			return Math.min(thenBranch.getEstimatedSuccessWeight(), elseBranch.getEstimatedSuccessWeight());
		}
	}

	@Override
	public boolean isFailedPlan() {
		return false;
	}

	@Override
	public State execute(State state) {
		State result;
		Pair<Boolean, State> isSatisfiedAndstateAfterCheckingIfGoalIsSatisfied = contingentGoal.isSatisfied(state);
		Boolean isSatisfied = isSatisfiedAndstateAfterCheckingIfGoalIsSatisfied.first;
		State stateAfterSatisfiabilityDecision = isSatisfiedAndstateAfterCheckingIfGoalIsSatisfied.second;
		Plan branchToFollow = isSatisfied? thenBranch : elseBranch;
		result = branchToFollow.execute(stateAfterSatisfiabilityDecision);
		return result;
	}

	@Override
	public void reward(double reward) {
		// TODO can we do better?
		thenBranch.reward(reward);
		elseBranch.reward(reward);
	}

	@Override
	public String operatorName() {
		return "if (" + contingentGoal + ")";
	}
	
	@Override
	public String toString() {
		return operatorName() + " then " + thenBranch + " else " + elseBranch;
	}

	@Override
	public Tree<String> stringTree() {
		Tree<String> thenTree = new DefaultTree<>("then", list(thenBranch.stringTree()));
		Tree<String> elseTree = new DefaultTree<>("else", list(elseBranch.stringTree()));
		Tree<String> result = new DefaultTree<String>(operatorName(), list(thenTree, elseTree));
		return result;
	}

}
