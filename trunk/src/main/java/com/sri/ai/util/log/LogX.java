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

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.MDC;
import org.slf4j.Marker;
import org.slf4j.ext.LoggerWrapper;
import org.slf4j.profiler.Profiler;

import com.google.common.annotations.Beta;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * A Class for providing high level log output representing the execution trace
 * of a program.
 * 
 * @author oreilly
 * 
 */
@Beta
public class LogX extends LoggerWrapper {
	private static final String MDC_KEY_SUFFIX_LOGX_TRACE_LEVEL       = "::LogX:Trace:Level";
	private static final String MDC_KEY_SUFFIX_LOGX_PROFILE_INFO      = "::LogX:Profile:Info";
	private static final String MDC_KEY_SUFFIX_LOGX_ROOT_PROFILE_INFO = "::LogX:RootProfile:Info";
	private static final LogX _defaultLogX = LogXFactory.getLogX(LogX.class);
	//
	private Cache<Thread, List<Profiler>> activeProfilers          = CacheBuilder.newBuilder().weakKeys().build();
	private Cache<Thread, Long>           activeRootProfilerStart  = CacheBuilder.newBuilder().weakKeys().build();

	/**
	 * 
	 * @return the default LogX instance, intended to be used for global logging
	 *         information.
	 */
	public static LogX getDefaultLogX() {
		return _defaultLogX;
	}

	/**
	 * 
	 * @param logxName
	 *            the name of the LogX logger.
	 * @return the MDC key for the loggers passed in trace level.
	 */
	public static String getMDCTraceLevelKey(String logxName) {
		return logxName + MDC_KEY_SUFFIX_LOGX_TRACE_LEVEL;
	}

	/**
	 * 
	 * @param logxName
	 *            the name of the LogX logger.
	 * @return the MDC key for the loggers passed in profile information.
	 */
	public static String getMDCProfileInfoKey(String logxName) {
		return logxName + MDC_KEY_SUFFIX_LOGX_PROFILE_INFO;
	}
	
	/**
	 * 
	 * @param logxName
	 *            the name of the LogX logger.
	 * @return the MDC key for the loggers passed in root profile information.
	 */
	public static String getMDCRootProfileInfoKey(String logxName) {
		return logxName + MDC_KEY_SUFFIX_LOGX_ROOT_PROFILE_INFO;
	}

	/**
	 * 
	 * @param logxName
	 *            the name of the LogX logger.
	 * @return the trace level associated with the passed in logger. If no trace
	 *         level defined returns 0 by default.
	 */
	public static final Integer getTraceLevel(String logxName) {
		Integer level = 0;

		String slevel = MDC.get(getMDCTraceLevelKey(logxName));
		if (null != slevel) {
			level = Integer.parseInt(slevel);
		}

		return level;
	}

	/**
	 * @param logxName
	 *            the name of the LogX logger.
	 * @return optional profiler information, time in nanoseconds since the
	 *         process started, associated with the logger. null returned if no
	 *         profiler information registered.
	 */
	public static final Long getProfileInfo(String logxName) {
		Long profilerInfo = null;

		String sProfileInfo = MDC.get(getMDCProfileInfoKey(logxName));
		if (sProfileInfo != null) {
			profilerInfo = Long.parseLong(sProfileInfo);
		}

		return profilerInfo;
	}
	
	/**
	 * @param logxName
	 *            the name of the LogX logger.
	 * @return optional root profiler information, time in nanoseconds since the
	 *         process started, associated with the logger. null returned if no
	 *         profiler information registered.
	 */
	public static final Long getRootProfileInfo(String logxName) {
		Long rootProfilerInfo = null;

		String sRootProfileInfo = MDC.get(getMDCRootProfileInfoKey(logxName));
		if (sRootProfileInfo != null) {
			rootProfilerInfo = Long.parseLong(sRootProfileInfo);
		}

		return rootProfilerInfo;
	}

	/**
	 * Indent the trace output a level, for the default Execution Trace Logger.
	 * 
	 * @param msg
	 *            the message to be logged, optionally parameterized with args
	 *            (see slf4j APIs for details).
	 * @param args
	 *            the optional parameterized values for the message.
	 */
	public static void in(String msg, Object... args) {
		in(null, msg, args);
	}
	
	/**
	 * Indent the trace output a level, for the default Execution Trace Logger.
	 * 
	 * @param marker
	 * 	          the marker data specific to this log statement.
	 * @param msg
	 *            the message to be logged, optionally parameterized with args
	 *            (see slf4j APIs for details).
	 * @param args
	 *            the optional parameterized values for the message.
	 */
	public static void in(Marker marker, String msg, Object... args) {
		getDefaultLogX().indent(marker, msg, args);
	}

	/**
	 * Un-indent the trace output a level, for the default Execution Trace
	 * Logger.
	 * 
	 * @param msg
	 *            the message to be logged, optionally parameterized with args
	 *            (see slf4j APIs for details).
	 * @param args
	 *            the optional parameterized values for the message.
	 */
	public static void out(String msg, Object... args) {
		out(null, msg, args);
	}
	
