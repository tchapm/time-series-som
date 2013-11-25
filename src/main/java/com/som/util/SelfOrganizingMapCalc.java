package com.som.util;

import java.io.IOException;
import java.util.ArrayList;

import com.som.model.Centroid;
import com.som.model.Cluster;
import com.som.model.DataPoint;
import com.som.model.MinSpanTree;
import com.som.model.SelfOrganizingMapMain;

/**
 * 
 * @author Tyler Chapman
 * @version 1.0
 * 
 *          This class holds all the data to create the object for the
 *          clustering analysis. Each instance of SOM object is associated with
 *          multiple clusters, a MST, and a Vector of DataPoint objects. The SOM
 *          and DataPoint classes are the only classes available from other
 *          packages.
 * 
 * 
 */

public class SelfOrganizingMapCalc
{
	public static final double CUTOFF = 0.01;
	public static final int MAX_ITERATION = 7;
	private ArrayList<Cluster> clusters = new ArrayList<Cluster>();
	private ArrayList<DataPoint> mapDataPoints = new ArrayList<DataPoint>();
	private MinSpanTree minSpanTree;
	private int dimensions;
	@SuppressWarnings("unused")
	private String branchFilePath = null;
	private double[][] euclDistMat = null;

	public SelfOrganizingMapCalc(int numClusters,
			ArrayList<DataPoint> dataPoints, int dimensions, String branchFile)
	{
		// create the number of clusters required for the analysis
		for (int i = 0; i < numClusters; i++)
		{
			clusters.add(new Cluster("Cluster" + i));
		}
		this.dimensions = dimensions;
		// get the data points that were instantiated within the main
		this.mapDataPoints = dataPoints;
		this.branchFilePath = branchFile;
	}

	public SelfOrganizingMapCalc(int numCentroids, ArrayList<DataPoint> datPoints, int numDim)
	{
		// create the number of clusters required for the analysis
		for (int i = 0; i < numCentroids; i++)
		{
			clusters.add(new Cluster("Cluster" + i));
		}
		this.dimensions = numDim;
		// get the data points that were instantiated within the main
		this.mapDataPoints = datPoints;
	}

	public void initializeClustersRandomly()
	{
		// set Starting centroid positions at random positions
		setInitialCentroidsRandomly(mapDataPoints, clusters);
		// PrintHelpers.displayTwoDimCentPos(clusters);
		// assign datapoints based on location
		assignDataPtsByDistance(mapDataPoints, clusters);
		// PrintHelpers.displayTwoDimCentPos(clusters);
		// recalculate Cluster centroids
		recalculateClusters(clusters);			

	}

	public void initializeRandomPartition()
	{
		setInitialCentroidsRandomly(mapDataPoints, clusters);
		SelfOrganizingMapCalc.clearDataPts(clusters);
		for(DataPoint dataPoint : mapDataPoints)
		{
			clusters.get((int)(Math.random()*(clusters.size()))).addDataPoint(dataPoint);
		}
		recalculateClusters(clusters);
	}
	
	public void initializeForgy()
	{
		setInitialCentroidsForgy(mapDataPoints, clusters);
		assignDataPtsByDistance(mapDataPoints, clusters);
		recalculateClusters(clusters);
	}
	


	public void initializeOldWay()
	{
//		setInitialCentroids();
		setInitialCentroidsRandomly(mapDataPoints, clusters);
		int n = 0;
		//assign all DataPoints to clusters in an even fasion
		loop1: while (true) {
			for (int l = 0; l < clusters.size(); l++) 
			{
				clusters.get(l).addDataPoint(mapDataPoints.get(n));
				n++;
				if (n >= mapDataPoints.size())
					break loop1;
			}
		}
		
		recalculateClusters(clusters);
		
	}
	
	public void initializeDataPointsInOrder()
	{
		setInitialCentroidsRandomly(mapDataPoints, clusters);
		SelfOrganizingMapCalc.clearDataPts(clusters);
		int clusterNum = 0;
		for(DataPoint dataPoint : mapDataPoints)
		{
			if(clusterNum==clusters.size()){
				clusterNum = 0;
			}
			clusters.get(clusterNum++).addDataPoint(dataPoint);
		}
		recalculateClusters(clusters);
	}

