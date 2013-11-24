package com.som.util;
/**
*
* @author Tyler Chapman
* @version 1.0
* 
*  Class to create the SOM tree and evaluate all possible paths. 
*  This will determine the best path based on the smallest cumulative distance
*  from the data points and their projection onto the respective path. 
*/
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

import com.som.model.Cluster;
import com.som.model.Coords;
import com.som.model.DataPoint;
import com.som.model.PathAndDistance;



public class TreeEvaluator {
	private static final double MAX_DOUBLE_VALUE = Double.MAX_VALUE;
	private ArrayList<DataPoint> dataPoints = new ArrayList<DataPoint>();
	private int[][] predMatrix; 
	private int[][] mstDistMatrix;
	private ArrayList<Cluster> theClusters;
	private Vector<Integer> bestPath = new Vector<Integer>();
	private int numNodes;
	private ArrayList<PathAndDistance> allPaths = new ArrayList<PathAndDistance>(); 
	private double lowestError;
	private HashMap<Integer,Coords> projPoints = new HashMap<Integer, Coords>();
	
	
	public TreeEvaluator(ArrayList<DataPoint> inputDataPoints, ArrayList<Cluster> arrayList, int[][] preMatrix, int[][] distMatrix) {
		this.dataPoints = inputDataPoints;
		this.theClusters = arrayList;
		this.predMatrix = preMatrix;
		this.mstDistMatrix = distMatrix;
		this.numNodes = theClusters.size();
	}

	//look at each path and find the one with the lowest error, set the lowest if you get find a smaller one
	public void evaluatePaths(){
		double lowestIndError = MAX_DOUBLE_VALUE;
		double lowestOfAllError = MAX_DOUBLE_VALUE;
		Vector<Integer> bestIndividualPath = null; 
		Vector<Integer> bestOfAllPath = null; 
		PathAndDistance tempPathAndDist = null;
		for(int j=0; j<numNodes; j++){
			for(int i=0; i<numNodes; i++){
				if(i!=j){
					tempPathAndDist = getPathError(i,j);
					if(tempPathAndDist.getDist()<lowestIndError){
						lowestIndError = tempPathAndDist.getDist();
						bestIndividualPath = tempPathAndDist.getPath();
					}
				}
			}
			
			if(lowestIndError<lowestOfAllError){
				lowestOfAllError = lowestIndError;
				bestOfAllPath = bestIndividualPath;
			}
			lowestIndError = MAX_DOUBLE_VALUE;
			bestIndividualPath = null; 
		}
		this.lowestError = lowestOfAllError;
		this.bestPath = bestOfAllPath;
		Collections.sort(allPaths, PathAndDistance.comparator);
	}
	
	//find the path error for a specific starting point with the end point
	private PathAndDistance getPathError(int i, int j) {
		int nextNode = -1, prevNode = -1;
		PathAndDistance indPath = new PathAndDistance();
		double indivPathError = 0.0;
		Vector<Integer> indivPath = new Vector<Integer>();
		indivPath.add(j);
		ArrayList<Double> centroidToSegment = new ArrayList<Double>();
		for(int k=0; k<dataPoints.size(); k++){
			centroidToSegment.add(MAX_DOUBLE_VALUE);
		}
		nextNode = predMatrix[i][j];
		indivPath.add(nextNode);
		getDistanceNearPoints(j, nextNode, centroidToSegment);
		prevNode = nextNode;
		while(nextNode!=i){
			nextNode = predMatrix[i][nextNode];
			indivPath.add(nextNode);
			getDistanceNearPoints(prevNode,nextNode, centroidToSegment);
			prevNode = nextNode;
		}
		indivPathError = totalLengths(centroidToSegment);
		
		indPath = new PathAndDistance(indivPathError, indivPath, mstDistMatrix[i][j]);
		allPaths.add(indPath);
		return indPath;
	}
	
	//method cycles through all branches and sets point on the one that is nearest
	public void setPointsOnBranches(PathAndDistance topPath){
		int nextNode = -1, prevNode = -1;
		int i = topPath.getPath().firstElement();
		int end = topPath.getPath().lastElement();
		ArrayList<Double> centroidToSegment = new ArrayList<Double>();
		for(int j=0; j<dataPoints.size(); j++){
			centroidToSegment.add(MAX_DOUBLE_VALUE);
		}
		nextNode = predMatrix[i][end];
		getPointsOnBranches(end, nextNode, centroidToSegment);
		prevNode = nextNode;
		while(nextNode!=i){
			nextNode = predMatrix[i][nextNode];
			getPointsOnBranches(prevNode,nextNode, centroidToSegment);
			prevNode = nextNode;
		}

		
	}
//	calls method to find distance to projection between two nodes and replaces 
//	projection if smaller than current projection
	private void getPointsOnBranches(int pointA, int pointB, ArrayList<Double> centroidToSegment) {
		ArrayList<Double> posC = new ArrayList<Double>();
		double tempSegmentDistance;
		Coords tempCoords;
		for(int i=0; i<dataPoints.size(); i++){
			posC = dataPoints.get(i).getPosition();
			ArrayList<Double> posA = theClusters.get(pointA).getCentroid().getPosition();
			ArrayList<Double> posB = theClusters.get(pointB).getCentroid().getPosition();
			tempSegmentDistance = getDistanceFromBranch(posA,posB,posC);
			tempCoords = getCoordOnBranch(posA,posB,posC);

			if(tempSegmentDistance<centroidToSegment.get(i)){
				tempCoords.setSegment(pointA,pointB);
				centroidToSegment.set(i, tempSegmentDistance);
				projPoints.put(i, tempCoords);
			}
		}	
	}


