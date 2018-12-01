package com.sri.ai.util.planning.dnf.core;

import static com.sri.ai.util.Util.join;
import static com.sri.ai.util.Util.set;
import static com.sri.ai.util.Util.setFrom;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import com.sri.ai.util.Util;
import com.sri.ai.util.planning.dnf.api.Conjunction;
import com.sri.ai.util.planning.dnf.api.DNF;

public class DefaultDNF<T> implements DNF<T> {
	
	Set<? extends Conjunction<T>> conjunctions;

	@SafeVarargs
	public DefaultDNF(Conjunction<T>... conjunctions) {
		this(new LinkedHashSet<>(Arrays.asList(conjunctions)));
	}

	public DefaultDNF(Set<? extends Conjunction<T>> conjunctions) {
		this.conjunctions = conjunctions;
	}

	@Override
	public DNF<T> and(DNF<T> another) {
		Set<Conjunction<T>> conjoinedConjunctions = set();
		for (Conjunction<T> conjunction : getConjunctions()) {
			DNF<T> conjunctionAndAnother = conjunction.conjoin(another);
			conjoinedConjunctions.addAll(conjunctionAndAnother.getConjunctions());
		}
		return new DefaultDNF<T>(conjoinedConjunctions);
	}

	@Override
	public DNF<T> or(DNF<T> another) {
		Set<Conjunction<T>> unionOfConjunctions = setFrom(conjunctions);
		unionOfConjunctions.addAll(another.getConjunctions());
		return new DefaultDNF<T>(unionOfConjunctions);
	}

	@Override
	public boolean isTrue() {
		boolean result = 
				getConjunctions().size() == 1
				&& Util.getFirst(getConjunctions()).isTrue();
		return result;
	}

	@Override
	public boolean isFalse() {
		boolean result = getConjunctions().isEmpty();
		return result;
	}

	@Override
	public Set<? extends Conjunction<T>> getConjunctions() {
		return conjunctions;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((conjunctions == null) ? 0 : conjunctions.hashCode());
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
		if (conjunctions == null) {
			if (other.conjunctions != null)
				return false;
		} else if (!conjunctions.equals(other.conjunctions))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return isFalse()? "false" : join("or", getConjunctions());
	}

}
