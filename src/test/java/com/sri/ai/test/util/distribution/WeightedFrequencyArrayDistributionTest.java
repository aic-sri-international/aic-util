package com.sri.ai.test.util.distribution;

import static com.sri.ai.util.Util.arrayList;
import static com.sri.ai.util.Util.println;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

import com.sri.ai.util.distribution.WeightedFrequencyArrayDistribution;

public class WeightedFrequencyArrayDistributionTest {

	@Test
	public void test() {
		WeightedFrequencyArrayDistribution distribution;
		ArrayList<Double> expectedProbabilities;
		double expectedPartition;
		
		distribution = new WeightedFrequencyArrayDistribution(4, 0.0);
		distribution.add(0, 1.0);
		distribution.add(1, 1.0);
		expectedProbabilities = arrayList(0.5, 0.5, 0.0, 0.0);
		expectedPartition = 2.0;
		runTest(distribution, expectedProbabilities, expectedPartition);
		
		distribution = new WeightedFrequencyArrayDistribution(4, 0.0);
		distribution.add(0, 2.0);
		distribution.add(1, 2.0);
		expectedProbabilities = arrayList(0.5, 0.5, 0.0, 0.0);
		expectedPartition = 4.0;
		runTest(distribution, expectedProbabilities, expectedPartition);

		distribution = new WeightedFrequencyArrayDistribution(4, 0.1);
		distribution.add(0, 1.0);
		distribution.add(1, 1.0);
		expectedProbabilities = arrayList(0.47727272727272724, 0.47727272727272724, 0.022727272727272728, 0.022727272727272728);
		expectedPartition = 2.2;
		runTest(distribution, expectedProbabilities, expectedPartition);
		
		distribution = new WeightedFrequencyArrayDistribution(4, 0.0);
		distribution.add(0, 0.0);
		distribution.add(1, 1.0);
		expectedProbabilities = arrayList(0.0, 1.0, 0.0, 0.0);
		expectedPartition = 1.0;
		runTest(distribution, expectedProbabilities, expectedPartition);
		
		distribution = new WeightedFrequencyArrayDistribution(4, 0.0);
		distribution.add(0, 1.0);
		distribution.add(1, 2.0);
		expectedProbabilities = arrayList(0.3333333333333333, 0.6666666666666666, 0.0, 0.0);
		expectedPartition = 3.0;
		runTest(distribution, expectedProbabilities, expectedPartition);
		
		distribution = new WeightedFrequencyArrayDistribution(4, 0.1);
		distribution.add(0, 0.0);
		distribution.add(1, 1.0);
		expectedProbabilities = arrayList(0.022727272727272728, 0.9318181818181817, 0.022727272727272728, 0.022727272727272728);
		expectedPartition = 1.1;
		runTest(distribution, expectedProbabilities, expectedPartition);
		
		distribution = new WeightedFrequencyArrayDistribution(4, 0.1);
		distribution.add(0, 1.0);
		distribution.add(1, 2.0);
		expectedProbabilities = arrayList(0.32575757575757575, 0.6287878787878789, 0.02272727272727273, 0.02272727272727273);
		expectedPartition = 3.3;
		runTest(distribution, expectedProbabilities, expectedPartition);
		
	}

	public void runTest(
			WeightedFrequencyArrayDistribution distribution, 
			ArrayList<Double> expectedProbabilities, 
			double expectedPartition) {
		
		println("Probabilities: " + distribution.getProbabilities());
		println("Partition: " + distribution.getPartition());
		assertEquals(expectedProbabilities, distribution.getProbabilities());
		assertEquals(expectedPartition, distribution.getPartition());
	}
}
