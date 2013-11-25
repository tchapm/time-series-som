package com.som.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.som.kmeans.model.KMeansClustering;
import com.som.pq.model.InputDataCollection;
import com.som.util.PrintHelpers;
import com.som.util.SelfOrganizingMapCalc;
import com.som.util.TreeEvaluator;

/**
 * 
 * @author Tyler Chapman
 * @version 1.0
 * 
 *          This is the main file that runs the SOM program. It is designed to
 *          take in a .txt file that has rows as time points and columns as a
 *          dimensional position. It will calculate the location of the units of
 *          the map as well as the connecting arcs of the MST that characterizes
 *          the SOM. It will also output the projection of the data points onto
 *          the tree. These will be output in a .txt file that has the number of
 *          components and the data points. This file can be used by any
 *          graphing program to display the resulting map.
 * @see SelfOrganizingMapCalc
 * @see DataPoint
 * @see MinSpanTree
 * @see Centroid
 * @see Cluster
 */

public class SelfOrganizingMapMain {

	private static final String FILE_DIVIDER = "/";
	public static final Logger logger = Logger.getLogger(SelfOrganizingMapMain.class);
	static {
		PropertyConfigurator.configure("log4j.properties");
	}

	private static void execute(String inputFile, boolean printOutput, int mdsDimensions, int numCycles, boolean forgyEachRun, boolean forgyKmeans)
			throws IOException {
		InputDataCollection dataColl = new InputDataCollection(inputFile);
		dataColl.setDataToScale(mdsDimensions);
		String inputFolder = inputFile.substring(0, inputFile.lastIndexOf(FILE_DIVIDER));
		double lowestError = Double.MAX_VALUE;
		int bestNumCent = 0;
		Vector<Integer> bestMapPath = null;
		ArrayList<Integer> bestPointPath = null;
		KMeansClustering bestMapPositions = null;

		for (int numCentroids = dataColl.getNumPts() - 4; numCentroids < dataColl.getNumPts(); numCentroids++) {
			KMeansClustering kCluster = SelfOrganizingMapMain.runCompleteSomAnalysis(dataColl, numCentroids, numCycles, inputFolder, forgyEachRun, forgyKmeans);
			MinSpanTree clusterTree = kCluster.getMinSpanTree();
			// mst connecting the map points
			TreeEvaluator tev = new TreeEvaluator(dataColl.getDataPoints(),
					clusterTree.getTreeClusters(), clusterTree.getPredMatrix(), clusterTree.getDistanceMatrix());
			tev.evaluatePaths();
			if (tev.getLowestError() < lowestError) {
				lowestError = tev.getLowestError();
				bestNumCent = numCentroids;
				bestMapPath = tev.getBestPath();
				bestPointPath = tev.finishPathAnal(0);
				bestMapPositions = kCluster;
			}

			PrintHelpers.printBestPath(tev, inputFolder, numCentroids,
					printOutput);
			PrintHelpers.printTopPaths(tev, inputFolder);

		}
		PrintHelpers.displayTwoDimCentPos(bestMapPositions.getClusters(), true);
		logger.info("Lowest of all error value: " + lowestError);
		PrintHelpers.printMatAndClust(bestMapPositions);
		PrintHelpers.printMat(bestMapPositions.getMinSpanTree().getPredMatrix());
		logger.info("Number of centroids: " + bestNumCent);
		logger.info("Map Space Order: " + bestMapPath.toString());
		logger.info("Data Point Order: " + bestPointPath.toString());
	}
	
