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
package com.sri.ai.util.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.sri.ai.util.AICUtilConfiguration;

/**
 * A general purpose utility service for simplifying performing concurrent
 * execution of work that can be split into sub-tasks or into smaller pieces
 * recursively (i.e. very similar to JDK 7's Fork/Join ExecutorService concept
 * but without explicitly detailing the mechanism - Note: may switch to using
 * this internally in a later iteration).
 * 
 * @author oreilly
 * 
 */
@Beta
public class BranchAndMerge {
	// Used to identify worker threads used by the Branch And Merge utility
	// service.
	private static final String             _threadIdentifierPrefix            = "Branch-And-Merge-";
	private static int                      _sharedExecutorNumberWorkerThreads = 0;
	private static AtomicInteger            _sharedExecutorActiveWorkerThreads = new AtomicInteger();
	// Note: _sharedExecutorService must be initialized after everything else.
	private static ListeningExecutorService _sharedExecutorService             = newExecutorService();

	/**
	 * The result of branching and then merging the results from a collection of
	 * Callables.
	 * 
	 * @author oreilly
	 * 
	 * @param <T>
	 *            the type of the result to be returned when all the Callable
	 *            results are merged together.
	 */
	public static interface Result<T> {
		/**
		 * 
		 * @return true if a failure occurred when calling a collection of
		 *         Callable objects.
		 */
		boolean failureOccurred();

		/**
		 * 
		 * @return the result from branching and then merging a collection of
		 *         Callable objects.
		 */
		T getResult();
	}

	/**
	 * Reset the shared Branch and Merge Utility service (i.e. drop and reset up
	 * any worker threads it is configured to use.).
	 * 
	 * <b>Note:</b> Only call this method at a safe point in your logic (i.e.
	 * when no other thread should be using this service).
	 */
	public static void reset() {
		// If currently up, shut it down before resetting it up.
		if (_sharedExecutorService != null) {
			_sharedExecutorService.shutdown();
		}
		_sharedExecutorService = newExecutorService();
	}

	/**
	 * Branch and merge call, the results from calling a list of tasks are
	 * collected into a list (order of results match those of the tasks).
	 * 
	 * @param tasks
	 *            a list of tasks that are to be branched.
	 * @return a list of the results returned by the branched tasks. The
	 *         ordering of the results will correspond to the ordering of the
	 *         tasks that generated them (null results are allowed, either
	 *         intentionally or due to a failure when calling a task).
	 * @param <V> the type of the results.
	 */
	public static <V> Result<List<V>> execute(List<? extends Callable<V>> tasks) {
		return execute(tasks, new NoResultsTransform<V>());
	}

	/**
	 * Branch and merge call, the results from calling a list of tasks are
	 * collected into a list (order of results match those of the tasks).
	 * 
	 * @param tasks
	 *            a list of tasks that are to be branched.
	 * @param cancelOutstandingOnSuccess
	 *            a predicate that returns true if all other outstanding tasks
	 *            should be cancelled if a task returns a specified success
	 *            value (i.e. can be used for short circuiting logic - e.g. when
	 *            dealing with conjunctions or disjunctions of tasks).
	 * @return a list of the results returned by the branched tasks. The
	 *         ordering of the results will correspond to the ordering of the
	 *         tasks that generated them (null results are allowed, either
	 *         intentionally or due to a failure when calling a task).
	 * @param <V> the type of the results.
	 */
	public static <V> Result<List<V>> execute(
			List<? extends Callable<V>> tasks,
			Predicate<V> cancelOutstandingOnSuccess) {
		return execute(tasks, cancelOutstandingOnSuccess,
				new NoResultsTransform<V>());
	}

