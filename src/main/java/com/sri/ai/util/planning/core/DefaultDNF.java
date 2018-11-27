package com.sri.ai.util.planning.core;

import java.util.Collection;

import com.sri.ai.util.planning.api.Conjunction;
import com.sri.ai.util.planning.api.DNF;

public class DefaultDNF implements DNF {

	public DefaultDNF(Conjunction... conjunction) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public DNF conjoin(DNF another) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DNF or(DNF another) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isTrue() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isFalse() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Collection<? extends Conjunction> getConjunctions() {
		// TODO Auto-generated method stub
		return null;
	}

}
