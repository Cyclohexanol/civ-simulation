package utility.factory;


import java.util.Map;

import entity.WorldMap;
import entity.building.*;
import entity.building.Building.Type;
import entity.tile.Tile.Material;
import game.GameData;
import utility.pathfinder.Point;
import utility.tools.Utility;

public class BuildingGenerator 
{
	private final WorldMap map;
	private final GameData data;
	
	public BuildingGenerator(WorldMap map, GameData data) {
		super();
		this.map = map;
		this.data = data;
	}
	
	public void addNewBuildingToBuild()
	{
		addHousesToBuild();
		addFoodMarketsToBuild();
		addWoodMarketsToBuild();
		addStoneMarketsToBuild();
		addCraftmanMarketsToBuild();
		addFarmsToBuild();
	}
	
	private void addHousesToBuild()
	{
		double needForHouse = data.getHouseNeed();
		double famWealth = data.getAverageFamilyWealth();
		while(needForHouse>0)
		{
			Building b;
			double bestVal = 0;
			do{
			int bx = 0;
			int by = 0;
			bestVal = 0;
			
			for(int i=0; i<20 ;++i)
			{
				int x = (int) (Math.random()*map.getWidth());
				int y = (int) (Math.random()*map.getHeight());
				Point p = new Point(x,y);
				double val = tileValueForHouse(p);
				if(val>bestVal)
				{
					bestVal = val;
					bx = x;
					by = y;
				}
			}
			
			int id = Utility.generateValidBuildingID(map.getBuildings());
			
			if(Math.random()*famWealth>50)
				b = new BigShed(id,bx,by);
			else
				b = new SmallShed(id,bx,by);
			
			}while(!map.addBuilding(b));
			
			System.out.println("Added " + b.getType() + " "+ b.getId() + " at " + b.getPos() + " - value : " + bestVal);
			needForHouse = data.getHouseNeed();
		}
	}
	
	private void addWoodMarketsToBuild()
	{
		double needForWoodMarket = data.getWoodMarketNeed();
		while(needForWoodMarket>0)
		{
			Building b;
			double bestVal = 0;
			do{
			int bx = 0;
			int by = 0;
			bestVal = 0;
			
			for(int i=0; i<10 ;++i)
			{
				int x = (int) (Math.random()*map.getWidth());
				int y = (int) (Math.random()*map.getHeight());
				Point p = new Point(x,y);
				double val = tileValueForMarket(p);
				val += 4/(map.getClosestTileOfMaterial(p, Material.FOREST).distance(p)+1);
				if(val>bestVal)
				{
					bestVal = val;
					bx = x;
					by = y;
				}
			}
			
			int id = Utility.generateValidBuildingID(map.getBuildings());
			
			b = new WoodMarket(id, bx, by);
			
			}while(!map.addBuilding(b));
			
			System.out.println("Added " + b.getType() + " "+ b.getId() + " at " + b.getPos() + " - value : " + bestVal);
			needForWoodMarket = data.getWoodMarketNeed();
		}
	}
	
	private void addCraftmanMarketsToBuild()
	{
		double needForCraftmanMarket = data.getCraftmanMarketNeed();
		while(needForCraftmanMarket>0)
		{
			Building b;
			double bestVal = 0;
			do{
			int bx = 0;
			int by = 0;
			bestVal = 0;
			
			for(int i=0; i<10 ;++i)
			{
				int x = (int) (Math.random()*map.getWidth());
				int y = (int) (Math.random()*map.getHeight());
				Point p = new Point(x,y);
				double val = tileValueForMarket(p);
				for(Map.Entry<Integer, Building> e : map.getCompletedBuildingWithType(Type.MARKET).entrySet())
					val += 4/ (e.getValue().getPos().distance(new Point(x,y))+1);
				if(val>bestVal)
				{
					bestVal = val;
					bx = x;
					by = y;
				}
			}
			
			int id = Utility.generateValidBuildingID(map.getBuildings());
			
			b = new CraftmanMarket(id, bx, by);
			
			}while(!map.addBuilding(b));
			
			System.out.println("Added " + b.getType() + " "+ b.getId() + " at " + b.getPos() + " - value : " + bestVal);
			needForCraftmanMarket = data.getCraftmanMarketNeed();
		}
	}
	private void addStoneMarketsToBuild()
	{
		double needForStoneMarket = data.getStoneMarketNeed();
		while(needForStoneMarket>0)
		{
			Building b;
			double bestVal = 0;
			do{
			int bx = 0;
			int by = 0;
			bestVal = 0;
			
			for(int i=0; i<10 ;++i)
			{
				int x = (int) (Math.random()*map.getWidth());
				int y = (int) (Math.random()*map.getHeight());
				Point p = new Point(x,y);
				double val = tileValueForMarket(p);
				val += 4/(map.getClosestTileOfMaterial(p, Material.ROCK).distance(p)+1);
				if(val>bestVal)
				{
					bestVal = val;
					bx = x;
					by = y;
				}
			}
			
			int id = Utility.generateValidBuildingID(map.getBuildings());
			
			b = new StoneMarket(id, bx, by);
			
			}while(!map.addBuilding(b));
			
			System.out.println("Added " + b.getType() + " "+ b.getId() + " at " + b.getPos() + " - value : " + bestVal);
			needForStoneMarket = data.getStoneMarketNeed();
		}
	}
	
