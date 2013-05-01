package util;
/**
*
* @author Tyler Chapman
* @version 1.0
* 
*  This is a helper class for the specific paths the SOM tree can generate.
*  Each possible path will be represented as a member of this class. 
*/

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Vector;

public class PathAndDistance {
	private double dist;
	private Vector<Integer> path;
	private HashMap<Integer,Coords> pointOnBranch = new HashMap<Integer, Coords>();
	private int branchDist;
	private HashMap<ArrayList<Double>, ArrayList<Integer>> degenerateList = new HashMap<ArrayList<Double>, ArrayList<Integer>>();
	
	public PathAndDistance(Double totDist, Vector<Integer> object, int mstDist) {
		this.dist = totDist;
		this.path = object;
		this.branchDist = mstDist;
	}
	public PathAndDistance() {
	}
	
	//constructor for making a diameter path
	public PathAndDistance(int longestPath, int pathDistance, Vector<Integer> diamPath) {
		this.dist = longestPath;
		this.branchDist = pathDistance;
		this.path = diamPath;
	}

	public Double getDist() {
		return dist;
	}
	public void setDist(Double dist) {
		this.dist = dist;
	}
	public Vector<Integer> getPath() {
		return path;
	}
	public void setPath(Vector<Integer> path) {
		this.path = path;
	}
	public String printPath(){
		String pathSequence = "Path:";
		for(int i=0; i<path.size(); i++){
			pathSequence += " " + this.path.get(i).toString();
		}
		pathSequence += " = " + this.dist;
		return pathSequence;
	}

	public HashMap<Integer, Coords> getPointOnBranch() {
		return pointOnBranch;
	}
	public void setPointOnBranch(HashMap<Integer, Coords> pointOnBranch) {
		this.pointOnBranch = pointOnBranch;
	}
	public int getBranchDist() {
		return branchDist;
	}
	public void setBranchDist(int branchDist) {
		this.branchDist = branchDist;
	}
	public static Comparator<PathAndDistance> comparator = new Comparator<PathAndDistance>() {
		@Override
		public int compare(PathAndDistance a, PathAndDistance b) {
			int ret = 0;
			if ((double)a.getDist() > (double)b.getDist()) {
				ret = 1;
			} else if ((double)a.getDist() < (double)b.getDist()) {
				ret = -1;
			}
			return ret;
		}
	};
	public Comparator<PathAndDistance> getComparator(){
		return comparator;
	}
	
	public void setDegeneracy(HashMap<ArrayList<Double>, ArrayList<Integer>> degeneracyList) {
		degenerateList = degeneracyList;
	}
	
	public String printDegeneracy(){
		String degStr = "{";
		for(ArrayList<Integer> degPts : degenerateList.values()){
			degStr += degPts.toString() + ",";
		}
		degStr = degStr.substring(0, degStr.length()-1) + "}";
		return degStr;
	}

}
