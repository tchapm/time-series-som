package com.tyler.init;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import com.tyler.som.model.Coords;

/**
 *
 * @author Tyler Chapman
 * @version 1.0
 * 
 *  This is a helper class to refactor the raw data from the caulobacter and drosophila data sets.
 *  Should be used as a reference for how the data needs to be formatted for the program to run smoothly.
 */

public class InputDataParser {
	public ArrayList<ArrayList<Double>> data;
	public int numElements = -1;

	public static void main(String[] args) throws IOException {
		String inFile = "/Users/tchap/Documents/SOMfiles/CellCycleDownload.txt";
		String dataTok = "ORF";
		int dataSize = 13;
		InputDataParser parser = new InputDataParser();
		//		setInputVal(inFile, dataTok, dataSize);

		String outFile = "/Users/tchap/Documents/SOMfiles/cauloValues.txt";
		int outMax = 11;
		int outMin = 0;
		//		outputToFile(outFile, outMin, outMax);
//		System.out.println("Finished writing to " + outFile);


/*		parser.data = new ArrayList<ArrayList<Double>>();
		inFile = "/Users/tchap/Documents/SOMfiles/rawDataSets/firstWhiteInput.txt";
		parser.setInputValWellFormed(inFile);
		outFile = "/Users/tchap/Documents/SOMfiles/firstWhiteSet.txt";
		outMin = 0;
		outMax = parser.numElements;
		parser.trimByStdDev(outMin, outMax, 0.0);//.3
		parser.outputToFile(outFile, outMin, outMax);
		
		parser.data = new ArrayList<ArrayList<Double>>();
		inFile = "/Users/tchap/Documents/SOMfiles/rawDataSets/secondWhiteInput.txt";
		parser.setInputValWellFormed(inFile);
		outFile = "/Users/tchap/Documents/SOMfiles/secondWhiteSet.txt";
		outMin = 0;
		outMax = parser.numElements;
		parser.trimByStdDev(outMin, outMax, 0.0);//.35
		parser.outputToFile(outFile, outMin, outMax);
		
		parser.data = new ArrayList<ArrayList<Double>>();
		inFile = "/Users/tchap/Documents/SOMfiles/rawDataSets/thirdWhiteInput.txt";
		parser.setInputValWellFormed(inFile);
		outFile = "/Users/tchap/Documents/SOMfiles/thirdWhiteSet.txt";
		outMin = 0;
		outMax = parser.numElements;
		parser.trimByStdDev(outMin, outMax, 0.3);//.27
		parser.outputToFile(outFile, outMin, outMax);
		
		parser.data = new ArrayList<ArrayList<Double>>();
		inFile = "/Users/tchap/Documents/SOMfiles/rawDataSets/fourthWhiteInput.txt";
		parser.setInputValWellFormed(inFile);
		outFile = "/Users/tchap/Documents/SOMfiles/fourthWhiteSet.txt";
		outMin = 0;
		outMax = parser.numElements;
		parser.trimByStdDev(outMin, outMax, 0.0);//.41
		parser.outputToFile(outFile, outMin, outMax);
		
		parser.data = new ArrayList<ArrayList<Double>>();
		inFile = "/Users/tchap/Documents/SOMfiles/rawDataSets/fifthWhiteInput.txt";
		parser.setInputValWellFormed(inFile);
		outFile = "/Users/tchap/Documents/SOMfiles/fifthWhiteSet.txt";
		outMin = 0;
		outMax = parser.numElements;
		parser.trimByStdDev(outMin, outMax, 0.0);//.45
		parser.outputToFile(outFile, outMin, outMax);
		*/
		parser.data = new ArrayList<ArrayList<Double>>();
		inFile = "/Users/tchap/Documents/SOMfiles/rawDataSets/all5DataSets.txt";
//		inFile = "/Users/tchap/Documents/SOMfiles/rawDataSets/all5.txt";
//		inFile = "/Users/tchap/Documents/SOMfiles/rawDataSets/only3-4.txt";
//		inFile = "/Users/tchap/Documents/SOMfiles/rawDataSets/only3-5.txt";
		dataTok = "IMAGE";
		dataSize = 123; //eliminates about 2k of the 13k data
		parser.setInputVal(inFile, dataTok, dataSize);
		int numResulting = 76; 
		parser.shaveCorrections(numResulting);

		outFile = "/Users/tchap/Documents/SOMfiles/firstWhiteSet.txt";
		outMin = 68;//1
		outMax = 75;//7
		int approxNum = 3000;
		String oneSetOutputfile = "/Users/tchap/Documents/SOMfiles/rawDataSets/fifthWhiteInput3.txt";
		parser.outputOnlyOneSet(outMin,outMax, oneSetOutputfile);
		parser.outputToFile(outFile, outMin, outMax);
		System.out.println("Finished writing to " + outFile);
		outFile = "/Users/tchap/Documents/SOMfiles/secondWhiteSet.txt";
		outMin = 12;
		outMax = 26; //26
		parser.trimByStdDev(outMin,outMax,approxNum);
		parser.outputToFile(outFile, outMin, outMax);
		System.out.println("Finished writing to " + outFile);
		outFile = "/Users/tchap/Documents/SOMfiles/thirdWhiteSet.txt";
		outMin = 39; //39
		outMax = 55; //55
		parser.trimByStdDev(outMin,outMax,approxNum);
		parser.outputToFile(outFile, outMin, outMax);
		System.out.println("Finished writing to " + outFile);
		outFile = "/Users/tchap/Documents/SOMfiles/fourthWhiteSet.txt";
		outMin = 86;
		outMax = 96;
		parser.outputToFile(outFile, outMin, outMax);
		System.out.println("Finished writing to " + outFile);
		outFile = "/Users/tchap/Documents/SOMfiles/fifthWhiteSet.txt";
		outMin = 106;
		outMax = 113;
		parser.outputToFile(outFile, outMin, outMax);
		System.out.println("Finished writing to " + outFile);

/*	parser.data = new ArrayList<ArrayList<Double>>();
		inFile = "/Users/tchap/Documents/SOMfiles/datasets/HeatShockCrabControl.txt";
		//		inFile = "/Users/tchap/Documents/SOMfiles/datasets/crabSmall.txt";
		dataTok = "CAYC";
		dataSize = 82;
		parser.setInputVal(inFile, dataTok, dataSize);

		outFile = "/Users/tchap/Documents/SOMfiles/crabControlValues.txt";
		int[] sizeList = {10,8,10,6,8,10,10,10,10};
		parser.outputToFileCrabData(sizeList);
		outMax =numElements;
		outMin = 0;
//		parser.outputToFile(outFile, outMin, numElements);
		System.out.println("Finished writing to " + outFile);

		parser.data = new ArrayList<ArrayList<Double>>();
		inFile = "/Users/tchap/Documents/SOMfiles/datasets/HeatShockCrabStress.txt";
		dataTok = "CAYC";
		dataSize = 82;
		parser.setInputVal(inFile, dataTok, dataSize);

		outFile = "/Users/tchap/Documents/SOMfiles/crabValues.txt";

		parser.outputToFileCrabData(sizeList);
		outMax =numElements;
		outMin = 0;
//		parser.outputToFile(outFile, outMin, outMax);
		System.out.println("Finished writing to " + outFile);

		parser.data = new ArrayList<ArrayList<Double>>();
		inFile = "/Users/tchap/Documents/SOMfiles/datasets/PlacentalData.txt";
		dataTok = "_at";
		dataSize = 36;
		parser.setInputVal(inFile, dataTok, dataSize);

		outFile = "/Users/tchap/Documents/SOMfiles/PlacentalValues.txt";
		int[] placentaList = {2,4,6,3,6,1,3,2,2,1,5,1};
//		parser.outputToFileCrabData(placentaList);
		outMax =numElements;
		outMin = 0;
//		parser.outputToFile(outFile, outMin, outMax);
		System.out.println("Finished writing to " + outFile);

		parser.data = new ArrayList<ArrayList<Double>>();
		inFile = "/Users/tchap/Documents/SOMfiles/ethanolExposure.txt";
		dataTok = "_at";
		dataSize = 16;
		parser.setInputVal(inFile, dataTok, dataSize);

		outFile = "/Users/tchap/Documents/SOMfiles/ethanolAirValues.txt";
		outMax =7;
		outMin = 0;
		//		outputToFile(outFile, outMin, outMax);
		System.out.println("Finished writing to " + outFile);

		outFile = "/Users/tchap/Documents/SOMfiles/ethanolEValues.txt";
		outMax =14;
		outMin = 7;
		//		outputToFile(outFile, outMin, outMax);
		System.out.println("Finished writing to " + outFile);


		outFile = "/Users/tchap/Documents/SOMfiles/ethanolSubValues.txt";
		outMax =14;
		outMin = 7;
		//		outputToFileSub(outFile, outMin, outMax);
		System.out.println("Finished writing to " + outFile);
		*/
	}



