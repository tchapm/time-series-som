package com.tyler.som.model;

import java.util.ArrayList;

/**
 *
 * @author Shyam Sivaraman and Tyler Chapman
 * @version 1.0
 * 
 * This class is modeled after a similar class that Shyam Sivaraman used for his K-means analysis.
 * It represents the Centroid for a Cluster. The initial centroid is calculated
 * using a equation which divides the sample space for each dimension into equal parts
 * depending upon the value of k.
 *
 */

public class Centroid {
	private ArrayList<Double> position = new ArrayList<Double>();
	private Cluster mCluster;

	public Centroid(ArrayList<Double> thePosition){
		position = thePosition;
	}
	
	public Centroid(double[] thePosition){
		ArrayList<Double> newPosition = new ArrayList<Double>();
		for(double pos : thePosition){
			newPosition.add(pos);
		}
		position = newPosition;
	}
	
	public ArrayList<Double> getPosition() {
		return position;
	}
	public void setPosition(ArrayList<Double> pos) {
		position = pos;
	}
	public void calcCentroid() { 
		int numDP = mCluster.getNumDataPoints();
		int i;
		//calculating the new Centroid
		if(numDP>0){
			ArrayList<Double> tempPos = new ArrayList<Double>(position.size());
			for(int j=0; j<position.size();j++){
				tempPos.add(0.0);
			}
			for (i = 0; i < numDP; i++) {
				for(int j=0;j<position.size();j++){
					tempPos.set(j, tempPos.get(j)+mCluster.getDataPoint(i).getPosition().get(j));
				}
			}
			for(int j=0;j<tempPos.size();j++){
				tempPos.set(j, tempPos.get(j)/(double)numDP);
			}
			this.position = tempPos;
		}
		//calculating the new Euclidean Distance for each Data Point
		for (i = 0; i < numDP; i++) {
			mCluster.getDataPoint(i).calcAndSetEucDist();
		}
		//calculate the new Sum of Squares for the Cluster
		mCluster.calcAndSetSumOfSquares();
	}

	public void setCluster(Cluster c) {
		mCluster = c;
	}

	public Cluster getCluster() {
		return mCluster;
	}

	public double calCentroidDist(Centroid tempCent){
		double tempDist = 0.0;
		for(int i=0; i<position.size(); i++){
			tempDist += Math.pow(position.get(i)-tempCent.getPosition().get(i),2);
		}
		return Math.sqrt(tempDist);
	}

	public ArrayList<Double> getMiddlePt(Centroid centB) {
		ArrayList<Double> centMid = new ArrayList<Double>();
		for(int i=0; i<position.size(); i++){
			centMid.add((position.get(i)-centB.getPosition().get(i))/2 + centB.getPosition().get(i));
		}
		return centMid;
	}
}