package com.tyler.kmeans.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import com.tyler.som.model.Centroid;
import com.tyler.som.model.Cluster;
import com.tyler.som.model.DataPoint;

public class KMeansClustering {
	public static final double CUTOFF = 0.0001;
	public static final int MAX_ITERATION = 7;
	
	private ArrayList<Cluster> clusters = new ArrayList<Cluster>();
	private Vector<DataPoint> mapDataPoints = new Vector<DataPoint>();
	private int dimensions;
	
	
	public KMeansClustering(int numCentroids, Vector<DataPoint> datPoints, int numDim) {
		//create the number of clusters required for the analysis
		for (int i = 0; i < numCentroids; i++) {
			clusters.add(new Cluster("Cluster" + i));
		}
		this.dimensions = numDim;
		//get the data points that were instantiated within the main
		this.mapDataPoints = datPoints;
	}
	
	public ArrayList<Centroid> startAnalysis() throws IOException {
		//set Starting centroid positions at random positions
		setInitialCentroids();
		int n = 0;
		//assign datapoints based on location
		while (n < mapDataPoints.size()) {
			double currDist = Double.MAX_VALUE;
			int numClust = -1;
			for (int l = 0; l < clusters.size(); l++) 
			{
				double tempDist = mapDataPoints.elementAt(n).testEuclideanDistance(clusters.get(l).getCentroid());
				if(tempDist<currDist){
					currDist = tempDist;
					numClust = l;
				}
			}
			clusters.get(numClust).addDataPoint(mapDataPoints.elementAt(n));
			n++;
		}
		//recalculate Cluster centroids
		for (int i = 0; i < clusters.size(); i++) {
			clusters.get(i).getCentroid().calcCentroid();
		}

		//finds the movement of the centroids at each step and checks with cutoff before concluding
		double aveMoveLength = Double.MIN_VALUE;
		double prevMoveLength = Double.MAX_VALUE;
		int moves=0;
		while (Math.abs(prevMoveLength-aveMoveLength)>CUTOFF){
			prevMoveLength = aveMoveLength; 
			//store the clusters for the comparison at the end of the loop 
			ArrayList<Cluster> initClusters = new ArrayList<Cluster>();
			for(int f=0; f<clusters.size();f++){
				initClusters.add(new Cluster("InitCluster" + f));
				initClusters.get(f).setCentroid(clusters.get(f).getCentroid());
			}
			for (int j = 0; j < clusters.size(); j++) {
				for (int k = 0; k < clusters.get(j).getNumDataPoints(); k++) {
					//pick the first element of the first cluster
					//get the current Euclidean distance

					double tempEuDt = clusters.get(j).getDataPoint(k).getCurrentEuDt();
					Cluster tempCluster = null;
					boolean matchFoundFlag = false;
					for (int l = 0; l < clusters.size(); l++) {
						//test to see if testEuclidean < currentEuclidean for each data point
						//to determine if it needs to be moved to a different cluster
						double tempDist = clusters.get(j).getDataPoint(k).testEuclideanDistance(clusters.get(l).getCentroid());
						if (tempEuDt > tempDist){ 
							tempEuDt = tempDist;
							tempCluster = clusters.get(l);
							matchFoundFlag = true;
						}
					}
					//if the DataPoint was found to be closer than the cluster it is 
					//currently associated with, switch it to the nearer cluster
					if (matchFoundFlag) {
						tempCluster.addDataPoint(clusters.get(j).getDataPoint(k));
						clusters.get(j).removeDataPoint(clusters.get(j).getDataPoint(k));
					}
				}
			}
//			PrintHelpers.printClusterDataMembers(this);
			//recalculate the centroid of each cluster to get its new position after the DataPoints have been redistributed
			for (int m = 0; m < clusters.size(); m++) {
				clusters.get(m).getCentroid().calcCentroid();
			}

			//find how much they moved from the previous iteration to determine if map has reached convergence
			for(int i=0; i<initClusters.size(); i++){
				aveMoveLength += initClusters.get(i).calcClusterChange(clusters.get(i));
			}
			aveMoveLength = aveMoveLength/clusters.size();
			//			System.out.println("Ave centroid move distance = " + aveMoveLength);
			//			System.out.println("Move Distance change= " + Math.abs((prevMoveLength-aveMoveLength)));
			moves++;
		}
		
//		System.out.println("Moves = " + moves);
		
		return this.getCentroids();
	}
	
	private void setInitialCentroids() {
		ArrayList<Double> cPos;
		Centroid c1 = null;
		double tempPos = 0.0;
		for (int n = 0; n < clusters.size(); n++) {
			cPos = new ArrayList<Double>();
			for(int j=0; j<mapDataPoints.elementAt(0).getPosition().size();j++){
				tempPos = (getMaxValue(j) - getMinValue(j))  * Math.random() + getMinValue(j);
				cPos.add(tempPos);
			}
			c1 = new Centroid(cPos);
			clusters.get(n).setCentroid(c1);
			c1.setCluster(clusters.get(n));
		}
	}
	
	private double getMaxValue(int pos) {
		double temp;
		temp = mapDataPoints.elementAt(0).getPosition().get(pos);
		for (int i = 0; i < mapDataPoints.size(); i++) {
			DataPoint dp = mapDataPoints.elementAt(i);
			temp = (dp.getPosition().get(pos) > temp) ? dp.getPosition().get(pos) : temp;
		}
		return temp;
	}
	
	private double getMinValue(int pos) {
		double temp;
		temp = mapDataPoints.elementAt(0).getPosition().get(pos);
		for (int i = 0; i < mapDataPoints.size(); i++) {
			DataPoint dp = mapDataPoints.elementAt(i);
			temp = (dp.getPosition().get(pos) < temp) ? dp.getPosition().get(pos) : temp;
		}
		return temp;
	}
	
	public ArrayList<Centroid> getCentroids() {
		ArrayList<Centroid> theCentroids = new ArrayList<Centroid>();
		Centroid tempCentroid = null;
		for(int i=0; i<clusters.size();i++){
			tempCentroid = new Centroid(clusters.get(i).getCentroid().getPosition());
			theCentroids.add(tempCentroid);
		}
		return theCentroids;
	}
}
