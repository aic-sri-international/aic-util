package com.sri.ai.util.computation.anytime.gradientdescent.api;

import java.util.List;

import com.sri.ai.util.computation.anytime.api.Anytime;
import com.sri.ai.util.computation.anytime.api.Approximation;

public interface GradientDescentAnytime<T> {

	Double getAbsoluteVolumeVariationWithRespectTo(Anytime<T> sub);
	
}
