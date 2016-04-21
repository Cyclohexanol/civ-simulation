package entity.unit;

import java.awt.Color;

import utility.pathfinder.Path;
import utility.pathfinder.Point;

public abstract class Unit 
{
	public static final Color COLOR_HUMAN_MALE = new Color(0,15,140);
	public static final Color COLOR_HUMAN_FEMALE = new Color(140,0,140);
	
	private final int id;
	private final double size;
	private final Type type;
	private boolean alive;
	private Point pos;
	private final Color color;
	private long idleTickCount, lastActionTickCount;
	
	public Unit(int id, double size, Type type, Color color) {
		super();
		this.id = id;
		this.size = size;
		this.type = type;
		alive = true;
		this.color = color;
		this.pos = new Point(0,0);
		this.idleTickCount = 0;
		this.lastActionTickCount = 0;
	}
	
	public boolean isHuman()
	{
		if(this.type==Type.HUMAN)
			return true;
		return false;
	}
	
	public long getIdleTickCount() {
		return idleTickCount;
	}

	public void setIdleTickCount(long idleTickCount) {
		this.idleTickCount = idleTickCount;
	}

	public long getLastActionTickCount() {
		return lastActionTickCount;
	}

	public void setLastActionTickCount(long lastActionTickCount) {
		this.lastActionTickCount = lastActionTickCount;
	}
	
	public Color getColor() {
		return color;
	}

	public int getxPos() {
		return pos.getX();
	}
	
	public Point getPos() {
		return pos;
	}

	public void setPos(Point pos) {
		this.pos = pos;
	}

	public void setxPos(int xPos) {
		this.pos.setX(xPos);
	}

	public int getyPos() {
		return pos.getY();
	}

	public void setyPos(int yPos) {
		this.pos.setY(yPos);
	}

	public boolean isAlive() {
		return alive;
	}

	public void die() {
		this.alive = false;
	}

	public int getId() {
		return id;
	}

	public double getSize() {
		return size;
	}

	public Type getType() {
		return type;
	}

	public enum Type
	{
		HUMAN,ANIMAL
	}
}
