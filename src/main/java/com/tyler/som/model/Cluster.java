package com.tyler.som.model;


import java.util.Vector;

/**
*
* @author Shyam Sivaraman and Tyler Chapman
* @version 1.0
* 
* This class was modeled after a similar class that Shyam Sivaraman used for K-means calculation.
* This class represents the clusters connected to the centroids within the SOM. A Cluster is associated
* only one SOM Instance, and is related to more than one DataPoints.
* 
*/

public class Cluster {
	private String mName;
	private Centroid mCentroid;
	private double mSumSqr;
	private Vector<DataPoint> mDataPoints;

	public Cluster(String name) {
		this.mName = name;
		this.mCentroid = null; //will be set by calling setCentroid()
		mDataPoints = new Vector<DataPoint>();
	}

	public void setCentroid(Centroid c) {
		mCentroid = c;
	}

	public Centroid getCentroid() {
		return mCentroid;
	}

	public void addDataPoint(DataPoint dp) { 
		dp.setCluster(this); 
		this.mDataPoints.addElement(dp);
		calcAndSetSumOfSquares();
	}

	public void removeDataPoint(DataPoint dp) {
		this.mDataPoints.removeElement(dp);
		calcAndSetSumOfSquares();
	
	}

	public int getNumDataPoints() {
		return this.mDataPoints.size();
	}

	public DataPoint getDataPoint(int pos) {
		return (DataPoint) this.mDataPoints.elementAt(pos);
	}

	public void calcAndSetSumOfSquares() { 
		int size = this.mDataPoints.size();
		double temp = 0;
		for (int i = 0; i < size; i++) {
			temp = temp + ((DataPoint)this.mDataPoints.elementAt(i)).getCurrentEuDt();
		}
		this.mSumSqr = temp;
	}

	public double getSumSqr() {
		return this.mSumSqr;
	}

	public String getName() {
		return this.mName;
	}

	public Vector<DataPoint> getDataPoints() {
		return this.mDataPoints;
	}

	public double calcClusterChange(Cluster cluster){
		double change = 0.0;
		for(int i=0; i<this.getCentroid().getPosition().size(); i++){
			change += Math.pow(this.getCentroid().getPosition().get(i)-cluster.getCentroid().getPosition().get(i),2);
		}
		return Math.sqrt(change);
	}
}