	/**
	 * Un-indent the trace output a level, for the default Execution Trace
	 * Logger.
	 * 
	 * @param marker
	 *            the marker data specific to this log statement.
	 * @param msg
	 *            the message to be logged, optionally parameterized with args
	 *            (see slf4j APIs for details).
	 * @param args
	 *            the optional parameterized values for the message.
	 */
	public static void out(Marker marker, String msg, Object... args) {
		getDefaultLogX().outdent(marker, msg, args);
	}

	/**
	 * Output a log message at the current trace level to the default Execution
	 * Trace Logger.
	 * 
	 * @param msg
	 *            the message to be logged, optionally parameterized with args
	 *            (see slf4j APIs for details).
	 * @param args
	 *            the optional parameterized values for the message.
	 */
	public static void log(String msg, Object... args) {
		log(null, msg, args);
	}
	
	/**
	 * Output a log message at the current trace level to the default Execution
	 * Trace Logger.
	 * 
	 * @param marker
	 *            the marker data specific to this log statement. 
	 * @param msg
	 *            the message to be logged, optionally parameterized with args
	 *            (see slf4j APIs for details).
	 * @param args
	 *            the optional parameterized values for the message.
	 */
	public static void log(Marker marker, String msg, Object... args) {
		getDefaultLogX().trace(marker, msg, args);
	}

	//
	// PUBLIC METHODS
	//

	public LogX(Logger logger) {
		this(logger, LogX.class.getName());
	}

	public LogX(Logger logger, String fqcn) {
		super(logger, fqcn);

		setTraceLevel(0);
		MDC.remove(getMDCProfileInfoKey());
		MDC.remove(getMDCRootProfileInfoKey());
	}
	
	public void indent(String msg, Object... args) {
		indent(null, msg, args);
	}

	public void indent(Marker marker, String msg, Object... args) {
		if (logger.isTraceEnabled()) {
			pushActiveProfiler();
			setTraceLevel(getTraceLevel() + 1);
			trace(marker, msg, args);
		}
	}
	
	public void outdent(String msg, Object... args) {
		outdent(null, msg, args);
	}

	public void outdent(Marker marker, String msg, Object... args) {
		if (logger.isTraceEnabled()) {
			// Stop the nested profiler at this trace level
			popActiveProfiler();
			setTraceLevel(getTraceLevel() - 1);
			// Output the message
			trace(marker, msg, args);
			MDC.remove(getMDCProfileInfoKey());
			MDC.remove(getMDCRootProfileInfoKey());
		}
	}

	public int getTraceLevel() {
		int traceLevel = 0;
		String strTraceLevel = MDC.get(getMDCTraceLevelKey());
		if (strTraceLevel != null) {
			traceLevel = Integer.parseInt(strTraceLevel);
		}
		return traceLevel;
	}

	public void setTraceLevel(int level) {
		if (0 > level) {
			throw new IllegalArgumentException("Trace level must be >= 0.");
		}
		MDC.put(getMDCTraceLevelKey(), "" + level);
	}

	//
	// PACKAGE PROTECTED
	//
	String getMDCTraceLevelKey() {
		return getMDCTraceLevelKey(getName());
	}

	String getMDCProfileInfoKey() {
		return getMDCProfileInfoKey(getName());
	}
	
	String getMDCRootProfileInfoKey() {
		return getMDCRootProfileInfoKey(getName());
	}
	
	//
	//  PRIVATE
	//
	private void pushActiveProfiler() {
		List<Profiler> thisThreadsProfilers = activeProfilers.getIfPresent(Thread.currentThread());
		if (thisThreadsProfilers == null) {
			thisThreadsProfilers = new ArrayList<Profiler>();
			activeProfilers.put(Thread.currentThread(), thisThreadsProfilers);
		}
		Profiler cp = null;
		
		if (thisThreadsProfilers.size() == 0) {
			cp = new Profiler(""+getTraceLevel());
			activeRootProfilerStart.put(Thread.currentThread(), System.nanoTime());
		} 
		else {
			cp = thisThreadsProfilers.get(thisThreadsProfilers.size() - 1)
				.startNested("" + getTraceLevel());
		}
		thisThreadsProfilers.add(cp);
	}
	
	private void popActiveProfiler() {
		List<Profiler> thisThreadsProfilers = activeProfilers.getIfPresent(Thread.currentThread());
		if (thisThreadsProfilers != null) {
			Profiler cp = thisThreadsProfilers.remove(thisThreadsProfilers.size() - 1);
			cp.stop();
			MDC.put(getMDCProfileInfoKey(), "" + cp.elapsedTime());
			// If not at the root profiler, then track the root information too.
			if (thisThreadsProfilers.size() > 0) {
				MDC.put(getMDCRootProfileInfoKey(), "" + (System.nanoTime() - activeRootProfilerStart.getIfPresent(Thread.currentThread())));
			}
			// Ensure clean up fully.
			if (thisThreadsProfilers.size() == 0) {
				activeProfilers.invalidate(Thread.currentThread());
				activeRootProfilerStart.invalidate(Thread.currentThread());
			}
		}
	}
}
