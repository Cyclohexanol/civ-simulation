package entity;

import java.util.HashMap;
import java.util.Map;

import entity.building.Building;
import entity.building.Farm;
import entity.building.House;
import entity.building.ItemMarket;
import entity.building.Market;
import entity.building.Market.ItemType;
import entity.building.OwnableBuilding;
import entity.tile.Tile;
import entity.tile.Tile.Material;
import entity.unit.Family;
import entity.unit.Human;
import entity.unit.Item;
import entity.unit.Unit;
import entity.unit.Human.Job;
import utility.pathfinder.Point;

public class WorldMap 
{
	private final Tile [][] tiles;
	private final Map<Integer, Building> buildings;
	private final Map<Integer,Unit> units;
	private final Map<Integer,Family> families;
	private final int xSize, ySize;
	
	public WorldMap(Tile [][] tiles, Building [][] buildings)
	{
		if(tiles == null)
			throw new IllegalArgumentException("Map must contain at least one tile!");
		
		if(tiles.length!=buildings.length || tiles[0].length!=buildings[0].length)
			throw new IllegalArgumentException("Tile and building grid must be of same dimension!");
		this.xSize = tiles.length;
		this.ySize = tiles[0].length;
		this.buildings = new HashMap<Integer,Building>();
		this.tiles = tiles;
		this.units = new HashMap<Integer,Unit>();
		this.families = new HashMap<Integer,Family>();
	}
	
	public Map<Integer, Family> getFamilies() {
		return families;
	}
	
	public boolean isInMap(int x, int y)
	{
		if(x<0||y<0||x>=xSize||y>=ySize)
			return false;
		return true;
	}
	
	public Map<Integer,Family> getHomelessFamilies()
	{
		Map<Integer,Family> f = new HashMap<Integer,Family>();
		for(Map.Entry<Integer, Family> e : families.entrySet())
		{
			if(e.getValue().getHouses().isEmpty())
				f.put(e.getKey(), e.getValue());
		}
		return f;
	}
	
	public Map<Integer,Human> getHumans()
	{
		Map<Integer,Human> h = new HashMap<Integer,Human>();
		for(Map.Entry<Integer, Unit> e : units.entrySet())
		{
			Unit u = e.getValue();
			if(u.isHuman()&&u.isAlive())
				h.put(e.getKey(), (Human)e.getValue());
		}
		return h;
	}
	
	public int getJoblessCount()
	{
		int count = 0;
		Map<Integer,Human> h = getHumans();
		for(Map.Entry<Integer, Human> e : h.entrySet())
		{
			Human u = e.getValue();
			if(u.getJob()==null&&u.getAge()>17)
				++count;
		}
		return count;
	}
	
	public int getItemsInMarkets(Item.Object obj)
	{
		int quantity = 0;
		for(Map.Entry<Integer, Building> b : buildings.entrySet())
		{
			if(b.getValue().getType()!=Building.Type.MARKET)
				continue;
			if(((Market)b.getValue()).getItemType()!=ItemType.ITEM)
				continue;

			quantity +=((ItemMarket)b.getValue()).getSupply(obj);
		}
		return quantity;
	}
	
	public void checkBirthDays(long tickCount)
	{
		Map<Integer,Human> h = getHumans();
		for(Map.Entry<Integer, Human> e : h.entrySet())
		{
			Human hu = e.getValue();
			if(hu.getBirthday()==tickCount%10000)
			{
				hu.addAge();
				System.out.println(hu.getId() + " just turned " + hu.getAge() + "!");
			}
		}
	}
	
	public void printFamiliesState()
	{
		System.out.println("__________________");
		System.out.println("Families :");
		Map<Integer, Family> f = families;
		for(Map.Entry<Integer, Family> e : f.entrySet())
		{
			Family fa = e.getValue();
			System.out.println(fa.getId() + " : " + fa.getBuildings().size() + " buildings - " 
			+ fa.getMembers().size() + "/" + fa.getHousesCapacity() + " members - " 
			+ fa.getMoney() + "$ - Food : " + fa.getFood());
		}
		System.out.println("__________________");
	}
	
	public void removeEmptyFamilies()
	{
		Map<Integer,Family> famToRemove = new HashMap<Integer,Family>();
		for(Map.Entry<Integer, Family> e : families.entrySet())
		{
			Family f = e.getValue();
			if(f.getMembers().isEmpty())
			{
				for(Map.Entry<Integer, OwnableBuilding> b : f.getBuildings().entrySet())
				{
					b.getValue().setOwned(false);
				}
				famToRemove.put(f.getId(), f);
			}
		}
		
		for(Map.Entry<Integer, Family> e : famToRemove.entrySet())
			families.remove(e.getKey());
			

		
	}
	
