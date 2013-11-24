package com.som.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Vector;

import com.som.kmeans.model.KMeansClustering;
import com.som.model.Centroid;
import com.som.model.Cluster;
import com.som.model.DataPoint;
import com.som.model.SelfOrganizingMapMain;

public class PrintHelpers
{

	private static final String FILE_OUT_CENT_POS = "centroidPos.txt";
	private static final String OPTIMAL_PATH = "centroidLinesOpt.txt";
	private static final String BRANCH_PROJECTION = "centroidLinesProj.txt";

	public static void writeClustOut(SelfOrganizingMapCalc som,
			String outputFolder) throws IOException
	{
		FileWriter fstream = new FileWriter(outputFolder + FILE_OUT_CENT_POS);
		BufferedWriter out = new BufferedWriter(fstream);
		Cluster tempCluster;
		out.write(som.getClusters().size() + "\n");
		for (int i = 0; i < som.getClusters().size(); i++)
		{
			tempCluster = som.getCluster(i);
			// retrieve the centroids to write to a file.
			for (int j = 0; j < tempCluster.getCentroid().getPosition().size(); j++)
			{
				out.write(tempCluster.getCentroid().getPosition().get(j) + " ");
			}
			out.write("\n");
		}
		out.close();
	}

	public static void printClusterDataMembers(SelfOrganizingMapCalc som)
	{
		printClusterDataMembers(som.getClusterOutput());
	}

	private static void printClusterDataMembers(KMeansClustering mapPositions)
	{
		printClusterDataMembers(mapPositions.getClusterOutput());
	}

	private static void printClusterDataMembers(
			ArrayList<ArrayList<DataPoint>> dataPointMatrix)
	{
		// loop to display the number of data points linked to each map point
		for (int i = 0; i < dataPointMatrix.size(); i++)
		{
			StringBuilder sb = new StringBuilder();
			ArrayList<DataPoint> tempV = dataPointMatrix.get(i);
			for (DataPoint dp : tempV)
			{
				sb.append(dp.getName() + " ");
			}
			SelfOrganizingMapMain.logger.trace("Cluster[" + i
					+ "] corresponds to points: " + sb.toString());
		}
	}

	public static void printMatAndClust(SelfOrganizingMapCalc som)
	{
		printClusterDataMembers(som);
		printEuclDistMat(som.getEuclDistMat(), som.getClusters());
	}

	public static void printEuclDistMat(double[][] euclDistMat,
			ArrayList<Cluster> clusters)
	{
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(2);

		for (int i = 0; i < clusters.size(); i++)
		{
			StringBuilder sb = new StringBuilder();
			sb.append("| ");
			for (int j = 0; j < clusters.size(); j++)
			{
				sb.append(df.format(euclDistMat[i][j]) + " ");
			}
			sb.append("|");
			SelfOrganizingMapMain.logger.trace(sb.toString());
		}
	}

	public static void printMatAndClust(KMeansClustering mapPositions)
	{
		printClusterDataMembers(mapPositions);
	}

	public static void printMat(int[][] predMatrix)
	{
		for (int i = 0; i < predMatrix.length; i++)
		{
			StringBuilder sb = new StringBuilder();
			sb.append("| ");
			for (int j = 0; j < predMatrix.length; j++)
			{
				sb.append(" " + predMatrix[i][j]);
			}
			sb.append(" |");
			SelfOrganizingMapMain.logger.trace(sb.toString());
		}

	}

	public static void printBestPath(TreeEvaluator tev, String outputFolder,
			int numCentroids, Boolean filePrint) throws IOException
	{
		Vector<Integer> bestPath = tev.getBestPath();
		SelfOrganizingMapMain.logger.info("Number of centroids: "
				+ numCentroids);
		SelfOrganizingMapMain.logger.info("Lowest error value: "
				+ tev.getLowestError());
		SelfOrganizingMapMain.logger.info("Optimal Map Path: "
				+ bestPath.toString());
		ArrayList<Integer> finalPath = null;
		if (filePrint)
		{
			finalPath = tev.printCoordOnBranch(0, outputFolder
					+ BRANCH_PROJECTION, outputFolder + OPTIMAL_PATH);
		}
		else
		{
			finalPath = tev.finishPathAnal(0);
		}
		SelfOrganizingMapMain.logger.info("Calculated order: " + finalPath);
	}

	public static void printTopPaths(TreeEvaluator tev, String outputFolder)
			throws IOException
	{
		double bestTau = 0.0;
		int bestPathIndex = 0;
		for (int i = 0; i < tev.getAllPaths().size() && i < 20; i += 2)
		{
			ArrayList<Integer> tempPath = tev.printCoordOnBranch(i,
					outputFolder + BRANCH_PROJECTION, outputFolder
							+ OPTIMAL_PATH);
			Vector<Integer> tempOrder = tev.getPath(i);
			int mstDistance = tev.getMstdist(i);
			KendalsTau kenTau = new KendalsTau(tempPath);
			double tau = Math.abs(kenTau.getTau());
			SelfOrganizingMapMain.logger.trace((i + 2) / 2 + ". "
					+ "Map Path: " + tempOrder.toString()
					+ "  BranchDistance: " + mstDistance);
			SelfOrganizingMapMain.logger.trace(tev.printDegeneracy(i));
			SelfOrganizingMapMain.logger.trace("Calculated order: "
					+ kenTau.getInferredOrder());
			SelfOrganizingMapMain.logger.trace("Kendal's Tau: " + tau);
			if (tau > bestTau)
			{
				bestTau = tau;
				bestPathIndex = i / 2;
			}
		}
		SelfOrganizingMapMain.logger.trace("Highest Tau: " + bestTau
				+ " Path index: " + (bestPathIndex + 1));
	}

	public static void displayTwoDimCentPos(ArrayList<Cluster> clusters, boolean isInfo)
	{
		ArrayList<Centroid> centroids = PrintHelpers.getCentroids(clusters);
		if(isInfo) {
			SelfOrganizingMapMain.logger.info("\n");
		} else {
			SelfOrganizingMapMain.logger.trace("\n");
		}
		for (Centroid mapPt : centroids)
		{
			StringBuilder sb = new StringBuilder();
			ArrayList<Double> pos = mapPt.getPosition();
			sb.append("Averaged Position: ");
			for (int i = 0; i < 2; i++)
			{
				sb.append(pos.get(i) + " ");
			}
			if(isInfo) {
				SelfOrganizingMapMain.logger.info(sb.toString());
			} else {
				SelfOrganizingMapMain.logger.trace(sb.toString());
			}
		}
	}

	private static ArrayList<Centroid> getCentroids(ArrayList<Cluster> clusters)
	{
		ArrayList<Centroid> centroids = new ArrayList<Centroid>();
		for (Cluster aCluster : clusters)
		{
			centroids.add(aCluster.getCentroid());
		}
		return centroids;
	}

}
