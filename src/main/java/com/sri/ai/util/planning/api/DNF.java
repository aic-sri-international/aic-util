package com.sri.ai.util.planning.api;

import java.util.Collection;

public interface DNF {

	DNF conjoin(DNF another);

	DNF or(DNF dnfForAntecedent);

	boolean isTrue();

	boolean isFalse();

	Collection<? extends Conjunction> getConjunctions();

}
