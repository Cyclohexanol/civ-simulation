package utility.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import entity.WorldMap;
import entity.building.*;
import entity.tile.Tile.Material;
import entity.unit.*;
import entity.unit.Human.Job;
import entity.unit.Human.Sex;
import game.GameData;
import utility.ai.Action.Type;
import utility.factory.HumanGenerator;
import utility.pathfinder.Path;
import utility.pathfinder.PathFinder;
import utility.pathfinder.Point;
import utility.tools.TickCounter;

public class HumanAction 
{
	private final PathFinder pf;
	private final WorldMap map;
	private final TickCounter ticks;
	private final GameData data;
	private final HumanGenerator humGen;
	
	public HumanAction(WorldMap map, TickCounter ticks, GameData data) {
		super();
		this.pf = new PathFinder(map);
		this.map = map;
		this.ticks = ticks;
		this.data = data;
		humGen = new HumanGenerator(map, data, ticks);
	}

	//perform
	public void performAllActions()
	{
		Map<Integer, Human> h = map.getHumans();
		for(Map.Entry<Integer, Human> e : h.entrySet())
			performAction(e.getValue());
	}
	
	public void performAction(Human unit)
	{
		live(unit);
		
		if(isIdle(unit))
			return;
		
		if(!unit.isMaried()&&unit.getAge()>=20&&unit.getSex()==Sex.MALE)
			tryToMarry(unit);
		
		if(unit.isMaried()&&unit.getSex()==Sex.FEMALE&&unit.getAge()<35&&Math.random()<0.0001&&unit.getFamily().getMembers().size()<unit.getFamily().getHousesCapacity())
			goGiveBirth(unit);
		
		if(unit.getFamily().getHouses().isEmpty()||unit.getFamily().getMembers().size()>unit.getFamily().getHousesCapacity()*0.75)
			tryToBuyHouse(unit);
		
		if(unit.getFamily().getHouses().size()>1||unit.getFamily().getMembers().size()<unit.getFamily().getHousesCapacity()*0.5||Math.random()<0.1)
			tryToSellHouse(unit);
		
		if(unit.getJob()==Job.FARMER&&unit.getFamily().getFarms().isEmpty())
			tryToBuyFarm(unit);
		
		if(unit.getAction()==null)
			selectAction(unit);

		if(canPerformAction(unit, Type.EAT))
		{
			unit.addHunger(100);
			setIdleFor(unit, 10);
			if(unit.getHunger()>2000)
				unit.setAction(null);
			return;
		}
		if(canPerformAction(unit, Type.SLEEP_INSIDE))
		{
			unit.addEnergy(15);
			setIdleFor(unit, 10);
			if(unit.getEnergy()>2000)
				unit.setAction(null);
			return;
		}
		if(canPerformAction(unit, Type.SLEEP_OUTSIDE))
		{
			unit.addEnergy(7);
			setIdleFor(unit, 10);
			if(unit.getEnergy()>2000)
				unit.setAction(null);
			return;
		}
		if(canPerformAction(unit, Type.DRINK))
		{
			unit.addThirst(400);
			setIdleFor(unit, 10);
			if(unit.getThirst()>2000)
			{
				unit.setAction(null);
				if(unit.hasItem(Item.Object.FULL_WATER_FLASK))
				{
					unit.removeItem(Item.Object.FULL_WATER_FLASK,1);
					unit.addItem(Item.Object.EMPTY_WATER_FLASK, 1);
				}
				if(map.getClosestDrinkableTile(unit.getPos()).distance(unit.getPos())==0)
					while(unit.removeItem(Item.Object.EMPTY_WATER_FLASK, 1))
						unit.addItem(Item.Object.FULL_WATER_FLASK, 1);
			}
			return;
		}
		if(canPerformAction(unit, Type.GIVE_BIRTH))
		{
			humGen.generateChild(unit);
			setIdleFor(unit, 400);
			unit.setAction(null);
			return;
		}
		
		if(!(unit.getActionType()==Type.DRINK
				||unit.getActionType()==Type.SLEEP_OUTSIDE
				||unit.getActionType()==Type.SLEEP_INSIDE
				||unit.getActionType()==Type.EAT))
			if(!checkVitals(unit))
				return;
		
		if(canPerformAction(unit, Type.WALK))
		{
			unit.setAction(null);
			return;
		}
		
		if(canPerformAction(unit, Type.BUILD))
		{
			Building b = map.getBuildingOnTile(unit.getxPos(), unit.getyPos());
			if(b == null||b.isFinished())
				unit.setAction(null);
			b.addCompletion(2);
			unit.addMoney(1);
			setIdleFor(unit, 50);
			return;
		}
		if(canPerformAction(unit, Type.FARM))
		{
			if(Math.random()<0.05)
				unit.addFood(1);
			setIdleFor(unit, 40);
		}
		if(canPerformAction(unit, Type.MINE))
		{
			double minVal = 0.07;
			if(unit.hasItem(Item.Object.PICKAXE_STONE))
				minVal = 0.1;
			if(Math.random()<minVal)
				unit.addItem(Item.Object.STONE, 1);;
			setIdleFor(unit, 40);
		}
		if(canPerformAction(unit, Type.CHOP_WOOD))
		{
			double minVal = 0.1;
			if(unit.hasItem(Item.Object.AXE_STONE))
				minVal = 0.15;
			if(Math.random()<minVal)
				unit.addItem(Item.Object.CLASSIC_WOOD, 1);;
			setIdleFor(unit, 40);
		}
		if(canPerformAction(unit, Type.BUY_FOOD))
		{
			FoodMarket fm = (FoodMarket) map.getBuildingOnTile(unit.getxPos(),unit.getyPos());
			if(unit.getFood()/unit.getFamily().getMembers().size()>4||unit.removeMoney(fm.getItemBuyPrice())||!fm.removeSupply(1))
			{
				unit.setAction(null);
				return;
			}
			unit.addFood(1);
			setIdleFor(unit, 12);
		}
		if(canPerformAction(unit, Type.SELL_FOOD))
		{
			FoodMarket fm = (FoodMarket) map.getBuildingOnTile(unit.getxPos(),unit.getyPos());
			if(unit.getFood()/unit.getFamily().getMembers().size()<=6)
			{
				unit.setAction(null);
				return;
			}
			unit.removeFood(1);
			unit.addMoney(fm.getItemSellPrice());
			fm.addSupply(1);
			setIdleFor(unit, 12);
		}
		if(canPerformAction(unit, Type.SELL_WOOD))
		{
			ItemMarket m = (ItemMarket) map.getBuildingOnTile(unit.getxPos(),unit.getyPos());
			if(unit.getItemQuantity(Item.Object.CLASSIC_WOOD)==0)
			{
				unit.setAction(null);
				return;
			}
			if(unit.removeItem(Item.Object.CLASSIC_WOOD, 1))
			{
				unit.addMoney((int) (Item.Object.CLASSIC_WOOD.getValue()*0.8));
				m.addSupply(Item.Object.CLASSIC_WOOD, 1);
			}
			setIdleFor(unit, 12);
		}
		if(canPerformAction(unit, Type.SELL_STONE))
		{
			ItemMarket m = (ItemMarket) map.getBuildingOnTile(unit.getxPos(),unit.getyPos());
			if(unit.getItemQuantity(Item.Object.STONE)==0)
			{
				unit.setAction(null);
				return;
			}
			if(unit.removeItem(Item.Object.STONE, 1))
			{
				unit.addMoney((int) (Item.Object.STONE.getValue()*0.8));
				m.addSupply(Item.Object.STONE, 1);
			}
			setIdleFor(unit, 12);
		}
		if(canPerformAction(unit, Type.BUY_CRAFT_SUPPLY))
		{
			ItemMarket m = (ItemMarket)map.getBuildingOnTile(unit.getxPos(),unit.getyPos());
			if(unit.getMoney()<20||unit.getNeededItems().isEmpty())
				unit.setAction(null);
			List<ItemStack> stacks = unit.getNeededItems();
			List<ItemStack> itemsBought = new ArrayList<ItemStack>();
			for(int i=0; i<stacks.size(); ++i)
			{
				ItemStack s = stacks.get(i);
				if(m.hasSupply(s.getObject(), s.getQuantity())&&unit.getMoney()-30>s.getQuantity()*s.getValue())
				{
					m.removeSupply(s.getObject(), s.getQuantity());
					unit.removeMoney(s.getQuantity()*s.getValue());
					unit.addItem(s);
					itemsBought.add(s);
				}
			}
			for(int j=0;j<itemsBought.size();++j)
				unit.getNeededItems().remove(itemsBought.get(j));
			setIdleFor(unit, 30);
			unit.setAction(null);
		}
		if(canPerformAction(unit, Type.CRAFT))
		{
			Item.Object [] items = Item.Object.values(); 
			for(int i=0; i<items.length; ++i)
			{
				if(unit.canCraft(items[i])
						&&map.getItemsInMarkets(items[i])+unit.getItemQuantity(items[i])<=10)
				{
					unit.craft(items[i]);
					setIdleFor(unit, items[i].getRecipe().getCraftTime());
					return;
				}
			}
			unit.setAction(null);
			
		}
		if(canPerformAction(unit, Type.SELL_CRAFT_ITEMS))
		{
			ItemMarket m = (ItemMarket)map.getBuildingOnTile(unit.getxPos(),unit.getyPos());
			List<ItemStack> itemsSold = new ArrayList<ItemStack>();
			Map<Item.Object,Integer> items = unit.getItems();
			for(Map.Entry<Item.Object, Integer> e : items.entrySet())
			{
				if(!unit.ableToCraft(e.getKey())||!m.hasSupply(e.getKey()))
					continue;
				ItemStack s = new ItemStack(e.getKey(),e.getValue());
				m.addSupply(s.getObject(), s.getValue());
				unit.addMoney((int) (s.getValue()*s.getQuantity()*0.8));
				itemsSold.add(s);
			}
			for(int i=0; i<itemsSold.size(); ++i)
				unit.removeItemStack(itemsSold.get(i));
		}
		move(unit);
	}

