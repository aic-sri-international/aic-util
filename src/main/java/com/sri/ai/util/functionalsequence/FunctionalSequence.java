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
package com.sri.ai.util.functionalsequence;

import java.util.Iterator;

import com.google.common.annotations.Beta;

/**
 * <p>
 * Intuitively, a functional sequence is the sequence formed by an (optional)
 * initial arbitrary value, followed by the results of applying a function to a
 * set of arguments, when these arguments are updated, one at a time. Each
 * argument is updated from a specific sequence itself, and when that sequence
 * reaches its end, that argument is no longer updated. The main sequence stops
 * being generated when all arguments have no longer values to be updated with.
 * <p>
 * Here is a more precise definition: let f be a n-ary function from arguments
 * of type V to a set T and s_1, ..., s_n be n (possibly infinite) sequences,
 * with s_{i,j} the j-th element of the sequence s_i. Let k be a (possibly
 * infinite) sequence of index arrays (k_{1,1} ... k_{1,n}), ..., (k_{m,1}, ...,
 * k_{m, n}), ..., where k_{t,i} is an integer greater than 0, k_{1,i} = 1 for
 * all i, and each array k_t differs from k_{t+1} in exactly one element i(t),
 * and k_{t+1,i(t)} = k_{t,i(t)} + 1. Let f0 be an initial value in CoDomain.
 * Then the functional sequence fs(f, f0, s, k) is the sequence f0,
 * f(s_{1,k_{1,1}}, ..., s_{n,k_{1,n}}), ..., f(s_{1,k_{t,1}}, ...,
 * s_{n,k_{t,n}}), ...
 * <p>
 * 
 * @author braz
 * 
 * @param <CoDomain>
 */
@Beta
public interface FunctionalSequence<CoDomain> extends Iterator<CoDomain> {

}
