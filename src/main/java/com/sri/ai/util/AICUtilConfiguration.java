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
package com.sri.ai.util;

import com.google.common.annotations.Beta;
import com.sri.ai.util.cache.CacheMap;

/**
 * Programmatic properties for the aic-util project.
 * 
 * @author oreilly
 *
 */
@Beta
public class AICUtilConfiguration extends Configuration {
	// This property is for testing purposes.	
	public static final String KEY_TEST_CONFIGURATION_SCRIPT_SETTINGS     = "aic.util.test.configuration.script.settings";
	public static final String DEFAULT_TEST_CONFIGURATION_SCRIPT_SETTINGS = "Set programmatically";
	
	//
	public static final String  KEY_BRANCH_AND_MERGE_THREADING_ENABLED                                    = "aic.util.branch.and.merge.threading.enabled";
	public static final Boolean DEFAULT_VALUE_BRANCH_AND_MERGE_THREADING_ENABLED                          = Boolean.FALSE;
	//
	public static final String  KEY_BRANCH_AND_MERGE_USE_NUMBER_PROCESSORS_FOR_THREAD_POOL_SIZE           = "aic.util.branch.and.merge.use.number.processors.for.thread.pool.size";
	public static final Boolean DEFAULT_VALUE_BRANCH_AND_MERGE_USE_NUMBER_PROCESSORS_FOR_THREAD_POOL_SIZE = Boolean.TRUE;
	// 
	public static final String  KEY_BRANCH_AND_MERGE_DELTA_NUMBER_PROCESSORS_FOR_THREAD_POOL_SIZE         = "aic.util.branch.and.merge.delta.number.processors.for.thread.pool.size";
	public static final Integer DEFAULT_VALUE_DELTA_NUMBER_PROCESSORS_FOR_THREAD_POOL_SIZE                = new Integer(0);
	// Used if BRANCH_AND_MERGE_USE_NUMBER_PROCESSORS_FOR_THREAD_POOL_SIZE is false
	public static final String  KEY_BRANCH_AND_MERGE_FIXED_THREAD_POOL_SIZE                               = "aic.util.branch.and.merge.fixed.thread.pool.size";
	public static final Integer DEFAULT_VALUE_BRANCH_AND_MERGE_FIXED_THREAD_POOL_SIZE                     = new Integer(10);
	// Note: < 0 means no limit, 0 means no caching, > 0 means cache to that size.
	public static final String  KEY_DEFAULT_CACHE_MAP_MAXIMUM_SIZE                                        = "aic.util.cache.map.default.maximum.size";
	public static final Long    DEFAULT_VALUE_DEFAULT_CACHE_MAP_MAXIMUM_SIZE                              = CacheMap.NO_MAXIMUM_SIZE;
	//
	public static final String  KEY_RECORD_CACHE_STATISTICS                                               = "aic.util.cache.record.statistics";
	public static final Boolean DEFAULT_VALUE_RECORD_CACHE_STATISTICS                                     = Boolean.FALSE;
	//
	public static final String  KEY_RAIONAL_APPROXIMATION_ENABLED                                         = "aic.util.rational.approximation.enabled";
	public static final Boolean DEFAULT_VALUE_RAIONAL_APPROXIMATION_ENABLED                               = Boolean.FALSE;
	//
	public static final String  KEY_RAIONAL_APPROXIMATION_ACTIVE_AFTER_N_BITS                             = "aic.util.rational.approximation.active.after.n.bits";
	public static final Integer DEFAULT_VALUE_APPROXIMATION_ACTIVE_AFTER_N_BITS                           = new Integer(256); // NOTE: best to use multiple of 32 (as BigInteger uses int arrays for their internal representation, which are used by Rational)
	//
	public static final String  KEY_RAIONAL_APPROXIMATION_ZERO_K_BITS                                     = "aic.util.rational.approximation.zero.k.bits";
	public static final Integer DEFAULT_VALUE_APPROXIMATION_ZERO_K_BITS                                   = new Integer(32); // NOTE: best to use multiple of 32, must be < active_after_n_bits value
	
	public static String getTestConfigurationScriptSettings() {
		String result = getString(KEY_TEST_CONFIGURATION_SCRIPT_SETTINGS, DEFAULT_TEST_CONFIGURATION_SCRIPT_SETTINGS);
		return result;
	}
	
	public static boolean isBranchAndMergeThreadingEnabled() {
		boolean result = getBoolean(KEY_BRANCH_AND_MERGE_THREADING_ENABLED, DEFAULT_VALUE_BRANCH_AND_MERGE_THREADING_ENABLED);
		
		return result;
	}
	
	public static boolean isBranchAndMergeUseNumberProcessorsForThreadPoolSize() {
		Boolean result = getBoolean(KEY_BRANCH_AND_MERGE_USE_NUMBER_PROCESSORS_FOR_THREAD_POOL_SIZE, DEFAULT_VALUE_BRANCH_AND_MERGE_USE_NUMBER_PROCESSORS_FOR_THREAD_POOL_SIZE);
		
		return result;
	}
	
	public static int getBranchAndMergeDeltaNumberProcessorsForThreadPoolSize() {
		int result = getInt(KEY_BRANCH_AND_MERGE_DELTA_NUMBER_PROCESSORS_FOR_THREAD_POOL_SIZE, DEFAULT_VALUE_DELTA_NUMBER_PROCESSORS_FOR_THREAD_POOL_SIZE);
		
		return result;
	}
	
	public static int getBranchAndMergeFixedThreadPoolSize() {
		int result = getInt(KEY_BRANCH_AND_MERGE_FIXED_THREAD_POOL_SIZE, DEFAULT_VALUE_BRANCH_AND_MERGE_FIXED_THREAD_POOL_SIZE);
		
		return result;
	}
	
	public static long getDefaultCacheMapMaximumSize() {
		long result = getLong(KEY_DEFAULT_CACHE_MAP_MAXIMUM_SIZE, DEFAULT_VALUE_DEFAULT_CACHE_MAP_MAXIMUM_SIZE);
		
		return result;
	}
	
	public static boolean isRecordCacheStatistics() {
		boolean result = getBoolean(KEY_RECORD_CACHE_STATISTICS, DEFAULT_VALUE_RECORD_CACHE_STATISTICS);
		
		return result;
	}
	
	public static boolean isRationalApproximationEnabled() {
		boolean result = getBoolean(KEY_RAIONAL_APPROXIMATION_ENABLED, DEFAULT_VALUE_RAIONAL_APPROXIMATION_ENABLED);
		
		return result;
	}
	
	public static int getRationalApproximationActiveAfterNBits() {
		int result = getInt(KEY_RAIONAL_APPROXIMATION_ACTIVE_AFTER_N_BITS, DEFAULT_VALUE_APPROXIMATION_ACTIVE_AFTER_N_BITS);
		
		return result;
	}
	
	public static int getRationalApproximationZeroKBits() {
		int result = getInt(KEY_RAIONAL_APPROXIMATION_ZERO_K_BITS, DEFAULT_VALUE_APPROXIMATION_ZERO_K_BITS);
		
		return result;
	}
}
