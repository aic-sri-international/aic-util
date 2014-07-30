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
import java.util.Iterator;
import java.util.LinkedList;

import com.google.common.annotations.Beta;
import com.sri.ai.util.base.BinaryProcedure;
import com.sri.ai.util.rangeoperation.api.DependencyAwareEnvironment;
import com.sri.ai.util.rangeoperation.api.Range;

/**
 * Provides basic Range functionality, only leaving to the user the task of defining
 * {@link #evaluate()}, which should provide a new iterator over a range of values.
 */
@Beta
public abstract class AbstractRange implements Range {
	/** Builds a range with a given variable name. */
	public AbstractRange(String name) {
		this.name = name;
	}
	@Override
	public String getName() {
		return name;
	}
	@Override
	public void setEnvironment(DependencyAwareEnvironment environment) {
		this.environment = environment;
	}
	@Override
	public void initialize() {
		iterator = (Iterator<?>) apply();
	}
	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}
	@Override
	public void next() {
		Object value = iterator.next();
		environment.put(name, value);
		for (BinaryProcedure<String, Object> listener : listeners) {
			listener.apply(name, value);
		}
	}
	@Override
	public void addIterationListener(BinaryProcedure<String, Object> listener) {
		listeners.add(listener);
	}
	protected String name;
	protected Iterator<?> iterator;
	protected DependencyAwareEnvironment environment;
	protected Collection<BinaryProcedure<String, Object>> listeners = new LinkedList<BinaryProcedure<String, Object>>();
}