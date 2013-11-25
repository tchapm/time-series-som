package com.som.model;

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
	private int name;
	private Cluster cluster;
	private double euclidDist = 0.0;
	private boolean indecisive = false;
	private boolean inTree = false;
	
	public DataPoint(ArrayList<Double> thePosition, int name){
		this.position = thePosition;
		this.name = name;
	}

	public void setCluster(Cluster cluster) {
		this.cluster = cluster;
		calcAndSetEucDist();
	}

	public void calcAndSetEucDist() {
		euclidDist = 0.0;
		for(int i=0; i<cluster.getCentroid().getPosition().size(); i++){
			euclidDist +=Math.pow(this.position.get(i) - cluster.getCentroid().getPosition().get(i), 2);
		}
		euclidDist = Math.sqrt(euclidDist);
	}

	public double testEuclideanDistance(Centroid c) {
		double dist = 0.0;
		for(int i=0; i<c.getPosition().size(); i++){
			dist += Math.pow(this.position.get(i) - c.getPosition().get(i), 2);
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
		return cluster;
	}

	public double getCurrentEuDt() {
		return euclidDist;
	}

	public int getName()
	{
		return name;
	}

	public void setName(int name)
	{
		this.name = name;
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
			if(distMat[name][i]==1){
				degCount++;
			}
		}
		if(degCount>2){
			indecisive = true;
		}
		
	}

	@Override
	public String toString()
	{
		return "DataPoint [position=" + position + ", mObjName=" + name
				+ ", mEuDt=" + euclidDist + ", indecisive=" + indecisive + ", inTree=" + inTree + "]";
	}

	
}