	/*
	 * This method is the meat of the analysis. It will add the data points to
	 * the clusters for the Voronoi Tesselation, send a call out for the MST,
	 * and send a call out to do the Kernal smoothing. At the end of each
	 * iteration it checks to see the cumulative distance that the centroids
	 * moved and will stop if it has moved further than the cutoff value.
	 */
	public void runAnalysis() throws IOException
	{
		// finds the movement of the centroids at each step and checks with
		// cutoff before concluding
		double aveMoveLength = Double.MIN_VALUE;
		double prevMoveLength = Double.MAX_VALUE;
		int moves = 0;
		while (Math.abs(prevMoveLength - aveMoveLength) > CUTOFF)
		{
			prevMoveLength = aveMoveLength;
			// store the clusters for the comparison at the end of the loop
			ArrayList<Cluster> initalClusters = new ArrayList<Cluster>();
			for (int f = 0; f < clusters.size(); f++)
			{
				initalClusters.add(new Cluster("InitCluster" + f));
				initalClusters.get(f).setCentroid(clusters.get(f).getCentroid());
			}
			// run the Voronoi tesselation. This is done just like one step of
			// the K-means algorithm
			for (int j = 0; j < clusters.size(); j++)
			{
				for (int k = 0; k < clusters.get(j).getNumDataPoints(); k++)
				{
					// pick the first element of the first cluster
					// get the current Euclidean distance

					double tempEuDt = clusters.get(j).getDataPoint(k).getCurrentEuDt();
					Cluster tempCluster = null;
					boolean matchFoundFlag = false;
					for (int l = 0; l < clusters.size(); l++)
					{
						// test to see if testEuclidean < currentEuclidean for
						// each data point
						// to determine if it needs to be moved to a different
						// cluster
						double tempDist = clusters
								.get(j)
								.getDataPoint(k)
								.testEuclideanDistance(
										clusters.get(l).getCentroid());
						if (tempEuDt > tempDist)
						{
							tempEuDt = tempDist;
							tempCluster = clusters.get(l);
							matchFoundFlag = true;
						}
					}
					// if the DataPoint was found to be closer than the cluster
					// it is
					// currently associated with, switch it to the nearer
					// cluster
					if (matchFoundFlag)
					{
						tempCluster.addDataPoint(clusters.get(j)
								.getDataPoint(k));
						clusters.get(j).removeDataPoint(
								clusters.get(j).getDataPoint(k));
					}
				}
			}
			// PrintHelpers.printClusterDataMembers(this);
			// recalculate the centroid of each cluster to get its new position
			// after the DataPoints have been moved
			recalculateClusters(clusters);
			// System.out.println("Before smoothing " + moves + " :");
//			PrintHelpers.displayTwoDimCentPos(clusters);

			// Kernel smoothing step
			// first create a MST of all the centroids and create a distance
			// matrix of their branch distances in map space
			MinSpanTree tempTree = new MinSpanTree(getCentroids(clusters),
					clusters);
			tempTree.makeMinSpanTree();
			tempTree.calcDistMatrix();
			// move centroids based on the monotomically decreasing neighborhood
			// function
			recalcDataPts(moves + 1, tempTree);
			// System.out.println("After smoothing " + moves + " :");
			// this.displayCentPos();
			// find how much they moved from the previous iteration to determine
			// if map has reached convergence
			for (int i = 0; i < initalClusters.size(); i++)
			{
				aveMoveLength += initalClusters.get(i).calcClusterChange(
						clusters.get(i));
			}
			aveMoveLength = aveMoveLength / clusters.size();
			SelfOrganizingMapMain.logger.trace("Ave centroid move distance = "
					+ aveMoveLength);
			SelfOrganizingMapMain.logger.trace("Move Distance change= "
					+ Math.abs((prevMoveLength - aveMoveLength)));
			SelfOrganizingMapMain.logger.trace("After iteration " + moves
					+ " :");
			PrintHelpers.displayTwoDimCentPos(clusters, false);
			moves++;
		}

		ArrayList<Centroid> theCentroids = getCentroids(clusters);
		minSpanTree = new MinSpanTree(theCentroids, clusters);
		minSpanTree.makeMinSpanTree();
		minSpanTree.calcDistMatrix();
		setEuclDistMat();
		SelfOrganizingMapMain.logger.trace("Total Number of Moves = " + moves);

	}

