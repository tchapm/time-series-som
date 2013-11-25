package com.som.pq.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import com.som.mdscale.MDS;
import com.som.model.DataPoint;

public class InputDataCollection {
	private double[][] distMat;
	private double[][] dataDistMat;
	private ArrayList<DataPoint> dataPoints;
	private int numPts;
	private int numDimensions;
	
	public InputDataCollection (String inputFile) throws IOException {
		BufferedReader input = new BufferedReader(new FileReader(inputFile));
		dataPoints = setCoord(input);
		input.close();
		distMat = setDistMat(dataPoints);
		dataDistMat = setDataSpaceItems();
	}

	public double[][] getDistMat() {
		return distMat;
	}

	public double[][] setDistMat(ArrayList<DataPoint> dPts) {
		double[][] tempMat = new double[dPts.size()][dPts.size()];
		for(int i=0;i<dPts.size();i++){
			DataPoint pointA = dPts.get(i);
			for(int j=0;j<dPts.size();j++){
				DataPoint pointB = dPts.get(j);
				tempMat[i][j] = pointA.getEuclDist(pointB);
			}
		}
		return tempMat;
	}
	
	public double[][] setDataSpaceItems() {
		ArrayList<DataPoint> dataSpace = new ArrayList<DataPoint>();
		ArrayList<Double> position;
		for(int i=0;i<numDimensions;i++){
			position = new ArrayList<Double>();
			for(DataPoint dp : dataPoints){
				position.add(dp.getPosition().get(i));
			}
			dataSpace.add(new DataPoint(position, i));
		}
		return setDistMat(dataSpace);
	}
	
	public void setDataToScale(int dim) {
		if(dim<=0){
			return;
		}
		double[][] scalingMatrix = MDS.classicalScaling(distMat, dim);
		ArrayList<DataPoint> tempDataArr = new ArrayList<DataPoint>();
		ArrayList<Double> position;
		int name = 0;
		for(int i=0; i<dataPoints.size(); i++){
			position = new ArrayList<Double>();
			for(int j=0; j<dim; j++){
				position.add(scalingMatrix[j][i]);
			}
			tempDataArr.add(new DataPoint(position,name));
			name++;
		}
		dataPoints = tempDataArr;
		numDimensions = dim;
		
	}


	public ArrayList<DataPoint> getDataPoints() {
		return dataPoints;
	}

	public void setDataPoints(ArrayList<DataPoint> datPoints) {
		this.dataPoints = datPoints;
	}

	public int getNumPts() {
		return numPts;
	}

	public void setNumPts(int numPts) {
		this.numPts = numPts;
	}

	public int getNumDim() {
		return numDimensions;
	}

	public void setNumDim(int numDim) {
		this.numDimensions = numDim;
	}

	public void printDataDistMat(){
		for(int i=0;i<distMat.length;i++){
			System.out.print("| ");
			for(int j=0;j<distMat.length;j++){
				System.out.printf("%.2f ", distMat[i][j] );
			}
			System.out.println("|");
		}
	}

	/*
	 * Method to set the coordinants found in the input file into dataPoint objects. 
	 * j represents the name of the data point and can be used to retrieve the point.
	 */
	
	public ArrayList<DataPoint> setCoord(BufferedReader input) throws NumberFormatException, IOException {
		ArrayList<DataPoint> dataPts = new ArrayList<DataPoint>();
		String str = null;
		StringTokenizer st;
		int name=0;
		ArrayList<Double> position;
		while((str = input.readLine())!=null){
			position = new ArrayList<Double>();
			st = new StringTokenizer(str);
			while(st.hasMoreTokens()){
				position.add(Double.parseDouble(st.nextToken()));
			}
			dataPts.add(new DataPoint(position,name));
			name++;
		}
		numPts = name; 
		numDimensions = dataPts.get(0).getPosition().size();
		return dataPts;
	}

}
