package com.tyler.som.model;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.tyler.som.model.Coords;



import Jama.EigenvalueDecomposition;
import Jama.Matrix;

public class DistanceCheck {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {

		double x1,x2,x0,y1,y2,y0;
		x1=0;y1=10;
		x2=10;y2=5;
		String posString = "/Users/tchap/Documents/SOMfiles/testerPos.txt";
		FileWriter fstream3 = new FileWriter(posString);
		BufferedWriter out3 = new BufferedWriter(fstream3);
		out3.write(2 + "\n");
		out3.write(x1 + " " + y1 + "\n");
		out3.write(x2 + " " + y2 + "\n");
		out3.close();
		String cenString = "/Users/tchap/Documents/SOMfiles/dataTesterLinesOpt.txt";
		FileWriter fstream4 = new FileWriter(cenString);
		BufferedWriter out4 = new BufferedWriter(fstream4);
		out4.write(1 + "\n");
		out4.write(0 + " " + 1 + "\n");
//		out4.write(x2 + " " + y2 + "\n");
		out4.close();
//		x0=2;y0=2;
//		x0=4;y0=1;
//		x0=0;y0=1;
		
		double t;
		double b_aSq = Math.pow((x1-x2),2) + Math.pow(y1-y2, 2);

		double xMin,yMin;
		double dSeg,dMin;

		double d1,d2;
		ArrayList<Double> posA = new ArrayList<Double>();
		posA.add(x1);posA.add(y1);
		ArrayList<Double> posB = new ArrayList<Double>();;
		posB.add(x2);posB.add(y2);
		String branchString = "/Users/tchap/Documents/SOMfiles/branchTester2.txt";
		FileWriter fstream = new FileWriter(branchString);
		BufferedWriter out = new BufferedWriter(fstream);
		String dataPoints = "/Users/tchap/Documents/SOMfiles/dataTester.txt";
		FileWriter fstream2 = new FileWriter(dataPoints);
		BufferedWriter out2 = new BufferedWriter(fstream2);
		out.write((11*11) + "\n");
		out2.write((11*11) + "\n");
		for(int i=0;i<=10;i++){
			for(int j=0;j<=10;j++){
				x0=i;y0=j;
				
				System.out.println("test1");
				out2.write(x0 + " " + y0 + "\n");
//				writeProj(posA,posB, x0, y0, out);
				writeProj2(x1, x2, y1, y2, x0, y0, out);
//				System.out.println("X0: " + x0 + " Y0:" + y0);
//				ArrayList<Double> posC = new ArrayList<Double>();
//				posC.add(x0);posC.add(y0);
//				Coords projection = getCoordOnBranch(posA,posB,posC);
//				System.out.println("XMin2 = " + projection.getLocation().get(0) + " YMin2= " + projection.getLocation().get(1));
//				out.write(projection.getLocation().get(0) + " " + projection.getLocation().get(1) + "\n");
			}
		}
		out.close();
		out2.close();
	
	}
	
	private static void writeProj2(double x1, double x2, double y1, double y2,
			double x0, double y0, BufferedWriter out) throws IOException {
		ArrayList<Double> posA = new ArrayList<Double>();
		posA.add(x1);posA.add(y1);
		ArrayList<Double> posB = new ArrayList<Double>();;
		posB.add(x2);posB.add(y2);
		ArrayList<Double> posC = new ArrayList<Double>();
		posC.add(x0);posC.add(y0);
		Coords projection = getCoordOnBranch2(x1, y1, x2, y2, x0, y0);
		System.out.println("2. XMin1 = " + projection.getLocation().get(0) + " YMin1= " + projection.getLocation().get(1));
		out.write(projection.getLocation().get(0) + " " + projection.getLocation().get(1) + "\n");		
	}