	public void recalculateClusters()
	{
		recalculateClusters(clusters);
	}

	public static void recalculateClusters(ArrayList<Cluster> clusterList)
	{
		for (int i = 0; i < clusterList.size(); i++)
		{
			clusterList.get(i).getCentroid().recalculateCentroidPos();
		}
	}

	public void setInitialCentroidPosition()
	{
		setInitialCentroidsRandomly(mapDataPoints, clusters);
	}

	public static void setInitialCentroidsRandomly(ArrayList<DataPoint> dataPointList, ArrayList<Cluster> clusterList)
	{
		ArrayList<Double> centPos;
		Centroid tempCent = null;
		double tempPos = 0.0;
		for (int i = 0; i < clusterList.size(); i++)
		{
			centPos = new ArrayList<Double>();
			for (int j = 0; j < dataPointList.get(0).getPosition().size(); j++)
			{
				double minVal = getMinValue(dataPointList, j);
				tempPos = (getMaxValue(dataPointList, j) - minVal) * Math.random() + minVal;
				centPos.add(tempPos);
			}
			tempCent = new Centroid(centPos);
			clusterList.get(i).setCentroid(tempCent);
			tempCent.setCluster(clusterList.get(i));
		}
	}

	public static void setInitialCentroidsForgy(ArrayList<DataPoint> dataPointList, ArrayList<Cluster> clusterList)
	{
		ArrayList<DataPoint> tempList = ((ArrayList<DataPoint>) dataPointList.clone());
		for(Cluster cluster : clusterList)
		{
			int index = (int)(Math.random()*(tempList.size()));
			DataPoint tempPoint = tempList.get(index);
			Centroid tempCent = new Centroid(tempPoint.getPosition());
			tempList.remove(index);
			cluster.setCentroid(tempCent);
			tempCent.setCluster(cluster);
		}
		
	}
	
	public void assignDataPtsByDistance()
	{
		assignDataPtsByDistance(mapDataPoints, clusters);
	}

	public static void assignDataPtsByDistance(ArrayList<DataPoint> dataPointList, ArrayList<Cluster> clusterList)
	{
		SelfOrganizingMapCalc.clearDataPts(clusterList);
		for (DataPoint dataPoint : dataPointList)
		{
			double currDist = Double.MAX_VALUE;
			Cluster tempCluster = null;
			for(Cluster cluster : clusterList){
				double tempDist = dataPoint.testEuclideanDistance(cluster.getCentroid());
				if (tempDist < currDist)
				{
					currDist = tempDist;
					tempCluster = cluster;
				}
			}
			tempCluster.addDataPoint(dataPoint);
		}
	}

	@SuppressWarnings({ "unchecked", "unused" })
	private boolean addOrMergeClust(MinSpanTree tempTree, double maxDistance,
			double minDistance)
	{
		int[][] treePath = tempTree.getTreePath();
		Centroid c1;
		boolean changed = false;
		ArrayList<Cluster> currClusters = (ArrayList<Cluster>) clusters.clone();
		for (int i = 0; i < treePath.length; i++)
		{
			Centroid centA = currClusters.get(treePath[i][0]).getCentroid();
			Centroid centB = currClusters.get(treePath[i][1]).getCentroid();
			if (clusters.size() >= mapDataPoints.size())
			{
				return changed;
			}
			if (!centA.equals(centB))
			{
				double dist = centA.calCentroidDist(centB);
				if (dist > maxDistance)
				{
					ArrayList<Double> cPos = centA.getMiddlePt(centB);
					c1 = new Centroid(cPos);
					clusters.add(new Cluster("Cluster" + clusters.size()));
					clusters.get(clusters.size() - 1).setCentroid(c1);
					c1.setCluster(clusters.get(clusters.size() - 1));
					changed = true;
				}
				else if (dist < minDistance)
				{
					Cluster mergeClust = new Cluster("Cluster"
							+ clusters.size());
					Cluster clustA = currClusters.get(treePath[i][0]);
					Cluster clustB = currClusters.get(treePath[i][1]);
					ArrayList<Double> cPos = centA.getMiddlePt(centB);
					c1 = new Centroid(cPos);
					c1.setCluster(mergeClust);
					mergeClust.setCentroid(c1);
					for (int j = 0; j < clustA.getNumDataPoints(); j++)
					{
						mergeClust.addDataPoint(clustA.getDataPoint(j));
					}
					for (int j = 0; j < clustB.getNumDataPoints(); j++)
					{
						mergeClust.addDataPoint(clustB.getDataPoint(j));
					}
					c1.recalculateCentroidPos();
					clusters.add(mergeClust);
					clusters.remove(clustA);
					clusters.remove(clustB);
					changed = true;
				}
			}

		}
		return changed;
	}

