import java.util.Vector;

import util.DataPoint;


public class PQNode {
	private Vector<DataPoint> dataChildren = new Vector<DataPoint>();
	private boolean isPNode = false;
	private boolean isLeaf = false;
	private Vector<PQNode> nodeChildren = new Vector<PQNode>();
	
	public PQNode(boolean pNode) {
		isPNode  = (pNode) ? true : false; 
	}

	public PQNode(boolean isP, boolean isL, DataPoint dp) {
		this.isPNode = isP;
		this.isLeaf = isL;
		dataChildren.add(dp);
	}
	
	public Vector<DataPoint> getDataChildren() {
		return dataChildren;
	}

	public void setDataChildren(Vector<DataPoint> dataChildren) {
		this.dataChildren = dataChildren;
	}

	public boolean isPNode() {
		return isPNode;
	}

	public void setPNode(boolean isPNode) {
		this.isPNode = isPNode;
	}
	
	public boolean isLeaf() {
		return isLeaf;
	}

	public void setLeaf(boolean isLeaf) {
		this.isLeaf = isLeaf;
	}

	public Vector<PQNode> getNodeChildren() {
		return nodeChildren;
	}

	public void setNodeChildren(Vector<PQNode> nodeChildren) {
		this.nodeChildren = nodeChildren;
	}


	public void addDataChild(DataPoint dataPoint) {
		dataChildren.add(dataPoint);
	}

	public void addNodeChild(PQNode tempNode) {
		nodeChildren.add(tempNode);
	}
	
	public StringBuilder printNode(StringBuilder sb, int level){
		sb.append(level);
		sb.append((isPNode) ? "P:  " : "Q:  ");
		for(DataPoint dp : dataChildren){
			sb.append(dp.getObjName() + "   ");
		}
		sb.append((dataChildren.size()==0) ? "Main" : "\t");
		sb.append("\n");
		for(PQNode node : nodeChildren){
			node.printNode(sb, level+1);
			sb.append("\t");
		}
		
		return sb;
	}


	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
