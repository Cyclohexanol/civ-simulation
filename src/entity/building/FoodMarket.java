package entity.building;

public class FoodMarket extends Market
{
	private int supply;
	private final int itemSellPrice, itemBuyPrice;
	
	public FoodMarket(int id, int xPos, int yPos) {
		super(id, COLOR_FOOD_MARKET, xPos, yPos, 300, ItemType.FOOD);
		this.supply = 10;
		this.itemSellPrice = 8;
		this.itemBuyPrice = 10;
	}

	public int getItemSellPrice() {
		return itemSellPrice;
	}

	public int getItemBuyPrice() {
		return itemBuyPrice;
	}

	public int getSupply() {
		return supply;
	}

	public void setSupply(int supply) {
		this.supply = supply;
	}
	
	public void addSupply(int i)
	{
		this.supply += i;
	}
	
	public boolean removeSupply(int i)
	{
		if(this.supply-i<0)
			return false;
		this.supply -= i;
		return true;
	}
	
	public boolean hasStock()
	{
		if(this.supply>0)
			return true;
		return false;
	}
}
