package entity.building;

import entity.unit.Item.Object;

public class WoodMarket extends ItemMarket
{

	public WoodMarket(int id, int xPos, int yPos) {
		super(id, COLOR_WOOD_MARKET, xPos, yPos, 450);
		addItem(Object.CLASSIC_WOOD);
		
	}

}
