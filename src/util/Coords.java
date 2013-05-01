package util;
/**
*
* @author Tyler Chapman
* @version 1.0
* 
*  This is a helper class to hold all the information needed about each data point
*  after the tree has been created.
*/
import java.util.ArrayList;
import java.util.Comparator;

public class Coords {
	private ArrayList<Double> location = new ArrayList<Double>();
	//map points that make the branch that data point is projected onto
	private int segA;
	private int segB;
	//distance to projection
	private double distToSeg;
	private int name;

	public Coords() {}
	public ArrayList<Double> getLocation() {
		return location;
	}

	public void setLocation(ArrayList<Double> location) {
		this.location = location;
	}

	public int getSegA() {
		return segA;
	}

	public void setSegA(int segA) {
		this.segA = segA;
	}

	public int getSegB() {
		return segB;
	}

	public void setSegB(int segB) {
		this.segB = segB;
	}

	public double getDistToSeg() {
		return distToSeg;
	}

	public void setDistToSeg(double distToSeg) {
		this.distToSeg = distToSeg;
	}

	public static void setComparator(Comparator<Coords> comparator) {
		Coords.comparator = comparator;
	}

	public void setSegment(int pointA, int pointB) {
		this.segA=pointA;
		this.segB=pointB;
	}

	public String printSegment() {
		return this.segA + " " + this.segB;
	}

	public void setDist(ArrayList<Double> position) {
		double tempDist=0.0;
		for(int i=0;i<this.location.size();i++){
			tempDist+=Math.pow(position.get(i)-this.location.get(i),2);
		}
		this.distToSeg = Math.sqrt(tempDist);
	}
	public static Comparator<Coords> comparator = new Comparator<Coords>() {
		@Override
		public int compare(Coords a, Coords b) {
			int ret = 0;
			if ((double)a.getDistToSeg() > (double)b.getDistToSeg()) {
				ret = 1;
			} else if ((double)a.getDistToSeg() < (double)b.getDistToSeg()) {
				ret = -1;
			}
			return ret;
		}
	};
	public Comparator<Coords> getComparator(){
		return comparator;
	}

	public void setName(int i) {
		this.name = i;
	}
	public int getName() {
		return this.name;
	}
}
