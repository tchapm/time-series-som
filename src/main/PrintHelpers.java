package main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import util.Cluster;
import util.InputDataCollection;
import util.DataPoint;
import util.KendalsTau;
import util.SOMCalculator;
import util.TreeEvaluator;

public class PrintHelpers {
	
	public static void writeClustOut(SOMCalculator som, String outlines, int numCentroids) throws IOException {
		FileWriter fstream = new FileWriter(outlines);
		BufferedWriter out = new BufferedWriter(fstream);
		Cluster tempCluster;
		out.write(numCentroids + "\n");
		for(int i=0;i<numCentroids;i++){
			tempCluster = som.getCluster(i);
			//retrieve the centroids to write to a file.
			for(int j=0; j<tempCluster.getCentroid().getPosition().size(); j++){
				out.write(tempCluster.getCentroid().getPosition().get(j) + " ");
			}
			out.write("\n");
		}
		out.close();
	}

	public static void printClusterDataMembers(SOMCalculator som){
		Vector<DataPoint>[] v = som.getClusterOutput();
		//loop to display the number of data points linked to each map point
		for (int i=0; i<v.length; i++){
			Vector<DataPoint> tempV = v[i];
			System.out.println("Cluster[" + i + "] corresponds to " + tempV.size() + " data points");
		}
		return;
	}
	
	public static void printMatAndClust(SOMCalculator som, InputDataCollection distMat) {
		distMat.printDataDistMat();
		printClusterDataMembers(som);
		som.printEuclDistMat();		
	}

	public static void printBestPath(TreeEvaluator tev,
			String branchProjection, String optimalPath, int numCentroids, Boolean filePrint) throws IOException {
		Vector<Integer> bestPath = tev.getBestPath();
		System.out.println("Number of centroids: " + numCentroids);
		System.out.println("Lowest error value: " + tev.getLowestError());
		System.out.println("Optimal Map Path: " + bestPath.toString());
		ArrayList<Integer> finalPath = null;
		if(filePrint){
			finalPath = tev.printCoordOnBranch(0,branchProjection, optimalPath);
		}else{
			finalPath = tev.finishPathAnal(0);
		}
		KendalsTau kenTau = new KendalsTau(finalPath);
		kenTau.print();		
	}


	public static void printTopPaths(TreeEvaluator tev, String branchProjection, String outlines) throws IOException {
		double bestTau = 0.0;
		int bestPathIndex = 0;
		for(int i=0; i<tev.getAllPaths().size() && i<20; i+=2){
			ArrayList<Integer> tempPath = tev.printCoordOnBranch(i,branchProjection, outlines);
			Vector<Integer> tempOrder = tev.getPath(i);
			int mstDistance = tev.getMstdist(i);
			KendalsTau kenTau = new KendalsTau(tempPath);
			double tau = Math.abs(kenTau.getTau());
			System.out.print((i+2)/2 + ". ");
			System.out.println("Map Path: " + tempOrder.toString() + "  BranchDistance: " + mstDistance);
			System.out.println(tev.printDegeneracy(i));
			kenTau.print();
			if(tau>bestTau){
				bestTau = tau;
				bestPathIndex = i/2;
				//	kenTau.print();
			}
		}
		System.out.println("Highest Tau: " + bestTau + " Path index: " + (bestPathIndex+1));		
	}


}
