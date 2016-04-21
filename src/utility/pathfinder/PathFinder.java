package utility.pathfinder;

import java.util.ArrayList;
import java.util.Collections;

import entity.WorldMap;
import entity.tile.Tile.Material;

public class PathFinder 
{
	private ArrayList<Node> closed = new ArrayList<Node>();
	private SortedList open = new SortedList();
	private WorldMap map;
	private int maxSearchDistance;
	private Node[][] nodes;
	
	public PathFinder(WorldMap map) {
		super();
		this.map = map;
		this.maxSearchDistance = Integer.MAX_VALUE;
		this.nodes = new Node[map.getWidth()][map.getHeight()];
		for(int x=0; x<map.getWidth(); ++x)
			for(int y=0; y<map.getHeight();++y)
				nodes[x][y] = new Node(x,y);
	}
	
	public PathFinder(WorldMap map, int maxSearchDistance) {
		super();
		this.map = map;
		this.maxSearchDistance = maxSearchDistance;
		this.nodes = new Node[map.getWidth()][map.getHeight()];
		for(int x=0; x<map.getWidth(); ++x)
			for(int y=0; y<map.getHeight();++y)
				nodes[x][y] = new Node(x,y);
	}
	
	public Path findPath(Point start, Point end)
	{
		if(start==null||end==null)
			return null;
		
		return findPath(start.getX(),start.getY(), end.getX(), end.getY());
	}
	
	public Path findPath(int sx, int sy, int ex, int ey)
	{
		if(!map.isWalkableTile(ex, ey))
			return null;
		
		nodes[sx][sy].setCost(0);
		nodes[sx][sy].setDepth(0);
		closed.clear();
		open.clear();
		open.add(nodes[sx][sy]);
		
		nodes[ex][ey].setParent(null);
		int maxDepth = 0;
		while(maxDepth<maxSearchDistance&&open.size() != 0)
		{
			Node current = getFirstInOpen();
			if (current == nodes[ex][ey]) {
				break;
			}
			
			removeFromOpen(current);
			addToClosed(current);
			
			for (int x=-1;x<2;x++) {
				for (int y=-1;y<2;y++) {

					if ((x == 0) && (y == 0)) {
						continue;
					}
					
					int xp = x + current.getX();
					int yp = y + current.getY();
					float distance = current.distance(xp, yp);
					
					if (map.isWalkableTile(xp, yp)) {
						float nextStepCost = current.getCost();
						if(!map.isWalkableTileWithoutBuilding(xp, yp))
							nextStepCost += 5*distance;
						else if(map.getTile(xp, yp).getMaterial()==Material.FOREST)
							nextStepCost += 2*distance;
						else
							nextStepCost += 1*distance;
						Node neighbour = nodes[xp][yp];
						
						if (nextStepCost < neighbour.getCost()) {
							if (inOpenList(neighbour)) {
								removeFromOpen(neighbour);
							}
							if (inClosedList(neighbour)) {
								removeFromClosed(neighbour);
							}
						}
						
						if (!inOpenList(neighbour) && !(inClosedList(neighbour))) {
							neighbour.setCost(nextStepCost);
							neighbour.setHeuristic((float) neighbour.distance(ex, ey));
							maxDepth = Math.max(maxDepth, neighbour.setParent(current));
							addToOpen(neighbour);
						}
					}
				}
			}
		}
		
		if (nodes[ex][ey].getParent() == null) {
			return null;
		}

		Path path = new Path();
		Node target = nodes[ex][ey];
		while (target != nodes[sx][sy]) {
			path.prependStep(target.getX(), target.getY());
			target = target.getParent();
		}
		path.prependStep(sx,sy);

		return path;
	}

	protected Node getFirstInOpen() {
		return (Node) open.first();
	}

	protected void addToOpen(Node node) {
		open.add(node);
	}
	
	protected boolean inOpenList(Node node) {
		return open.contains(node);
	}

	protected void removeFromOpen(Node node) {
		open.remove(node);
	}

	protected void addToClosed(Node node) {
		closed.add(node);
	}

	protected boolean inClosedList(Node node) {
		return closed.contains(node);
	}

	protected void removeFromClosed(Node node) {
		closed.remove(node);
	}
	
	private class SortedList {
		private ArrayList<Node> list = new ArrayList<Node>();
		
		public Object first() {
			return list.get(0);
		}
		
		public void clear() {
			list.clear();
		}
		
		public void add(Node o) {
			list.add(o);
			Collections.sort(list);
		}
		
		public void remove(Node o) {
			list.remove(o);
		}
	
		public int size() {
			return list.size();
		}
		

		public boolean contains(Node o) {
			return list.contains(o);
		}
	}
}