	private void addFoodMarketsToBuild()
	{
		double needForFoodMarket = data.getFoodMarketNeed();
		while(needForFoodMarket>0)
		{
			Building b;
			double bestVal = 0;
			do{
			int bx = 0;
			int by = 0;
			bestVal = 0;
			
			for(int i=0; i<10 ;++i)
			{
				int x = (int) (Math.random()*map.getWidth());
				int y = (int) (Math.random()*map.getHeight());
				Point p = new Point(x,y);
				double val = tileValueForMarket(p);
				if(val>bestVal)
				{
					bestVal = val;
					bx = x;
					by = y;
				}
			}
			
			int id = Utility.generateValidBuildingID(map.getBuildings());
			
			b = new FoodMarket(id, bx, by);
			
			}while(!map.addBuilding(b));
			
			System.out.println("Added " + b.getType() + " "+ b.getId() + " at " + b.getPos() + " - value : " + bestVal);
			needForFoodMarket = data.getFoodMarketNeed();
		}
	}
	
	private void addFarmsToBuild()
	{
		double needForFarms = data.getFarmNeed();
		while(needForFarms>0)
		{
			Farm b;
			double bestVal = 0;
			do{
			int bx = 0;
			int by = 0;
			bestVal = 0;
			
			for(int i=0; i<10 ;++i)
			{
				int x = (int) (Math.random()*map.getWidth());
				int y = (int) (Math.random()*map.getHeight());
				Point p = new Point(x,y);
				double val = tileValueForFarm(p);
				if(val>bestVal)
				{
					bestVal = val;
					bx = x;
					by = y;
				}
			}
			
			int id = Utility.generateValidBuildingID(map.getBuildings());
			
			b = new Farm(id, bx, by);
			
			}while(!map.addFarm(b));
			
			System.out.println("Added " + b.getType() + " "+ b.getId() + " at " + b.getPos() + " - value : " + bestVal);
			needForFarms = data.getFarmNeed();
		}
	}
	
	private double tileValueForHouse(Point p)
	{
		double val = 0;
		val += 10/(map.getClosestDrinkableTile(p).distance(p)+1);
		
		Map<Integer, Building> h = map.getBuildingWithType(Type.MARKET);
		for(Map.Entry<Integer, Building> e : h.entrySet())
		{
			val += 40/(e.getValue().getPos().distance(p)+1);
		}
		
		return val;
	}
	
	private double tileValueForMarket(Point p)
	{
		double val = 0;
		Map<Integer, Building> h = map.getBuildingWithType(Type.HOUSE);
		for(Map.Entry<Integer, Building> e : h.entrySet())
		{
			val += 1/(e.getValue().getPos().distance(p)+1);
		}
		
		return val;
	}
	
	private double tileValueForFarm(Point p)
	{
		double val = 0;
		Map<Integer, Building> h = map.getBuildingWithType(Type.HOUSE);
		for(Map.Entry<Integer, Building> e : h.entrySet())
		{
			val += 1/(e.getValue().getPos().distance(p)+1);
		}
		
		return val;
	}
	
}
