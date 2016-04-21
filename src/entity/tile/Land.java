package entity.tile;

import java.awt.Color;

public abstract class Land extends Tile
{

	public Land(Material material, boolean walkable, boolean constructable, Color color) {
		super(Type.LAND, material, walkable, constructable,color);
	}

}
