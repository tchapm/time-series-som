package com.som.model;

import java.io.IOException;
import java.util.ArrayList;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.som.kmeans.model.KMeansClustering;
import com.som.mdscale.MDS;
import com.som.pq.model.InputDataCollection;
import com.som.util.PrintHelpers;
import com.som.util.SelfOrganizingMapCalc;

public class SelfOrganizingMapMainTest
{
	String testDataSet = "src/main/resources/linear2dNoiseData.txt";
	private InputDataCollection dataColl;
	int numCentroids;

	@Before
	public void setup() throws Exception
	{
		dataColl = new InputDataCollection(testDataSet);
		numCentroids = dataColl.getNumPts() - 2;
	}

	@Test
	@Ignore
	public void testInputData()
	{
		double[][] distMat = dataColl.getDistMat();
		Assert.assertEquals(distMat[0][0], 0.0);
		Assert.assertEquals(distMat[0][1], 0.5179044313384469);
		double[][] scalingMat = MDS.classicalScaling(distMat, 2);
		Assert.assertEquals(distMat[0][0], 0.0);
		Assert.assertEquals(distMat[0][1], 0.5179044313384469);
	}

	@Test
	@Ignore
	public void testSomGeneration() throws IOException
	{
		SelfOrganizingMapCalc som = new SelfOrganizingMapCalc(numCentroids,
				dataColl.getDataPoints(), dataColl.getNumDim());

		som.setInitialCentroidPosition();
		Assert.assertEquals(numCentroids,
				SelfOrganizingMapCalc.getCentroids(som.getClusters()).size());

		som.assignDataPtsByDistance();
		ArrayList<Centroid> cenList = SelfOrganizingMapCalc.getCentroids(som
				.getClusters());
		som.recalculateClusters();

		Assert.assertNotSame(cenList,
				SelfOrganizingMapCalc.getCentroids(som.getClusters()));
	}

	@Test
	@Ignore
	public void testAnalysis() throws IOException
	{
		SelfOrganizingMapCalc som = new SelfOrganizingMapCalc(numCentroids,
				dataColl.getDataPoints(), dataColl.getNumDim());
		som.initializeClustersRandomly();
		som.runAnalysis();
		double[][] distMat = som.getEuclDistMat();
		Assert.assertEquals(distMat[0][0], 0.0);

		som.getMinSpanTree();
	}

	@Test
	public void testNumCycleEffect() throws IOException {
		int maxCycles = 2;
		double aveDistance = 0.0;
//		for(int i=1; i<=maxCycles; i++ ){
//			aveDistance += testDifferenInitializations(i, true);
//		}
//		System.out.println("Random Clusters: Average distance over " + maxCycles + " cycles = " + aveDistance/(double)maxCycles);
//		aveDistance = 0.0;
//		for(int i=1; i<=maxCycles; i++ ){
//			aveDistance += testDifferenInitializations(i, true);
//		}
		testDifferenInitializations(maxCycles, false);
		System.out.println("Random DataPoints: Average distance over " + maxCycles + " cycles = " + aveDistance/(double)maxCycles);
	}
	
	public double testDifferenInitializations(int numCycles, boolean isRandCluster) throws IOException
	{
		ArrayList<ArrayList<Centroid>> centArr = new ArrayList<ArrayList<Centroid>>();

		if(isRandCluster){
			for (int i=0; i<numCycles; i++){
				SelfOrganizingMapCalc som = new SelfOrganizingMapCalc(numCentroids, dataColl.getDataPoints(), dataColl.getNumDim());
				som.initializeRandomPartition();
				som.runAnalysis();
				PrintHelpers.displayTwoDimCentPos(som.getClusters(), false);
				//			System.out.println("NumDataPts: " + dataColl.getDataPoints().size() + "  NumSOMClusters: " + som.getClusters().size());
				centArr.add(SelfOrganizingMapCalc.getCentroidsWithData(som.getClusters()));
			}
			System.out.println("Running with random cluster assignment");
			return runClusterEval(centArr, numCycles);
		} else {
			centArr = new ArrayList<ArrayList<Centroid>>();
			for (int i=0; i<numCycles; i++){
				SelfOrganizingMapCalc som = new SelfOrganizingMapCalc(numCentroids, dataColl.getDataPoints(), dataColl.getNumDim());
				som.initializeForgy();
				som.runAnalysis();
				PrintHelpers.displayTwoDimCentPos(som.getClusters(), true);
				//			System.out.println("NumDataPts: " + dataColl.getDataPoints().size() + "  NumSOMClusters: " + som.getClusters().size());
				centArr.add(SelfOrganizingMapCalc.getCentroidsWithData(som.getClusters()));
			}
			System.out.println("Running with random data point assignment");
			return runClusterEval(centArr, numCycles);
		}
	}

	private double runClusterEval(ArrayList<ArrayList<Centroid>> centArr, int numCycles) throws IOException
	{
		KMeansClustering clusterOfClusters = new KMeansClustering(centArr, numCentroids);
		clusterOfClusters.initializeForgy();
		clusterOfClusters.getSetAverageMap(clusterOfClusters.runAnalysis());
//		System.out.println("NumDataPts: " + dataColl.getDataPoints().size() + "  NumClusters: " + clusterOfClusters.getCentroids().size());
		SelfOrganizingMapCalc.assignDataPtsByDistance(dataColl.getDataPoints(), clusterOfClusters.getClusters());
		double totalDistance = 0;
		for(Cluster cluster : clusterOfClusters.getClusters()){
			totalDistance += cluster.getSumSqr();
		}
		PrintHelpers.displayTwoDimCentPos(clusterOfClusters.getClusters(), true);
		System.out.println("Total distance: " + totalDistance + " With numCycles: " + numCycles);
		
		return totalDistance;
	}

}
