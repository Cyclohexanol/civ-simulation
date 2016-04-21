package entity.tile;

import java.awt.Color;

public abstract class Water extends Tile
{
	private final boolean potable;

	public Water(Material material, boolean potable, Color color) {
		super(Type.WATER, material, false, false, color);
		this.potable = potable;
	}

	public boolean isPotable() {
		return potable;
	}


	
}
