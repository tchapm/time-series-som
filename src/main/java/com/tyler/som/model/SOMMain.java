package com.tyler.som.model;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;

import com.tyler.kmeans.model.KMeansClustering;
import com.tyler.pq.model.InputDataCollection;
import com.tyler.util.PrintHelpers;
import com.tyler.util.SOMCalculator;
import com.tyler.util.TreeEvaluator;



/**
 *
 * @author Tyler Chapman
 * @version 1.0
 * 
 *  This is the main file that runs the SOM program. It is designed to take
 *  in a .txt file that has the number of data points and the dimensions of 
 *  analysis in the first line. It will calculate the location of the units 
 *  of the map as well as the connecting arcs of the MST that characterizes 
 *  the SOM. It will also output the projection of the data points onto the tree.
 *  These will be output in a .txt file that has the number of 
 *  components and the data points. This file can be used by any graphing 
 *  program to display the resulting map.
 * @see SOMCalculator
 * @see DataPoint
 * @see MinSpanTree
 * @see Centroid
 * @see Cluster
 */

public class SOMMain {
	
	public static void main (String args[]) throws IOException{
		
		String filePath = args[0];
		String inputFile = filePath;
		String outlines = filePath;
		String branchPath = filePath;
		String optimalPath = filePath;
		String branchProjection = filePath;
		int numCentroids = -1;
		boolean inputFromCommandLine = false;
		boolean writeClusters = false;
		boolean longPrint = false;
		boolean bestPath = true;
		boolean topTenPaths = false;
		boolean printOutput = false;
		//array to handle the type of input and output for the program
		boolean[] displayArr = {inputFromCommandLine, writeClusters, longPrint, bestPath, topTenPaths};
		
		if(displayArr[0]){
			String outFolder = "";

			System.out.print("Enter the path to the input data file: ");
			BufferedReader brIn = new BufferedReader(new InputStreamReader(System.in));
			//			read the input file from the command line
			try {
				inputFile = brIn.readLine();
			} catch (IOException ioe) {
				System.out.println("IO error reading file");
				System.exit(1);
			}
			System.out.print("Enter the folder for the output file: ");
			try {
				outFolder = brIn.readLine();
				if(!outFolder.endsWith("/")){
					outFolder+="/";
				}
				outlines = outFolder + "centroidPos.txt";
			} catch (IOException ioe) {
				System.out.println("IO error reading file");
				System.exit(1);
			}
			//			create output files needed to display results if it is 3D or less
			outFolder = "/Users/tchap/Documents/"; 
			branchPath = outFolder + "centroidLines.txt";
			optimalPath = outFolder + "centroidLinesOpt.txt";
			branchProjection = outFolder + "centroidLinesProj.txt";
			System.out.print("Enter the number of SOM units: ");
			try {
				numCentroids = Integer.parseInt(brIn.readLine());
			} catch (IOException ioe) {
				System.out.println("IO error reading file");
				System.exit(1);
			}
		}else {
			//what set are you running
			int analysisSet = 16; 
			//		1: yeast from Spellman et al.
			//		2: caulobacter from Lamb et al.
			//		3: artificial linear 2D
			//		4: artificial sine 2D
			//		5: artificial linear 3D
			//		6: artificial sine 3D
			//		7: artificial crossover 3D
			//		8: artificial crossover 2D
			//		9: artificial jellyroll 2D
			//		10: drosophila exposed to air
			//		11: drosophila exposed to ethonal
			//		12: crab shock data averaged
			//		13: crab shock control data averaged
			//		14: placenta values
			//		15: first white set
			//		16: second white set
			//		17: third white set
			//		18: fourth white set
			//		19: fifth white set
			
			switch(analysisSet) {
			case 1:
				inputFile += "SOMfiles/alphaValuesTrans.txt";
				printOutput = false;
				break;
			case 2:
				inputFile += "SOMfiles/cauloValues.txt";
				printOutput = false;
				break;
			case 3:
				inputFile += "SOMfiles/linear2dNoiseData.txt";
				outlines += "SOMfiles/linear2dPos.txt";
				branchPath += "SOMfiles/linear2dLines.txt";
				optimalPath += "SOMfiles/linear2dLinesOpt.txt";
				branchProjection += "SOMfiles/linear2dLinesProj.txt";
				break;
			case 4:
				inputFile += "SOMfiles/sinusoid2dPos.txt";
				branchPath += "SOMfiles/sinusoid2dLines.txt";
				optimalPath += "SOMfiles/sinusoid2dLinesOpt.txt";
				branchProjection += "SOMfiles/sinusoid2dLinesProj.txt";
				break;
			case 5:
				inputFile += "SOMfiles/linear3dNoiseData.txt";
				outlines += "SOMfiles/linear3dPos.txt";
				branchPath += "SOMfiles/linear3dLines.txt";
				optimalPath += "SOMfiles/linear3dLinesOpt.txt";
				branchProjection += "SOMfiles/lin3dLinesProj.txt";
				break;
			case 6:
				inputFile += "SOMfiles/sinusoid3dNoiseData.txt";
				outlines += "SOMfiles/sinusoid3dPos.txt";
				branchPath += "SOMfiles/sinusoid3dLines.txt";
				optimalPath += "SOMfiles/sinusoid3dLinesOpt.txt";
				branchProjection += "SOMfiles/sinusoid3dLinesProj.txt";
				break;
			case 7:
				inputFile += "SOMfiles/crossingTimeData3D.txt";
				outlines += "SOMfiles/crossingTimePos3D.txt";
				branchPath += "SOMfiles/crossingTime3DLines.txt";
				optimalPath += "SOMfiles/crossingTimeOpt3D.txt";
				branchProjection += "SOMfiles/crossingTimeProj3D.txt";
				break;
			case 8:
				inputFile += "SOMfiles/crossingTimeData2D.txt";
				outlines += "SOMfiles/crossingTimePos2D.txt";
				branchPath += "SOMfiles/crossingTime2DLines.txt";
				optimalPath += "SOMfiles/crossingTimeOpt2D.txt";
				branchProjection += "SOMfiles/crossingTimeProj2D.txt";
				break;
			case 9:
				inputFile += "SOMfiles/jellyroll.txt";
				outlines += "SOMfiles/jelly2dPos.txt";
				branchPath += "SOMfiles/jelly2dLines.txt";
				optimalPath += "SOMfiles/jelly2dLinesOpt.txt";
				branchProjection += "SOMfiles/jelly2dLinesProj.txt";
				break;
			case 10:
				inputFile += "SOMfiles/ethanolAirValues.txt";
				printOutput = false;
				break;
			case 11:
				inputFile += "SOMfiles/ethanolSubValues.txt";
				printOutput = false;
				break;
			case 12:
				inputFile += "SOMfiles/crabValues.txt";
				printOutput = false;
				break;
			case 13:				
				inputFile += "SOMfiles/crabControlValues.txt";
				printOutput = false;
				break;
			case 14:
				inputFile += "SOMfiles/PlacentalValues.txt";
				printOutput = false;;
				break;
			case 15:
				inputFile += "SOMfiles/firstWhiteSet.txt";
				printOutput = false;
				break;
			case 16:
				inputFile += "SOMfiles/secondWhiteSet.txt";
				printOutput = false;
				break;
			case 17:
				inputFile += "SOMfiles/thirdWhiteSet.txt";
				printOutput = false;
				break;
			case 18:
				inputFile += "SOMfiles/fourthWhiteSet.txt";
				printOutput = false;
				break;
			case 19:
				inputFile += "SOMfiles/fifthWhiteSet.txt";
				printOutput = false;
				break;
			}
			

		}

		//reader that takes in the values and sets the coordinants for the map calculation in SOM
		InputDataCollection dataColl = null;
		try {
			dataColl = new InputDataCollection(inputFile);
		}catch (IOException e) {
			System.out.println("Error: " + e.getMessage());
		}
		double lowestError = Double.MAX_VALUE;
		int bestNumCent = 0; 
		Vector<Integer> bestMapPath = null;
		ArrayList<Integer> bestPointPath = null;
		ArrayList<ArrayList<Centroid>> centArr = new ArrayList<ArrayList<Centroid>>();
		int numCycles = 10;
		Boolean isRandom = true; //to set initial map points randomly or assign data pts randomly
		for(int i=dataColl.getNumPts()-3; i<dataColl.getNumPts(); i++){
			SOMCalculator som = null;
			centArr = new ArrayList<ArrayList<Centroid>>();
			for(int j=0; j<numCycles; j++){
				numCentroids = i;
				if(printOutput){
					som = new SOMCalculator(numCentroids, dataColl.getDataPoints(), dataColl.getNumDim(),branchPath);
				}else{
					som = new SOMCalculator(numCentroids, dataColl.getDataPoints(), dataColl.getNumDim());
				}
				//threshold values for adding nodes
				som.startAnalysis(isRandom);
				som.getMST().getDistanceMatrix();
				som.getMST().getPredMatrix();
				//set and print the euclidian distance Matrix
				som.setEuclDistMat();
				
				if(displayArr[1]){
					PrintHelpers.writeClustOut(som, outlines, numCentroids);
				}
				if(displayArr[2]){
					PrintHelpers.printMatAndClust(som, dataColl);
				}
//				som.printEuclDistMat();
				centArr.add(som.getCentroids());
			}
//			som.getAndSetAveMap(centArr);
//			som.printEuclDistMat();
			TreeEvaluator tev = new TreeEvaluator(dataColl.getDataPoints(), som.getClusters(), som.getMST().getPredMatrix(), som.getMST().getDistanceMatrix());
			tev.evaluatePaths();
			if(tev.getLowestError()<lowestError){
				lowestError = tev.getLowestError();
				bestNumCent = numCentroids;
				bestMapPath = tev.getBestPath();
				bestPointPath = tev.finishPathAnal(0);
			}

			if(displayArr[3]){
				PrintHelpers.printBestPath(tev, branchProjection, optimalPath, numCentroids, printOutput);
			}
			if(displayArr[4]){
				PrintHelpers.printTopPaths(tev, branchProjection, outlines);
			}

		}

		System.out.println("\n\nLowest of all error value: " + lowestError);
		System.out.println("Number of centroids: " + bestNumCent);
		System.out.println("Map Space Order: " + bestMapPath.toString());
		System.out.println("Data Point Order: " + bestPointPath.toString());

	}






}