	//action select
	private boolean checkVitals(Human unit)
	{
		if(unit.getThirst()<data.getThirstLevel())
		{
			goDrink(unit);
			return false;
		}
		if(unit.getHunger()<data.getHungerLevel())
		{
			goEat(unit);
			return false;
		}
		if(unit.getEnergy()<data.getEnergyLevel())
		{
			goSleep(unit);
			return false;
		}
		return true;
	}
	
	public void selectAction(Human unit)
	{
		if(!checkVitals(unit))
			return;
		
		if(unit.getAge()>12)
			if(unit.getFamily().getFood()/unit.getFamily().getMembers().size()<3&&unit.getMoney()>10)
				goBuyFood(unit);
			
		if((unit.getFamily().getFood()-10)/unit.getFamily().getMembers().size()>5&&unit.getJob()==Job.FARMER)
				goSellFood(unit);	
		
		tryToSellDisposableGoods(unit);
		
		if(unit.getAction()!=null)
			return;
		
		if(unit.getJob()==null&&unit.getAge()>17)
			selectJob(unit);
		
		if(unit.getJob()!=null)
			goWork(unit);
		
		if(unit.getJob()==null)
			walkToClosestHouse(unit);
			
	}

	public void selectJob(Human unit)
	{
		if(data.getFarmerNeed()>0)
		{
			unit.setJob(Job.FARMER);
			return;
		}
		if(data.getBuilderNeed()>0)
		{
			unit.setJob(Job.BUILDER);
			return;
		}
		if(data.getLumberjackNeed()>0)
		{
			unit.setJob(Job.LUMBERJACK);
			return;
		}
		if(data.getMinerNeed()>0)
		{
			unit.setJob(Job.MINER);
			return;
		}
		if(data.getCraftmanNeed()>0)
		{
			unit.setJob(Job.CRAFTMAN);
			return;
		}
		
	}
	
