package utility.pathfinder;

import java.util.ArrayList;
import java.util.List;

public class Path {

	private int currentStep = 0;
	private List<Node> steps = new ArrayList<Node>();
	
	public Point getNextStep()
	{
		++currentStep;
		if(currentStep>=steps.size())
			return null;
		
		Node n = steps.get(currentStep);
		Point p = n.toPoint();
		return p;
	}
	
	public Point getDesitnation()
	{
		int nbSteps = steps.size();
		return steps.get(nbSteps-1).toPoint();
	}
	
	public int getLength()
	{
		return steps.size();
	}
	
	public Node getStep(int i)
	{
		return steps.get(i);
	}
	
	public int getX(int i)
	{
		return steps.get(i).getX();
	}
	
	
	public int getY(int i)
	{
		return steps.get(i).getY();
	}
	
	public boolean contains(int x, int y)
	{
		return steps.contains(new Node(x,y));
	}
	
	public void appendStep(int x, int y)
	{
		steps.add(new Node(x,y));
	}
	
	public void prependStep(int x, int y)
	{
		steps.add(0, new Node(x,y));
	}
	

	@Override
	public String toString()
	{
		StringBuilder s = new StringBuilder();
		
		for(int i=0; i<steps.size();++i)
		{
			s.append("[");
			s.append(steps.get(i).getX());
			s.append(",");
			s.append(steps.get(i).getY());
			s.append("]");
			if(i!=0&&i%8==0)
				s.append("\n");
			if(i<steps.size()-1)
				s.append("->");
		}
		
		return s.toString();
	}
}
