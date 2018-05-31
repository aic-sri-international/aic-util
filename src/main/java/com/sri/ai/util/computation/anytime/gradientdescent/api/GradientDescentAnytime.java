package com.sri.ai.util.computation.anytime.gradientdescent.api;

import com.sri.ai.util.computation.anytime.api.Anytime;

public interface GradientDescentAnytime<T> {

	Double getAbsolutePartialDerivativeWithRespectTo(Anytime<T> sub);
	
}