	private double totalLengths(ArrayList<Double> centroidToSegment) {
		double sumLengths = 0.0;
		for(int i=0; i<centroidToSegment.size(); i++){
			sumLengths+=centroidToSegment.get(i);
		}
		return sumLengths;
	}
	
	//get sum of distances from projection onto the path
	private void getDistanceNearPoints(int pointA, int pointB, ArrayList<Double> centroidToSegment) {
		ArrayList<Double> aPos = theClusters.get(pointA).getCentroid().getPosition();
		ArrayList<Double> bPos = theClusters.get(pointB).getCentroid().getPosition();
		ArrayList<Double> cPos = new ArrayList<Double>();
		double tempSegmentDistance;
		for(int i=0; i<dataPoints.size(); i++){
			cPos = dataPoints.get(i).getPosition();
			tempSegmentDistance = getDistanceFromBranch(aPos, bPos, cPos);
			if(tempSegmentDistance<centroidToSegment.get(i)){
				centroidToSegment.set(i, tempSegmentDistance);
			}
		}
	}

	private Coords getCoordOnBranch(ArrayList<Double> posA, ArrayList<Double> posB, ArrayList<Double> posC) {
		double b_aSq = 0.0;
		double ca_ba = 0.0;
		double t = 0.0;
		ArrayList<Double> posP = new ArrayList<Double>();
		Coords tempCoord = new Coords();
		for(int i=0;i<posA.size();i++){
			b_aSq+=(posB.get(i)-posA.get(i))*(posB.get(i)-posA.get(i));
			ca_ba+=(posC.get(i)-posA.get(i))*(posB.get(i)-posA.get(i));
		}
		t = ca_ba/b_aSq;
		for(int i=0;i<posA.size();i++){
			posP.add(posA.get(i) + t*(posB.get(i)-posA.get(i)));
		}
		if(ca_ba<=0){
			tempCoord.setLocation(posA);
		}else if(b_aSq<=ca_ba){
			tempCoord.setLocation(posB);
		}else {
			tempCoord.setLocation(posP);
		}
		return tempCoord;
	}
	
	private double getDistanceFromBranch(ArrayList<Double> posA, ArrayList<Double> posB, ArrayList<Double> posC) {
		double b_aSq = 0.0;
		double ca_ba = 0.0;
		double t = 0.0;
		double dist1=0.0, dist2=0.0;
		double distanceLine =0.0;
		ArrayList<Double> posP = new ArrayList<Double>();
		
		for(int i=0;i<posA.size();i++){
			b_aSq+=(posB.get(i)-posA.get(i))*(posB.get(i)-posA.get(i));
			ca_ba+=(posC.get(i)-posA.get(i))*(posB.get(i)-posA.get(i));
			dist1 += (posC.get(i)-posA.get(i))*(posC.get(i)-posA.get(i));
			dist2 += (posC.get(i)-posB.get(i))*(posC.get(i)-posB.get(i));
		}
		dist1 = Math.sqrt(dist1);
		dist2 = Math.sqrt(dist2);
		t = ca_ba/b_aSq;

		for(int i=0;i<posA.size();i++){
			posP.add(posA.get(i) + t*(posB.get(i)-posA.get(i)));
		}

		for(int i=0;i<posA.size();i++){
			distanceLine+=(posC.get(i)-posP.get(i))*(posC.get(i)-posP.get(i));
		}
		distanceLine=Math.sqrt(distanceLine);
		if(ca_ba<=0){
			return dist1;
		}else if(b_aSq<=ca_ba){
			return dist2;
		}else {
			return distanceLine;
		}
	}

