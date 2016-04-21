package entity.building;

public class Farm extends OwnableBuilding
{

	public Farm(int id, int xPos, int yPos) {
		super(id, 2, 2, Type.FARM, COLOR_FARM, xPos, yPos, 50, 30);
	}

}
