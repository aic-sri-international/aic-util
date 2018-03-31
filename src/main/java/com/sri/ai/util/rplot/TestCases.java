package com.sri.ai.util.rplot;
/**
 * TODO:- This class should probably in praise, but I am putting everything together in the same package until I have Rodigo's opition
 * 
 * TODO: either change my doc or add some compiler that supprots equations....
 * 
 * @author gabriel
 */
public class TestCases {

	/**
	 * An Ising model is a N dimensional lattice (like a N-dimensional grid), where each node interact with its nearest neighbors. 
	 * Each node can assume the values +1 or -1, and has an index "i" associated to its position. We usually represent the node
	 * at position i by <math>\sigma_i</math>. The indexes are usually given so that sigma 0 is in teh center of the hyper-cube
	 * <p>
	 * If N = 2, the Ising model is simply a squared grid. Below we represent a 3X3 s dimension Ising model
	 *  <p>
	 *  sig2  --  sig3  --  sig4<p>
	 *   |		   |	 	 |  <p>
	 *  sig-1 --  sig0  --  sig1<p>
	 *   |		   | 		 |  <p>
	 *  sig-4 --  sig-3 --  sig-2<p>
	 *  
	 * If N = 1, the model is a line (sig1 -- sig2 -- sig3 -- sig4 -- sig5 ...)<p>
	 * 
	 * we define <math>\sigma = (\sigma_1,\sigma_2,...,\sigma_n)</math>.<p>
	 * 
	 * The Ising model is represented by the following equation:<p>
	 * 
	 * :<math>\tilde{P}(\sigma) = exp(-\beta H(\sigma)) </math><p>
	 * 
	 * Where beta is the POTENTIAL<p>
	 * :<math>H(\sigma) = - \sum_{\langle i~j\rangle} J_{ij} \sigma_i \sigma_j -\mu \sum_{j} h_j\sigma_j</math>
	 * 
	 * Simplifications usually consider  <math> J_{ij} = \mu_j = 1 </math>. That way, the grid model can be represented in the following way: <p>
	 * 
	 * :<math>\tilde{P}(\sigma) = (\prod_{<ij>}\phi(\sigma_i,\sigma_j))(\prod_i\phi'(\simga_i)) </math>
	 * 
	 * Where <ij> mean the set of (i,j) that are directly neighbors, and the factors are defined as follows:<p>
	 * 
	 * :<math>\phi(X,Y)= exp(\beta X Y),\phi'(X) = exp(h X) </math>
	 * 
	 * -----------------------------------------------------------------------------------<p>
	 * Important results about the Ising model:<p>
	 * 
	 * - Suppose we take as evidence that all the nodes in the frontier of the lattice (surface of the hypercube) 
	 * are equal +1. There exists a <math>\beta_c</math>, such that, 
	 * <math>P(\sigma_0 = True) > \alpha > 0.5</math> for ARBITRARYLY LARGE number of odes on the lattice   <p>
	 * 
	 * - This means that AEBP is going to converge to an interval of size 2*alpha, and then subbenly drop t sero in the frontier;
	 * 
	 * @param dimension
	 * @param potential
	 */
	public static void isingModel(int dimension, double potential) {
	//TODO	return a set of Factors
	}
	
}
