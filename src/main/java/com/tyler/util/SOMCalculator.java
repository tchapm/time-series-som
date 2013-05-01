package com.tyler.util;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;


import com.tyler.kmeans.model.KMeansClustering;
import com.tyler.som.model.Centroid;
import com.tyler.som.model.Cluster;
import com.tyler.som.model.DataPoint;
import com.tyler.som.model.MinSpanTree;




/**
 *
 * @author Tyler Chapman
 * @version 1.0
 * 
 * This class holds all the data to create the object for the clustering analysis.
 * Each instance of SOM object is associated with multiple clusters, a MST,
 * and a Vector of DataPoint objects. The SOM and DataPoint classes are
 * the only classes available from other packages.
 * 
 * 
 */

public class SOMCalculator {
	public static final double CUTOFF = 0.01;
	public static final int MAX_ITERATION = 7;
	private ArrayList<Cluster> clusters = new ArrayList<Cluster>();
	private Vector<DataPoint> mapDataPoints = new Vector<DataPoint>();
	private MinSpanTree MST;
	private int dimensions;
	private String branchFilePath=null;
	private double[][] euclDistMat = null;


	public SOMCalculator(int k, Vector<DataPoint> dataPoints, int dimensions, String branchFile) {
		//create the number of clusters required for the analysis
		for (int i = 0; i < k; i++) {
			clusters.add(new Cluster("Cluster" + i));
		}
		this.dimensions = dimensions;
		//get the data points that were instantiated within the main
		this.mapDataPoints = dataPoints;
		this.branchFilePath = branchFile;
	}

	public SOMCalculator(int numCentroids, Vector<DataPoint> datPoints, int numDim) {
		//create the number of clusters required for the analysis
		for (int i = 0; i < numCentroids; i++) {
			clusters.add(new Cluster("Cluster" + i));
		}
		this.dimensions = numDim;
		//get the data points that were instantiated within the main
		this.mapDataPoints = datPoints;
	}

	/*
	 * This method is the meat of the analysis. It will add the data points to 
	 * the clusters for the Voronoi Tesselation, send a call out for the MST, 
	 * and send a call out to do the Kernal smoothing. At the end of each iteration 
	 * it checks to see the cumulative distance that the centroids moved and will 
	 * stop if it has moved further than the cutoff value.
	 */
	public void startAnalysis(Boolean isRandom) throws IOException {
		//set Starting centroid positions at random positions
		setInitialCentroids();
//		System.out.println("After initialization: ");
//		this.displayCentPos();

		int n = 0;
		//assign all DataPoints to clusters in an even fashion
		if(!isRandom){
			while (n < mapDataPoints.size()) {
				for (int l = 0; l < clusters.size(); l++) 
				{
					clusters.get(l).addDataPoint(mapDataPoints.elementAt(n));
					n++;
					if (n >= mapDataPoints.size())
						break;
				}
			}
		}else{
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
		}

		//		PrintHelpers.printClusterDataMembers(this);
		//recalculate Cluster centroids
		for (int i = 0; i < clusters.size(); i++) {
			clusters.get(i).getCentroid().calcCentroid();
		}
//		System.out.println("After adding pts: ");
//		this.displayCentPos();
		//variable to keep track of how many cycles the algorithm has undergone

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
			//run the Voronoi tesselation. This is done just like one step of the K-means algorithm 
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
			//recalculate the centroid of each cluster to get its new position after the DataPoints have been moved
			for (int m = 0; m < clusters.size(); m++) {
				clusters.get(m).getCentroid().calcCentroid();
			}
//			System.out.println("Before smoothing " + moves + " :");
//			this.displayCentPos();
			//Kernel smoothing step
			//first create a MST of all the centroids and create a distance matrix of their branch distances in map space
			ArrayList<Centroid> theCentroids = getCentroids();
			if(branchFilePath==null){
				MST = new MinSpanTree(theCentroids, clusters);
				MST.makeMSTNoWriting();
			}else{
				MST = new MinSpanTree(clusters.size(), theCentroids, clusters, branchFilePath);
				MST.makeMST();
			}
			MST.calcDistMatrix();
			//move centroids based on the monotomically decreasing neighborhood function
			recalcDataPts(moves+1);
//			System.out.println("After smoothing " + moves + " :");
//			this.displayCentPos();
			//find how much they moved from the previous iteration to determine if map has reached convergence
			for(int i=0; i<initClusters.size(); i++){
				aveMoveLength += initClusters.get(i).calcClusterChange(clusters.get(i));
			}
			aveMoveLength = aveMoveLength/clusters.size();
			//			System.out.println("Ave centroid move distance = " + aveMoveLength);
			//			System.out.println("Move Distance change= " + Math.abs((prevMoveLength-aveMoveLength)));
//			System.out.println("After iteration " + moves + " :");
//			this.displayCentPos();
			moves++;
		}
		
