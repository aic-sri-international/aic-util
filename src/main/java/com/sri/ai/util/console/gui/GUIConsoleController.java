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
package com.sri.ai.util.console.gui;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import com.google.common.annotations.Beta;
import com.sri.ai.util.console.ConsoleIterator;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

@Beta
public class GUIConsoleController {
	//
	@FXML private Label promptLabel;
	@FXML private TextField commandLineTextField;
	@FXML private TextArea outputTextArea;
	//
	private List<String> commandHistory       = new LinkedList<>();
	private int          commandHistoryOffset = -1;
	private Set<String>  enders               = new LinkedHashSet<>(ConsoleIterator.DEFAULT_ENDERS);
	private boolean      exited               = false;
	
	private OutputStream outputStream = new OutputStream() {
		@Override
		public void write(int b) throws IOException {
			Platform.runLater(() -> {
				outputTextArea.appendText(new String(new byte[] {(byte) b}));
			});
		}
	};
	private PrintWriter outAndErrWriter = new PrintWriter(outputStream, true);
	
	
	public boolean isExited() {
		return exited;
	}
	
	public String getNextCommand() {
		final StringBuilder result = new StringBuilder();		
		if (!isExited()) {
			final CountDownLatch waitLatch = new CountDownLatch(1); 
			final InvalidationListener editListener = new InvalidationListener() {
				 @Override
				public void invalidated(Observable observable) {
					 if (!commandLineTextField.isEditable()) {
						 result.append(commandLineTextField.getText());
						 commandLineTextField.setText("");
						 waitLatch.countDown();
					 }
				 }
			};
			Platform.runLater(() -> {
				commandLineTextField.editableProperty().addListener(editListener);
				commandLineTextField.setEditable(true);
				commandLineTextField.requestFocus();
			});
			
			try {
				waitLatch.await();
			}
			catch (Throwable t) {
				t.printStackTrace();
			}
			commandLineTextField.editableProperty().removeListener(editListener);
		}
		return result.length() > 0 ? result.toString().trim() : null;
	}
	
	public PrintWriter getOutputWriter() {
		return outAndErrWriter;
	}
	
	public PrintWriter getErrorWriter() {
		return outAndErrWriter;
	}
	
	public String getPrompt() {
		return promptLabel.getText();
	}
	
	public void setPrompt(String prompt) {
		promptLabel.setText(prompt);
	}
	
	public Collection<String> getEnders() {
		return enders;
	}

	public void setEnders(Collection<String> enders) {
		this.enders.clear();
		this.enders.addAll(enders);
	}
	
	
	@FXML
	private void initialize() {
		promptLabel.setText(ConsoleIterator.DEFAULT_PROMPT);
		
		commandLineTextField.setEditable(false);
		
		commandLineTextField.setOnAction(value -> {
			String command = commandLineTextField.getText().trim();
			if (command.length() > 0) {
				if (enders.contains(command)) {
					exited = true;
					Platform.exit();
				}
				commandHistory.add(0, command);
				commandHistoryOffset = 0;
				outputTextArea.appendText(promptLabel.getText());
				outputTextArea.appendText(command);
				outputTextArea.appendText("\n");
				commandLineTextField.setEditable(false);
			}
		});
		
		commandLineTextField.setOnKeyPressed(keyEvent -> {
			if (commandHistory.size() > 0) {
				if (keyEvent.getCode() == KeyCode.UP) {
					commandLineTextField.setText(commandHistory.get(commandHistoryOffset));
					if (commandHistoryOffset < (commandHistory.size()-1)) {
						commandHistoryOffset++;
					}
				}
				else if (keyEvent.getCode() == KeyCode.DOWN) {
					if (commandHistoryOffset > 0) {
						commandHistoryOffset--;
					}
					commandLineTextField.setText(commandHistory.get(commandHistoryOffset));
				}
			}
		});
	}
}