	public static ArrayList<Centroid> getCentroids(
			ArrayList<Cluster> clusterList)
	{
		ArrayList<Centroid> theCentroids = new ArrayList<Centroid>();
		Centroid tempCentroid = null;
		for (int i = 0; i < clusterList.size(); i++)
		{
			tempCentroid = new Centroid(clusterList.get(i).getCentroid()
					.getPosition());
			theCentroids.add(tempCentroid);
		}
		return theCentroids;
	}

	public static ArrayList<Centroid> getCentroidsWithData(ArrayList<Cluster> clusterList)
	{
		ArrayList<Centroid> theCentroids = new ArrayList<Centroid>();
		for (int i = 0; i < clusterList.size(); i++)
		{
			if (clusterList.get(i).getNumDataPoints() > 0)
			{
				theCentroids.add(new Centroid(clusterList.get(i).getCentroid()
						.getPosition()));
			}
		}
		return theCentroids;
	}

	/*
	 * Method to move the units and their associated centroids based on the
	 * monotomically decreasing neighborhood function.
	 */
	private void recalcDataPts(int iterNum, MinSpanTree tempTree)
	{
		Centroid[] setOfCentroids = new Centroid[clusters.size()];
		ArrayList<Double> smoothCentroid = new ArrayList<Double>();
		ArrayList<ArrayList<Double>> centroidPos = new ArrayList<ArrayList<Double>>(
				clusters.size());
		double[] centroidX = new double[clusters.size()];
		ArrayList<double[]> centroidPositions = new ArrayList<double[]>();
		for (int m = 0; m < clusters.size(); m++)
		{
			setOfCentroids[m] = clusters.get(m).getCentroid();
			centroidPos.add(setOfCentroids[m].getPosition());
		}
		// smoothing correction calculation
		for (int i = 0; i < dimensions; i++)
		{
			centroidPositions.add(calcCentroidWt(centroidPos, i, iterNum,
					tempTree));
		}
		Centroid tempCentroid = null;

		// set the new centroid positions to the clusters
		for (int j = 0; j < clusters.size(); j++)
		{
			smoothCentroid = new ArrayList<Double>();
			for (int i = 0; i < dimensions; i++)
			{
				centroidX = centroidPositions.get(i);
				smoothCentroid.add(centroidX[j]);
			}
			tempCentroid = new Centroid(smoothCentroid);
			clusters.get(j).setCentroid(tempCentroid);
			tempCentroid.setCluster(clusters.get(j));
		}

	}

