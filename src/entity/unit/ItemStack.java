package entity.unit;

import entity.unit.Item.Object;

public class ItemStack 
{
	private final Item.Object object;
	private int quantity;
	
	public ItemStack(Object object, int quantity) {
		super();
		this.object = object;
		this.quantity = quantity;
	}
	
	public int getQuantity() {
		return quantity;
	}
	
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
	public Item.Object getObject() {
		return object;
	}
	
	public int getValue()
	{
		return object.getValue();
	}
}