	/**
	 * Branch and merge call, the results from calling a list of tasks are
	 * collected into a list (order of results match those of the tasks).
	 * 
	 * @param tasks
	 *            a list of tasks that are to be branched.
	 * @param cancelOutstandingOnSuccess
	 *            a predicate that returns true if all other outstanding tasks
	 *            should be cancelled if a task returns a specified success
	 *            value (i.e. can be used for short circuiting logic - e.g. when
	 *            dealing with conjunctions or disjunctions of tasks).
	 * @param cancelOutstandingOnFailure
	 *            a predicate that returns true if all outstanding tasks should
	 *            be cancelled if an failure occurs when executing one of the
	 *            tasks.
	 * @return a list of the results returned by the branched tasks. The
	 *         ordering of the results will correspond to the ordering of the
	 *         tasks that generated them (null results are allowed, either
	 *         intentionally or due to a failure when calling a task).
	 * @param <V> the type of the results.
	 */
	public static <V> Result<List<V>> execute(
			List<? extends Callable<V>> tasks,
			Predicate<V> cancelOutstandingOnSuccess,
			Predicate<Throwable> cancelOutstandingOnFailure) {
		return execute(tasks, cancelOutstandingOnSuccess,
				cancelOutstandingOnFailure, new NoResultsTransform<V>());
	}

	/**
	 * Branch and merge call, the results from calling a list of tasks are
	 * passed thru to a transformation function that will create a desired merge
	 * result for the call.
	 * 
	 * @param tasks
	 *            a list of tasks that are to be branched.
	 * @param transformResults
	 *            a function that will be called with a list of all of the
	 *            results returned by the branched tasks (order will match that
	 *            of the tasks themselves). This function should be able to
	 *            handle null results as these can be legal, either
	 *            intentionally returned by the task or as the result of a
	 *            failure when calling the task.
	 * @return the result returned from applying transformResults on the list of
	 *         results collected from the branched tasks.
	 * @param <V> the type of the result.
	 * @param <T> the type of the transformed result.
	 */
	public static <V, T> Result<T> execute(List<? extends Callable<V>> tasks,
			Function<List<V>, T> transformResults) {
		return execute(tasks, new CancelOutstandingOnSuccess<V>(false),
				transformResults);
	}

	/**
	 * Branch and merge call, the results from calling a list of tasks are
	 * passed thru to a transformation function that will create a desired merge
	 * result for the call.
	 * 
	 * @param tasks
	 *            a list of tasks that are to be branched.
	 * @param cancelOutstandingOnSuccess
	 *            a predicate that returns true if all other outstanding tasks
	 *            should be cancelled if a task returns a specified success
	 *            value (i.e. can be used for short circuiting logic - e.g. when
	 *            dealing with conjunctions or disjunctions of tasks).
	 * @param transformResults
	 *            a function that will be called with a list of all of the
	 *            results returned by the branched tasks (order will match that
	 *            of the tasks themselves). This function should be able to
	 *            handle null results as these can be legal, either
	 *            intentionally returned by the task or as the result of a
	 *            failure when calling the task.
	 * @return the result returned from applying transformResults on the list of
	 *         results collected from the branched tasks.
	 * @param <V> the type of the result.
	 * @param <T> the type of the transformed result.
	 */
	public static <V, T> Result<T> execute(List<? extends Callable<V>> tasks,
			Predicate<V> cancelOutstandingOnSuccess,
			Function<List<V>, T> transformResults) {
		return execute(tasks, cancelOutstandingOnSuccess,
				new CancelOutstandingOnFailure(true), transformResults);
	}

