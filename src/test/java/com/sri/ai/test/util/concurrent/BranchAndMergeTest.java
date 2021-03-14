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
package com.sri.ai.test.util.concurrent;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.sri.ai.util.AICUtilConfiguration;
import com.sri.ai.util.Configuration;
import com.sri.ai.util.concurrent.BranchAndMerge;
import com.sri.ai.util.concurrent.BranchAndMerge.Result;

public class BranchAndMergeTest {
	private static int _numberComputationTasksToCreate = 20;
	private static int _indexOfOutcomeFlagToSetToFalse = 6;
	//
	private AtomicInteger tasksExecutedCount = null;
	
	@Before
	public void setUp() {
		// Ensure is enabled
		Configuration.setProperty(AICUtilConfiguration.KEY_BRANCH_AND_MERGE_THREADING_ENABLED, Boolean.TRUE.toString());
		BranchAndMerge.reset();	
		
		tasksExecutedCount = new AtomicInteger(0);
	}

	@Test
	public void testFailure() {      
		List<ComputationTask> tasks = createComputationTasks(0L, true);
		Result<List<ComputationResult>> result = BranchAndMerge.execute(tasks);
		output("Failure", result);
		Assert.assertTrue(result.failureOccurred());
		Assert.assertNull(result.getResult());
	}
	
	@Test
	public void testSimpleTaskExecution() {      
		List<ComputationTask> tasks = createComputationTasks(0L, false);
		Result<List<ComputationResult>> result = BranchAndMerge.execute(tasks);
		output("Simple Task Execution", result);
		Assert.assertEquals(_numberComputationTasksToCreate, sizeExcludingNulls(result.getResult()));
	}
	
	@Test
	public void testCancelOnOutcomeFlagFalse() {
		List<ComputationTask> tasks = createComputationTasks(0L, false);
		Result<List<ComputationResult>> result = BranchAndMerge.execute(tasks, new CancelOnOutcomeFlagFalse());
		output("Cancel On Outcome Flag False", result);
		// All we should be able to guarantee here is that ther is a least 1 result
		// related to the cancelling condition (i.e. could finish first).
		Assert.assertTrue(sizeExcludingNulls(result.getResult()) >= 1);
	}
	
	@Test
	public void testCancelOnOutcomeFlagFalseInterruptible() {
		List<ComputationTask> tasks = createComputationTasks(200L, false);
		Result<List<ComputationResult>> result = BranchAndMerge.execute(tasks, new CancelOnOutcomeFlagFalse());
		output("Cancel On Outcome Flag False Interruptible", result);
		// All we should be able to guarantee here is that ther is a least 1 result
		// related to the cancelling condition (i.e. could finish first).
		Assert.assertTrue(sizeExcludingNulls(result.getResult()) >= 1);
	}
	
	@Test
	public void testRecursiveBranchAndMerge() {
		int numberParentTasks = Runtime.getRuntime().availableProcessors();
		List<Callable<List<ComputationResult>>> parentTasks = new ArrayList<Callable<List<ComputationResult>>>();
		for (int i = 0; i < numberParentTasks; i++) {
			final int id = i+1;
			parentTasks.add(new Callable<List<ComputationResult>>() {
				@Override
				public List<ComputationResult> call() {
					List<ComputationTask> tasks = createComputationTasks(0L, false);
					Result<List<ComputationResult>> result = BranchAndMerge.execute(tasks);
				
					List<ComputationResult> results = new ArrayList<ComputationResult>();
					for (ComputationResult cr : result.getResult()) {
						results.add(new ComputationResult(cr.outcomeFlag, "parent task "+id+": "+cr.computationInfo));
					}
					return results;
				}
			});
		}
		
		Function<List<List<ComputationResult>>, List<ComputationResult>> transform = new Function<List<List<ComputationResult>>, List<ComputationResult>>() {
			@Override
			public List<ComputationResult> apply(List<List<ComputationResult>> results) {
				List<ComputationResult> combined = new ArrayList<ComputationResult>();
				for (List<ComputationResult> parentResults : results) {
					combined.addAll(parentResults);
				}
				return combined;
			}
		};
		
		Result<List<ComputationResult>> result = BranchAndMerge.execute(parentTasks, transform);
		output("Recursive Branch and Merge", result);
		Assert.assertEquals(numberParentTasks * _numberComputationTasksToCreate, sizeExcludingNulls(result.getResult()));
	}
	
	
	//
	// PRIVATE METHODS
	//
	private void output(String testName, Result<List<ComputationResult>> result) {
		System.out.println("------------------");
		System.out.println("test name        = "+testName);
		System.out.println("tasks executed   = "+tasksExecutedCount);
		System.out.println("failure occurred = "+result.failureOccurred());
		System.out.println("results          = ");
		if (result.getResult() != null) {
			for (ComputationResult cr : result.getResult()) {
				System.out.println(""+cr);
			}
		} 
		else {
			System.out.println("null");
		}
	}
	