	//actions setters
	public void live(Human unit)
	{
		unit.removeEnergy(data.getHumanEnergyPerTick());
		unit.removeHunger(data.getHumanHungerPerTick());
		unit.removeThirst(data.getHumanThirstPerTick());
	}
	
	private void goGiveBirth(Human unit)
	{
		Point p = getClosestHouseLocation(unit);
		
		if(p==null)
			return;
		unit.setAction(new Action(p,pf.findPath(unit.getPos(), p),Action.Type.GIVE_BIRTH));
	}
	
	private void goWork(Human unit)
	{
		if(unit.getJob()==Job.BUILDER)
		{
			goBuild(unit);
			return;
		}
		if(unit.getJob()==Job.FARMER)
		{
			goFarm(unit);
			return;
		}
		if(unit.getJob()==Job.MINER)
		{
			goMine(unit);
			return;
		}
		if(unit.getJob()==Job.LUMBERJACK)
		{
			goChopWood(unit);
			return;
		}
		if(unit.getJob()==Job.CRAFTMAN)
		{
			Map<Item.Object,Integer> items = unit.getItems();
			for(Map.Entry<Item.Object, Integer> e : items.entrySet())
			{
				if(unit.ableToCraft(e.getKey()))
				{
					goSellCraftItems(unit,e.getKey());
					return;
				}
					
			}
		}
		if(unit.getJob()==Job.CRAFTMAN)
		{
			Item.Object [] items = Item.Object.values(); 
			for(int i=0; i<items.length; ++i)
			{
				if(unit.canCraft(items[i])
						&&map.getItemsInMarkets(items[i])+unit.getItemQuantity(items[i])<=10)
				{
					goCraftItems(unit);
					return;
				}
			}
		}
		if(unit.getJob()==Job.CRAFTMAN)
		{
			goBuyCraftSupply(unit);
		}
	}
	