	private void trimByStdDev(int outMin, int outMax, double stdDevCutoff) {
		int numPts = (outMax-outMin);
		ArrayList<ArrayList<Double>> allLines = new ArrayList<ArrayList<Double>>();
		for (int i = 0; i < data.size(); i++) {
			ArrayList<Double> vec = data.get(i);
			
			double mean = 0.0, stdDev = 0.0;
			for (int j = outMin; j < outMax; j++) {
				mean+=vec.get(j);
			}
			mean/=numPts;
			for (int j = outMin; j < outMax; j++) {
				stdDev+=(vec.get(j)-mean)*(vec.get(j)-mean);
			}
			stdDev = Math.sqrt(stdDev/numPts);
			if(stdDev>=stdDevCutoff){
				GeneExpression aGene = new GeneExpression(i, vec, mean, stdDev);
				allLines.add(vec);
			}
		}	
		data = new ArrayList<ArrayList<Double>>();
		data = allLines;
//		Collections.sort(pathMembers, Coords.comparator);
		return;
	}

	private void shaveCorrections(int numResulting) {
		for(ArrayList<Double> line : this.data){
			while(line.size()>numResulting){
				line.remove(0);
			}
		}
		return;
	}

	private void outputToFileSub(String outFile, int outMin, int outMax) throws IOException {
		double[][] matrixTran = new double[numElements][data.size()];
		double[][] matrixTranAir = new double[numElements][data.size()];
		double[][] matrixTranEth = new double[numElements][data.size()];
		for (int i = 0; i < data.size(); i++) {
			List<Double> vec = data.get(i);
			for (int j = 0; j < vec.size()-2; j++) {
				matrixTran[j][i] = vec.get(j);
			}
		}
		for (int j = outMin; j < outMax; j++) {
			for(int i=0;i<data.size();i++){
				double test1 = Math.abs(matrixTran[j][i] - matrixTran[j-7][i]);
				double test = Math.log10(Math.abs(matrixTran[j][i] - matrixTran[j-7][i]));
				matrixTran[j][i] = Math.abs(matrixTran[j][i] - matrixTran[j-7][i]);
			}
		}
		double mean=0.0;
		for (int j = outMin; j < outMax; j++) {
			for(int i=0;i<data.size();i++){
				mean+=matrixTran[j][i];
			}
			mean = mean/(data.size()*(outMax-outMin));
			for(int i=0;i<data.size();i++){
				matrixTran[j][i] = matrixTran[j][i]-mean;
			}
			mean = 0.0;
		}
		FileWriter fstream = new FileWriter(outFile);
		BufferedWriter out = new BufferedWriter(fstream);
		out.write((outMax-outMin) + " " + data.size() + "\n");
		for (int i = outMin; i < outMax; i++) {
			for (int j = 0; j < data.size(); j++) {
				out.write(Double.toString(matrixTran[i][j]) + " ");
			}
			out.write("\n");
		}
		out.close();
	}
	private void setInputValWellFormed(String inFile) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(inFile));
		while (br.ready()) {
			String dataLine = br.readLine();
			StringTokenizer line = new StringTokenizer(dataLine);
			ArrayList<Double> vec = new ArrayList<Double>();
			while (line.hasMoreTokens()) {
				try{
					vec.add(Double.parseDouble(line.nextToken()));
				}catch(Exception e){

				}
			}
			data.add(vec);
			numElements = vec.size();
		}
 		return;
	}
	private void setInputVal(String inFile, String dataTok, int dataSize) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(inFile));
		String secondDataTok = "1";
		HashMap<Integer,Integer> sizes = new HashMap<Integer, Integer>();
