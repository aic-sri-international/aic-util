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
package com.sri.ai.util.console;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.google.common.annotations.Beta;

/**
 * An iterator ranging over lines entered on the console. Properties prompt and
 * ender can be set, defining which prompt appears, and which strings ends the
 * iterator.
 * 
 * @author braz
 */
@Beta
public interface ConsoleIterator extends Iterator<String> {
	
	List<String> DEFAULT_ENDERS = Collections.unmodifiableList(Arrays.asList(
		    "end", 
		    "bye", 
		    "good bye", 
		    "goodbye", 
		    "quit", "exit", 
		    "hasta la vista, baby", 
		    "adios", 
		    "hasta luego", 
		    "arrivederci", 
		    "auf wiedersehen", 
		    "ciao", 
		    "a bien tot", 
		    "adieu", 
		    "au revoir", 
		    "adeus", 
		    "tchau"));

	@Override
	default void remove() throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}
	
	PrintWriter getOutputWriter();
	
	PrintWriter getErrorWriter();
	
	/**
	 * Gives access to current string without iterating.
	 * 
	 * @return the current string without iterating.
	 */
	String currentString();

	String getPrompt();

	void setPrompt(String prompt);

	Collection<String> getEnders();

	void setEnders(Collection<String> enders);
}
