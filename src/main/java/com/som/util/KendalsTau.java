package com.som.util;
/**
*
* @author Tyler Chapman
* @version 1.0
* 
*  Class to calculate the Kendal's Tau of the inferred ordering of data points. The true ordering 
*  is always assumed to start at 0 and go up to the number of data points. It is 
*  designed to give a numerical value for the similarity between the actual and inferred ordering.
*/


import java.util.ArrayList;

import com.som.model.SelfOrganizingMapMain;

public class KendalsTau {

	private ArrayList<Integer> trueOrder = new ArrayList<Integer>();
	private ArrayList<Integer> inferredOrder = new ArrayList<Integer>();
	private double tau;

	public ArrayList<Integer> getTrueOrder() {
		return trueOrder;
	}

	public void setTrueOrder(ArrayList<Integer> trueOrder) {
		this.trueOrder = trueOrder;
	}

	public ArrayList<Integer> getInferredOrder() {
		return inferredOrder;
	}

	public void setInferredOrder(ArrayList<Integer> inferredOrder) {
		this.inferredOrder = inferredOrder;
	}

	public double getTau() {
		return tau;
	}

	public void setTau(double tau) {
		this.tau = tau;
	}

	public KendalsTau(ArrayList<Integer> in){
		for(int i=0; i<in.size(); i++){
			trueOrder.add(i);
		}
		inferredOrder = in;
		tau = calcKendalTau(trueOrder,inferredOrder);
	}
	
	private static double calcKendalTau(ArrayList<Integer> trueOrder, ArrayList<Integer> inferredOrder) {
		int checkedVal;
		int observedVal;
		int concord = 0;
		int discord = 0;
		for(int i=0; i<inferredOrder.size()-1; i++){
			checkedVal = inferredOrder.get(i);
			for(int j=i+1;j<inferredOrder.size();j++){
				observedVal = inferredOrder.get(j);
				if(checkedVal<observedVal){
					concord++;
				}else{
					discord++;
				}
			}
		}
		double tau = (concord-discord)/(0.5*inferredOrder.size()*(inferredOrder.size()-1));
		return tau;
	}
	
	public void print() {
//		SOMMain.logger.info("Actual order: " + trueOrder.toString());
		SelfOrganizingMapMain.logger.info("Calculated order: " + inferredOrder.toString());
		SelfOrganizingMapMain.logger.info("Kendal's Tau: " + tau);
	}


}