	private static void writeProj(ArrayList<Double> posA, ArrayList<Double> posB, double x0, double y0, BufferedWriter out) throws IOException {
		ArrayList<Double> posC = new ArrayList<Double>();
		posC.add(x0);posC.add(y0);
		Coords projection = getCoordOnBranch(posA,posB,posC);
		System.out.println("1. XMin1 = " + projection.getLocation().get(0) + " YMin1= " + projection.getLocation().get(1));
		out.write(projection.getLocation().get(0) + " " + projection.getLocation().get(1) + "\n");
	}

	private static Coords getCoordOnBranch(ArrayList<Double> posA, ArrayList<Double> posB, ArrayList<Double> posC) {
		double b_aSq = 0.0;
		double ac_ba = 0.0;
		double ca_ba = 0.0;
		double t = 0.0;
		double dist1=0.0, dist2=0.0;
		double distanceLine =0.0;
		ArrayList<Double> posP = new ArrayList<Double>();
		Coords tempCoord = new Coords();
		for(int i=0;i<posA.size();i++){
			b_aSq+=(posB.get(i)-posA.get(i))*(posB.get(i)-posA.get(i));
			ac_ba+=(posC.get(i)-posA.get(i))*(posB.get(i)-posA.get(i));
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
			System.out.println(posA.get(0) + " " + posA.get(1));
			tempCoord.setLocation(posA);
		}else if(b_aSq<=ca_ba){
			System.out.println(posB.get(0) + " " + posB.get(1));
			tempCoord.setLocation(posB);
		}else {
			System.out.println(posP.get(0) + " " + posP.get(1));
			tempCoord.setLocation(posP);
		}
		return tempCoord;
	}

	
	private static Coords getCoordOnBranch2(double ax, double ay, double bx,
			double by, double cx, double cy) {
		double r_numerator = (cx-ax)*(bx-ax) + (cy-ay)*(by-ay);
		double r_denomenator = (bx-ax)*(bx-ax) + (by-ay)*(by-ay);
		double r = r_numerator / r_denomenator;
		double distanceLine;
		//
		double px = ax + r*(bx-ax);
		double py = ay + r*(by-ay);
		//    
		//double s =  ((ay-cy)*(bx-ax)-(ax-cx)*(by-ay) ) / r_denomenator;
		//double s =  (((cx-ax)*(by-ay)-(cy-ay)*(bx-ax))-((cx-bx)*(bz-az)-(cz-az)*(bx-ax))+((cy-ay)*(bz-az)-(cz-az)*(by-ay))) / r_denomenator;
		int nCols=2,nRows=2;
		Matrix mat = new Matrix(nRows,nCols);
		mat.set(0, 0, cy-ay);mat.set(0, 1, 0);
		mat.set(1, 0, cy-by);mat.set(1, 1, 0);
		double s = mat.det();
		mat = new Matrix(nRows,nCols);
		mat.set(0, 0, cx-ax);mat.set(0, 1, 0);
		mat.set(1, 0, cx-bx);mat.set(1, 1, 0);
		s -=mat.det();
		mat = new Matrix(nRows,nCols);
		mat.set(0, 0, cx-ax); mat.set(0, 1, cy-ay);
		mat.set(1, 0, cx-bx);mat.set(1, 1, cy-by);
		s +=mat.det();
		distanceLine = Math.abs(s)/Math.sqrt(r_denomenator);

		//
		// (xx,yy,zz) is the point on the lineSegment closest to (cx,cy)
		//
		double xx = px;
		double yy = py;

		if (!( (r >= 0) && (r <= 1) )) {
			double dist1 = (cx-ax)*(cx-ax) + (cy-ay)*(cy-ay);
			double dist2 = (cx-bx)*(cx-bx) + (cy-by)*(cy-by);
			if (dist1 < dist2) {
				xx = ax;
				yy = ay;
			} else {
				xx = bx;
				yy = by;
			}
		}
		ArrayList<Double> posA = new ArrayList<Double>();
		posA.add(xx);posA.add(yy);
		Coords pointOnBranch = new Coords();
		pointOnBranch.setLocation(posA);
		return pointOnBranch;
	}
	
	
	
	
	
	
	
	
	
	
	
	
}