	/**
	 * Branch and merge call, the results from calling a list of tasks are
	 * passed thru to a transformation function that will create a desired merge
	 * result for the call.
	 * 
	 * @param tasks
	 *            a list of tasks that are to be branched.
	 * @param cancelOutstandingOnSuccess
	 *            a predicate that returns true if all other outstanding tasks
	 *            should be cancelled if a task returns a specified success
	 *            value (i.e. can be used for short circuiting logic - e.g. when
	 *            dealing with conjunctions or disjunctions of tasks).
	 * @param cancelOutstandingOnFailure
	 *            a predicate that returns true if all outstanding tasks should
	 *            be cancelled if an failure occurs when executing one of the
	 *            tasks.            
	 * @param transformResults
	 *            a function that will be called with a list of all of the
	 *            results returned by the branched tasks (order will match that
	 *            of the tasks themselves). This function should be able to
	 *            handle null results as these can be legal, either
	 *            intentionally returned by the task or as the result of a
	 *            failure when calling the task.
	 * @return the result returned from applying transformResults on the list of
	 *         results collected from the branched tasks.
	 * @param <V> the type of the result.
	 * @param <T> the type of the transformed result.
	 */
	public static <V, T> Result<T> execute(List<? extends Callable<V>> tasks,
			Predicate<V> cancelOutstandingOnSuccess,
			Predicate<Throwable> cancelOutstandingOnFailure,
			Function<List<V>, T> transformResults) {

		Result<T> result = null;
		boolean executeConcurrent = true;
		if (_sharedExecutorService == null) {
			executeConcurrent = false;
		} 
		else {
			// Increase the # of active worker threads
			int activeThreads = _sharedExecutorActiveWorkerThreads.addAndGet(tasks.size());
			// If I've exceeded the number of available worker threads 
			// and I'm being called from one of these worker threads, 
			// then want to execute this sequentially on the worker 
			// thread. Otherwise I could very easily end up in deadlock 
			// situations very easily.
			if (activeThreads > _sharedExecutorNumberWorkerThreads
					&& Thread.currentThread().getName().startsWith(_threadIdentifierPrefix)) {
				
				// TODO: this is currently a very simplistic/non-optimal
				// approach as it doesn't leverage using a subset of 
				// available worker threads and completing the task in 
				// two parts by having some of the tasks branched and 
				// some of them run sequentially on the current thread.
				executeConcurrent = false;
				
				// Ensure I reduce the active worker threads as I'm actually
				// going to execute sequentially on the current worker thread.
				_sharedExecutorActiveWorkerThreads.addAndGet(tasks.size() * -1);
			}
		}

		if (executeConcurrent) {
// System.out.println("BranchAndMerge Concurrent "+_sharedExecutorActiveWorkerThreads+" of "+_sharedExecutorNumberWorkerThreads + " for " + tasks.size() + " tasks at " + System.currentTimeMillis());
			result = executeConcurrent(tasks, cancelOutstandingOnSuccess,
					cancelOutstandingOnFailure, transformResults);
		} 
		else {
// System.out.println("BranchAndMerge Sequential "+_sharedExecutorActiveWorkerThreads+" of "+_sharedExecutorNumberWorkerThreads + " for " + tasks.size() + " tasks at " + System.currentTimeMillis());
			result = executeSequential(tasks, cancelOutstandingOnSuccess,
					cancelOutstandingOnFailure, transformResults);
		}

		return result;
	}

	//
	// PRIVATE METHODS
	//
	private static ListeningExecutorService newExecutorService() {
		ListeningExecutorService result = null;
		int nThreads = 0; // i.e. not allowed by default

		// Running concurrently is optional.
		if (AICUtilConfiguration.isBranchAndMergeThreadingEnabled()) {
			if (AICUtilConfiguration
					.isBranchAndMergeUseNumberProcessorsForThreadPoolSize()) {
				nThreads = Runtime.getRuntime().availableProcessors();
				int delta = AICUtilConfiguration
						.getBranchAndMergeDeltaNumberProcessorsForThreadPoolSize();
				nThreads += delta;
				if (nThreads < 1) {
					nThreads = 1;
				}
			} 
			else {
				nThreads = AICUtilConfiguration
						.getBranchAndMergeFixedThreadPoolSize();
			}
		}

		if (nThreads > 0) {
			ThreadFactory threadFactory = new ThreadFactoryBuilder()
					// Do this so its simple to identify worker threads
					.setNameFormat(_threadIdentifierPrefix + "%s")
					// This is a service so want the worker threads
					// to be Daemons so that the JVM can exit normally
					// without needing any special calls to this service.
					.setDaemon(true).build();
			
			// We'll use fixed pool size for the worker threads as this
			// makes it easier to determine degrading. 
			ExecutorService executorService = Executors.newFixedThreadPool(
					nThreads, threadFactory);
			result = MoreExecutors.listeningDecorator(executorService);

			_sharedExecutorNumberWorkerThreads = nThreads;
			_sharedExecutorActiveWorkerThreads = new AtomicInteger();
		}

		return result;
	}

