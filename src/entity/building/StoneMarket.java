package entity.building;

import entity.unit.Item.Object;

public class StoneMarket extends ItemMarket
{
	public StoneMarket(int id, int xPos, int yPos) {
		super(id, COLOR_STONE_MARKET, xPos, yPos, 450);
		addItem(Object.STONE);
		
	}
}
