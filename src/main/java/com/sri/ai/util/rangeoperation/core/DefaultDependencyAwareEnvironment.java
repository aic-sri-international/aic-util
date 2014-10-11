/*
 * Copyright (c) 2013, SRI International
 * All rights reserved.
 * Licensed under the The BSD 3-Clause License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * 
 * http://opensource.org/licenses/BSD-3-Clause
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
 * Neither the name of the aic-util nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) 
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.sri.ai.util.rangeoperation.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;

import com.google.common.annotations.Beta;
import com.sri.ai.util.Util;
import com.sri.ai.util.rangeoperation.api.DAEFunction;
import com.sri.ai.util.rangeoperation.api.DependencyAwareEnvironment;

@SuppressWarnings("serial")
@Beta
public class DefaultDependencyAwareEnvironment extends TreeMap<String, Object> implements DependencyAwareEnvironment {
    
	/** Map from variables to set of variables depending on it. */
	private Map<String, Collection<String>> fromParentsToChildren = new HashMap<String, Collection<String>>();
	
	private Set<String> randomVariables = new HashSet<String>();
	
	private Stack<String> variablesBeingComputed = new Stack<String>();

	@Override
	public Object getResultOrRecompute(DAEFunction function) {
		String variable = function.toString();

		if (function.isRandom()) {
			randomVariables.add(variable);
		}
		
		if ( ! randomVariables.contains(variable) && containsKey(variable)) {
			return super.get(variable);
		}
		
		startingCalculation(variable);
		Object value = function.apply(this);
		finishedCalculation(variable, value);
		
		Object result = value;
		return result;
	}

	private void startingCalculation(String variable) {
		variablesBeingComputed.push(variable);
	}

	private void registerDependencyOfVariableBeingCurrentlyComputedOn(String variable) {
		if ( ! variablesBeingComputed.isEmpty()) {
			String beingCurrentlyComputed = variablesBeingComputed.peek();
			Util.addToCollectionValuePossiblyCreatingIt(fromParentsToChildren, variable, beingCurrentlyComputed, HashSet.class);
			if (randomVariables.contains(variable)) {
				randomVariables.add(beingCurrentlyComputed);
			}
		}
	}

	private void finishedCalculation(String variable, Object value) {
		if ( ! variablesBeingComputed.isEmpty() && ! variablesBeingComputed.peek().equals(variable)) {
			Util.fatalError("DependencyAwareEnvironment.finishedCalculation called on " + variable + " when " + variablesBeingComputed.peek() + " is the currently calculated variable.");
		}
		put(variable, value);
		variablesBeingComputed.pop();
		registerDependencyOfVariableBeingCurrentlyComputedOn(variable);
	}

	@Override
	public Object get(String variable) {
		registerDependencyOfVariableBeingCurrentlyComputedOn(variable);
		Object result = super.get(variable);
		return result;
	}

	@Override
	public Object put(String variable, Object value) {
		Object previousValue = super.get(variable);
		if ((value == null && previousValue == null) || value.equals(previousValue)) {
			return value;
		}
		removeChildrenOf(variable);
		Object result = super.put(variable, value);
		return result;
	}

	@Override
	public void remove(String variable) {
		removeChildrenOf(variable);
		super.remove(variable);
	}

	protected void removeChildrenOf(String variable) {
		Collection<String> children = fromParentsToChildren.get(variable);
		if (children != null) {
			for (String child : (children)) {
				remove(child);
			}
		}
		fromParentsToChildren.remove(variable);
	}

	///////////// CONVENIENCE METHODS ///////////////

	@Override
	public Object getOrUseDefault(String variable, Object defaultValue) {
		Object result = get(variable);
		if (result == null) {
			return defaultValue;
		}
		return result;
	}

	@Override
	public int getInt(String variable) {
		return ((Integer) get(variable)).intValue();
	}
}
