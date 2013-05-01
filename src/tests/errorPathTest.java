package tests;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.StringTokenizer;
import java.util.Vector;

import org.junit.Test;

import util.DataPoint;
import util.PathAndDistance;
import util.SOMCalculator;
import util.TreeEvaluator;

import main.SOMMain;


public class errorPathTest {
	@Test
	public void testGetBQFields() throws IOException {
		int end = 7, numDataPts, numCentroids = 14;
		String inputFile = "/Users/tchap/Documents/SOMfiles/alphaValues.txt";
		String branchPath = "/Users/tchap/Documents/SOMfiles/testLines.txt";

		BufferedReader input = new BufferedReader(new FileReader(inputFile));
		String str = input.readLine();
		StringTokenizer st = new StringTokenizer(str);
		numDataPts = Integer.parseInt(st.nextToken());
		int dimensions = Integer.parseInt(st.nextToken());

		Vector<DataPoint> dataPoints = new Vector<DataPoint>();
//		PrgMain.setCoord(input, dataPoints, numDataPts, dimensions);
		input.close();
		SOMCalculator som = new SOMCalculator(numCentroids,dataPoints,dimensions,branchPath);
//		som.startAnalysis();

		TreeEvaluator testTree = new TreeEvaluator(dataPoints, som.getClusters(), 
				som.getMST().getPredMatrix(), som.getMST().getDistanceMatrix());
		testTree.evaluatePaths();
		Vector<Integer> bestPath = testTree.getBestPath();
		for(int i=0; i<bestPath.size(); i++){
			System.out.println(bestPath.get(i).toString());
		}
		
//		PriorityQueue<PathAndDistance> thePaths = testTree.getAllPaths();
		PathAndDistance tempPath;
		Vector[] v = som.getClusterOutput();

//		while(!thePaths.isEmpty()){
//			tempPath = thePaths.remove();

//			for(int i=0; i<tempPath.path.size(); i++){
//				Vector tempV = v[tempPath.path.get(i)];
//				Iterator iter = tempV.iterator();
//				while(iter.hasNext()){
//					iter.next()
//					System.out.println(iter.next().toString());
//				}
//			}
//			System.out.println(tempPath.printPath());
//		}
		
	}
}
