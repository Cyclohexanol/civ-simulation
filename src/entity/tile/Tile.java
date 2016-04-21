package entity.tile;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;

public abstract class Tile 
{
	public static final Color COLOR_FOREST = new Color(70, 110, 30);
	public static final Color COLOR_GRASS = new Color(80, 150, 50);
	public static final Color COLOR_OCEAN = new Color(0, 40, 190);
	public static final Color COLOR_LAKE = new Color(110, 140, 255);
	public static final Color COLOR_ROCK = new Color(200, 200, 200);
	public static final Color COLOR_DIRT = new Color(200, 135, 80);
	
	private final Type type;
	private final Material material;
	private final boolean walkable;
	private final boolean constructable;
	private final Color color;
	
	public Tile(Type type, Material material, boolean walkable, boolean constructable, Color color) {
		super();
		this.type = type;
		this.material = material;
		this.walkable = walkable;
		this.constructable = constructable;
		this.color = color;
	}

	public Color getColor() {
		return color;
	}

	public Type getType() {
		return type;
	}

	public Material getMaterial() {
		return material;
	}

	public boolean isWalkable() {
		return walkable;
	}

	public boolean isConstructable() {
		return constructable;
	}

	public enum Type
	{
		WATER, LAND
	}
	
	public enum Material
	{
		DIRT,ROCK,LAKE,OCEAN,GRASS,FOREST
	}
	
	public JPanel getColoredPanel(int width, int height)
	{
		JPanel pan = new JPanel();
		pan.setPreferredSize(new Dimension(width,height));
		pan.setBackground(color);
		return pan;
	}
}
