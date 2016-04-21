package utility.tools;

public class TickCounter 
{
	public static int TICK_LENGTH = 5;
	
	private long tickCount = 0;
	
	public void addTick()
	{
		++tickCount;
	}
	
	public long getTickCount()
	{
		return tickCount;
	}
}
