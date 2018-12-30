package com.sri.ai.util.planning.dnf.api;

import java.util.Set;

public interface ConjunctiveClause<L> {

	Set<? extends L> getLiterals();

	DNF<L> conjoin(DNF<L> another);

	ConjunctiveClause<L> conjoin(ConjunctiveClause<L> another);

	boolean isTrue();

}
