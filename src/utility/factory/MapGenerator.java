package utility.factory;

import java.util.ArrayList;
import java.util.List;

import entity.WorldMap;
import entity.building.Building;
import entity.tile.*;
import entity.tile.Tile.Material;
import entity.tile.Tile.Type;

public class MapGenerator 
{
	private static Tile generateTile(List<Tile> neighbors)
	{
		int land = 0,
				forest = 0,
				dirt = 0,
				rock = 0,
				grass = 0,
				water = 0,
				lake = 0,
				ocean = 0;

		int nbNeighbors = neighbors.size();
		
		for(int i=0; i<nbNeighbors; ++i)
		{
			Tile n = neighbors.get(i);
			Type t = n.getType();
			Material m = n.getMaterial();
			
			if(t==Type.LAND)
				++land;
			else if(t==Type.WATER)
				++water;
			
			if(m==Material.DIRT)
				++dirt;
			else if(m==Material.GRASS)
				++grass;
			else if(m==Material.LAKE)
				++lake;
			else if(m==Material.ROCK)
				++rock;
			else if(m==Material.OCEAN)
				++ocean;
			else if(m==Material.FOREST)
				++forest;
		}
		
		if(Math.random()<ocean/4.0+0.001)
		{
			if(lake==0)
				return new Ocean();
		}
		else if(Math.random()<lake/5.0+0.005)
		{
			if(ocean==0)
				return new Lake();
		}
		else
		{
			if(Math.random()<rock/5.0+0.001)
				return new Rock();
			if(Math.random()<forest/4.2+0.005)
				return new Forest();
			if(Math.random()<dirt/7.0+0.2)
				return new Dirt();
			
		}
		return new Grass();
	}
	
	private static List<Tile> getNeighbors(Tile[][] tiles, int x, int y)
	{
		List<Tile> neighbors = new ArrayList<Tile>();
		for(int n=-1; n<=1; ++n)
			for(int m=-1; m<=1; ++m)
			{
				if(x+m<0 || x+m>=tiles.length || y+n<0 || y+n>=tiles[0].length)
					continue;
				
				if(tiles[x+m][y+n]==null)
					continue;
				
				neighbors.add(tiles[x+m][y+n]);
			}
		
		return neighbors;
	}
	
	private static Tile[][] generateTiles(int width, int height)
	{
		Tile [][] tiles = new Tile[width][height];
		
		for(int y=0;y<height;++y)
			for(int x=0;x<width; ++x)
			{
				tiles[x][y] = generateTile(getNeighbors(tiles,x,y));
			}
		
		return tiles;
	}
	
	public static WorldMap generateMap(int width, int height)
	{
		Tile [][] tiles = generateTiles(width,height);
		Building [][] buildings = new Building[width][height];
		
		return new WorldMap(tiles, buildings);
	}
}
