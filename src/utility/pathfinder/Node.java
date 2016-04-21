package utility.pathfinder;

public class Node implements Comparable<Object> {
	private int x;
	private int y;
	private float cost;
	private Node parent;
	private float heuristic;
	private int depth;
	
	public Node(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void addCost(float cost)
	{
		this.cost += cost;
	}
	
	public void setCost(float cost) {
		this.cost = cost;
	}

	public void setHeuristic(float heuristic) {
		this.heuristic = heuristic;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public float getCost() {
		return cost;
	}

	public float getHeuristic() {
		return heuristic;
	}

	public int getDepth() {
		return depth;
	}

	public Node getParent() {
		return parent;
	}

	public int setParent(Node parent) {
		if(parent != null)
			depth = parent.depth + 1;
		this.parent = parent;
		
		return depth;
	}
	
	public float distance(int x, int y)
	{
		return (float) Math.sqrt(Math.pow(x-this.x,2)+Math.pow(y-this.y,2));
	}
	
	public Point toPoint()
	{
		return new Point(x,y);
	}
	
	public int compareTo(Object other) {
		Node o = (Node) other;
		
		float f = heuristic + cost;
		float of = o.heuristic + o.cost;
		
		if (f < of) {
			return -1;
		} else if (f > of) {
			return 1;
		} else {
			return 0;
		}
	}
}
