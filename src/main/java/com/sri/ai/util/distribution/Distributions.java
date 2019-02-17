package com.sri.ai.util.distribution;

import java.util.Random;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.random.JDKRandomGenerator;

/**
 * A class with utilities for manipulating probability distributions.
 * 
 * @author braz
 *
 */
public class Distributions {

	public static NormalDistribution product(NormalDistribution normal1, NormalDistribution normal2, Random random) {
		double mu1 = normal1.getMean();
		double mu2 = normal2.getMean();
		double sigma1 = normal1.getStandardDeviation();
		double sigma2 = normal2.getStandardDeviation();
		double sigmaSquare1 = Math.pow(sigma1, 2);
		double sigmaSquare2 = Math.pow(sigma2, 2);
		double sigmaSquareInverse1 = 1.0/sigmaSquare1;
		double sigmaSquareInverse2 = 1.0/sigmaSquare2;
		
		double mu = (sigmaSquareInverse1*mu1 + sigmaSquareInverse2*mu2)/(sigmaSquareInverse1 + sigmaSquareInverse2);
		double sigmaSquare = sigmaSquare1*sigmaSquare2/(sigmaSquare1 + sigmaSquare2);
		double sigma = Math.sqrt(sigmaSquare);
		
		NormalDistribution result = new NormalDistribution(new JDKRandomGenerator(random.nextInt()), mu, sigma);
		
		return result;
	}
	
	public static String toString(NormalDistribution normalDistribution) {
		return "Normal(" + normalDistribution.getMean() + ", " + normalDistribution.getStandardDeviation() + ")";
	}
}
