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

import static com.sri.ai.util.Util.list;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Collection;

import com.google.common.annotations.Beta;

/**
 * A default/simple implementation of the ConsoleIterator interface.
 * 
 * @author braz
 */
@Beta
public class DefaultConsoleIterator implements ConsoleIterator {

	private BufferedReader console   = new BufferedReader(new InputStreamReader(System.in));
	private PrintWriter    outWriter = new PrintWriter(System.out, true);
	private PrintWriter    errWriter = new PrintWriter(System.err, true);

	private String prompt = "> ";
	private Collection<String> enders = list("end", "bye", "good bye", "goodbye", "quit", "exit", "hasta la vista, baby", "adios", "hasta luego", "arrivederci", "auf wiedersehen", "ciao", "a bien tot", "adieu", "au revoir", "adeus", "tchau");
	private String answer;

	public DefaultConsoleIterator() {
	}

	public DefaultConsoleIterator(String prompt, Collection<String> enders) {
		this.prompt = prompt;
		this.enders = enders;
	}
	
	@Override
	public PrintWriter getOutputWriter() {
		return outWriter;
	}
	
	@Override
	public PrintWriter getErrorWriter() {
		return errWriter;
	}

	@Override
	public boolean hasNext() {
		getOutputWriter().print(prompt);
		getOutputWriter().flush();
		try {
			String reply = console.readLine();
			if (enders.contains(reply)) {
				return false;
			}
			answer = reply;
		}
		catch (IOException ioe) {
			
		}
		return true;
	}

	@Override
	public String next() {
		return answer;
	}

	@Override
	public String currentString() {
		return answer;
	}

	@Override
	public String getPrompt() {
		return prompt;
	}

	@Override
	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}

	@Override
	public Collection<String> getEnders() {
		return enders;
	}

	@Override
	public void setEnders(Collection<String> enders) {
		this.enders = enders;
	}
}