		ArrayList<Centroid> theCentroids = getCentroids();
		if(branchFilePath==null){
			MST = new MinSpanTree(theCentroids, clusters);
			MST.makeMSTNoWriting();
		}else{
			MST = new MinSpanTree(clusters.size(), theCentroids, clusters, branchFilePath);
			MST.makeMST();
		}
		MST.calcDistMatrix();
		System.out.println("Moves = " + moves);

	}
	
	private void displayCentPos() {
		for(Centroid mapPt : this.getCentroids()){
			ArrayList<Double> pos = mapPt.getPosition();
			System.out.print("Averaged Position: ");
			for(int i=0;i<2;i++){
				System.out.print(pos.get(i) + " ");
			}
			System.out.println();
		}
	}

	public void startAnalysis(double minDistance, double maxDistance) throws IOException {
		//set Starting centroid positions at random positions
		setInitialCentroids();

		int n = 0;
		//assign all DataPoints to clusters in an even fashion
		loop1: while (true) {
			for (int l = 0; l < clusters.size(); l++) 
			{
				clusters.get(l).addDataPoint(mapDataPoints.elementAt(n));
				n++;
				if (n >= mapDataPoints.size())
					break loop1;
			}
		}
		//recalculate Cluster centroids
		for (int i = 0; i < clusters.size(); i++) {
			clusters.get(i).getCentroid().calcCentroid();
		}

		//variable to keep track of how many cycles the algorithm has undergone

		boolean runMapping = true;
		//finds the movement of the centroids at each step and checks with cutoff before concluding
		while(runMapping ){
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
				//run the Voronoi tesselation. This is done just like one step of the K-means algorithm 
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
				//recalculate the centroid of each cluster to get its new position after the DataPoints have been moved
				for (int m = 0; m < clusters.size(); m++) {
					clusters.get(m).getCentroid().calcCentroid();
				}

				//Kernel smoothing step
				//first create a MST of all the centroids and create a distance matrix of their branch distances in map space
				ArrayList<Centroid> theCentroids = getCentroids();
				MST = new MinSpanTree(clusters.size(), theCentroids, clusters, branchFilePath);
				MST.makeMST();
				MST.calcDistMatrix();
				//move centroids based on the monotomically decreasing neighborhood function
				recalcDataPts(moves+1);
				//find how much they moved from the previous iteration to determine if map has reached convergence
				for(int i=0; i<initClusters.size(); i++){
					aveMoveLength += initClusters.get(i).calcClusterChange(clusters.get(i));
				}
				aveMoveLength = aveMoveLength/clusters.size();
				//			System.out.println("Ave centroid move distance = " + aveMoveLength);
				//			System.out.println("Move Distance change= " + Math.abs((prevMoveLength-aveMoveLength)));
				moves++;
			}
			ArrayList<Centroid> theCentroids = getCentroids();
			if(branchFilePath.equals(null)){
				MST = new MinSpanTree(theCentroids, clusters);
				MST.makeMSTNoWriting();
			}else{
				MST = new MinSpanTree(clusters.size(), theCentroids, clusters, branchFilePath);
				MST.makeMST();
			}
			MST.calcDistMatrix();
			System.out.println("Moves = " + moves);
			if(clusters.size()<mapDataPoints.size()){
				runMapping = addOrMergeClust(maxDistance, minDistance);
			}else{
				runMapping = false;
			}

			runMapping = false;
		}

	}
	
	
	private boolean addOrMergeClust(double maxDistance, double minDistance) {
		int[][] treePath = MST.getTreePath();
		Centroid c1;
		boolean changed = false;
		ArrayList<Cluster> currClusters = (ArrayList<Cluster>) clusters.clone();
		for(int i=0;i<treePath.length;i++){
			Centroid centA = currClusters.get(treePath[i][0]).getCentroid();
			Centroid centB = currClusters.get(treePath[i][1]).getCentroid();
			if(clusters.size()>=mapDataPoints.size()){
				return changed;
			}
			if(!centA.equals(centB)){
				double dist = centA.calCentroidDist(centB);
				if(dist>maxDistance){
					ArrayList<Double> cPos = centA.getMiddlePt(centB);
					c1 = new Centroid(cPos);
					clusters.add(new Cluster("Cluster" + clusters.size()));
					clusters.get(clusters.size()-1).setCentroid(c1);
					c1.setCluster(clusters.get(clusters.size()-1));
					changed = true;
				}else if(dist<minDistance){
					Cluster mergeClust = new Cluster("Cluster" + clusters.size());
					Cluster clustA = currClusters.get(treePath[i][0]);
					Cluster clustB = currClusters.get(treePath[i][1]);
					ArrayList<Double> cPos = centA.getMiddlePt(centB);
					c1 = new Centroid(cPos);
					c1.setCluster(mergeClust);
					mergeClust.setCentroid(c1);
					for(int j=0; j<clustA.getNumDataPoints();j++){
						DataPoint dp = clustA.getDataPoint(j);
						mergeClust.addDataPoint(clustA.getDataPoint(j));
					}
					for(int j=0; j<clustB.getNumDataPoints();j++){
						mergeClust.addDataPoint(clustB.getDataPoint(j));
					}
					c1.calcCentroid();
					clusters.add(mergeClust);
					clusters.remove(clustA);
					clusters.remove(clustB);
					changed = true;
				}
			}

		}
		return changed;
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
	/*
	 * Method to move the units and their associated centroids 
	 * based on the monotomically decreasing neighborhood function.
	 */
	private void recalcDataPts(int iterNum) {
		Centroid[] setOfCentroids = new Centroid[clusters.size()];
		ArrayList<Double> smoothCentroid = new ArrayList<Double>();
		ArrayList<ArrayList<Double>> centroidPos = new ArrayList<ArrayList<Double>>(clusters.size());
		double[] centroidX = new double[clusters.size()];
		ArrayList<double[]> centroidPositions = new ArrayList<double[]>();
		for(int m=0;m<clusters.size();m++){ 
			setOfCentroids[m]=clusters.get(m).getCentroid();
			centroidPos.add(setOfCentroids[m].getPosition());
		}
		//smoothing correction calculation
		for(int i=0;i<dimensions;i++){
			centroidPositions.add(calcCentroidWt(centroidPos, i, iterNum));
		}
		Centroid tempCentroid = null;
		
		//set the new centroid positions to the clusters
		for (int j = 0; j < clusters.size(); j++) {
			smoothCentroid = new ArrayList<Double>();
			for(int i=0;i<dimensions; i++){
				centroidX = centroidPositions.get(i);
				smoothCentroid.add(centroidX[j]);
			}
			tempCentroid = new Centroid(smoothCentroid);
			clusters.get(j).setCentroid(tempCentroid);
			tempCentroid.setCluster(clusters.get(j));
		}

	}
	
	private void recalcDataPts2(int iterNum) {
		Centroid[] centroidSet = new Centroid[clusters.size()];
		double[] centroidWt = new double[dimensions];
		ArrayList<ArrayList<Double>> centroidPos = new ArrayList<ArrayList<Double>>(clusters.size());
		double[] centroidX = new double[clusters.size()];
		ArrayList<double[]> centroidPositions = new ArrayList<double[]>();
		for(int m=0;m<clusters.size();m++){ 
			centroidSet[m]=clusters.get(m).getCentroid();
			centroidPos.add(centroidSet[m].getPosition());
		}
		//smoothing correction calculation
		for(int i=0;i<dimensions;i++){
			centroidPositions.add(calcCentroidWt(centroidPos, i, iterNum));
		}
		Centroid tempCentroid = null;
		//set the new centroid positions to the clusters
		for (int j = 0; j < clusters.size(); j++) {
			centroidWt = new double[clusters.size()];
			for(int i=0;i<dimensions; i++){
				centroidX = centroidPositions.get(i);
				centroidWt[j] = centroidX[j];
			}
			tempCentroid = new Centroid(centroidWt);
			clusters.get(j).setCentroid(tempCentroid);
			tempCentroid.setCluster(clusters.get(j));
		}

	}

	/*
	 * Calculation for the neighborhood function. Run as a gaussian. The iteration 
	 * number is used to decrease the function at each iteration. It is multiplied 
	 * to have the desired effect on the movement process. This needs to be examined 
	 * for the future formulations of this algorithm. 
	 */
	private double[] calcCentroidWt(ArrayList<ArrayList<Double>> centroidPos, int positionComp, int iterNum) {
		double dist;
		double den = 0.0;
		double num = 0.0;
		int numMembers;
		int branchDist=0;
		Vector<DataPoint> v[] = getClusterOutput();
		double centroidWt[] = new double[centroidPos.size()];
		for(int i=0;i<centroidPos.size();i++){
			for(int j=0;j<centroidPos.size();j++){
				branchDist = MST.getPathMatrixValue(i, j);
				dist = branchDist*branchDist;
				numMembers = v[j].size();
				if(numMembers>0){
					num += Math.exp(-(dist*iterNum*20)/(2*MST.getVariance()))*centroidPos.get(j).get(positionComp)*numMembers;
					den += Math.exp(-(dist*iterNum*20)/(2*MST.getVariance()))*numMembers;
				}
			}
			centroidWt[i]= num/den;
			//System.out.println("CentroidWt["+ j + "] = " + centroidWt[j]);
			den = num = 0.0;
		}
		return centroidWt;
	}


	//	private int getClusterDistance(ArrayList<Double> centroid1, ArrayList<Double> centroid2) {
	//		double tempDist = 0.0;
	//		for(int i=0; i<centroid1.size(); i++){
	//			tempDist += Math.pow(centroid1.get(i)-centroid2.get(i),2);
	//		}
	//		return Math.sqrt(tempDist);
	//	}

	public Vector<DataPoint>[] getClusterOutput() {
		Vector<DataPoint> v[] = new Vector[clusters.size()];
		for (int i = 0; i < clusters.size(); i++) {
			v[i] = clusters.get(i).getDataPoints();
		}
		return v;
	}

	private void setInitialCentroids() {
		ArrayList<Double> cPos;
		Centroid c1 = null;
		double tempPos = 0.0;
		for (int n = 0; n < clusters.size(); n++) {
			cPos = new ArrayList<Double>();
			for(int j=0; j<mapDataPoints.elementAt(0).getPosition().size();j++){
				tempPos = (getMaxValue(j) - getMinValue(j))  * Math.random() + getMinValue(j);
				//				tempPos = (((getMaxValue(j) - getMinValue(j)) / (clusters.length + 1)) * n) + getMinValue(j);
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

	public int getTotalDataPoints() {
		return mapDataPoints.size();
	}

	public Cluster getCluster(int pos) {
		return clusters.get(pos);
	}

	public ArrayList<Cluster> getClusters() {
		return clusters;
	}

	public MinSpanTree getMST() {
		return MST;
	}

	public double[][] getEuclDistMat() {
		return euclDistMat;
	}
	public void printEuclDistMat(){
		if(euclDistMat.equals(null)){
			setEuclDistMat();
		}
		for(int i=0;i<clusters.size();i++){
			System.out.print("| ");
			for(int j=0;j<clusters.size();j++){
				System.out.printf("%.2f ", euclDistMat[i][j] );
			}
			System.out.println("|");
		}
	}
	public void setEuclDistMat() {
		euclDistMat = new double[clusters.size()][clusters.size()];
		for(int i=0;i<clusters.size();i++){
			Centroid centA = getCluster(i).getCentroid();
			for(int j=0;j<clusters.size();j++){
				Centroid centB = getCluster(j).getCentroid();
				euclDistMat[i][j] = centA.calCentroidDist(centB);
			}
		}
	}
	
	private ArrayList<Centroid> findAvePos(ArrayList<ArrayList<Centroid>> centArr) {
		Vector<DataPoint> mapPoints = new Vector<DataPoint>();
		int name = 0;
		int numFinalCents = centArr.get(0).size();
		for(Centroid firstGrp : centArr.get(0)){
//			System.out.println("First iteration position: " + firstGrp.getPosition());
		}
		for(ArrayList<Centroid> iterationCents : centArr){
			for(Centroid theCent : iterationCents){
				ArrayList<Double> pos = theCent.getPosition();
				mapPoints.add(new DataPoint(pos, ++name));
			}
		}
		
		
		int numDim = mapPoints.get(0).getPosition().size();
		KMeansClustering clusterOfClusters = new KMeansClustering(numFinalCents, mapPoints, numDim);
		ArrayList<Centroid>avePositions = null;
		try {
			avePositions = clusterOfClusters.startAnalysis();
			for(Centroid mapPt : avePositions){
//				System.out.println("Averaged Position: " + mapPt.getPosition());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return avePositions;
	}
	
	public void getAndSetAveMap(ArrayList<ArrayList<Centroid>> centArr) throws IOException {
		ArrayList<Centroid> aveCentroids = this.findAvePos(centArr);
		int centIndex = 0;
		for(Cluster theClust : clusters){
			theClust.setCentroid(aveCentroids.get(centIndex++));
		}
		MST = new MinSpanTree(clusters.size(), aveCentroids, clusters, branchFilePath);
		MST.makeMST();
		MST.calcDistMatrix();
		
	}
}
