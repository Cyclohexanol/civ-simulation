package entity.building;

import java.awt.Color;

public abstract class OwnableBuilding extends Building{

	private final int price;
	private boolean owned;
	public OwnableBuilding(int id, int width, int height, Type type, Color color, int xPos, int yPos, int price, int completionVal) {
		super(id, width, height, type, color, xPos, yPos, completionVal);
		this.price = price;
		this.owned = false;
	}
	public int getPrice() {
		return price;
	}
	public boolean isOwned() {
		return owned;
	}
	public void setOwned(boolean owned) {
		this.owned = owned;
	}
	
}
