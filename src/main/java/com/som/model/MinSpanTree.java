package com.som.model;
import java.util.ArrayList;


/**
*
* @author Tyler Chapman
* @version 1.0
* 
* This class creates and maintains the MST associated with the SOM. 
* The MST is created with the use of Prim's algorithm, and the distance 
* matrix is filled by using Floyd's algorithm.
* 
*/

public class MinSpanTree {
	private int[][] distanceMatrix; 
	private int[][] predMatrix; 
	private double variance = 0.0;
	private int totClusters;
	private ArrayList<Centroid> treeCentroids;
	private ArrayList<Cluster> treeClusters;
	private double totalPathLength;
	private int[][] treePath;

	
	public MinSpanTree(int numClusters, ArrayList<Centroid> inputCentroids, ArrayList<Cluster> clusters){
		this.totClusters = numClusters;
		this.treeCentroids = inputCentroids;
		this.treeClusters = clusters;
	}

	public MinSpanTree(int[][] path, int[][] pathDistance) {
		this.treePath = path;
		this.distanceMatrix = pathDistance;
	}

	public MinSpanTree(ArrayList<Centroid> centroids, ArrayList<Cluster> clusters) {
		this.totClusters = clusters.size();
		this.treeCentroids = centroids;
		this.treeClusters = clusters;
	}

	public int getTotClusters() {
		return totClusters;
	}

	public void setTotClusters(int totClusters) {
		this.totClusters = totClusters;
	}

	public ArrayList<Centroid> getTreeCentroids() {
		return treeCentroids;
	}

	public void setTreeCentroids(ArrayList<Centroid> treeCentroids) {
		this.treeCentroids = treeCentroids;
	}

	public ArrayList<Cluster> getTreeClusters() {
		return treeClusters;
	}

	public void setTreeClusters(ArrayList<Cluster> treeClusters) {
		this.treeClusters = treeClusters;
	}

	public double getTotalPathLength() {
		return totalPathLength;
	}

	public void setTotalPathLength(double totalPathLength) {
		this.totalPathLength = totalPathLength;
	}

	public void setVariance(double variance) {
		this.variance = variance;
	}

	

	public void makeMinSpanTree() {
		int[] inTree = new int[totClusters];
		double[] d = new double[totClusters];
		int[] whoTo = new int[totClusters]; 
		int[][] path = new int[2*(totClusters-1)][2];
		double[][] weight = new double[totClusters][totClusters];
		int[][] pathDistance = new int[totClusters][totClusters];
		

		int numberOfClusters = totClusters;
		//initialize weight matrix and pathDistance between centroids
		for(int i=0;i<numberOfClusters;i++){
			for (int j=0; j<numberOfClusters; j++) {	
				weight[i][j] = treeCentroids.get(i).calCentroidDist(treeClusters.get(j).getCentroid());
				pathDistance[i][j] = totClusters;
			}
		}
		for (int i = 0; i < numberOfClusters; ++i){
			d[i] = Double.MAX_VALUE;
		}
		for (int i = 0; i < numberOfClusters; ++i){
			inTree[i] = 0;
		}
		
		inTree[0] = 1;
		for (int i = 0; i < numberOfClusters; ++i){
			if ((weight[0][i] != 0) && (weight[0][i] < d[i])) {
				d[i] = weight[0][i];
				whoTo[i] = 0;
			}
		}

		for (int treeSize = 1; treeSize < numberOfClusters; ++treeSize) {
			// first find the node with the smallest distance within the tree
			int min = -1;
			for (int i = 0; i < numberOfClusters; ++i){
				if (inTree[i]==0){
					if ((min == -1) || (d[i] < d[min])){
						min = i;
					}
				}
			}
			//uncomment to debug
			/*String toVal = null;
			String minimum = null;
			toVal = Integer.toString(whoTo[min]);
			minimum = Integer.toString(min);
			System.out.println("Adding edge " + toVal + "-" + minimum);
			*/
			//Add the node and its associated arc that is located in the whoTo array
			path[(treeSize-1)][0] = whoTo[min];
			path[(treeSize-1)][1] = min;
			pathDistance[whoTo[min]][min]=pathDistance[min][whoTo[min]]=1;
			inTree[min] = 1;
			for (int i = 0; i < numberOfClusters; ++i){
				if ((weight[min][i] != 0) && (d[i] > weight[min][i])) {
					d[i] = weight[min][i];
					whoTo[i] = min;
				}
			}
		}
		treePath = path;
		distanceMatrix = pathDistance;
	}
	
	//Method to calculate the distances between the different nodes within the tree.
	//Modeled after Floyd's algorithm, by iterating through the distance matrix 
	//updating each connection until it is completely filled.
	public void calcDistMatrix(){
		int[][] pathDistance = new int[totClusters][totClusters];
		int[][] predNodes = new int[totClusters][totClusters];

		pathDistance=distanceMatrix;
		for(int i=0;i<totClusters; i++){
			for(int j =0;j<totClusters; j++){
				if(i==j){
					pathDistance[i][j] = 0;
					predNodes[i][j] = -1;
				}else{
					predNodes[i][j] = i;
				}
			}
		}
		for(int k=0;k<totClusters; k++){
			for(int i =0;i<totClusters; i++){
				for(int j=0; j<totClusters; j++){
					if (pathDistance[i][j]>(pathDistance[i][k]+pathDistance[k][j])){
						pathDistance[i][j]=pathDistance[i][k]+pathDistance[k][j];
						predNodes[i][j] = predNodes[k][j]; 
					}
				}
			}
		}
		distanceMatrix = pathDistance;
		predMatrix = predNodes;
		for(int k=0;k<totClusters-1; k++){
			for(int i =0;i<totClusters; i++){
				totalPathLength+=distanceMatrix[k][i];
			}
		}
		double meanPath = (double)totalPathLength/(totClusters*totClusters);
		for(int i=0; i<totClusters; i++){
			for(int k=0; k<totClusters; k++){
				variance += Math.pow(meanPath - distanceMatrix[i][k],2);
			}
		}
		variance = variance/(totClusters*totClusters);
//		System.out.println("The variance: " + variance);
		
	}

	public int getPathMatrixValue(int j, int i) {
		return distanceMatrix[j][i];
	}
	
	public int getPredMatrixValue(int j, int i) {
		return predMatrix[j][i];
	}

	public double getVariance() {
		return variance;
	}

	public void setDistanceMatrix(int[][] distMat){
		this.distanceMatrix = distMat;
	}
	public int[][] getDistanceMatrix() {
		return distanceMatrix;
	}
	public void setPredMatrix(int[][] predNodes) {
		this.predMatrix = predNodes;
	}

	public int[][] getPredMatrix() {
		return predMatrix;		
	}
	
	public int[][] getTreePath(){
		return treePath;
	}

}
