package game;

import java.util.Map;

import entity.WorldMap;
import entity.building.Building;
import entity.building.Building.Type;
import entity.building.FoodMarket;
import entity.building.ItemMarket;
import entity.building.Market;
import entity.building.Market.ItemType;
import entity.building.OwnableBuilding;
import entity.unit.Family;
import entity.unit.Human.Job;
import entity.unit.Item;

public class GameData 
{
	private final WorldMap map;
	private double thirstLevel = 1100;
	private double hungerLevel = 1100;
	private double energyLevel = 1050;
	private double humanEnergyPerTick = 0.05;
	private double humanThirstPerTick = 0.4;
	private double humanHungerPerTick = 0.1;
	private int population = 0;
	//discoveries
	private boolean waterFlask;
	
	public GameData(WorldMap map)
	{
		super();
		this.map = map;
	}
	
	public void updatePopulation()
	{
		this.population = map.getHumans().size();
	}
	
	public double getLumberjackNeed() 
	{
		int lumberjacks = map.getHumanWithJob(Job.LUMBERJACK).size();
		
		
		return population - lumberjacks*10;
		
	}
	
	public double getMinerNeed() 
	{
		int miners = map.getHumanWithJob(Job.MINER).size();
		
		
		return population - miners*10;
	}

	public double getCraftmanNeed() 
	{
		int craftmen = map.getHumanWithJob(Job.CRAFTMAN).size();
		int miners = map.getHumanWithJob(Job.MINER).size();
		int lumberjack = map.getHumanWithJob(Job.LUMBERJACK).size();
		
		return miners + lumberjack - craftmen*4;
	}

	public double getFarmNeed() 
	{
		int farmers = map.getHumanWithJob(Job.FARMER).size();
		int farms = map.getBuildingWithType(Type.FARM).size();
		
		return farmers - farms;
	}

	public double getFarmerNeed() 
	{
		int pop = population;
		int farmers = map.getHumanWithJob(Job.FARMER).size();
		
		
		return pop/5d - farmers + 5 - getFoodSupplyPerCapita()/2;
	}
	
	public double getBuilderNeed()
	{
		double val;
		int nbIncBuildings = map.getIncompletedBuildings().size();
		int nbBuilders = map.getHumanWithJob(Job.BUILDER).size();
		int population = map.getHumans().size();
		
		if(nbBuilders/(double)population<0.05)
			val = 1;
		else
			val = nbIncBuildings - nbBuilders*4;
		
		return val;
	}
	
	public double getHouseNeed()
	{
		double val;
		int nbEmptyHouse = map.getEmptyHouses().size();
		int nbIncompletedHouse = map.getIncompletedBuildingWithType(Type.HOUSE).size();
		int nbHomelessFamily = map.getHomelessFamilies().size();
		
		val = nbHomelessFamily - nbEmptyHouse - nbIncompletedHouse + 1 + population/10;
		return val;
	}
	
	public double getEnergyLevel() {
		return energyLevel;
	}

	public double getThirstLevel() {
		return thirstLevel;
	}
	public double getHungerLevel() {
		return hungerLevel;
	}
	public double getHumanEnergyPerTick() {
		return humanEnergyPerTick;
	}
	public double getHumanThirstPerTick() {
		return humanThirstPerTick;
	}
	public double getHumanHungerPerTick() {
		return humanHungerPerTick;
	}


	public boolean isWaterFlask() {
		return waterFlask;
	}

	public void setWaterFlask(boolean waterFlask) {
		this.waterFlask = waterFlask;
	}

	public WorldMap getMap() {
		return map;
	}

	public void setThirstLevel(double thirstLevel) {
		this.thirstLevel = thirstLevel;
	}

	public void setHungerLevel(double hungerLevel) {
		this.hungerLevel = hungerLevel;
	}

	public void setEnergyLevel(double energyLevel) {
		this.energyLevel = energyLevel;
	}

	public void setHumanEnergyPerTick(double humanEnergyPerTick) {
		this.humanEnergyPerTick = humanEnergyPerTick;
	}

