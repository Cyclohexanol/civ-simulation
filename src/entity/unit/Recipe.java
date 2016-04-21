package entity.unit;

import java.util.ArrayList;
import java.util.List;

import entity.unit.Human.Job;

public class Recipe 
{
	private final List<ItemStack> components;
	private final int craftTime;
	private final Job job;
	
	public Recipe(int craftTime, Job job, ItemStack ... component) {
		super();
		this.job = job;
		this.craftTime = craftTime;
		this.components = new ArrayList<ItemStack>();
		
		for(int i = 0; i<component.length; ++i)
			components.add(component[i]);
		
	}
	
	public Recipe(int craftTime, ItemStack ... component) {
		super();
		this.job = null;
		this.craftTime = craftTime;
		this.components = new ArrayList<ItemStack>();
		
		for(int i = 0; i<component.length; ++i)
			components.add(component[i]);
		
	}

	public List<ItemStack> getComponents() {
		return components;
	}

	public int getCraftTime() {
		return this.craftTime;
	}

	public Job getJob() {
		return job;
	}
}
