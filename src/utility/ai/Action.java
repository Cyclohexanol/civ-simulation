package utility.ai;

import utility.pathfinder.Path;
import utility.pathfinder.Point;

public class Action 
{
	private final Path path;
	private final Type type;
	private final Point location;
	private boolean atLocation;
	
	public Action(Point actionLocation, Path path, Type type) {
		super();
		this.location = actionLocation;
		this.type = type;
		this.path = path;
		if(path == null || actionLocation.equals(path.getStep(0).toPoint()))
			atLocation = true;
		else
			atLocation = false;
	}
	
	public boolean isAtLocation() {
		return atLocation;
	}

	public void setAtLocation(boolean atLocation) {
		this.atLocation = atLocation;
	}

	public Point getLocation() {
		return location;
	}

	public Path getPath() {
		return path;
	}
	
	public Type getType() {
		return type;
	}
	
	public enum Type 
	{
		DRINK,EAT,SLEEP_INSIDE,SLEEP_OUTSIDE,BUILD,WALK,FARM,BUY_FOOD,SELL_FOOD,GIVE_BIRTH,MINE,CRAFT,CHOP_WOOD,SELL_WOOD,SELL_STONE,BUY_CRAFT_SUPPLY,SELL_CRAFT_ITEMS
	}
}
