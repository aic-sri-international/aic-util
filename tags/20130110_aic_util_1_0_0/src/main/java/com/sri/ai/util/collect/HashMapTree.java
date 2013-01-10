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
package com.sri.ai.util.collect;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.google.common.annotations.Beta;
import com.sri.ai.util.Util;

/**
 * An implementation of {@link Tree} with HashMaps.
 * 
 * @author braz
 */
@Beta
public class HashMapTree extends HashMap implements Tree {
	private static final long serialVersionUID = 1L;
	//
	private boolean valid = false;

	@Override
	public Tree get(Object key) {
		return (Tree) super.get(key);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean put(Iterator path) {
		boolean result = false;
		if (path.hasNext()) {
			Object nextKey = path.next();
			result = result || !containsKey(nextKey);
			HashMapTree subTree = (HashMapTree) Util
					.getValuePossiblyCreatingIt(this, nextKey,
							HashMapTree.class);
			boolean subResult = subTree.put(path);
			return result || subResult;
		} 
		else {
			valid = true; // indicates that path ending here is valid.
		}
		return result;
	}

	private static class DefaultGetResult implements GetResult {
		public List consumedElements = new LinkedList();
		public List validPath = new LinkedList();
		public boolean valid = true;

		public DefaultGetResult() {
		}

		@SuppressWarnings("unchecked")
		@Override
		public List getConsumedElements() {
			return consumedElements;
		}

		@Override
		public boolean isValid() {
			return valid;
		}

		@SuppressWarnings("unchecked")
		@Override
		public List getValidPath() {
			if (isValid()) {
				return validPath;
			}
			return null;
		}
	}

	@Override
	public GetResult get(Iterator path) {
		return get(path, new DefaultGetResult());
	}

	@SuppressWarnings("unchecked")
	private GetResult get(Iterator path, DefaultGetResult result) {
		HashMapTree subTree = null;
		Object nextKey = null;

		if (path.hasNext()) {
			nextKey = path.next();
			result.consumedElements.add(nextKey);
			subTree = (HashMapTree) get(nextKey);
		}

		if (subTree == null) { // if path is not over, should go somewhere else
			result.valid = valid;
			return result;
		}

		List thisValidPath = null;
		if (valid) {
			thisValidPath = new LinkedList(result.validPath);
			// we save the current valid path in case the sub-tree does not
			// succeed.
		}

		result.validPath.add(nextKey);
		subTree.get(path, result);

		if (!result.isValid() && valid) {
			result.validPath = thisValidPath;
			result.valid = true;
		}
		return result;
	}
}
