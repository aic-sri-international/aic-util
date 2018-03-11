/*
 * Copyright (c) 2015, SRI International
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
 * Neither the name of the aic-praise nor the names of its
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
package com.sri.ai.util.livesets.core.lazy.memoryless;

import static com.sri.ai.util.Util.list;
import static com.sri.ai.util.Util.thereExists;

import java.util.Collection;

import com.google.common.base.Predicate;
import com.sri.ai.util.livesets.api.LiveSet;

public class ExtensionalLiveSet<T> implements LiveSet<T> {
	
	private Collection<T> elements;
	
	public ExtensionalLiveSet(Collection<T> elements) {
		this.elements = elements;
	}
	
	public boolean contains(T element) {
		boolean result = elements.contains(element);
		return result;
	}
	
	public static <T> ExtensionalLiveSet<T> liveSet(Collection<T> elements) {
		return new ExtensionalLiveSet<>(elements);
	}
	
	public static <T> ExtensionalLiveSet<T> liveSet(T element) {
		return new ExtensionalLiveSet<>(list(element));
	}

	@Override
	public boolean thereIsAnElementSatisfying(Predicate<T> predicate) {
		boolean result = thereExists(elements, predicate);
		return result;
	}
}