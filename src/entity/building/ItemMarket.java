package entity.building;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import entity.unit.Item;

public abstract class ItemMarket extends Market
{
	private final Map<Item.Object, Integer> supply;
	public ItemMarket(int id, Color color, int xPos, int yPos, int completionVal) 
	{
		super(id, color, xPos, yPos, completionVal, ItemType.ITEM);
		this.supply = new HashMap<Item.Object, Integer>();
	}

	public int getSupply(Item.Object obj)
	{
		if(!supply.containsKey(obj))
			return 0;
		
		return supply.get(obj);
	}
	
	public void addSupply(Item.Object obj, int quantity)
	{
		supply.replace(obj, supply.get(obj)+quantity);
	}
	
	public boolean hasSupply(Item.Object obj, int quantity)
	{
		if(!supply.containsKey(obj))
			return false;
		
		if(supply.get(obj)<quantity)
			return false;
		
		return true;
	}
	
	public boolean hasSupply(Item.Object obj)
	{
		if(!supply.containsKey(obj))
			return false;
		
		return true;
	}
	
	public boolean removeSupply(Item.Object obj, int quantity)
	{
		if(!hasSupply(obj,quantity))
			return false;
		
		supply.replace(obj, supply.get(obj)-quantity);
		return true;
	}
	
	protected void addItem(Item.Object obj)
	{
		this.supply.put(obj, 0);
	}
}