	private void goBuyCraftSupply(Human unit) 
	{
		if(unit.getNeededItems()==null||unit.getNeededItems().isEmpty())
		{
			List<ItemStack> neededItems = new ArrayList<ItemStack>();
			int itemVal = 0;
			Item.Object [] items = Item.Object.values(); 
			for(int i=0; i<items.length; ++i)
			{
				if(unit.canCraft(items[i])
						&&map.getItemsInMarkets(items[i])+unit.getItemQuantity(items[i])<=10)
				{
					int compPrice = 0;
					List<ItemStack> components = items[i].getRecipe().getComponents();
					for(int j=0; j<components.size(); ++j)
						compPrice += components.get(j).getQuantity()*components.get(j).getValue();
					if(unit.getMoney()-30>compPrice+itemVal)
					{
						itemVal += compPrice;
						for(int j=0; j<components.size(); ++j)
							neededItems.add(components.get(i));
					}
						
				}
			}
			if(!neededItems.isEmpty())
			{
				unit.setNeededItems(neededItems);
			}
		}
		if(unit.getNeededItems()!=null&&!unit.getNeededItems().isEmpty())
		{
			Point p = getClosestMarketLocation(unit, unit.getNeededItems().get(0).getObject());
			unit.setAction(new Action(p,pf.findPath(unit.getPos(), p), Action.Type.CRAFT));
		}
	}

	private void goCraftItems(Human unit) 
	{
		Point p = getClosestHouseLocation(unit);
		if(p==null)
			unit.setAction(new Action(unit.getPos(),pf.findPath(unit.getPos(), unit.getPos()), Action.Type.CRAFT));
		unit.setAction(new Action(p,pf.findPath(unit.getPos(), p), Action.Type.CRAFT));
	}

	private void goSellCraftItems(Human unit, Item.Object item) 
	{
		Point p = getClosestMarketLocation(unit, item);
		if(p==null)
			return;
		unit.setAction(new Action(p,pf.findPath(unit.getPos(), p), Action.Type.SELL_CRAFT_ITEMS));
	}

	private void goChopWood(Human unit) 
	{
		Point p = map.getClosestTileOfMaterial(unit.getPos(), Material.FOREST);
		if(p==null)
			return;
		 unit.setAction(new Action(p,pf.findPath(unit.getPos(), p),Action.Type.CHOP_WOOD));
	}

	private void goMine(Human unit) 
	{
		Point p = map.getClosestTileOfMaterial(unit.getPos(), Material.ROCK);
		if(p==null)
			return;
		 unit.setAction(new Action(p,pf.findPath(unit.getPos(), p),Action.Type.MINE));
	}

