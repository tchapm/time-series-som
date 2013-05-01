import java.awt.geom.GeneralPath;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.sound.sampled.ReverbType;
import javax.xml.soap.Node;

import main.SOMMain;

import util.DataPoint;
import util.InputDataCollection;
import util.MinSpanTree;
import util.PathAndDistance;


public class MagweneMethod {
	public MinSpanTree mst;
	public Vector<DataPoint> dataPoints = new Vector<DataPoint>();


	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException {
		String filePath = args[0];
		String inputFile = filePath;
		String outlines = filePath;
		String optimalPath = filePath;
		String branchProjection = filePath;
		int analysisSet = 4;
		if(analysisSet==1){
			inputFile += "SOMfiles/alphaValuesTrans.txt";
			//			inputFile += "SOMfiles/alphaValuesSmall.txt";
		}else if(analysisSet==2){
			inputFile += "SOMfiles/firstWhiteSet.txt";
		}else if(analysisSet==3){
			inputFile += "SOMfiles/secondWhiteSet.txt";
		}else if(analysisSet==4){
			inputFile += "SOMfiles/thirdWhiteSet.txt";
		}else if(analysisSet==5){
			inputFile += "SOMfiles/fourthWhiteSet.txt";
		}else if(analysisSet==6){
			inputFile += "SOMfiles/fifthWhiteSet.txt";
		}
		outlines += "SOMfiles/MstBranchPos.txt";
		//		optimalPath += "SOMfiles/jelly2dLinesOpt.txt";
		//		branchProjection += "SOMfiles/jelly2dLinesProj.txt";
		int numDataPts = -1;
		MagweneMethod theMeth = new MagweneMethod();
		try {
			InputDataCollection inData = new InputDataCollection(inputFile);
			/*
			BufferedReader input = new BufferedReader(new FileReader(inputFile));
			String str = input.readLine();
			StringTokenizer st = new StringTokenizer(str);
			numDataPts = Integer.parseInt(st.nextToken());
			int dimensions = Integer.parseInt(st.nextToken());
			*/
			theMeth.dataPoints = inData.getDataPoints();
			
		}catch (IOException e) {
			System.out.println("Error: " + e.getMessage());
		}
		theMeth.mst = makeMST(theMeth.dataPoints);
		PathAndDistance diamPath = calcDiamPath(theMeth.mst);
		for(DataPoint dp : theMeth.dataPoints){
			dp.setIndecisive(theMeth.mst.getDistanceMatrix());
		}
		int[] stEnd = findBackbone(theMeth.dataPoints, diamPath.getPath());
		PQNode qTotal = theMeth.runThroughBackbone(theMeth.dataPoints, diamPath.getPath(), new PQNode(false), stEnd[0], stEnd[1]);
		Vector<Vector<PQNode>> totPerm = generatePerm(qTotal.getNodeChildren());
		int i = 0;
		while(i!=((Vector<DataPoint>) totPerm.clone()).size()){
			i=((Vector<DataPoint>) totPerm.clone()).size();
			getAllPaths(totPerm);
		}
		Vector<PathAndDistance> allPaths = generatePaths(totPerm);
		Collections.sort(allPaths, PathAndDistance.comparator);
		for(int j=1;j<20; j+=2){
			System.out.println((j+1)/2 + ". " + allPaths.get(j).getPath() + "  distance: " + allPaths.get(j).getDist());
		}
		System.out.println(diamPath.printPath());
		StringBuilder sb = new StringBuilder();
		System.out.print(qTotal.printNode(sb, 0).toString());
	}

	private static Vector<PathAndDistance> generatePaths(Vector<Vector<PQNode>> totPerm) {
		Vector<PathAndDistance> allPaths = new Vector<PathAndDistance>();
		for(Vector<PQNode>temNodePath : totPerm){
			Vector<Integer> tempPath = new Vector<Integer>();
			double pathDistance = 0.0;
			DataPoint prevPt = null;
			for(PQNode temNode : temNodePath){
				DataPoint currPt = temNode.getDataChildren().get(0);
				tempPath.add(currPt.getObjName());
				if(prevPt!=null){
					pathDistance+=currPt.getEuclDist(prevPt);
				}
				prevPt = currPt;
			}
			allPaths.add(new PathAndDistance(pathDistance, tempPath, 0));
		}
		return allPaths;
	}