	private int sizeExcludingNulls(List<? extends Object> list) {
		int size = 0;
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i) != null) {
					size++;
				}
			}
		}
		return size;
	}
	
	private List<ComputationTask> createComputationTasks(long sleepDuration, boolean throwException) {
		List<ComputationTask>  tasks = new ArrayList<ComputationTask>();
		
		for (int i = 1; i <= _numberComputationTasksToCreate; i++) {
			final boolean outcomeFlag = i == _indexOfOutcomeFlagToSetToFalse ? false : true;
			
			ComputationTask task = new ComputationTask(i, outcomeFlag, tasksExecutedCount, sleepDuration, throwException);
			
			tasks.add(task);
		}
		
		return tasks;
	}
	
	//
	// Supporting Classes
	//
	private static class ComputationResult {
		public boolean outcomeFlag;
		public String  computationInfo;
		public ComputationResult(boolean outcomeFlag, String computationInfo) {
			this.outcomeFlag     = outcomeFlag;
			this.computationInfo = computationInfo;
		}
		
		@Override
		public String toString() {
			return String.format("%5s : %s", outcomeFlag, computationInfo);
		}
	}
	
	private static class ComputationTask implements Callable<ComputationResult> {
		private int           id                  = 0;
		private boolean       outcomeFlag         = false;
		private AtomicInteger tasksExecutedCount  = null;
		private long          sleepBeforeExecute  = 0L;
		private boolean       throwException      = false;
		
		public ComputationTask(int id,
				boolean outcomeFlag,
				AtomicInteger tasksExecutedCount,
				long sleepBeforeExecute,
				boolean throwException) {
			this.id                  = id;
			this.outcomeFlag         = outcomeFlag;
			this.tasksExecutedCount  = tasksExecutedCount;
			this.sleepBeforeExecute  = sleepBeforeExecute;
			this.throwException      = throwException;
		}
		
		@Override
		public ComputationResult call() {
			String computationInfo = "";
			try {
				if (throwException) {
					throw new RuntimeException("TOLD to throw an Exception");
				}
				if (sleepBeforeExecute > 0L) {
					Thread.sleep(sleepBeforeExecute);
				}
				// Mix things up a little by working out the thread
				// on a simple but non instantaneous task
				long sumOfFirstN = 1L;
				long firstN = 1234567890 - (new Random()).nextInt(1234567890);
				for (long l = 2; l <= firstN; l++) {
					sumOfFirstN = sumOfFirstN + l;
				}
				BigDecimal formulaAnswer = ((new BigDecimal(firstN)).multiply(new BigDecimal(firstN+1))).divide(new BigDecimal(2));
				computationInfo = Thread.currentThread().getName()+" ran task "+id+", sum of first " + firstN + " natural numbers is " + sumOfFirstN + ", n(n+1)/2 = "+formulaAnswer;
				tasksExecutedCount.addAndGet(1);
			} catch (InterruptedException iex) {
				// ignore as I was interrupted.
				computationInfo = "computation interrupted:"+Thread.currentThread().getName();
			}
			return new ComputationResult(outcomeFlag, computationInfo);
		}
	}
	
	private static class CancelOnOutcomeFlagFalse implements Predicate<ComputationResult> {
		@Override
		public boolean apply(ComputationResult value) {
			return value.outcomeFlag == false;
		}
	}
}
