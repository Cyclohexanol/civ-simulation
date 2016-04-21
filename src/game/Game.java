package game;

import javax.swing.JFrame;

import entity.WorldMap;
import entity.unit.Item;
import graphics.MapPanel;
import utility.ai.HumanAction;
import utility.factory.BuildingGenerator;
import utility.factory.MapGenerator;
import utility.factory.HumanGenerator;
import utility.tools.TickCounter;

public class Game
{
	public static void main(String[] args) {
                WorldMap map = MapGenerator.generateMap(80, 80);
                MapPanel mp = new MapPanel(map, 800, 800);
                GameData data = new GameData(map);
                BuildingGenerator bm = new BuildingGenerator(map, data);
                TickCounter ticks = new TickCounter();
                HumanAction humAct = new HumanAction(map,ticks,data);
                HumanGenerator humGen = new HumanGenerator(map, data, ticks);
                
                humGen.generateMigrant(5);
                
                JFrame frame = new JFrame("Game");
                frame.add(mp);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setVisible(true);
                
                long lastTick = 0;
                long thisTick = 0;
                long lastTickDuration = 0;
                boolean gameEnded = false;
                
                while(!gameEnded)
                {
                	lastTick = thisTick;
                	thisTick = System.currentTimeMillis();
                	lastTickDuration = thisTick - lastTick;
                	
                	long tick = ticks.getTickCount();
                	humAct.performAllActions();
                	
                	if(tick%501==3)
                		if(Math.random()<0.1)
                			humGen.generateMigrant(4);
                	if(tick%131==9)
                		map.removeEmptyFamilies();
                	if(tick%1603==9)
                		map.printFamiliesState();
                	if(tick%300==5)
                		bm.addNewBuildingToBuild();
                	if(tick%1603==10)
                		map.printHumansState();
                	if(tick%1603==812)
                	{		
                		System.out.println("______________________________");
                		System.out.println("Tick length : " + (lastTickDuration) + " ms - Unemployement : " + data.getJoblessPourcentage() + "% - Population : " + data.getPopulation());
                		System.out.println("Food supply per capita : " + data.getFoodSupplyPerCapita() + " - Food in markets : " + data.getFoodMarketsSupply());
                		Item.Object [] items = Item.Object.values(); 
            			for(int i=0; i<items.length; ++i)
            			{
            				int q = map.getItemsInMarkets(items[i]);
            				System.out.println(items[i] + " : " + q);
            			}
            			System.out.println("______________________________");
                	}
                	
                	map.checkBirthDays(ticks.getTickCount());
                	frame.repaint();
                	try {
                			Thread.sleep(TickCounter.TICK_LENGTH);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
                	ticks.addTick();
                }
    }
}
