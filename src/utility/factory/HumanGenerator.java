package utility.factory;

import entity.WorldMap;
import entity.unit.Family;
import entity.unit.Human;
import entity.unit.HumanFemale;
import entity.unit.HumanMale;
import game.GameData;
import utility.pathfinder.Point;
import utility.tools.TickCounter;
import utility.tools.Utility;

public class HumanGenerator 
{
	private final WorldMap map;
	private final GameData data;
	private final TickCounter ticks;
	
	public HumanGenerator(WorldMap map, GameData data, TickCounter ticks) {
		super();
		this.map = map;
		this.data = data;
		this.ticks = ticks;
	}
	
	public void generateMigrant(int quantity)
	{
		for(int i=0; i<quantity; ++i)
		{
			if(5<data.getJoblessPourcentage()&&data.getPopulation()>5)
				return;
			
			int fID = Utility.generateValidFamilyID(map.getFamilies());
			int hID = Utility.generateValidUnitID(map.getUnits());
			Family  f = new Family(fID, 125, 5);
			Human h;
			int bday = (int) (Math.random()*10000);
			int age = (int) (Math.random()*12 + 18);
			if(Math.random()<0.5)
				h = new HumanFemale(hID,f,age,bday);
			else
				h = new HumanMale(hID,f,age,bday);
			
			Point pos = Utility.getRandomWalkableTileWithoutBuilding(map);
			h.setPos(pos);
			
			map.addFamily(f);
			map.addUnit(h);
			data.updatePopulation();
		}
	}
	
	public void generateChild(Human mother)
	{
		int hID = Utility.generateValidUnitID(map.getUnits());
		Family  f = mother.getFamily();
		Human h;
		int bday = (int) (ticks.getTickCount()%10000);
		if(Math.random()<0.5)
			h = new HumanFemale(hID,f,0,bday);
		else
			h = new HumanMale(hID,f,0,bday);
		Point pos = mother.getPos();
		h.setPos(pos);
		
		map.addFamily(mother.getFamily());
		map.addUnit(h);
		System.out.println(mother.getId() + " had a child !");
		data.updatePopulation();
	}
}
