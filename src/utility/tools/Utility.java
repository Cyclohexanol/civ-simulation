package utility.tools;

import java.util.Map;
import java.util.Random;

import entity.WorldMap;
import entity.building.Building;
import entity.unit.Family;
import entity.unit.Unit;
import utility.pathfinder.Point;

public class Utility {
	private static Random r = new Random();
	
	public static int generateValidBuildingID(Map<Integer,Building> m)
	{
		int id;
		do
		{
			id = Math.abs(r.nextInt());
		}while(m.containsKey(id));
		return id;
	}
	
	public static int generateValidUnitID(Map<Integer,Unit> m)
	{
		int id;
		do
		{
			id = Math.abs(r.nextInt());
		}while(m.containsKey(id));
		return id;
	}
	
	public static int generateValidFamilyID(Map<Integer,Family> m)
	{
		int id;
		do
		{
			id = Math.abs(r.nextInt());
		}while(m.containsKey(id));
		return id;
	}
	
	public static Point getRandomWalkableTileWithoutBuilding(WorldMap map)
	{
		int x;
		int y;
		do
		{
			x = (int) (Math.random()*map.getWidth());
			y = (int) (Math.random()*map.getHeight());
		}while(!map.isWalkableTileWithoutBuilding(x, y));
		return new Point(x,y);
	}
}