	private void goBuyFood(Human unit)
	{
		Point p = getClosestNoneEmptyFoodMarketLocation(unit);
		if(p==null)
			return;
		 unit.setAction(new Action(p,pf.findPath(unit.getPos(), p),Action.Type.BUY_FOOD));
	}
	
	private void goSellFood(Human unit)
	{
		Point p = getClosestFoodMarketLocation(unit);
		if(p==null)
			return;
		 unit.setAction(new Action(p,pf.findPath(unit.getPos(), p),Action.Type.SELL_FOOD));
	}
	
	private void goSellWood(Human unit)
	{
		Point p = getClosestMarketLocation(unit,Item.Object.CLASSIC_WOOD);
		if(p==null)
			return;
		 unit.setAction(new Action(p,pf.findPath(unit.getPos(), p),Action.Type.SELL_WOOD));
	}
	
	private void goSellStone(Human unit)
	{
		Point p = getClosestMarketLocation(unit,Item.Object.STONE);
		if(p==null)
			return;
		 unit.setAction(new Action(p,pf.findPath(unit.getPos(), p),Action.Type.SELL_STONE));
	}
	
	private void goFarm(Human unit) 
	{
		Point p = getClosestFarmLocation(unit);
		
		if(p==null)
		{
			walkToClosestHouse(unit);
			return;
		}
		
		unit.setAction(new Action(p,pf.findPath(unit.getPos(), p),Action.Type.FARM));
		
	}

	private void goBuild(Human unit)
	{
		Point p = getClosestUnfinishedBuilding(unit);
		
		if(p==null)
		{
			walkToClosestHouse(unit);
			return;
		}
		
		unit.setAction(new Action(p,pf.findPath(unit.getPos(), p),Action.Type.BUILD));	
	}
	
	public void goSleep(Human unit)
	{
		Point p = getClosestHouseLocation(unit);
		if(p==null)
			unit.setAction(new Action(unit.getPos(), pf.findPath(unit.getPos(), unit.getPos()), Action.Type.SLEEP_OUTSIDE));
		else
			unit.setAction(new Action(p, pf.findPath(unit.getPos(), p), Action.Type.SLEEP_INSIDE));
	}
	
	public void goDrink(Human unit)
	{
		Point p;
		
		if(unit.hasItem(Item.Object.FULL_WATER_FLASK))
		{
			p = unit.getPos();
			unit.setAction(new Action(p, pf.findPath(unit.getPos(), p), Action.Type.DRINK));
		}

		p = getClosestDrinkableTile(unit);
		if(p==null)
			return;
		
		unit.setAction(new Action(p, pf.findPath(unit.getPos(), p), Action.Type.DRINK));
	}
	
	public void goEat(Human unit)
	{
		if(unit.removeFood(1))
		{
			Point p = unit.getPos();
			unit.setAction(new Action(p,pf.findPath(p, p),Action.Type.EAT));
		}
	}
	
	public void move(Human unit)
	{
		if(isIdle(unit))
			return;
		
		if(unit.getPath()==null||unit.checkAtActionLocation())
			return;
		
		Point nextPos = unit.getPath().getNextStep();
		
		unit.setPos(nextPos);
		setIdleFor(unit, 10);
	}

	public void walkTo(Human unit, Point p)
	{
		unit.setAction(new Action(p, pf.findPath(unit.getPos(), p), Action.Type.WALK));
	}
	
	public void walkTo(Human unit, int x, int y)
	{
		unit.setAction(new Action(new Point(x,y), pf.findPath(unit.getxPos(), unit.getyPos(), x, y), Action.Type.WALK));
	}

	public void walkToClosestHouse(Human unit)
	{
		Point p = getClosestHouseLocation(unit);
		if(p==null)
			return;
		
		unit.setAction(new Action(p, pf.findPath(unit.getPos(), p), Action.Type.WALK));
	}
	
	//utilities
	private boolean isIdle(Human unit)
	{
		if(unit.getIdleTickCount()+unit.getLastActionTickCount()>=ticks.getTickCount())
			return true;
		return false;
	}

