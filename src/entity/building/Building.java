package entity.building;

import java.awt.Color;

import utility.pathfinder.Point;

public abstract class Building 
{
	public static final Color COLOR_SHED = new Color(80,80,80);
	public static final Color COLOR_FOOD_MARKET = new Color(220,80,80);
	public static final Color COLOR_FARM = new Color(100,250,100);
	public static final Color COLOR_WOOD_MARKET = new Color(20,120,20);
	public static final Color COLOR_STONE_MARKET = new Color(180,180,180);
	public static final Color COLOR_CRAFTMAN_MARKET = new Color(160,100,160);
	
	private final Type type;
	private final Color color;
	private final int id;
	private final int width, height;
	private double completion; 
	private int completionVal;
	private final Point pos;
	
	public Building(int id, int width, int height, Type type, Color color, int xPos, int yPos, int completionVal) {
		super();
		this.width = width;
		this.height = height;
		this.id = id;
		this.type = type;
		this.color = color;
		this.pos = new Point(xPos,yPos);
		this.completion = 0;
		this.completionVal = completionVal;
	}
	
	public boolean isFinished() {
		if(completionVal<=completion)
			return true;
		return false;
	}

	public int getxPos() {
		return pos.getX();
	}
	
	public int getyPos() {
		return pos.getY();
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getId() {
		return id;
	}

	public double getCompletion() {
		return completion;
	}

	public void setCompletion(double completion) {
		this.completion = completion;
	}

	public void addCompletion(double completion) {
		this.completion += completion;
	}
	
	public int getCompletionVal() {
		return completionVal;
	}

	public void setCompletionVal(int completionVal) {
		this.completionVal = completionVal;
	}

	public Point getPos() {
		return pos;
	}

	public Type getType() {
		return type;
	}

	public Color getColor() {
		return color;
	}

	public boolean isOwnable()
	{
		if(type==Type.HOUSE||type==Type.FARM)
			return true;
		return false;
	}

	public enum Type
	{
		HOUSE,FARM,MARKET,CULTURE
	}
}
