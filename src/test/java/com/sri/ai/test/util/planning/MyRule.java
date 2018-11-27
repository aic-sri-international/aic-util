package com.sri.ai.test.util.planning;

import static com.sri.ai.util.Util.join;
import static com.sri.ai.util.Util.println;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.sri.ai.util.planning.api.Goal;
import com.sri.ai.util.planning.api.Rule;
import com.sri.ai.util.planning.api.State;

public class MyRule implements Rule {

	List<Goal> consequents;
	List<Goal> antecedents;
	
	public MyRule(List<Goal> consequents, List<Goal> antecedents) {
		this.consequents = consequents;
		this.antecedents = antecedents;
	}

	@Override
	public String toString() {
		return join(consequents) + " <= " + join(antecedents);
	}

	@Override
	public double getEstimatedSuccessWeight() {
		return 0;
	}

	@Override
	public void execute(State state) {
		println(this);
	}

	@Override
	public void reward(double reward) {
	}

	@Override
	public Collection<? extends Goal> getAntecendents() {
		return antecedents;
	}

	@Override
	public Collection<? extends Goal> getConsequents() {
		return consequents;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((antecedents == null) ? 0 : antecedents.hashCode());
		result = prime * result + ((consequents == null) ? 0 : consequents.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MyRule other = (MyRule) obj;
		if (antecedents == null) {
			if (other.antecedents != null)
				return false;
		} else if (!antecedents.equals(other.antecedents))
			return false;
		if (consequents == null) {
			if (other.consequents != null)
				return false;
		} else if (!consequents.equals(other.consequents))
			return false;
		return true;
	}

	public static Rule rule(LinkedList<Goal> consequent, LinkedList<Goal> antecedent) {
		return new MyRule(consequent, antecedent);
	}
}