	private boolean canPerformAction(Human unit, Action.Type type)
	{
		if(unit.getAction()==null)
			return false;
		if(unit.checkAtActionLocation()&&unit.getActionType()==type)
			return true;
		return false;
	}
	
	private void tryToSellDisposableGoods(Human unit) 
	{
		if(unit.getJob()==Job.MINER&&unit.getItemQuantity(Item.Object.STONE)>20)
			goSellStone(unit);
		if(unit.getJob()==Job.LUMBERJACK&&unit.getItemQuantity(Item.Object.CLASSIC_WOOD)>30)
			goSellWood(unit);
	}
	
	private Point getClosestDrinkableTile(Human unit)
	{
		return map.getClosestDrinkableTile(unit.getPos());
	}
	
	private Point getClosestHouseLocation(Human unit)
	{
		Map<Integer,OwnableBuilding> b = unit.getFamily().getBuildings();
		if(b.size()==0)
			return null;
		Point hp = unit.getPos();
		Point sp = null;
		int shortestPathLength = Integer.MAX_VALUE;
		
		for(Map.Entry<Integer, OwnableBuilding> e : b.entrySet())
		{
			OwnableBuilding bu = e.getValue();
			if(bu.getType()!=Building.Type.HOUSE)
				continue;
			if(unit.getFamily().getHouses().containsKey(e.getKey()))
				return null;
			for(int i=0; i<bu.getHeight(); ++i)
				for(int j=0; j<bu.getWidth();++j)
				{
					int x = j + bu.getxPos();
					int y = i + bu.getyPos();
					Point cp =  new Point(x,y);
					Path path = pf.findPath(hp, cp);
					if(path==null)
						continue;
					int length = path.getLength();
					if(length<shortestPathLength)
					{
						shortestPathLength = length;
						sp = cp;
					}
				}
		}
		
		return sp;
	}
	
	private Point getClosestFarmLocation(Human unit)
	{
		Map<Integer,OwnableBuilding> b = unit.getFamily().getBuildings();
		if(b.size()==0)
			return null;
		Point hp = unit.getPos();
		Point sp = null;
		int shortestPathLength = Integer.MAX_VALUE;
		
		for(Map.Entry<Integer, OwnableBuilding> e : b.entrySet())
		{
			OwnableBuilding bu = e.getValue();
			if(bu.getType()!=Building.Type.FARM)
				continue;
			for(int i=0; i<bu.getHeight(); ++i)
				for(int j=0; j<bu.getWidth();++j)
				{
					int x = j + bu.getxPos();
					int y = i + bu.getyPos();
					Point cp =  new Point(x,y);
					Path path = pf.findPath(hp, cp);
					if(path==null)
						continue;
					int length = path.getLength();
					if(length<shortestPathLength)
					{
						shortestPathLength = length;
						sp = cp;
					}
				}
		}
		
		return sp;
	}
	
	private Point getClosestFoodMarketLocation(Human unit)
	{
		Map<Integer,Building> b = map.getCompletedBuildingWithType(Building.Type.MARKET);
		if(b.size()==0)
			return null;
		Point hp = unit.getPos();
		Point sp = null;
		int shortestPathLength = Integer.MAX_VALUE;
		
		for(Map.Entry<Integer, Building> e : b.entrySet())
		{
			Market bu = (Market)e.getValue();
			if(bu.getItemType()!=Market.ItemType.FOOD)
				continue;
			
			for(int i=0; i<bu.getHeight(); ++i)
				for(int j=0; j<bu.getWidth();++j)
				{
					int x = j + bu.getxPos();
					int y = i + bu.getyPos();
					Point cp =  new Point(x,y);
					Path path = pf.findPath(hp, cp);
					if(path==null)
						continue;
					int length = path.getLength();
					if(length<shortestPathLength)
					{
						shortestPathLength = length;
						sp = cp;
					}
				}
		}
		
		return sp;
	}
	
