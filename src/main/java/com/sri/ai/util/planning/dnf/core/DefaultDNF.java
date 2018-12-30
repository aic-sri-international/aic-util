package com.sri.ai.util.planning.dnf.core;

import static com.sri.ai.util.Util.join;
import static com.sri.ai.util.Util.set;
import static com.sri.ai.util.Util.setFrom;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import com.sri.ai.util.Util;
import com.sri.ai.util.planning.dnf.api.ConjunctiveClause;
import com.sri.ai.util.planning.dnf.api.DNF;

public class DefaultDNF<T> implements DNF<T> {
	
	Set<? extends ConjunctiveClause<T>> conjunctiveClauses;

	@SafeVarargs
	public DefaultDNF(ConjunctiveClause<T>... conjunctiveClauses) {
		this(new LinkedHashSet<>(Arrays.asList(conjunctiveClauses)));
	}

	public DefaultDNF(Set<? extends ConjunctiveClause<T>> conjunctiveClauses) {
		this.conjunctiveClauses = conjunctiveClauses;
	}

	@Override
	public DNF<T> and(DNF<T> another) {
		Set<ConjunctiveClause<T>> conjoinedConjunctions = set();
		for (ConjunctiveClause<T> conjunctiveClause : getConjunctiveClauses()) {
			DNF<T> conjunctiveClauseAndAnother = conjunctiveClause.conjoin(another);
			conjoinedConjunctions.addAll(conjunctiveClauseAndAnother.getConjunctiveClauses());
		}
		return new DefaultDNF<T>(conjoinedConjunctions);
	}

	@Override
	public DNF<T> or(DNF<T> another) {
		Set<ConjunctiveClause<T>> unionOfConjunctions = setFrom(conjunctiveClauses);
		unionOfConjunctions.addAll(another.getConjunctiveClauses());
		return new DefaultDNF<T>(unionOfConjunctions);
	}

	@Override
	public boolean isTrue() {
		boolean result = 
				getConjunctiveClauses().size() == 1
				&& Util.getFirst(getConjunctiveClauses()).isTrue();
		return result;
	}

	@Override
	public boolean isFalse() {
		boolean result = getConjunctiveClauses().isEmpty();
		return result;
	}

	@Override
	public Set<? extends ConjunctiveClause<T>> getConjunctiveClauses() {
		return conjunctiveClauses;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((conjunctiveClauses == null) ? 0 : conjunctiveClauses.hashCode());
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
		DefaultDNF other = (DefaultDNF) obj;
		if (conjunctiveClauses == null) {
			if (other.conjunctiveClauses != null)
				return false;
		} else if (!conjunctiveClauses.equals(other.conjunctiveClauses))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return isFalse()? "false" : join("or", getConjunctiveClauses());
	}

}