	private static Vector<Vector<PQNode>> getReplacementOrder(Vector<PQNode> indOrder, PQNode tempNode, Vector<Vector<PQNode>> temPerm) {
		Vector<Vector<PQNode>> tempOrder = new Vector<Vector<PQNode>>();
		for(Vector<PQNode> reorder : temPerm){
			Vector<PQNode> newOrder = (Vector<PQNode>) indOrder.clone();
			int j = indOrder.indexOf(tempNode);
			newOrder.remove(j);
			for(PQNode node : reorder){
				newOrder.insertElementAt(node, j++);
			}
			tempOrder.add(newOrder);
		}
		return tempOrder;
	}

	private static void replaceTotalOrder(Vector<Vector<PQNode>> topPerm,
			Vector<PQNode> indOrder, Vector<Vector<PQNode>> nextPerm) {
		int j = topPerm.indexOf(indOrder);
		topPerm.remove(j);
		for(Vector<PQNode> vec : nextPerm){
			topPerm.insertElementAt(vec, j++);
		}

	}
	static Vector<Vector<DataPoint>> totPoints = new Vector<Vector<DataPoint>>();

	@SuppressWarnings("unchecked")
	private static void getAllPaths(Vector<Vector<PQNode>> topPerm) {
		Vector<PQNode> tempOrder = topPerm.get(0); 
		for(PQNode tempNode : tempOrder){
			Vector<PQNode> temVec = new Vector<PQNode>();
			if(!tempNode.isLeaf()){
				if(tempNode.isPNode()){
					if(tempNode.getDataChildren()!=null){
						Vector<DataPoint> temData = tempNode.getDataChildren();
						for(DataPoint dp : temData){
							temVec.add(new PQNode(true, true, dp));
						}
					}
					if(tempNode.getNodeChildren()!=null){
						temVec.addAll(tempNode.getNodeChildren());
					}
					Vector<Vector<PQNode>> temPerm = generatePerm(temVec);
					for(Vector<PQNode> indOrder : (Vector<Vector<PQNode>>)topPerm.clone()){
						Vector<Vector<PQNode>> nextPerm = getReplacementOrder(indOrder,tempNode,temPerm);
						replaceTotalOrder(topPerm,indOrder,nextPerm);
					}
				}else if(!tempNode.isPNode()){
					Vector<DataPoint> temData = tempNode.getDataChildren();
					for(DataPoint dp : temData){
						temVec.add(new PQNode(false, true, dp));
					}
					Vector<Vector<PQNode>> temPerm = generateQPerm(temVec);
					for(Vector<PQNode> indOrder : (Vector<Vector<PQNode>>)topPerm.clone()){
						Vector<Vector<PQNode>> nextPerm = getReplacementOrder(indOrder,tempNode,temPerm);
						replaceTotalOrder(topPerm,indOrder,nextPerm);
					}
				}
			}
		}
	}

	public static <T> Vector<Vector<T>> generatePerm(Vector<T> original) {
		if (original.size() == 0) { 
			Vector<Vector<T>> result = new Vector<Vector<T>>();
			result.add(new Vector<T>());
			return result;
		}
		T firstElement = original.remove(0);
		Vector<Vector<T>> returnValue = new Vector<Vector<T>>();
		Vector<Vector<T>> permutations = generatePerm(original);
		for (Vector<T> smallerPermutated : permutations) {
			for (int index=0; index <= smallerPermutated.size(); index++) {
				Vector<T> temp = new Vector<T>(smallerPermutated);
				temp.add(index, firstElement);
				returnValue.add(temp);
			}
		}
		return returnValue;
	}

	private static Vector<Vector<PQNode>> generateQPerm(Vector<PQNode> dataChildren) {
		Vector<Vector<PQNode>> tempPerm = new Vector<Vector<PQNode>>();
		Vector<PQNode> revChildren = new Vector<PQNode>(dataChildren.size());
		for(int i=0; i<dataChildren.size() ; i++){
			revChildren.add(dataChildren.get(dataChildren.size()-1-i));
		}
		tempPerm.add(dataChildren);tempPerm.add(revChildren);
		return tempPerm;
	}

