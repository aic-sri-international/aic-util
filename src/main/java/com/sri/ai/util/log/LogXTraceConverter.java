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

import com.google.common.annotations.Beta;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * A logback <a
 * href="http://logback.qos.ch/manual/layouts.html#customConversionSpecifier"
 * >Custom Conversion Specifier</a> that can be used in a pattern layout to pad
 * LogX trace message correctly and to suffix profiler information when
 * appropriate (i.e. when exiting a level in the trace). This can be configured
 * in a logback configuration file as follows:
 * 
 * <pre>
 * &lt;conversionRule conversionWord="logxtracemsg" converterClass="com.sri.ai.util.log.LogXTraceConverter" /&gt;
 * </pre>
 * 
 * and used in a Pattern Layout in the following manner:
 * 
 * <pre>
 * &lt;Pattern&gt;%50.50class{0}|%logxtracemsg%n&lt;/Pattern&gt;
 * </pre>
 * 
 * @author oreilly
 */
@Beta
public class LogXTraceConverter extends ClassicConverter {

	@Override
	public String convert(ILoggingEvent event) {
		StringBuilder sb = new StringBuilder();
		
		sb.append(padForTraceLevel(LogX.getTraceLevel(event.getLoggerName())));

		Long profileInfo = LogX.getProfileInfo(event.getLoggerName());
		if (null != profileInfo) {
			sb.append("[");
			// Convert nanoseconds to milliseconds
			sb.append(profileInfo / 1000000);
			sb.append("ms");
			Long rootProfileInfo = LogX.getRootProfileInfo(event.getLoggerName());
			if (null != rootProfileInfo) {
				sb.append(", ");
				sb.append(rootProfileInfo / 1000000);
				sb.append("ms total");
			}
			sb.append("] ");
		}

		sb.append(event.getFormattedMessage());

		return sb.toString();
	}

	//
	// PRIVATE METHODS
	//
	private String padForTraceLevel(int traceLevel) {
		StringBuilder sb = new StringBuilder();
		for (int i = 1; i < traceLevel; i++) {
			sb.append("    ");
		}

		return sb.toString();
	}
}