	/*
	 * Calculation for the neighborhood function. Run as a gaussian. The
	 * iteration number is used to decrease the function at each iteration. It
	 * is multiplied to have the desired effect on the movement process. This
	 * needs to be examined for the future formulations of this algorithm.
	 */
	private double[] calcCentroidWt(ArrayList<ArrayList<Double>> centroidPos,
			int positionComp, int iterNum, MinSpanTree tempTree)
	{
		double dist;
		double den = 0.0;
		double num = 0.0;
		int numMembers;
		int branchDist = 0;
		ArrayList<ArrayList<DataPoint>> v = getClusterOutput();
		double treeVariance = tempTree.getVariance();
		double centroidWt[] = new double[centroidPos.size()];
		for (int i = 0; i < centroidPos.size(); i++)
		{
			for (int j = 0; j < centroidPos.size(); j++)
			{
				branchDist = tempTree.getPathMatrixValue(i, j);
				dist = branchDist * branchDist;
				numMembers = v.get(j).size();
				if (numMembers > 0)
				{
					num += Math
							.exp(-(dist * iterNum * 20) / (2 * treeVariance))
							* centroidPos.get(j).get(positionComp) * numMembers;
					den += Math
							.exp(-(dist * iterNum * 20) / (2 * treeVariance))
							* numMembers;
				}
			}
			centroidWt[i] = num / den;
			// System.out.println("CentroidWt["+ j + "] = " + centroidWt[j]);
			den = num = 0.0;
		}
		return centroidWt;
	}

	public ArrayList<ArrayList<DataPoint>> getClusterOutput()
	{
		ArrayList<ArrayList<DataPoint>> dataPointsMatrix = new ArrayList<ArrayList<DataPoint>>();
		for (int i = 0; i < clusters.size(); i++)
		{
			ArrayList<DataPoint> tempPointList = clusters.get(i)
					.getDataPoints();
			dataPointsMatrix.add(tempPointList);
		}
		return dataPointsMatrix;
	}

	private static double getMaxValue(ArrayList<DataPoint> dataPts, int pos)
	{
		double temp;
		temp = dataPts.get(0).getPosition().get(pos);
		for (int i = 0; i < dataPts.size(); i++)
		{
			DataPoint dp = dataPts.get(i);
			temp = (dp.getPosition().get(pos) > temp) ? dp.getPosition().get(pos) : temp;
		}
		return temp;
	}

	private static double getMinValue(ArrayList<DataPoint> dataPts, int pos)
	{
		double temp;
		temp = dataPts.get(0).getPosition().get(pos);
		for (int i = 0; i < dataPts.size(); i++)
		{
			DataPoint dp = dataPts.get(i);
			temp = (dp.getPosition().get(pos) < temp) ? dp.getPosition().get(
					pos) : temp;
		}
		return temp;
	}

	public int getTotalDataPoints()
	{
		return mapDataPoints.size();
	}

	public Cluster getCluster(int pos)
	{
		return clusters.get(pos);
	}

	public ArrayList<Cluster> getClusters()
	{
		return clusters;
	}

	public MinSpanTree getMinSpanTree()
	{
		return minSpanTree;
	}

	public double[][] getEuclDistMat()
	{
		return euclDistMat;
	}

	public void setEuclDistMat()
	{
		euclDistMat = new double[clusters.size()][clusters.size()];
		for (int i = 0; i < clusters.size(); i++)
		{
			Centroid centA = getCluster(i).getCentroid();
			for (int j = 0; j < clusters.size(); j++)
			{
				Centroid centB = getCluster(j).getCentroid();
				euclDistMat[i][j] = centA.calCentroidDist(centB);
			}
		}
	}

	public static double getTotalDistanceFromCentroids(ArrayList<Cluster> clusters) {
		double totalDistance = 0;
		for(Cluster cluster : clusters){
			cluster.calcAndSetSumOfSquares();
			totalDistance += cluster.getSumSqr();
			
		}
		return totalDistance;
	}
	
	public static void clearDataPts(ArrayList<Cluster> clusterList)
	{
		for(Cluster cluster : clusterList) {
			cluster.clearDataPts();
		}
	}
	
	public void runPrintFunctions(String inputFolder) throws IOException
	{
		// getMST().getDistanceMatrix();
		// getMST().getPredMatrix();
		// set and print the euclidian distance Matrix
		PrintHelpers.writeClustOut(this, inputFolder);
		PrintHelpers.printClusterDataMembers(this);
		if (getEuclDistMat().equals(null))
		{
			setEuclDistMat();
		}
		PrintHelpers.printEuclDistMat(getEuclDistMat(), getClusters());
	}

}
