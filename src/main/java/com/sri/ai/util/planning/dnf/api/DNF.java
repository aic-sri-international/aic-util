package com.sri.ai.util.planning.dnf.api;

import java.util.Set;

public interface DNF<L> {

	DNF<L> and(DNF<L> another);

	DNF<L> or(DNF<L> dnfForAntecedent);

	boolean isTrue();

	boolean isFalse();

	Set<? extends ConjunctiveClause<L>> getConjunctiveClauses();

}
