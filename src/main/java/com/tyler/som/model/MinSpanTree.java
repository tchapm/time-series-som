package com.tyler.som.model;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


/**
*
* @author Tyler Chapman
* @version 1.0
* 
* This class creates and maintains the MST associated with the SOM. 
* The MST is created with the use of Kruskal's algorithm, and the distance 
* matrix is filled by using Floyd's algorithm.
* 
*/

public class MinSpanTree {
	private int[][] distanceMatrix; 
	private int[][] predMatrix; 
	private double variance = 0.0;
	private int totClusters;
	private ArrayList<Centroid> theCentroids;
	private ArrayList<Cluster> theClusters;
	private double totalPathLength;
	private String branchFilePath=null;
	private static int[][] treePath;

	
	public MinSpanTree(int numClusters, ArrayList<Centroid> inputCentroids, ArrayList<Cluster> clusters, String branchPath){
		this.totClusters = numClusters;
		this.theCentroids = inputCentroids;
		this.theClusters = clusters;
		this.branchFilePath = branchPath;
	}

	public MinSpanTree(int[][] path, int[][] pathDistance) {
		this.treePath = path;
		this.distanceMatrix = pathDistance;
	}

	public MinSpanTree(ArrayList<Centroid> centroids, ArrayList<Cluster> clusters) {
		this.totClusters = clusters.size();
		this.theCentroids = centroids;
		this.theClusters = clusters;
	}

	public MinSpanTree(int size, ArrayList<Centroid> aveCentroids,
			ArrayList<Cluster> clusters) {
		this.totClusters = clusters.size();
		this.theCentroids = aveCentroids;
		this.theClusters = clusters;
	}

	public void makeMST() throws IOException {
		int[] inTree = new int[totClusters];
		double[] d = new double[totClusters];
		int[] whoTo = new int[totClusters]; 
		int[][] path = new int[2*(totClusters-1)][2];
		double[][] weight = new double[totClusters][totClusters];
		int[][] pathDistance = new int[totClusters][totClusters];
		
		String toVal = null;
		String minimum = null;
		int m = totClusters;
		
		for(int i=0;i<m;i++){
			for (int j=0; j<m; j++) {	
				weight[i][j] = theCentroids.get(i).calCentroidDist(theClusters.get(j).getCentroid());
				pathDistance[i][j] = totClusters;
			}
		}
		for (int i = 0; i < m; ++i){
			d[i] = Double.MAX_VALUE;
		}
		for (int i = 0; i < m; ++i){
			inTree[i] = 0;
		}
		inTree[0] = 1;
		FileWriter fstream = null;
		
		for (int i = 0; i < m; ++i){
			if ((weight[0][i] != 0) && (d[i] > weight[0][i])) {
				d[i] = weight[0][i];
				whoTo[i] = 0;
			}
		}
		
//		String outlines = branchFilePath;
//		fstream = new FileWriter(outlines);
//		BufferedWriter out = new BufferedWriter(fstream);
		//write number of paths to the output file
//		out.write(m-1 + "\n");
		
		for (int treeSize = 1; treeSize < m; ++treeSize) {
			// first find the node with the smallest distance within the tree
			int min = -1;
			for (int i = 0; i < m; ++i){
				if (inTree[i]==0){
					if ((min == -1) || (d[min] > d[i])){
						min = i;
					}
				}
			}
			//Add the node and its associated arc that is located in the whoTo array
			toVal = Integer.toString(whoTo[min]);
			minimum = Integer.toString(min);
//			System.out.println("Adding edge " + toVal + "-" + minimum);
//			out.write(toVal + " " + minimum + "\n");
			path[2*(treeSize-1)][0] = whoTo[min];
			path[2*(treeSize-1)][1] = min;
			path[2*(treeSize-1)+1][0] = min;
			path[2*(treeSize-1)+1][1] = whoTo[min];
			pathDistance[whoTo[min]][min]=pathDistance[min][whoTo[min]]=1;
			inTree[min] = 1;
			for (int i = 0; i < m; ++i){
				if ((weight[min][i] != 0) && (d[i] > weight[min][i])) {
					d[i] = weight[min][i];
					whoTo[i] = min;
				}
			}
		}
		setTreePath(path);
		distanceMatrix = pathDistance;
//		out.close();	
	}
	

	public void makeMSTNoWriting() {
		int[] inTree = new int[totClusters];
		double[] d = new double[totClusters];
		int[] whoTo = new int[totClusters]; 
		int[][] path = new int[2*(totClusters-1)][2];
		double[][] weight = new double[totClusters][totClusters];
		int[][] pathDistance = new int[totClusters][totClusters];
		

		int m = totClusters;
		
		for(int i=0;i<m;i++){
			for (int j=0; j<m; j++) {	
				weight[i][j] = theCentroids.get(i).calCentroidDist(theClusters.get(j).getCentroid());
				pathDistance[i][j] = totClusters;
			}
		}
		for (int i = 0; i < m; ++i){
			d[i] = Double.MAX_VALUE;
		}
		for (int i = 0; i < m; ++i){
			inTree[i] = 0;
		}
		
		inTree[0] = 1;
		for (int i = 0; i < m; ++i){
			if ((weight[0][i] != 0) && (d[i] > weight[0][i])) {
				d[i] = weight[0][i];
				whoTo[i] = 0;
			}
		}

		for (int treeSize = 1; treeSize < m; ++treeSize) {
			// first find the node with the smallest distance within the tree
			int min = -1;
			for (int i = 0; i < m; ++i){
				if (inTree[i]==0){
					if ((min == -1) || (d[min] > d[i])){
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
			path[2*(treeSize-1)][0] = whoTo[min];
			path[2*(treeSize-1)][1] = min;
			path[2*(treeSize-1)+1][0] = min;
			path[2*(treeSize-1)+1][1] = whoTo[min];
			pathDistance[whoTo[min]][min]=pathDistance[min][whoTo[min]]=1;
			inTree[min] = 1;
			for (int i = 0; i < m; ++i){
				if ((weight[min][i] != 0) && (d[i] > weight[min][i])) {
					d[i] = weight[min][i];
					whoTo[i] = min;
				}
			}
		}
		setTreePath(path);
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
	
	public static int[][] setTreePath(int[][] doublePath){
		int[][] tempPath = new int[doublePath.length][2];
		for(int i=0; i<doublePath.length; i+=2){
			tempPath[i/2][0] = doublePath[i][0];
			tempPath[i/2][1] = doublePath[i][1];
		}
		treePath = tempPath;
		return tempPath;
	}
	
	public int[][] getTreePath(){
		return treePath;
	}

	public double getLengthBranch(int i, int j) {
		// TODO Auto-generated method stub
		return 0;
	}



}
