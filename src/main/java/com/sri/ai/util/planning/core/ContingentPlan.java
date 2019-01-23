package com.sri.ai.util.planning.core;

import static com.sri.ai.util.Util.list;

import com.sri.ai.util.planning.api.ContingentGoal;
import com.sri.ai.util.planning.api.Plan;
import com.sri.ai.util.planning.api.State;

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
	public double getEstimatedSuccessWeight() {
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
	public void execute(State state) {
		if (contingentGoal.isSatisfied(state)) {
			thenBranch.execute(state);
		}
		else { 
			elseBranch.execute(state);
		}
	}

	@Override
	public void reward(double reward) {
		// TODO can we do better?
		thenBranch.reward(reward);
		elseBranch.reward(reward);
	}

	@Override
	public String operatorName() {
		return "contingent[" + contingentGoal + "]";
	}

}
