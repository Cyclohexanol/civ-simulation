package entity.unit;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import utility.ai.Action;
import utility.pathfinder.Path;

public abstract class Human extends Unit
{
	private final Map<Item.Object, Integer> items;
	private List<ItemStack> neededItems;
	private final Sex sex;
	private Job job;
	private int partnerID;
	private double thirst, hunger, energy;
	private int familyID;
	private Family family;
	private Action action;
	private final int birthday;
	private int age;
	
	public Human(int id, Sex sex, Color color, Family family, int age, int birthday) {
		super(id, 0.8, Type.HUMAN, color);
		this.sex = sex;
		this.job = null;
		this.partnerID = -1;
		this.thirst = 2000;
		this.hunger = 2000;
		this.energy = 2000;
		this.familyID = family.getId();
		this.action = null;
		this.family = family;
		this.birthday = birthday%10000;
		this.age = age;
		family.addMember(this);
		this.items = new HashMap<Item.Object,Integer>();
		this.neededItems = null;
	}
	
	public int getAge() {
		return age;
	}
	
	public void addAge()
	{
		++this.age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public int getBirthday() {
		return birthday;
	}

	public Action getAction() {
		return action;
	}
	
	public int getMoney()
	{
		return family.getMoney();
	}
	
	public void addMoney(int money)
	{
		family.addMoney(money);
	}
	
	public boolean removeMoney(int money)
	{
		return family.removeMoney(money);
	}

	public int getFood()
	{
		return family.getFood();
	}
	
	public void addFood(int food)
	{
		family.addFood(food);
	}
	
	public boolean removeFood(int food)
	{
		return family.removeFood(food);
	}
	
	public void setAction(Action action) {
		this.action = action;
	}
	
	public boolean checkAtActionLocation()
	{
		if(action==null)
			return true;
		
		if(action.isAtLocation()==true)
			return true;
		
		if(getPos().equals(action.getLocation()))
			action.setAtLocation(true);
		
		return action.isAtLocation();
	}
	
	
	public Action.Type getActionType()
	{
		if(action==null)
			return null;
		
		return action.getType();
	}
	
	public Path getPath()
	{
		if(action==null)
			return null;
		return action.getPath();
	}
	
	public int getPartnerID() {
		return partnerID;
	}

	public void setPartnerID(int partnerID) {
		this.partnerID = partnerID;
	}

	public double getThirst() {
		return thirst;
	}
	
	public void setThirst(double thirst) {
		this.thirst = thirst;
	}
	
	public void addThirst(double thirst)
	{
		this.thirst += thirst;
	}
	
	public void removeThirst(double thirst) {
		if(this.thirst<0)
		{
			die();
			System.out.println(getId() + " has died of thirst !");
		}
		this.thirst -= thirst;
	}

	public double getHunger() {
		return hunger;
	}

	public void setHunger(double hunger) {
		this.hunger = hunger;
	}

	public void resetHunger()
	{
		this.hunger = 1000;
	}
	
	public void addHunger(double hunger)
	{
		this.hunger += hunger;
	}
	
	public void removeHunger(double hunger) {
		if(this.hunger<0)
		{
			die();
			System.out.println(getId() + " has died of hunger !");
		}
		this.hunger -= hunger;
	}
	
	public Map<Item.Object, Integer> getItems() {
		return items;
	}

	public void die()
	{
		super.die();
		this.family.removeMember(this);
	}
	
	public double getEnergy() {
		return energy;
	}

	public void setEnergy(double energy) {
		this.energy = energy;
	}
	
	public void resetEnergy()
	{
		this.energy = 1000;
	}
	
	public void addEnergy(double energy)
	{
		this.energy += energy;
	}
	
	public void removeEnergy(double energy) {
		if(this.energy<0)
		{
			die();
			System.out.println(getId() + " has died of exhaustion !");
		}
		this.energy -= energy;
	}
	
	public int getFamilyID() {
		return familyID;
	}

	public void setFamily(Family family) {
		this.familyID = family.getId();
		this.family = family;
	}

	public void resetThirst()
	{
		this.thirst = 1000;
	}

	public boolean isMaried()
	{
		if(partnerID==-1)
			return false;
		return true;
	}
	
	
	public Job getJob() {
		return job;
	}

	public void setJob(Job job) {
		this.job = job;
	}

	public Sex getSex() {
		return sex;
	}
	
	public Family getFamily()
	{
		return family;
	}
	
	public boolean craft(Item.Object object)
	{
		if(!canCraft(object))
			return false;
		
		List<ItemStack> neededStacks = object.getRecipe().getComponents();
		for(int i = 0; i<neededStacks.size(); ++i)
			removeItemStack(neededStacks.get(i));
		
		addItem(object,1);
		
		return true;	
	}
	
	public List<Item.Object> getAbleToCraftList()
	{
		List<Item.Object> craftableObj = new ArrayList<Item.Object>();
		Item.Object objs[] = Item.Object.values();
		for(int i=0;i<objs.length; ++i)
			if(ableToCraft(objs[i]))
				craftableObj.add(objs[i]);
		
		return craftableObj;
	}
	
	public boolean ableToCraft(Item.Object object)
	{
		if(object.getRecipe()==null)
			return false;
		
		if(this.job!=object.getRecipe().getJob())
			return false;
		return true;
	}
	
	public boolean canCraft(Item.Object object)
	{
		if(!ableToCraft(object))
			return false;
		
		List<ItemStack> neededStacks = object.getRecipe().getComponents();
		
		for(int i = 0; i<neededStacks.size(); ++i)
			if(!hasItem(neededStacks.get(i)))
				return false;
		return true;
	}
	
	public void addItem(ItemStack stack)
	{
		if(items.containsKey(stack.getObject()))
			items.replace(stack.getObject(), items.get(stack.getObject())+stack.getQuantity());
		else
			items.put(stack.getObject(), stack.getQuantity());
	}
	
	public void addItem(Item.Object object, int i)
	{
		if(items.containsKey(object))
			items.replace(object, items.get(object)+i);
		else
			items.put(object, i);
	}
	
	public boolean removeItemStack(ItemStack stack)
	{
		if(items.containsKey(stack.getObject()))
			return false;
		
		if(items.get(stack.getObject())<stack.getValue())
			return false;
		else if(items.get(stack.getObject())==stack.getValue())
		{
			items.remove(stack.getObject());
			return true;
		}
		else
		{
			items.replace(stack.getObject(), items.get(stack.getObject())-stack.getValue());
			return true;
		}
	}
	
	public boolean removeItem(Item.Object object, int i)
	{
		if(!items.containsKey(object))
			return false;
		
		if(items.get(object)<i)
			return false;
		else if(items.get(object)==i)
		{
			items.remove(object);
			return true;
		}
		else
		{
			items.replace(object, items.get(object)-i);
			return true;
		}
	}
	
	public boolean hasItem(Item.Object object)
	{
		for(Map.Entry<Item.Object, Integer> i : items.entrySet())
			if(i.getKey()==object&&i.getValue()>0)
				return true;
		return false;
	}
	
	public int getItemQuantity(Item.Object obj)
	{
		if(!items.containsKey(obj))
			return 0;
		return items.get(obj);
	}
	
	public boolean hasItem(ItemStack stack)
	{
		for(Map.Entry<Item.Object, Integer> i : items.entrySet())
			if(i.getKey()==stack.getObject()&&i.getValue()>=stack.getQuantity())
				return true;
		return false;
	}

	public List<ItemStack> getNeededItems() {
		return neededItems;
	}

	public void setNeededItems(List<ItemStack> neededItems) {
		this.neededItems = neededItems;
	}


	public enum Sex
	{
		FEMALE, MALE
	}
	
	public enum Job
	{
		BUILDER,FARMER,CRAFTMAN,LUMBERJACK,MINER,NONE 
	}
}