	public Vector<Integer> getBestPath() {
		return this.bestPath;
	}
	public ArrayList<PathAndDistance> getAllPaths() {
		return allPaths;
	}
	public void setAllPaths(ArrayList<PathAndDistance> allPaths) {
		this.allPaths = allPaths;
	}
	public double getLowestError() {
		return lowestError;
	}
	public void setLowestError(double lowestError) {
		this.lowestError = lowestError;
	}

//Method to output the data to the text files for analysis. Will only be called once analysis of all paths have been made. 
	public ArrayList<Integer> printCoordOnBranch(int j, String branchString, String outlines) throws IOException {
		FileWriter fstream = new FileWriter(branchString);
		BufferedWriter out = new BufferedWriter(fstream);
		PathAndDistance chosenPath = new PathAndDistance();
		chosenPath = allPaths.get(j);
//		double distance = chosenPath.getDist();
//		System.out.println("Total path error: " + distance);
//		HashMap<Integer,Coords> pathMap = setPointsOnBranches(chosenPath);
		setPointsOnBranches(chosenPath);
		out.write(projPoints.size() + "\n");
		HashMap<ArrayList<Double>, ArrayList<Integer>> degenerateList = new HashMap<ArrayList<Double>, ArrayList<Integer>>();
		for(int i=0; i<projPoints.size(); i++){
//			out.write(pathMap.get(i).printCoord() + "\n");
			ArrayList<Double> tempPath = projPoints.get(i).getLocation();
			if(degenerateList.containsKey(tempPath)){
//				ArrayList<Integer> tempList = degenerateList.get(tempPath).add(i);
//				degenerateList.put(tempPath, degenerateList.get(tempPath).add(i));
				degenerateList.get(tempPath).add(i);
			}else{
				ArrayList<Integer> tempList = new ArrayList<Integer>();
				tempList.add(i);
				degenerateList.put(tempPath, tempList);
			}
//			System.out.print("coord " + i + ": ");
			for(double k : tempPath){
		//		System.out.print(k + " ");
				out.write(k + " ");
			}
//			System.out.print("\n");
			out.write("\n");
			projPoints.get(i).setName(i);
//			System.out.println(pathMap.get(i).printSegment());
		}
		out.close();
		allPaths.get(j).setDegeneracy(degenerateList);
		fstream = new FileWriter(outlines);
		out = new BufferedWriter(fstream);
		//write number of paths to the output file
		out.write(chosenPath.getPath().size()-1 + "\n");
		String startPt = chosenPath.getPath().get(0).toString();
		for(int i=1; i<chosenPath.getPath().size(); i++){
			out.write(startPt + " " + chosenPath.getPath().get(i).toString() + "\n");
			startPt = chosenPath.getPath().get(i).toString();
		}
		out.close();
		int posA;
		ArrayList<Coords> pathMembers;
		ArrayList<Integer> finalPath = new ArrayList<Integer>();
		for(int i=0;i<chosenPath.getPath().size();i++){
			pathMembers = new ArrayList<Coords>();
			posA = chosenPath.getPath().get(i);
			for(int k=0; k<projPoints.size(); k++){
				if(projPoints.get(k).getSegB()==posA){
					projPoints.get(k).setDist(theClusters.get(posA).getCentroid().getPosition());
					pathMembers.add(projPoints.get(k));
				}
			}
			Collections.sort(pathMembers, Coords.comparator);
			for(Coords n : pathMembers){
				finalPath.add(n.getName());
			}
		}
		return finalPath;
	}

	public ArrayList<Integer> finishPathAnal(int pathIndex) {
		PathAndDistance chosenPath = new PathAndDistance();
		chosenPath = allPaths.get(pathIndex);
		setPointsOnBranches(chosenPath);
		HashMap<ArrayList<Double>, ArrayList<Integer>> degenerateList = new HashMap<ArrayList<Double>, ArrayList<Integer>>();
		for(int i=0; i<projPoints.size(); i++){
			ArrayList<Double> tempPath = projPoints.get(i).getLocation();
			if(degenerateList.containsKey(tempPath)){
				degenerateList.get(tempPath).add(i);
			}else{
				ArrayList<Integer> tempList = new ArrayList<Integer>();
				tempList.add(i);
				degenerateList.put(tempPath, tempList);
			}
			projPoints.get(i).setName(i);
		}
		allPaths.get(pathIndex).setDegeneracy(degenerateList);
		int posA;
		ArrayList<Coords> pathMembers;
		ArrayList<Integer> finalPath = new ArrayList<Integer>();
		for(int i=0;i<chosenPath.getPath().size();i++){
			pathMembers = new ArrayList<Coords>();
			posA = chosenPath.getPath().get(i);
			for(int k=0; k<projPoints.size(); k++){
				if(projPoints.get(k).getSegB()==posA){
					projPoints.get(k).setDist(theClusters.get(posA).getCentroid().getPosition());
					pathMembers.add(projPoints.get(k));
				}
			}
			Collections.sort(pathMembers, Coords.comparator);
			for(Coords n : pathMembers){
				finalPath.add(n.getName());
			}
		}
		return finalPath;
	}

	public Vector<Integer> getPath(int i) {
		return allPaths.get(i).getPath();
	}

	public int getMstdist(int i) {
		return allPaths.get(i).getBranchDist();
	}

	public String printDegeneracy(int i) {
		return allPaths.get(i).printDegeneracy();
	}
	
}
