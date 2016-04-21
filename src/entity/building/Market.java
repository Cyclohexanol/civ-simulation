package entity.building;

import java.awt.Color;

public abstract class Market extends Building{
	private final ItemType goodsType;

	public Market(int id, Color color, int xPos, int yPos, int completionVal,
			ItemType goodsType) {
		super(id, 2, 2, Type.MARKET, color, xPos, yPos, completionVal);
		this.goodsType = goodsType;
	}

	public ItemType getItemType() {
		return goodsType;
	}


	public enum ItemType
	{
		FOOD, ITEM
	}
}