	private static KMeansClustering runCompleteSomAnalysis(InputDataCollection dataColl, int numCentroids, int numCycles, String inputFolder, boolean forgyEachRun, boolean forgyKmeans) throws IOException {
		SelfOrganizingMapCalc som = null;
		ArrayList<ArrayList<Centroid>> centArr = new ArrayList<ArrayList<Centroid>>();
		// run analysis numCycle number of times and use
		for (int j = 0; j < numCycles; j++) {
			// threshold values for adding nodes
			som = new SelfOrganizingMapCalc(numCentroids, dataColl.getDataPoints(), dataColl.getNumDim());
//			som.initializeClustersRandomly();
//			som.initializeDataPointsRandomly();
//			
			if(forgyEachRun){
				som.initializeForgy();
			} else {
				som.initializeDataPointsInOrder();
			}
			
			som.runAnalysis();
			som.runPrintFunctions(inputFolder);
			centArr.add(SelfOrganizingMapCalc.getCentroidsWithData(som.getClusters()));
		}
		PrintHelpers.displayTwoDimCentPos(som.getClusters(), true);
		MinSpanTree clusterTree = som.getMinSpanTree();
		TreeEvaluator tev = new TreeEvaluator(dataColl.getDataPoints(), som.getClusters(), clusterTree.getPredMatrix(), clusterTree.getDistanceMatrix());
		tev.evaluatePaths();
		PrintHelpers.printBestPath(tev, inputFolder, numCentroids, false);
		PrintHelpers.printTopPaths(tev, inputFolder);
		KMeansClustering clusterOfClusters = new KMeansClustering(centArr, numCentroids);
//		clusterOfClusters.initializeClustersRandomly();
		if(forgyKmeans){
			clusterOfClusters.initializeForgy();
		} else {
			clusterOfClusters.initializeDataPointsInOrder();
		}
//		
		clusterOfClusters.getSetAverageMap(clusterOfClusters.runAnalysis());
//		SelfOrganizingMapCalc.assignDataPtsByDistance(dataColl.getDataPoints(), clusterOfClusters.getClusters());
		return clusterOfClusters;
	}

	private static String getInput(int analysisSet) {
		switch (analysisSet) {
		case 1:
			return "alphaValuesTrans.txt";
		case 2:
			return "cauloValues.txt";
		case 3:
			return "linear2dNoiseData.txt";
		case 4:
			return "sinusoid2dPos.txt";
		case 5:
			return "linear3dNoiseData.txt";
		case 6:
			return "sinusoid3dNoiseData.txt";
		case 7:
			return "crossingTimeData3D.txt";
		case 8:
			return "crossingTimeData2D.txt";
		case 9:
			return "jellyroll.txt";
		case 10:
			return "ethanolAirValues.txt";
		case 11:
			return "ethanolSubValues.txt";
		case 12:
			return "crabValues.txt";
		case 13:
			return "crabControlValues.txt";
		case 14:
			return "PlacentalValues.txt";
		case 15:
			return "firstWhiteSet.txt";
		case 16:
			return "secondWhiteSet.txt";
		case 17:
			return "thirdWhiteSet.txt";
		case 18:
			return "fourthWhiteSet.txt";
		case 19:
			return "fifthWhiteSet.txt";
		default:
			return null;
		}
	}

	public static void main(String args[]) throws IOException {
		String filePath = (args.length == 0) ? "src/main/resources/" : args[0];
		StringBuilder inFileSb = new StringBuilder();
		inFileSb.append(filePath);
		String inputFile = null;

		// the raw data set
		int analysisSet = 1;
		// 1: yeast from Spellman et al.
		// 2: caulobacter from Lamb et al.
		// 3: artificial linear 2D
		// 4: artificial sine 2D
		// 5: artificial linear 3D
		// 6: artificial sine 3D
		// 7: artificial crossover 3D
		// 8: artificial crossover 2D
		// 9: artificial jellyroll 2D
		// 10: drosophila exposed to air
		// 11: drosophila exposed to ethonal
		// 12: crab shock data averaged
		// 13: crab shock control data averaged
		// 14: placenta values
		// 15: first white set
		// 16: second white set
		// 17: third white set
		// 18: fourth white set
		// 19: fifth white set
		inFileSb.append(SelfOrganizingMapMain.getInput(analysisSet));
		inputFile = inFileSb.toString();
		logger.info("Input File Path: " + inputFile);
		boolean printOutput = false;
		int numCycles = 1;
		int mdsDimensions = 5;
		boolean forgyEachRun = true;
		boolean forgyKmeans = true;
		SelfOrganizingMapMain.execute(inputFile, printOutput, mdsDimensions, numCycles, forgyEachRun, forgyKmeans);
	}

}