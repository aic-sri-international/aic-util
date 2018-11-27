package com.sri.ai.util.planning.dnf.api;

import java.util.Set;

public interface Conjunction<L> {

	Set<? extends L> getGoals();

	DNF<L> conjoin(DNF<L> another);

	Conjunction<L> conjoin(Conjunction<L> another);

	boolean isTrue();

}