	private Point getClosestMarketLocation(Human unit, Item.Object obj)
	{
		Map<Integer,Building> b = map.getCompletedBuildingWithType(Building.Type.MARKET);
		if(b.size()==0)
			return null;
		Point hp = unit.getPos();
		Point sp = null;
		int shortestPathLength = Integer.MAX_VALUE;
		
		for(Map.Entry<Integer, Building> e : b.entrySet())
		{
			Market m= (Market)e.getValue();
			if(m.getItemType()!=Market.ItemType.ITEM)
				continue;
			ItemMarket im = (ItemMarket) m;
			if(!im.hasSupply(obj))
				continue;
			
			for(int i=0; i<im.getHeight(); ++i)
				for(int j=0; j<im.getWidth();++j)
				{
					int x = j + im.getxPos();
					int y = i + im.getyPos();
					Point cp =  new Point(x,y);
					Path path = pf.findPath(hp, cp);
					if(path==null)
						continue;
					int length = path.getLength();
					if(length<shortestPathLength)
					{
						shortestPathLength = length;
						sp = cp;
					}
				}
		}
		
		return sp;
	}
	
	private Point getClosestNoneEmptyFoodMarketLocation(Human unit)
	{
		Map<Integer,Building> b = map.getCompletedBuildingWithType(Building.Type.MARKET);
		if(b.size()==0)
			return null;
		Point hp = unit.getPos();
		Point sp = null;
		int shortestPathLength = Integer.MAX_VALUE;
		
		for(Map.Entry<Integer, Building> e : b.entrySet())
		{
			Market bu = (Market)e.getValue();
			if(bu.getItemType()!=Market.ItemType.FOOD)
				continue;
			if(((FoodMarket)bu).getSupply()<1)
				continue;
			
			for(int i=0; i<bu.getHeight(); ++i)
				for(int j=0; j<bu.getWidth();++j)
				{
					int x = j + bu.getxPos();
					int y = i + bu.getyPos();
					Point cp =  new Point(x,y);
					Path path = pf.findPath(hp, cp);
					if(path==null)
						continue;
					int length = path.getLength();
					if(length<shortestPathLength)
					{
						shortestPathLength = length;
						sp = cp;
					}
				}
		}
		
		return sp;
	}
	
	private Point getClosestUnfinishedBuilding(Human unit)
	{
		Map<Integer,Building> b = map.getBuildings();
		if(b.size()==0)
			return null;
		Point hp = unit.getPos();
		Point sp = null;
		int shortestPathLength = Integer.MAX_VALUE;
		
		for(Map.Entry<Integer, Building> e : b.entrySet())
		{
			Building bu = e.getValue();
			if(bu.isFinished())
				continue;
			for(int i=0; i<bu.getHeight(); ++i)
				for(int j=0; j<bu.getWidth();++j)
				{
					int x = j + bu.getxPos();
					int y = i + bu.getyPos();
					Point cp =  new Point(x,y);
					Path path = pf.findPath(hp, cp);
					if(path==null)
						continue;
					int length = path.getLength();
					if(bu.getType()==Building.Type.FARM)
						length /= 6;
					if(bu.getType()==Building.Type.MARKET)
						length /= 3;
					
					if(length<shortestPathLength)
					{
						shortestPathLength = length;
						sp = cp;
					}
				}
		}
		return sp;
	}
	
	private void setIdleFor(Human unit, int time)
	{
		unit.setIdleTickCount(time);
		unit.setLastActionTickCount(ticks.getTickCount());
	}
	
	private void tryToBuyHouse(Human unit)
	{
		double bestVal = 0;
		House bestHouse = null;
		
		for(Map.Entry<Integer, House> e : map.getEmptyHouses().entrySet())
		{
			House h = e.getValue();
			if(unit.getMoney()+75<h.getPrice())
				continue;
			double val = getHouseValue(unit, h);
			
			
			if(bestVal<val)
			{
				bestVal = val;
				bestHouse = h;
			}
		}
		
		if(bestHouse!=null&&bestVal!=0)
		{
			unit.removeMoney(bestHouse.getPrice());
			bestHouse.setOwned(true);
			unit.getFamily().addBuilding(bestHouse);
			System.out.println(unit.getId() + " has bought a house of value " + bestVal + "!");
		}
	}
	