	public Map<Integer,Building> getIncompletedBuildings()
	{
		Map<Integer,Building> b = new HashMap<Integer,Building>();
		for(Map.Entry<Integer, Building> e : buildings.entrySet())
		{
			Building bu = e.getValue();
			if(!bu.isFinished())
				b.put(e.getKey(), bu);
		}
		return b;
	}
	
	public boolean isBuildingOnTileFinished(Point p)
	{
		for(Map.Entry<Integer, Building> e : buildings.entrySet())
		{
			Building bu = e.getValue();
			for(int i=0; i<bu.getHeight(); ++i)
				for(int j=0; j<bu.getWidth();++j)
				{
					int xb = j + bu.getxPos();
					int yb = i + bu.getyPos();
					Point cp =  new Point(xb,yb);
					if(p.equals(cp))
						return bu.isFinished();
				}
		}
		return true;
	}
	
	public Map<Integer,Human> getHumanWithJob(Human.Job job)
	{
		Map<Integer,Human> h = new HashMap<Integer,Human>();
		for(Map.Entry<Integer, Unit> e : units.entrySet())
		{
			Unit u = e.getValue();
			if(u.isHuman()&&u.isAlive())
				if(((Human)u).getJob()==job)
					h.put(e.getKey(), (Human)e.getValue());
		}
		return h;
	}
	
	public Map<Integer,House> getEmptyHouses()
	{
		Map<Integer,Building> b = getCompletedBuildingWithType(Building.Type.HOUSE);
		Map<Integer,House> h = new HashMap<Integer,House>();
		for(Map.Entry<Integer, Building> e : b.entrySet())
		{
			House bu = (House)(e.getValue());
			if(!bu.isOwned())
				h.put(bu.getId(),bu);
		}
		
		return h;
	}
	
	public Map<Integer,Building> getIncompletedBuildingWithType(Building.Type type)
	{
		Map<Integer,Building> h = new HashMap<Integer,Building>();
		for(Map.Entry<Integer, Building> e : buildings.entrySet())
		{
			Building b = e.getValue();
			if(b.getType()==type&&!b.isFinished())
				h.put(e.getKey(), b);
		}
		
		return h;
	}
	
	public Map<Integer,Building> getCompletedBuildingWithType(Building.Type type)
	{
		Map<Integer,Building> h = new HashMap<Integer,Building>();
		for(Map.Entry<Integer, Building> e : buildings.entrySet())
		{
			Building b = e.getValue();
			if(b.getType()==type&&b.isFinished())
				h.put(e.getKey(), b);
		}
		
		return h;
	}
	
	public Map<Integer,Building> getBuildingWithType(Building.Type type)
	{
		Map<Integer,Building> h = new HashMap<Integer,Building>();
		for(Map.Entry<Integer, Building> e : buildings.entrySet())
		{
			Building b = e.getValue();
			if(b.getType()==type)
				h.put(e.getKey(), b);
		}
		
		return h;
	}
	
	public boolean canDrinkFromTile(Point p)
	{
		int x = p.getX();
		int y = p.getY();
		
		if(!isWalkableTile(x, y))
			return false;
		
		for(int i=-1; i<=1;++i)
			for(int j=-1; j<=1;++j)
			{
				if(i==0&&j==0)
					continue;
				if(i!=0&&j!=0)
					continue;
				
				int xp = j + x;
				int yp = i + y;
				
				if(!isInMap(xp,yp))
					continue;
				
				if(tiles[xp][yp].getMaterial()==Tile.Material.LAKE)
					return true;
			}
		
		return false;
	}
	
	public boolean canDrinkFromTile(int x, int y)
	{
		if(!isWalkableTile(x, y))
			return false;
		
		for(int i=-1; i<=1;++i)
			for(int j=-1; j<=1;++j)
			{
				if(i==0&&j==0)
					continue;
				if(i!=0&&j!=0)
					continue;
				
				int xp = j + x;
				int yp = i + y;
				if(tiles[xp][yp].getMaterial()==Tile.Material.LAKE)
					return true;
			}
		
		return false;
	}

	public boolean isConstructableTile(int x, int y)
	{
		if(!isInMap(x,y))
			return false;
		
		if(!tiles[x][y].isConstructable())
			return false;
		
		if(hasBuilding(x,y))
			return false;
		
		return true;
	}
	
	public boolean isWalkableTile(int x, int y)
	{
		if(!isInMap(x,y))
			return false;
		
		if(!tiles[x][y].isWalkable())
			return false;
		
		return true;
	}
	
	public Map<Integer, Unit> getUnits() {
		return units;
	}

	public boolean isWalkableTileWithoutBuilding(int x, int y)
	{
		if(hasBuilding(x,y))
			return false;
		return isWalkableTile(x, y);
	}

	public Family getFamily(int i)
	{
		return families.get(i);
	}
	