	private PQNode runThroughBackbone(Vector<DataPoint> dataForPoints, Vector<Integer> diamPath, PQNode pqNode, int startPt, int endPt) throws IOException {
		int st = getLocInVec(startPt, diamPath, dataForPoints);
		int end = getLocInVec(endPt, diamPath, dataForPoints);
		if(st==end && dataForPoints.size()!=dataPoints.size()){
			PQNode tempNode = new PQNode(false);
			for(int diamNames : diamPath){
				tempNode.addDataChild(dataForPoints.get(diamNames));
			}
			pqNode.addNodeChild(tempNode);
			for(DataPoint dp : dataForPoints){
				dp.setInTree(true);
			}
			return pqNode;
		}
		for(int i=st; i<=end; i++){
			DataPoint tempPt = dataForPoints.get(diamPath.get(i));
			tempPt.setInTree(true);
			if(!tempPt.isIndecisive()){
				pqNode.addDataChild(tempPt);
			}else {
				PQNode tempNode = new PQNode(true);
				tempNode.addDataChild(tempPt);
				pqNode.addNodeChild(tempNode);
			}
		}
		for(PQNode newNode : pqNode.getNodeChildren()){
			DataPoint dp  = newNode.getDataChildren().get(0);
			int[][] distMat = mst.getDistanceMatrix();
			for(int i=0; i<distMat.length; i++){
				if(distMat[dp.getObjName()][i]==1 && !dataForPoints.get(i).isInTree()){
					Vector<DataPoint> tempDataPts = makeBranchedTree(mst.getPredMatrix(), dp.getObjName(), i);
					if(tempDataPts.size()==1){
						//						newNode.addDataChild(dataForPoints.get(i));
						PQNode tempNode = new PQNode(true);
						tempNode.addDataChild(dataPoints.get(i));
						dataPoints.get(i).setInTree(true);
						newNode.addNodeChild(tempNode);
					}else{
						MinSpanTree tempTree = makeMST(tempDataPts);
						PathAndDistance tempDiamPath = calcDiamPath(tempTree);
						for(DataPoint tempPt : tempDataPts){
							tempPt.setIndecisive(mst.getDistanceMatrix());
						}
						int[] stEnd = findBackbone(tempDataPts, tempDiamPath.getPath());
						runThroughBackbone(tempDataPts, tempDiamPath.getPath(), newNode, stEnd[0], stEnd[1]);
					}
				}
			}
		}
		return pqNode;

	}

	public static int getLocInVec(int pointName, Vector<Integer> diamPath, Vector<DataPoint> dataPoints){
		for(int i=0; i<diamPath.size(); i++){
			DataPoint dp = dataPoints.get(diamPath.get(i));
			if(dp.getObjName()==pointName){
				return i;
			}
		}
		return -1;
	}

	private static int[] findBackbone(Vector<DataPoint> dataPoints,Vector<Integer> path) {
		int start=-1, end=-1;
		int j = path.size()-1;
		int[] startAndEnd = new int[2];
		for(int i=0; i<path.size(); i++){
			DataPoint dpStart = dataPoints.get(path.get(i));
			DataPoint dpEnd = dataPoints.get(path.get(j-i));
			if(start<0 && dpStart.isIndecisive()){
				start = dpStart.getObjName();
			}
			if(end<0 && dpEnd.isIndecisive()){
				end = dpEnd.getObjName();
			}
			if(start>=0 && end>=0){
				startAndEnd[0] = start;
				startAndEnd[1] = end;
				return startAndEnd;
			}
		}
		return startAndEnd;
	}

