package entity.building;

import java.awt.Color;

public abstract class House extends OwnableBuilding{

	private final int maxNbFamilly;
	private final int maxNbInhabitants;
	
	public House(int id, int width, int height, Color color, int xPos, int yPos, int maxNbFamilly, int maxNbInhabitants, int price, int completionVal) {
		super(id, width, height, Type.HOUSE, color, xPos, yPos, price, completionVal);
		this.maxNbFamilly = maxNbFamilly;
		this.maxNbInhabitants = maxNbInhabitants;
	}
	
	public int getMaxNbInhabitants() {
		return maxNbInhabitants;
	}

	public int getMaxNbFamilly() {
		return maxNbFamilly;
	}
	

}
