package entity.unit;

import entity.unit.Human.Job;

public class Item 
{
	private final Object object;
	
	public Item(int value, Object object) {
		super();
		this.object = object;
	}

	public int getValue() {
		return object.getValue();
	}

	public Object getObject() {
		return object;
	}

	public enum Object
	{
		CLASSIC_WOOD(3),
		STONE(4),
		EMPTY_WATER_FLASK(5, new Recipe(15, Job.CRAFTMAN, new ItemStack(Object.CLASSIC_WOOD,1))), 
		FULL_WATER_FLASK(5), 
		HOE_STONE(30,new Recipe(35, Job.CRAFTMAN, new ItemStack(Object.CLASSIC_WOOD,2), new ItemStack(Object.STONE,3))), 
		AXE_STONE(40, new Recipe(35, Job.CRAFTMAN, new ItemStack(Object.CLASSIC_WOOD,3), new ItemStack(Object.STONE,3))),
		PICKAXE_STONE(40, new Recipe(35, Job.CRAFTMAN, new ItemStack(Object.CLASSIC_WOOD,3), new ItemStack(Object.STONE,3)));
		
		private final int value;
		private final Recipe recipe;
		
		private Object(int value, Recipe recipe)
		{
			this.value = value;
			this.recipe = recipe;
		}
		
		private Object(int value)
		{
			this(value, null);
		}
		
		public int getValue()
		{
			return this.value;
		}

		public Recipe getRecipe() {
			return recipe;
		}
		
		
	}
}
