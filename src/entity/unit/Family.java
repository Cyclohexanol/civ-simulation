package entity.unit;

import java.util.HashMap;
import java.util.Map;

import entity.building.Building;
import entity.building.Farm;
import entity.building.House;
import entity.building.OwnableBuilding;

public class Family 
{
	private final int id;
	private int money;
	private final Map<Integer,OwnableBuilding> buildings;
	private final Map<Integer,Human> members;
	private int food;
	
	public Family(int id, int money, int food) {
		super();
		this.id = id;
		this.money = money;
		this.food = food;
		this.buildings = new HashMap<Integer,OwnableBuilding>();
		this.members = new HashMap<Integer,Human>();
	}
	
	public Map<Integer, Human> getMembers() {
		return members;
	}
	
	public void addMember(Human h)
	{
		members.put(h.getId(), h);
		h.setFamily(this);
	}
	
	public void removeMember(int h)
	{
		members.remove(h);
	}
	
	public void removeMember(Human h)
	{
		members.remove(h.getId());
	}

	public int getHousesCapacity()
	{
		int cap = 0;
		for(Map.Entry<Integer, OwnableBuilding> e : buildings.entrySet())
		{
			Building b = e.getValue();
			if(b.getType()==Building.Type.HOUSE)
				cap += ((House)b).getMaxNbInhabitants();
		}
		
		return cap;
		
	}
	
	public Map<Integer,Farm> getFarms()
	{
		Map<Integer,Farm> h = new HashMap<Integer,Farm>();
		for(Map.Entry<Integer, OwnableBuilding> e : buildings.entrySet())
		{
			Building b = e.getValue();
			if(b.getType()==Building.Type.FARM)
				h.put(e.getKey(), (Farm)b);
		}
		
		return h;
	}
	
	public Map<Integer,House> getHouses()
	{
		Map<Integer,House> h = new HashMap<Integer,House>();
		for(Map.Entry<Integer, OwnableBuilding> e : buildings.entrySet())
		{
			Building b = e.getValue();
			if(b.getType()==Building.Type.HOUSE)
				h.put(e.getKey(), (House)b);
		}
		
		return h;
	}
	
	public int getId() {
		return id;
	}
	
	public int getMoney() {
		return money;
	}
	
	public void addMoney(int money)
	{
		this.money += money;
	}
	
	public boolean removeMoney(int money)
	{
		if(this.money<money)
			return false;
		
		this.money -= money;
		return true;
	}
	
	public Map<Integer,OwnableBuilding> getBuildings() {
		return buildings;
	}
	
	public void addBuilding(OwnableBuilding b)
	{
		buildings.put(b.getId(), b);
	}
	
	public void removeBuilding(OwnableBuilding b)
	{
		buildings.remove(b.getId());
	}
	
	public int getFood() {
		return food;
	}
	
	public void addFood(int quantity)
	{
		this.food += quantity;
	}
	
	public boolean removeFood(int quantity)
	{
		if(this.food<quantity)
			return false;
		
		this.food -= quantity;
		return true;
	}
}
