package com.som.kmeans.model;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.crypto.Data;

import org.junit.Before;
import org.junit.Test;

import com.som.model.Centroid;
import com.som.model.DataPoint;
import com.som.pq.model.InputDataCollection;

public class KMeansClusteringTest
{
	String testDataSet = "src/main/resources/linear2dNoiseData.txt";
	private InputDataCollection dataColl;
	int numCentroids;

	@Before
	public void setup() throws Exception
	{
		dataColl = new InputDataCollection(testDataSet);
		numCentroids = dataColl.getNumPts();
	}
	
	@Test
	public void testRunAnalysis() throws IOException 
	{
		KMeansClustering kmCluster = new KMeansClustering(numCentroids, dataColl.getDataPoints());
		kmCluster.initializeRandomPartition();
		
		ArrayList<Centroid> centroids = kmCluster.runAnalysis();
		int i = 1;
		for(Centroid mapPt : centroids){
			System.out.println("Centroid " + i++ + " Position: " + mapPt.getPosition());
		}
		System.out.println();
		for(DataPoint dp : dataColl.getDataPoints()){
			System.out.println("DataPoint " + i++ + " Position: " + dp.getPosition());
		}

	}
	@Test
	public void testRunWithReplicationDP() throws IOException 
	{
		
		ArrayList<DataPoint> doubleDp = new ArrayList<DataPoint>();
		doubleDp.addAll(dataColl.getDataPoints());
		for(DataPoint tempDp : dataColl.getDataPoints()) {
			tempDp.setName(tempDp.getName()+dataColl.getDataPoints().size());
			doubleDp.add(tempDp);
		}

		KMeansClustering kmCluster = new KMeansClustering(numCentroids, doubleDp);
		kmCluster.initializeRandomPartition();
		
		ArrayList<Centroid> centroids = kmCluster.runAnalysis();
		int i = 1;
		for(Centroid mapPt : centroids){
			System.out.println("Centroid " + i++ + " Position: " + mapPt.getPosition());
		}
		System.out.println();
		for(DataPoint dp : dataColl.getDataPoints()){
			System.out.println("DataPoint " + i++ + " Position: " + dp.getPosition());
		}

	}
}
