package com.tyler.som.model;

import java.util.ArrayList;

/**
 *
 * @author Shyam Sivaraman and Tyler Chapman
 * @version 1.0
 * 
 *  This class was modeled after a similar class that Shyam Sivaraman used for K-means calculation.
 *  It represents the data points taken in data space that are then applied
 *  to the clusters. A data point is required to have a name and euclidian coordinants.
 * 
 */


public class DataPoint {
	private ArrayList<Double> position = new ArrayList<Double>(); 
	private int mObjName;
	private Cluster mCluster;
	private double mEuDt = 0.0;
	private boolean indecisive = false;
	private boolean inTree = false;
	
	public DataPoint(ArrayList<Double> thePosition, int name){
		this.position = thePosition;
		this.mObjName = name;
	}

	public void setCluster(Cluster cluster) {
		this.mCluster = cluster;
		calcAndSetEucDist();
	}

	public void calcAndSetEucDist() {
		mEuDt = 0.0;
		for(int i=0; i<mCluster.getCentroid().getPosition().size(); i++){
			mEuDt +=Math.pow(this.position.get(i) - mCluster.getCentroid().getPosition().get(i), 2);
		}
		mEuDt = Math.sqrt(mEuDt);
	}

	public double testEuclideanDistance(Centroid c) {
		double dist = 0.0;
		for(int i=0; i<c.getPosition().size(); i++){
			dist +=Math.pow(this.position.get(i) - c.getPosition().get(i), 2);
		}
		dist = Math.sqrt(dist);
		return dist;
	}

	public double getEuclDist(DataPoint dp) {
		double dist = 0.0;
		for(int i=0; i<dp.getPosition().size(); i++){
			dist +=Math.pow(this.position.get(i) - dp.getPosition().get(i), 2);
		}
		dist = Math.sqrt(dist);
		return dist;
	}
	
	public Cluster getCluster() {
		return mCluster;
	}

	public double getCurrentEuDt() {
		return mEuDt;
	}

	public int getObjName() {
		return mObjName;
	}
	public ArrayList<Double> getPosition() {
		return position;
	}
	public void setPosition(ArrayList<Double> position) {
		this.position = position;
	}

	public boolean isIndecisive() {
		return indecisive;
	}

	public boolean isInTree() {
		return inTree;
	}

	public void setInTree(boolean inTree) {
		this.inTree = inTree;
	}

	public void setIndecisive(boolean indecisive) {
		this.indecisive = indecisive;
	}

	public void setIndecisive(int[][] distMat) {
		int degCount = 0;
		for(int i=0; i<distMat.length; i++){
			if(distMat[mObjName][i]==1){
				degCount++;
			}
		}
		if(degCount>2){
			indecisive = true;
		}
		
	}

	
}