	private void tryToBuyFarm(Human unit)
	{
		double bestVal = 0;
		Farm bestFarm = null;
		
		for(Map.Entry<Integer, Building> e : map.getCompletedBuildingWithType(Building.Type.FARM).entrySet())
		{
			Farm f = (Farm)e.getValue();
			if(f.isOwned())
				continue;
			if(unit.getMoney()<f.getPrice())
				continue;
			Point hPos = f.getPos();
			double val = 0;
			
			val += 10/(map.getClosestDrinkableTile(hPos).distance(hPos)+1);
			
			for(Map.Entry<Integer, OwnableBuilding> ub : unit.getFamily().getBuildings().entrySet())
			{
				val += 50/(ub.getValue().getPos().distance(hPos)+1);
			}
			
			
			if(bestVal<val)
			{
				bestVal = val;
				bestFarm = f;
			}
		}
		
		if(bestFarm!=null&&bestVal!=0)
		{
			unit.removeMoney(bestFarm.getPrice());
			bestFarm.setOwned(true);
			unit.getFamily().addBuilding(bestFarm);
			System.out.println(unit.getId() + " has bought a farm of value " + bestVal + "!");
		}
	}
	
	private void marryUnits(Human husband, Human wife)
	{		
		if(wife.getFamily().getMembers().size()==1)
		{
			husband.addFood(wife.getFamily().getFood());
			husband.addMoney(wife.getFamily().getMoney());
			wife.removeFood(wife.getFood());
			wife.removeMoney(wife.getMoney());
		}
		wife.getFamily().removeMember(wife);
		wife.setFamily(husband.getFamily());
		husband.getFamily().addMember(wife);
		husband.setPartnerID(wife.getId());
		wife.setPartnerID(husband.getId());
	}
	
	private void tryToMarry(Human husband) {
		Map<Integer,Human> h = map.getHumans();
		for(Map.Entry<Integer, Human> e : h.entrySet())
		{
			Human wife = e.getValue();
			if(wife.getSex()!=Sex.FEMALE||wife.getAge()<20||wife.isMaried())
				continue;
			if(Math.abs(wife.getAge()-husband.getAge())>5)
				continue;
			if(Math.random()>0.01)
				continue;

			marryUnits(husband,wife);
			System.out.println(husband.getId() + " and " + wife.getId() + " got married!");
			return;

		}
	}
	
	private double getHouseValue(Human unit, House house)
	{
		Point hPos = house.getPos();
		double val = 0;
		
		val += house.getPrice()/100.0;
		val += 10/(map.getClosestDrinkableTile(hPos).distance(hPos)+1);
		val += house.getMaxNbInhabitants()*4;
		
		for(Map.Entry<Integer, OwnableBuilding> ub : unit.getFamily().getBuildings().entrySet())
		{
			if(ub.getValue().getType()==Building.Type.HOUSE)
				continue;
		
			val += 30/(ub.getValue().getPos().distance(hPos)+1);
			
		}
		
		for(Map.Entry<Integer, Building> ub : map.getBuildings().entrySet())
		{
			if(ub.getValue().getType()!=Building.Type.MARKET)
				continue;
			
			val += 30/(ub.getValue().getPos().distance(hPos)+1);
		}
		return val;
	}
	
	private void tryToSellHouse(Human unit)
	{
		double worstVal = Double.MAX_VALUE;
		House worstHouse = null;
		
		for(Map.Entry<Integer, House> e : unit.getFamily().getHouses().entrySet())
		{
			House hu = e.getValue();
			if((unit.getFamily().getHousesCapacity()-hu.getMaxNbInhabitants())*0.66<unit.getFamily().getMembers().size())
				continue;

			double val = getHouseValue(unit, hu);
			
			if(worstVal>val)
			{
				worstVal = val;
				worstHouse = hu;
			}
		}
		
		if(worstHouse!=null&&worstVal!=0)
		{
			unit.addMoney((int) (worstHouse.getPrice()*0.9));
			worstHouse.setOwned(false);
			unit.getFamily().removeBuilding(worstHouse);
			System.out.println(unit.getId() + " has sold a house of value " + worstVal + "!");
		}
	}
}
