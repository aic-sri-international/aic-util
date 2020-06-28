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
package com.sri.ai.util.log;

import org.slf4j.LoggerFactory;

import com.google.common.annotations.Beta;

/**
 * A factory for instantiation instances of LogXs.
 * 
 * @author oreilly
 *
 */
@Beta
public class LogXFactory {
	/**
	 * Get a LogX instance by name.
	 * 
	 * @param name
	 *            the identifying name.
	 * @return the LogX instance corresponding to the given name.
	 */
	public static LogX getLogX(String name) {
		return new LogX(LoggerFactory.getLogger(name));
	}

	/**
	 * Get a LogX instance by name.
	 * 
	 * @param name
	 *            the identifying name.
	 * @param fqcn
	 *            the fully qualified class name of the caller.
	 * @return the LogX instance corresponding to the given name.
	 */
	public static LogX getLogX(String name, String fqcn) {
		return new LogX(LoggerFactory.getLogger(name), fqcn);
	}

	/**
	 * Get a new LogX instance by class. The returned LogX will be named after
	 * the class.
	 * 
	 * @param clazz
	 *            the identifying class.
	 * @return a new LogX instance named after the given class.
	 */
	public static LogX getLogX(Class<?> clazz) {
		return getLogX(clazz.getName());
	}

	/**
	 * Get a new LogX instance by class. The returned LogX will be named after
	 * the class.
	 * 
	 * @param clazz
	 *            the identifying class.
	 * @param fqcn
	 *            the fully qualified class name of the caller.
	 * @return a new LogX instance named after the given class.
	 */
	public static LogX getLogX(Class<?> clazz, String fqcn) {
		return getLogX(clazz.getName(), fqcn);
	}
}