	public void setHumanThirstPerTick(double humanThirstPerTick) {
		this.humanThirstPerTick = humanThirstPerTick;
	}

	public void setHumanHungerPerTick(double humanHungerPerTick) {
		this.humanHungerPerTick = humanHungerPerTick;
	}

	public double getFoodMarketNeed() {
		int pop = population;
		int foodMarkets = 0;
		
		Map<Integer,Building> b = map.getBuildingWithType(Type.MARKET);
		for(Map.Entry<Integer, Building> e : b.entrySet())
			if(((Market)(e.getValue())).getItemType()==ItemType.FOOD)
				++foodMarkets;
		
		return pop - foodMarkets*25;
	}
	
	public double getCraftmanMarketNeed() {
		int craftmanMarkets = 0;
		int craftmen = map.getHumanWithJob(Job.CRAFTMAN).size();
		
		Map<Integer,Building> b = map.getBuildingWithType(Type.MARKET);
		for(Map.Entry<Integer, Building> e : b.entrySet())
			if(((Market)(e.getValue())).getItemType()==ItemType.ITEM)
				if(((ItemMarket)(e.getValue())).hasSupply(Item.Object.EMPTY_WATER_FLASK))
					++craftmanMarkets;
		
		return craftmen - craftmanMarkets*25;
	}
	
	public double getJoblessPourcentage()
	{
		double pop = map.getHumans().size();
		double jobless = map.getJoblessCount();
		return jobless/pop * 100;
	}
	
	public double getAverageFamilyWealth()
	{
		double val = 0;
		for(Map.Entry<Integer, Family> e : map.getFamilies().entrySet())
		{
			Family f = e.getValue();
			val += f.getMoney();
			val += f.getFood()*5;
			
			for(Map.Entry<Integer, OwnableBuilding> eb : f.getBuildings().entrySet())
				val+=eb.getValue().getPrice();
				
		}
		return val / map.getFamilies().size();
	}
	
	public int getFoodMarketsSupply()
	{
		int val = 0;
		for(Map.Entry<Integer, Building> e : map.getCompletedBuildingWithType(Type.MARKET).entrySet())
		{
			if(((Market)e.getValue()).getItemType()!=ItemType.FOOD)
				continue;
			FoodMarket fm = (FoodMarket) e.getValue();
			
			val += fm.getSupply();
		}
		
		return val;
	}
	
	public int getFamiliesFoodSupply()
	{
		int val = 0;
		for(Map.Entry<Integer, Family> e : map.getFamilies().entrySet())
		{
			Family f = e.getValue();
			val += f.getFood();
		}
		return val;
	}
	
	public double getFoodSupplyPerCapita()
	{
		int val = 0;
		val += getFamiliesFoodSupply();
		val += getFoodMarketsSupply();
		
		return (double)(val)/population;
	}

	public int getPopulation() {
		return population;
	}

	public double getWoodMarketNeed() 
	{
		int lumbejacks = map.getHumanWithJob(Job.LUMBERJACK).size();
		int woodMarkets = 0;
		
		Map<Integer,Building> b = map.getBuildingWithType(Type.MARKET);
		for(Map.Entry<Integer, Building> e : b.entrySet())
			if(((Market)(e.getValue())).getItemType()==ItemType.ITEM)
				if(((ItemMarket)e.getValue()).hasSupply(Item.Object.CLASSIC_WOOD))
					++woodMarkets;
		
		return lumbejacks - woodMarkets*15;
	}
	
	public double getStoneMarketNeed() 
	{
		int miners = map.getHumanWithJob(Job.MINER).size();
		int stoneMarkets = 0;
		
		Map<Integer,Building> b = map.getBuildingWithType(Type.MARKET);
		for(Map.Entry<Integer, Building> e : b.entrySet())
			if(((Market)(e.getValue())).getItemType()==ItemType.ITEM)
				if(((ItemMarket)e.getValue()).hasSupply(Item.Object.STONE))
					++stoneMarkets;
		
		return miners - stoneMarkets*15;
	}
	
}