	public static MinSpanTree makeMST(Vector<DataPoint> theDataPts) throws IOException{
		int totNodes = theDataPts.size();
		int[] inTree = new int[totNodes];
		double[] d = new double[totNodes];
		int[] whoTo = new int[totNodes]; 
		int[][] path = new int[2*(totNodes-1)][2];
		double[][] weight = new double[totNodes][totNodes];
		int[][] pathDistance = new int[totNodes][totNodes];

		String toVal = null;
		String minimum = null;
		int m = totNodes;

		for(int i=0;i<m;i++){
			for (int j=0; j<m; j++) {	
				weight[i][j] = theDataPts.get(i).getEuclDist(theDataPts.get(j));
				pathDistance[i][j] = 1000;
			}
		}
		for (int i = 0; i < m; ++i){
			d[i] = 100000;
		}
		for (int i = 0; i < m; ++i)
			inTree[i] = 0;

		inTree[0] = 1;
		FileWriter fstream = null;

		for (int i = 0; i < m; ++i){
			if ((weight[0][i] != 0) && (d[i] > weight[0][i])) {
				d[i] = weight[0][i];
				whoTo[i] = 0;
			}
		}
		String outlines = "/Users/tchap/Documents/SOMfiles/jelly2dLinesProj.txt";
		//		fstream = new FileWriter(outlines);
		//		BufferedWriter out = new BufferedWriter(fstream);
		//write number of paths to the output file
		//		out.write(m-1 + "\n");

		for (int treeSize = 1; treeSize < m; ++treeSize) {
			// first find the node with the smallest distance within the tree
			int min = -1;
			for (int i = 0; i < m; ++i){
				if (inTree[i]==0){
					if ((min == -1) || (d[min] > d[i])){
						min = i;
					}
				}
			}
			//Add the node and its associated arc that is located in the whoTo array
			toVal = Integer.toString(whoTo[min]);
			minimum = Integer.toString(min);
			System.out.println("Adding edge " + toVal + "-" + minimum);
			//			out.write(toVal + " " + minimum + "\n");
			path[2*(treeSize-1)][0] = whoTo[min];
			path[2*(treeSize-1)][1] = min;
			path[2*(treeSize-1)+1][0] = min;
			path[2*(treeSize-1)+1][1] = whoTo[min];
			pathDistance[whoTo[min]][min]=pathDistance[min][whoTo[min]]=1;
			inTree[min] = 1;
			for (int i = 0; i < m; ++i){
				if ((weight[min][i] != 0) && (d[i] > weight[min][i])) {
					d[i] = weight[min][i];
					whoTo[i] = min;
				}
			}
		}
		MinSpanTree mst = new MinSpanTree(path, pathDistance);
		//		out.close();	
		return mst;
	}

	public static PathAndDistance calcDiamPath(MinSpanTree mst){
		int[][] pathDistance = mst.getDistanceMatrix();
		int totNodes = pathDistance.length;
		int[][] predNodes = new int[totNodes][totNodes];

		for(int i=0;i<totNodes; i++){
			for(int j =0;j<totNodes; j++){
				if(i==j){
					pathDistance[i][j] = 0;
					predNodes[i][j] = -1;
				}else{
					predNodes[i][j] = i;
				}
			}
		}
		for(int k=0;k<totNodes; k++){
			for(int i =0;i<totNodes; i++){
				for(int j=0; j<totNodes; j++){
					if (pathDistance[i][j]>(pathDistance[i][k]+pathDistance[k][j])){
						pathDistance[i][j]=pathDistance[i][k]+pathDistance[k][j];
						predNodes[i][j] = predNodes[k][j]; 
					}
				}
			}
		}
		int longestPath = -1;
		int startPt = -1;
		int endPt = -1;
		for(int i=0; i<totNodes; i++){
			for(int j=0; j<totNodes; j++){
				if(pathDistance[i][j]>longestPath){
					longestPath = pathDistance[i][j];
					startPt = i;
					endPt = j;
				}
			}
		}

		System.out.println("LongestPath {" + startPt + "," + endPt + "} = " + longestPath);

		mst.setDistanceMatrix(pathDistance);
		mst.setPredMatrix(predNodes);
		Vector<Integer> diamPath = getDiamPath(startPt, endPt, mst);
		return new PathAndDistance(longestPath, pathDistance[startPt][endPt], diamPath);
	}


	private Vector<DataPoint> makeBranchedTree(int[][] predMatrix, int indecPt, int frstBranch) {
		Vector<DataPoint> tempTreePoints = new Vector<DataPoint>();
		for(int i=0; i<predMatrix.length; i++){
			if(isOnNewTree(predMatrix, i, indecPt, frstBranch)){
				tempTreePoints.add(dataPoints.get(i));
			}
		}
		return tempTreePoints;
	}

	private static boolean isOnNewTree(int[][] predMatrix, int testPoint, int indecPoint, int firstBranch) {
		if(testPoint==indecPoint){
			return false;
		}
		int nextNode = -1;
		nextNode = predMatrix[firstBranch][testPoint];
		while(nextNode!=firstBranch && nextNode!=-1){
			if(nextNode==indecPoint){
				return false;
			}
			nextNode = predMatrix[firstBranch][nextNode];
		}
		return true;
	}

	private static Vector<Integer> getDiamPath(int i, int j, MinSpanTree mst) {
		int nextNode = -1;
		Vector<Integer> indivPath = new Vector<Integer>();
		indivPath.add(j);
		nextNode = mst.getPredMatrixValue(i, j);
		indivPath.add(nextNode);
		while(nextNode!=i){
			nextNode = mst.getPredMatrixValue(i, nextNode);
			indivPath.add(nextNode);
		}
		return indivPath;
	}
}