	private static <V, T> Result<T> executeConcurrent(
			List<? extends Callable<V>> tasks,
			Predicate<V> cancelOutstandingOnSuccess,
			Predicate<Throwable> cancelOutstandingOnFailure,
			Function<List<V>, T> transformResults) {
		boolean failureOccurred = false;
		T result = null;

		try {
			List<Future<V>> invokedFutures = _sharedExecutorService.invokeAll(tasks);
			List<ListenableFuture<V>> lfutures = new ArrayList<ListenableFuture<V>>();
			for (Future<V> future : invokedFutures) {
				lfutures.add((ListenableFuture<V>) future);
			}
			CancelOutstandingCallback<V> cancelOutstandingCallback = new CancelOutstandingCallback<V>(
					cancelOutstandingOnSuccess, cancelOutstandingOnFailure,
					lfutures);
			for (ListenableFuture<V> future : lfutures) {
				Futures.addCallback(future, cancelOutstandingCallback);
			}

			ListenableFuture<List<V>> resultsFuture = Futures
					.successfulAsList(lfutures);

			List<V> resultValues = resultsFuture.get();
			if (cancelOutstandingCallback.failureOccurred()) {
				failureOccurred = true;
				// If I don't cancel outstanding on failure
				// then I'll attempt to create a result
				if (!cancelOutstandingOnFailure.apply(null)) {
					result = transformResults.apply(resultValues);
				}
			} 
			else {
				result = transformResults.apply(resultValues);
			}
		} catch (Throwable t) {
			failureOccurred = true;
		}

		return new DefaultResult<T>(failureOccurred, result);
	}

	private static <V, T> Result<T> executeSequential(
			List<? extends Callable<V>> tasks,
			Predicate<V> cancelOutstandingOnSuccess,
			Predicate<Throwable> cancelOutstandingOnFailure,
			Function<List<V>, T> transformResults) {
		boolean failureOccurred = false;
		T result = null;

		List<V> resultValues = new ArrayList<V>(tasks.size());
		// Ensure all set to null initially
		for (int i = 0; i < tasks.size(); i++) {
			resultValues.add(null);
		}
		// Now attempt to get the values
		for (int i = 0; i < tasks.size(); i++) {
			try {
				V value = tasks.get(i).call();
				resultValues.set(i, value);
				if (cancelOutstandingOnSuccess.apply(value)) {
					break;
				}
			} catch (Throwable t) {
				failureOccurred = true;
				if (cancelOutstandingOnFailure.apply(t)) {
					break;
				}
			}
		}

		if (failureOccurred) {
			// If I don't cancel outstanding on failure
			// then I'll attempt to create a result
			if (!cancelOutstandingOnFailure.apply(null)) {
				result = transformResults.apply(resultValues);
			}
		} 
		else {
			result = transformResults.apply(resultValues);
		}

		return new DefaultResult<T>(failureOccurred, result);
	}

	//
	// PRIVATE CLASSES
	//
	private static class DefaultResult<T> implements Result<T> {
		private boolean failureOccurred = false;
		private T result = null;

		public DefaultResult(boolean failureOccurred, T result) {
			this.failureOccurred = failureOccurred;
			this.result = result;
		}

		@Override
		public boolean failureOccurred() {
			return failureOccurred;
		}

		@Override
		public T getResult() {
			return result;
		}
	}

	private static class CancelOutstandingCallback<V> implements
			FutureCallback<V> {
		private Predicate<V> cancelOutstandingOnSuccess = null;
		private Predicate<Throwable> cancelOutstandingOnFailure = null;
		private Iterable<ListenableFuture<V>> futures = null;
		private boolean failureOccurred = false;

		public CancelOutstandingCallback(
				Predicate<V> cancelOutstandingOnSuccess,
				Predicate<Throwable> cancelOutstandingOnFailure,
				Iterable<ListenableFuture<V>> futures) {
			this.cancelOutstandingOnSuccess = cancelOutstandingOnSuccess;
			this.cancelOutstandingOnFailure = cancelOutstandingOnFailure;
			this.futures = futures;
		}

		@Override
		public void onSuccess(V result) {
			_sharedExecutorActiveWorkerThreads.addAndGet(-1);
			if (cancelOutstandingOnSuccess.apply(result)) {
				cancel();
			}
		}

		@Override
		public void onFailure(Throwable t) {
			_sharedExecutorActiveWorkerThreads.addAndGet(-1);
			if (!(t instanceof CancellationException)) {
				failureOccurred = true;
				if (cancelOutstandingOnFailure.apply(t)) {
					cancel();
				}
			}
		}

		public boolean failureOccurred() {
			return failureOccurred;
		}

		//
		// PRIVATE METHODS
		//
		private void cancel() {
			for (ListenableFuture<V> future : futures) {
				if (!future.isCancelled()) {
					future.cancel(true);
				}
			}
		}
	}

}