//		String outFile = "/Users/tchap/Documents/SOMfiles/rawDataSets/only3-5.txt";
//		FileWriter fstream = new FileWriter(outFile);
//		BufferedWriter out = new BufferedWriter(fstream);
		while (br.ready()) {
			String dataLine = br.readLine();
			StringTokenizer line = new StringTokenizer(dataLine);
			ArrayList<Double> vec = new ArrayList<Double>();
			if(line.nextToken().contains(dataTok)){
				String next = line.nextToken();
				while(!next.equals(secondDataTok)){
					if(line.hasMoreElements()){
						next = line.nextToken();
					}else{
						break;
					}
				}
				while (line.hasMoreTokens()) {
					try{
						vec.add(Double.parseDouble(line.nextToken()));
					}catch(Exception e){

					}
				}
				
//				dataSize = 123;
//				if(vec.size()==dataSize){
					data.add(vec);
					numElements = vec.size();
//					dataElements.add(vec);
//					String theLine = br.readLine();
//					out.write(theLine + "\n");

//				}
				if(sizes.containsKey(vec.size())){
					sizes.put(vec.size(), sizes.get(vec.size())+1);
				}else{
					sizes.put(vec.size(), 1);
					System.out.print(numElements + ":   " +dataLine + "\n\n");
				}
//				totLines++;
//				if(sizes.containsKey(vec.size())){
//					int count = sizes.get(vec.size());
//					sizes.put(vec.size(), count+1);
//				}else{
//					sizes.put(vec.size(), 1);
//				}
//				if(vec.size()==85){
//					dataElements.add(vec);
//					out.write(dataLine + "\n");
//				}
//				}else{
//					System.out.print(line);
//				}
			

			}
		}
