package com.sri.ai.util.planning.dnf.core;

import static com.sri.ai.util.Util.join;
import static com.sri.ai.util.Util.set;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import com.sri.ai.util.planning.dnf.api.Conjunction;
import com.sri.ai.util.planning.dnf.api.DNF;

public class DefaultConjunction<L> implements Conjunction<L> {

	private Set<? extends L> literals;
	
	@SafeVarargs
	public DefaultConjunction(L... goals) {
		this(new LinkedHashSet<>(Arrays.asList(goals)));
	}

	public DefaultConjunction(Set<? extends L> goals) {
		this.literals = goals;
	}

	@Override
	public Set<? extends L> getGoals() {
		return literals;
	}

	@Override
	public DNF<L> conjoin(DNF<L> another) {
		Set<Conjunction<L>> conjunctions = set();
		for (Conjunction<L> conjunction : another.getConjunctions()) {
			Conjunction<L> conjunctionConjoined = this.conjoin(conjunction);
			conjunctions.add(conjunctionConjoined);
		}
		return new DefaultDNF<L>(conjunctions);
	}

	@Override
	public Conjunction<L> conjoin(Conjunction<L> another) {
		Set<L> literals = set();
		literals.addAll(another.getGoals());
		return new DefaultConjunction<L>(literals);
	}

	@Override
	public boolean isTrue() {
		boolean result = getGoals().isEmpty();
		return result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((literals == null) ? 0 : literals.hashCode());
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
		DefaultConjunction other = (DefaultConjunction) obj;
		if (literals == null) {
			if (other.literals != null)
				return false;
		} else if (!literals.equals(other.literals))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return join("and", getGoals());
	}

}
