package com.sri.ai.util.planning.dnf.core;

import static com.sri.ai.util.Util.join;
import static com.sri.ai.util.Util.set;
import static com.sri.ai.util.Util.setFrom;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import com.sri.ai.util.planning.dnf.api.ConjunctiveClause;
import com.sri.ai.util.planning.dnf.api.DNF;

/**
 * A conjunctive clause in which all literals are positive (atoms as opposed to negated atoms).
 * 
 * @author braz
 *
 * @param <L>
 */
public class PositiveConjunctiveClause<L> implements ConjunctiveClause<L> {

	private Set<? extends L> literals;
	
	@SafeVarargs
	public PositiveConjunctiveClause(L... literals) {
		this(new LinkedHashSet<>(Arrays.asList(literals)));
	}

	public PositiveConjunctiveClause(Set<? extends L> literals) {
		this.literals = literals;
	}

	@Override
	public Set<? extends L> getLiterals() {
		return literals;
	}

	@Override
	public DNF<L> conjoin(DNF<L> another) {
		Set<ConjunctiveClause<L>> conjunctiveClauses = set();
		for (ConjunctiveClause<L> conjunctiveClause : another.getConjunctiveClauses()) {
			ConjunctiveClause<L> conjunctionConjoined = this.conjoin(conjunctiveClause);
			conjunctiveClauses.add(conjunctionConjoined);
		}
		return new DefaultDNF<L>(conjunctiveClauses);
	}

	@Override
	public ConjunctiveClause<L> conjoin(ConjunctiveClause<L> another) {
		Set<L> literals = setFrom(this.literals);
		literals.addAll(another.getLiterals());
		return new PositiveConjunctiveClause<L>(literals);
	}

	@Override
	public boolean isTrue() {
		boolean result = getLiterals().isEmpty();
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
		PositiveConjunctiveClause other = (PositiveConjunctiveClause) obj;
		if (literals == null) {
			if (other.literals != null)
				return false;
		} else if (!literals.equals(other.literals))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return isTrue()? "true" : join("and", getLiterals());
	}

}
