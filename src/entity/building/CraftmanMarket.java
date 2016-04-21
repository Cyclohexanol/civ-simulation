package entity.building;

import entity.unit.Item.Object;

public class CraftmanMarket extends ItemMarket
{

	public CraftmanMarket(int id, int xPos, int yPos) {
		super(id, COLOR_CRAFTMAN_MARKET, xPos, yPos, 450);
		addItem(Object.AXE_STONE);
		addItem(Object.PICKAXE_STONE);
		addItem(Object.HOE_STONE);
		addItem(Object.EMPTY_WATER_FLASK);
	}
	
}