//		Collections.sort(sizes.values());
//		System.out.print(sizes);
//		out.close();
		return;
	}

	private void outputToFileCrabData(int[] sizeList) throws IOException {
		ArrayList<ArrayList<Double>> averagedData = new ArrayList<ArrayList<Double>>();
		int j = 1;
		ArrayList<Double> tempVec = new ArrayList<Double>(); 
		for (int i = 0; i < data.size(); i++) {
			List<Double> vec = data.get(i);
			tempVec = new ArrayList<Double>(); 
			int sizeGroup = 0;
			int startIndex = 0;
			for(int size : sizeList){
				//				System.out.println("Segment number: " + j++);
				startIndex += sizeGroup;
				sizeGroup = size;
				//to get the ave of the replicants
				tempVec.add(calcAve(vec,sizeGroup,startIndex));
				//to get just 1a of all values
				//				tempVec.add(vec.get(startIndex));
			}
			j=1;
			averagedData.add(tempVec);
		}
		numElements = tempVec.size();
		data = averagedData;
	}
	private Double calcAve(List<Double> vec, int sizeGroup,
			int startIndex) {
		double tempAve = 0.0;
		double stdDev = 0.0; 
		for (int j = startIndex; j < sizeGroup+startIndex; j++) {
			tempAve += vec.get(j);
		}
		for (int j=startIndex; j<sizeGroup+startIndex; j++) {
			//			System.out.println("Individual Distance: " + (tempAve/sizeGroup-vec.get(j)));
			stdDev += Math.pow(tempAve/sizeGroup-vec.get(j),2.0);
		}
		// 		System.out.println("Std Deviation " + Math.sqrt(stdDev/sizeGroup));
		return tempAve/sizeGroup;
	}
	private void outputOnlyOneSet(int outMin, int outMax, String oneSetOutputfile) throws IOException {
		FileWriter fstream = new FileWriter(oneSetOutputfile);
		BufferedWriter out = new BufferedWriter(fstream);
		
		for (int i = 0; i < data.size(); i++) {
			List<Double> vec = data.get(i);
			String experimentLine = "";
			for (int j = outMin; j <= outMax; j++) {
				experimentLine+= vec.get(j) + " ";
			}
			out.write(experimentLine+"\n");
			
		}		
		out.close();
		return;
	}
	private void outputToFile(String outFile, int outMin, int outMax) throws IOException {
		double[][] matrixTran = new double[numElements][data.size()];
		ArrayList<ArrayList<Double>> data2 = data;
		
		for (int i = 0; i < data.size(); i++) {
			List<Double> vec = data.get(i);
			for (int j = outMin; j < outMax; j++) {
				matrixTran[j][i] = vec.get(j);
				double test = matrixTran[j][i];
				if(test==Double.NaN){
					System.out.println("test: " + test);
				}
			}
		}
		boolean zeroCentered = false;
		if(zeroCentered){
			for (int i = outMin; i < outMax; i++) {
				double smallest = 0.0; 
				for(int j=0;j<data.size();j++){
					if(matrixTran[i][j]<smallest){
						smallest = matrixTran[i][j];
					}
				}
				for(int j=0;j<data.size();j++){
					matrixTran[i][j] = matrixTran[i][j]-smallest;
					double checkin = matrixTran[i][j];
					if(matrixTran[i][j]<=0.0){
						matrixTran[i][j] = Double.MIN_VALUE;
						System.out.println("matrixTran[" + i + "][" + j + "]" + " = " +  checkin);
					}
				}
			}
		}
		boolean takingMean = false;
		if(takingMean){
			double mean=0.0;
			for (int i = outMin; i < outMax; i++) {
				for(int j=0;j<data.size();j++){
					mean+=matrixTran[i][j];
				}
				mean = mean/(data.size());
				for(int j=0;j<data.size();j++){
					matrixTran[i][j] = matrixTran[i][j]-mean;
					double checkin = matrixTran[i][j];
					if(matrixTran[i][j]<=0.0){
						System.out.println("matrixTran[" + i + "][" + j + "]" + " = " +  checkin);
					}
				}

				mean = 0.0;
			}
		}

		
		boolean takingLog = false;
		if(takingLog){
			double logTwo = Math.log(2);
			for (int i = outMin; i < outMax; i++) {
				for (int j = 0; j < data.size(); j++) {
					
					double checkIn = matrixTran[i][j];
					if(matrixTran[i][j]<=0.0){
						System.out.println("matrixTran[" + i + "][" + j + "]" + " = " +  checkIn);
					}
					matrixTran[i][j] = Math.log(matrixTran[i][j])/logTwo;
				
					double test = matrixTran[i][j];
					if(test==Double.NaN){
						System.out.println("test: " + test);
					}
				}
			}
		}

		FileWriter fstream = new FileWriter(outFile);
		BufferedWriter out = new BufferedWriter(fstream);
		out.write((outMax-outMin) + " " + data.size() + "\n");
		for (int i = outMin; i < outMax; i++) {
			for (int j = 0; j < data.size(); j++) {
				if(matrixTran[i][j]<0.1){
					//					System.out.print("test");
				}
				out.write(Double.toString(matrixTran[i][j]) + " ");
			}
			out.write("\n");
		}
		out.close();
	}
}
