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

import java.util.Iterator;
import java.util.List;

import com.google.common.annotations.Beta;

/**
 * An interface for a tree, with inclusion and retrieval of valid paths. This is
 * an efficient way of retrieving strings by incrementally testing characters
 * sequentially.
 * 
 * @author braz
 */
@Beta
public interface RandomAccessTree {
	void clear();

	RandomAccessTree getSubTree(Object key);

	/**
	 * Inserts a valid path of elements into the tree, returning
	 * <code>true</code> if new nodes had to be created.
	 * 
	 * @param path
	 *        an iterator over a path of elements.
	 * @return true if new nodes had to be created.
	 */
	boolean put(Iterator<?> path);

	/**
	 * An interface for the results of method {@link RandomAccessTree#get(Iterator)}.
	 * 
	 * @author rodrigo
	 * 
	 */
	public interface GetResult {
		/**
		 * @return the elements consumed from iterator in the {@link RandomAccessTree#get(Iterator)}.
		 */
		List<?> getConsumedElements();

		/**
		 * @return true if the consumed path has reached a information.
		 */
		boolean isValid();

		/**
		 * If found a valid path, the valid path.
		 * @return a valid path.
		 */
		List<?> getValidPath();
	}

	/**
	 * Follows path with elements from iterator's range until no progress is
	 * possible anymore (by either reaching a information or having no subtree labeled
	 * with the next element). Returns a {@link GetResult} object containing the
	 * elements consumed in the process, and whether a valid path has been found
	 * and, if so, the longest valid path found.
	 * 
	 * @param path
	 *            and iterator over the path of elements.
	 * @return a {@link GetResult} object containing the elements consumed in
	 *         the process, and whether a valid path has been found and, if so,
	 *         the longest valid path found.
	 */
	GetResult get(Iterator<?> path);
}
