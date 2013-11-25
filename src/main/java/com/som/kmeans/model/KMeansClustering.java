package com.som.kmeans.model;

import java.io.IOException;
import java.util.ArrayList;

import com.som.model.Centroid;
import com.som.model.Cluster;
import com.som.model.DataPoint;
import com.som.model.MinSpanTree;
import com.som.pq.model.InputDataCollection;
import com.som.util.SelfOrganizingMapCalc;

public class KMeansClustering {
	public static final double CUTOFF = 0.0001;
	public static final int MAX_ITERATION = 7;
	
	private ArrayList<Cluster> clusters = new ArrayList<Cluster>();
	private ArrayList<DataPoint> mapDataPoints = new ArrayList<DataPoint>();
	private MinSpanTree minSpanTree;
	
	
	public ArrayList<Cluster> getClusters() {
		return clusters;
	}

	public void setClusters(ArrayList<Cluster> clusters) {
		this.clusters = clusters;
	}

	public ArrayList<DataPoint> getMapDataPoints() {
		return mapDataPoints;
	}

	public void setMapDataPoints(ArrayList<DataPoint> mapDataPoints) {
		this.mapDataPoints = mapDataPoints;
	}

	public MinSpanTree getMinSpanTree() {
		return minSpanTree;
	}

	public void setMinSpanTree(MinSpanTree minSpanTree) {
		this.minSpanTree = minSpanTree;
	}

	public KMeansClustering(int numCentroids, ArrayList<DataPoint> dataPoints) {
		//create the number of clusters required for the analysis
		initClusters(numCentroids);
		//get the data points that were instantiated within the main
		mapDataPoints = dataPoints;
	}
	
	public KMeansClustering(ArrayList<ArrayList<Centroid>> centArr, int numCentroids) throws IOException {
		int name = 0;
		for(ArrayList<Centroid> iterationCents : centArr){
			for(Centroid theCent : iterationCents){
				ArrayList<Double> pos = theCent.getPosition();
				mapDataPoints.add(new DataPoint(pos, ++name));
			}
		}
		initClusters((mapDataPoints.size()>numCentroids) ? numCentroids : mapDataPoints.size());
	}

	private void initClusters(int numCentroids) {
		for (int i = 0; i < numCentroids; i++) {
			clusters.add(new Cluster("Cluster" + i));
		}
	}

	
	public void getSetAverageMap(ArrayList<Centroid> aveCentroids) throws IOException {
		int centIndex = 0;
		for(Cluster theClust : clusters){
			aveCentroids.get(centIndex).setCluster(theClust);
			theClust.setCentroid(aveCentroids.get(centIndex++));
			
		}
		minSpanTree = new MinSpanTree(clusters.size(), aveCentroids, clusters);
		minSpanTree.makeMinSpanTree();
		minSpanTree.calcDistMatrix();

	}
	
	public ArrayList<Centroid> findAvePos() {
		ArrayList<Centroid> avePositions = null;
		try {
			avePositions = runAnalysis();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return avePositions;
	}
	
	public ArrayList<Centroid> runAnalysis() throws IOException {
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
			SelfOrganizingMapCalc.recalculateClusters(clusters);

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
	
	
	public void initializeRandomPartition()
	{
		// set Starting centroid positions at random positions
		SelfOrganizingMapCalc.setInitialCentroidsRandomly(mapDataPoints, clusters);
		SelfOrganizingMapCalc.clearDataPts(clusters);
		for(DataPoint dataPoint : mapDataPoints)
		{
			clusters.get((int)(Math.random()*(clusters.size()))).addDataPoint(dataPoint);
		}
		SelfOrganizingMapCalc.recalculateClusters(clusters);

	}
//	
//	public void initializeDataPointsRandomly()
//	{
//		SelfOrganizingMapCalc.setInitialCentroidsRandomly(mapDataPoints, clusters);
//		SelfOrganizingMapCalc.clearDataPts(clusters);
//		for(DataPoint dataPoint : mapDataPoints)
//		{
//			clusters.get((int)(Math.random()*(clusters.size()))).addDataPoint(dataPoint);
//		}
//		SelfOrganizingMapCalc.recalculateClusters(clusters);
//		
//	}
	

	public void initializeForgy()
	{
		// set Starting centroid positions at random positions
		SelfOrganizingMapCalc.setInitialCentroidsForgy(mapDataPoints, clusters);
		// PrintHelpers.displayTwoDimCentPos(clusters);
		// assign datapoints based on location
		SelfOrganizingMapCalc.assignDataPtsByDistance(mapDataPoints, clusters);
		// PrintHelpers.displayTwoDimCentPos(clusters);
		// recalculate Cluster centroids
		SelfOrganizingMapCalc.recalculateClusters(clusters);
		
	}
	
	public void initializeDataPointsInOrder()
	{
		SelfOrganizingMapCalc.setInitialCentroidsRandomly(mapDataPoints, clusters);
		SelfOrganizingMapCalc.clearDataPts(clusters);
		int clusterNum = 0;
		for(DataPoint dataPoint : mapDataPoints)
		{
			if(clusterNum==clusters.size()){
				clusterNum = 0;
			}
			clusters.get(clusterNum++).addDataPoint(dataPoint);
		}
		SelfOrganizingMapCalc.recalculateClusters(clusters);
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
	
	public ArrayList<ArrayList<DataPoint>> getClusterOutput() {
		ArrayList<ArrayList<DataPoint>> dataPointsMatrix = new ArrayList<ArrayList<DataPoint>>();
		for (int i = 0; i < clusters.size(); i++) {
			ArrayList<DataPoint> tempPointList = clusters.get(i).getDataPoints();
			dataPointsMatrix.add(tempPointList);
		}
		return dataPointsMatrix;
	}
	
	public static void main (String args[]) throws IOException{
//		String inputFile = "/Users/tchap/Documents/SOMfiles/linear2dNoiseData.txt";
		int numCentroids = Integer.parseInt(args[0]);
		String inputFile = args[1];
		InputDataCollection dataColl = null;
		try {
			dataColl = new InputDataCollection(inputFile);
		}catch (IOException e) {
			System.out.println("Error: " + e.getMessage());
		}

		ArrayList<DataPoint> dataPoints = new ArrayList<DataPoint>();
		dataPoints = dataColl.getDataPoints();
		KMeansClustering theClustering = new KMeansClustering(numCentroids, dataPoints);
		ArrayList<Centroid> centroids = theClustering.runAnalysis();
		int i = 1;
		for(Centroid mapPt : centroids){
			System.out.println("Centroid " + i++ + " Position: " + mapPt.getPosition());
		}
		
	}

	@Override
	public String toString()
	{
		return "KMeansClustering [clusters=" + clusters + ", mapDataPoints=" + mapDataPoints + ", minSpanTree=" + minSpanTree + "]";
	}



}
