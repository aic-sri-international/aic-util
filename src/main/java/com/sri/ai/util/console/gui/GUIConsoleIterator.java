/*
 * Copyright (c) 2016, SRI International
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
package com.sri.ai.util.console.gui;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

import com.google.common.annotations.Beta;
import com.sri.ai.util.console.ConsoleIterator;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

@Beta
public class GUIConsoleIterator extends Application implements ConsoleIterator {
	private static GUIConsoleController _controller;
	static {
		Thread instantiate = new Thread(() -> Application.launch(GUIConsoleIterator.class));
		instantiate.start();
	}
	
	private String answer;
	
	public GUIConsoleIterator() {
	}
	
	//
	// START ConsoleIterator
	@Override
	public PrintWriter getOutputWriter() {
		waitForController();
		return _controller.getOutputWriter();
	}
	
	@Override
	public PrintWriter getErrorWriter() {
		waitForController();
		return _controller.getErrorWriter();
	}
	
	@Override
	public boolean hasNext() {
		waitForController();
		answer = _controller.getNextCommand();
		return answer != null;
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
		waitForController();
		return _controller.getPrompt();
	}

	@Override 
	public void setPrompt(String prompt) {
		waitForController();
		_controller.setPrompt(prompt);
	}

	@Override
	public Collection<String> getEnders() {
		waitForController();
		return _controller.getEnders();
	}

	@Override
	public void setEnders(Collection<String> enders) {
		waitForController();
		_controller.setEnders(enders);
	}
	// END ConsoleIterator
	//
	
	@Override
	public void start(Stage primaryStage) throws IOException {
		FXMLLoader rootLoader = new FXMLLoader(getClass().getResource("guiconsole.fxml"));
		Parent root = rootLoader.load();
		_controller  = rootLoader.getController();
		Scene scene = new Scene(root, 1024, 768);
		primaryStage.setTitle("Console");
        primaryStage.setScene(scene);
        primaryStage.show();
	}

	public static void main(String[] args) {
		ConsoleIterator consoleIterator = new GUIConsoleIterator();
		while (consoleIterator.hasNext()) {
			// Just echo back
			consoleIterator.getOutputWriter().println(consoleIterator.next());
		}
	}
	
	//
	// PRIVATE
	private void waitForController() {
		while (_controller == null) {
			try {
				Thread.currentThread().wait(10);
			}
			catch (Throwable t) {
				// t.printStackTrace();
			}
		}
	}
}