	public void addFamily(Family family)
	{
		this.families.put(family.getId(), family);
	}
	
	public Unit getUnit(int i)
	{
		return units.get(i);
	}
	
	public void addUnit(Unit unit)
	{
		units.put(unit.getId(), unit);
	}
	
	public Tile getTile(int x, int y)
	{
		return tiles[x][y];
	}
	
	public Building getBuildingOnTile(int x, int y)
	{
		for(Map.Entry<Integer, Building> e : buildings.entrySet())
		{
			Building b = e.getValue();
			int xb = b.getxPos();
			int yb = b.getyPos();
			
			for(int i=0; i<b.getHeight();++i)
				for(int j=0; j<b.getWidth();++j)
					if(yb+i==y&&xb+j==x)
						return b;
		}
		
		return null;
	}
	
	public boolean hasBuilding(int x, int y)
	{
		for(Map.Entry<Integer, Building> e : buildings.entrySet())
		{
			Building b = e.getValue();
			int xb = b.getxPos();
			int yb = b.getyPos();
			
			for(int i=0; i<b.getHeight();++i)
				for(int j=0; j<b.getWidth();++j)
					if(yb+i==y&&xb+j==x)
						return true;
		}
		
		return false;
	}
	
	public Map<Integer, Building> getBuildings() {
		return buildings;
	}

	public boolean addFarm(Farm building)
	{
		if(building==null)
			return false;
		
		int xb = building.getxPos();
		int yb = building.getyPos();
		
		for(int i=0; i<building.getHeight();++i)
			for(int j=0; j<building.getWidth();++j)
				if(!isConstructableTile(xb+j, yb+i)||tiles[xb+j][yb+i].getMaterial()!=Material.GRASS)
					return false;
		buildings.put(building.getId(), building);
		return true;
	}
	
	public boolean addBuilding(Building building)
	{
		if(building==null)
			return false;
		
		int xb = building.getxPos();
		int yb = building.getyPos();
		
		for(int i=0; i<building.getHeight();++i)
			for(int j=0; j<building.getWidth();++j)
				if(!isConstructableTile(xb+j, yb+i))
					return false;
		buildings.put(building.getId(), building);
		return true;
	}
	
	public Tile[][] getTiles() {
		return tiles;
	}
	
	public int getHeight()
	{
		return tiles[0].length;
	}
	
	public int getWidth()
	{
		return tiles.length;
	}
	
	public Point getClosestTileOfMaterial(Point pos, Material material)
	{
		int px = pos.getX();
		int py = pos.getY();
		
		int size;
		if(ySize<xSize)
			size = xSize;
		else
			size = ySize;
		
		for(int r=0;r<size;++r)
			for(int i=-r;i<=r;++i)
				for(int j=-r;j<=r;++j)
				{
					if(i!=r&&j!=r&&i!=-r&&j!=-r)
						continue;
					int x = px + j;
					int y = py + i;
					Point p = new Point(x,y);
					if(!isInMap(x, y))
						continue;
					if(tiles[x][y].getMaterial()==material){
						return p;
					}
						
						
				}
		return null;
	}
	
	public Point getClosestDrinkableTile(Point pos)
	{
		int px = pos.getX();
		int py = pos.getY();
		
		int size;
		if(ySize<xSize)
			size = xSize;
		else
			size = ySize;
		
		for(int r=0;r<size;++r)
			for(int i=-r;i<=r;++i)
				for(int j=-r;j<=r;++j)
				{
					if(i!=r&&j!=r&&i!=-r&&j!=-r)
						continue;
					int x = px + j;
					int y = py + i;
					Point p = new Point(x,y);
					if(canDrinkFromTile(p)){
						return p;
					}
						
						
				}
		return null;
	}

	public void printHumansState()
	{
		StringBuilder s = new StringBuilder();
		s.append("__________________");
		s.append("\n");
		s.append("Humans :");
		s.append("\n");
		Map<Integer, Human> h = getHumans();
		for(Map.Entry<Integer, Human> e : h.entrySet())
		{
			Human hu = e.getValue();
			s.append(hu.getJob() + " " + hu.getSex() + " " + hu.getId() + " : " + hu.getActionType() + " - " 
			+ hu.getMoney() + "$ - Food : " + hu.getFood()) ;
			if(hu.getJob()==Job.MINER)
				s.append(" - Stone : " + hu.getItemQuantity(Item.Object.STONE));
			if(hu.getJob()==Job.LUMBERJACK)
				s.append(" - Wood : " + hu.getItemQuantity(Item.Object.CLASSIC_WOOD));
			s.append(" - Age : " + hu.getAge() + " - Maried : " + hu.isMaried());
			
			s.append("\n");
		}
		s.append("__________________");
		System.out.println(s.toString());
	}
}
