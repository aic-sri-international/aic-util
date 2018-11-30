package com.sri.ai.util.computation.anytime.gradientdescent.api;

import com.sri.ai.util.computation.anytime.api.Anytime;

public interface GradientDescentAnytime<T> {

	Double getAbsoluteVolumeVariationWithRespectTo(Anytime<T> sub);
	
}
