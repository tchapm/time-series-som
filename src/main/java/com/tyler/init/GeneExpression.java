package com.tyler.init;
import java.util.ArrayList;


public class GeneExpression {
	public int geneName;
	public String dataName;
	public ArrayList<Double> exprVals = new ArrayList<Double>();
	public double mean;
	public double stdDev;
	
	 GeneExpression(int geneName,
			ArrayList<Double> exprVals, double mean, double stdDev) {
		super();
		this.geneName = geneName;
		this.exprVals = exprVals;
		this.mean = mean;
		this.stdDev = stdDev;
	}
}
