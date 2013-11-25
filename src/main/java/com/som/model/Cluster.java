package com.som.model;


import java.util.ArrayList;

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
	private String name;
	private Centroid centroid;
	private double sumOfSquares;
	private ArrayList<DataPoint> dataPoints;

	public Cluster(String name) {
		this.name = name;
		this.centroid = null; //will be set by calling setCentroid()
		dataPoints = new ArrayList<DataPoint>();
	}

	public void setCentroid(Centroid c) {
		centroid = c;
	}

	public Centroid getCentroid() {
		return centroid;
	}

	public void addDataPoint(DataPoint dp) { 
		dp.setCluster(this); 
		this.dataPoints.add(dp);
		calcAndSetSumOfSquares();
	}

	public void removeDataPoint(DataPoint dp) {
		this.dataPoints.remove(dp);
		calcAndSetSumOfSquares();
	
	}

	public int getNumDataPoints() {
		return this.dataPoints.size();
	}

	public DataPoint getDataPoint(int pos) {
		return (DataPoint) this.dataPoints.get(pos);
	}

	public void calcAndSetSumOfSquares() { 
		double temp = 0;
		for (DataPoint dp : getDataPoints()){
			temp += dp.getCurrentEuDt();
		}
		this.sumOfSquares = temp;
	}

	public double getSumSqr() {
		return this.sumOfSquares;
	}

	public String getName() {
		return this.name;
	}

	public ArrayList<DataPoint> getDataPoints() {
		return this.dataPoints;
	}

	public double calcClusterChange(Cluster cluster){
		double change = 0.0;
		for(int i=0; i<this.getCentroid().getPosition().size(); i++){
			change += Math.pow(this.getCentroid().getPosition().get(i)-cluster.getCentroid().getPosition().get(i),2);
		}
		return Math.sqrt(change);
	}
	
	public void clearDataPts()
	{
		dataPoints = new ArrayList<DataPoint>();
	}
	
	@Override
	public String toString()
	{
		return "Cluster [mName=" + name + ", mSumSqr=" + sumOfSquares + ", mDataPoints=" + dataPoints + "]";
	}


}
