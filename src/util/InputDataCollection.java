package util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;

public class InputDataCollection {
	private double[][] distMat;
	private Vector<DataPoint> dataPoints;
	private int numPts;
	private int numDimensions;

	public InputDataCollection (String inputFile) throws IOException {
		BufferedReader input = new BufferedReader(new FileReader(inputFile));
		String str = input.readLine();
		dataPoints = setCoord(input);
		input.close();
		distMat = setDistMat();
	}

	public double[][] getDistMat() {
		return distMat;
	}

	public double[][] setDistMat() {
		double[][] tempMat = new double[dataPoints.size()][dataPoints.size()];
		for(int i=0;i<dataPoints.size();i++){
			DataPoint pointA = dataPoints.get(i);
			for(int j=0;j<dataPoints.size();j++){
				DataPoint pointB = dataPoints.get(j);
				tempMat[i][j] = pointA.getEuclDist(pointB);
			}
		}
		return tempMat;
	}

	public Vector<DataPoint> getDataPoints() {
		return dataPoints;
	}

	public void setDataPoints(Vector<DataPoint> datPoints) {
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
	public Vector<DataPoint> setCoord(BufferedReader input) throws NumberFormatException, IOException {
		Vector<DataPoint> dataPts = new Vector<DataPoint>();
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
