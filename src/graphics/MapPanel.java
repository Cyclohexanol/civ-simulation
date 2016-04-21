package graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Map;

import javax.swing.JPanel;

import entity.WorldMap;
import entity.building.Building;
import entity.building.Building.Type;
import entity.building.OwnableBuilding;
import entity.unit.Human;
import entity.unit.Unit;

public class MapPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	private final WorldMap map;
	private final int width;
	private final int height;
	private final int xSize;
	private final int ySize;
	private final int tileXSize;
	private final int tileYSize;
	
	public MapPanel(WorldMap map, int width, int height)
	{
		super();
		this.map = map;
		this.width = width;
		this.height = height;
		this.setPreferredSize(new Dimension(width,height));
		
		xSize = map.getWidth();
		ySize = map.getHeight();
		tileXSize = width/xSize;
		tileYSize = height/ySize;
	}
	
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.clearRect(0, 0, width, height);

        for (int i = 0; i < ySize; i++) {
            for (int j = 0; j < xSize; j++) {
                int x = j * tileXSize;
                int y = i * tileYSize;
                Color terrainColor = map.getTile(j, i).getColor();
                g.setColor(terrainColor);
                g.fillRect(x, y, tileXSize, tileYSize);
            }
        }
        
        for(Map.Entry<Integer, Building> e : map.getBuildings().entrySet()) 
        {
        	Building b = e.getValue();
        	int xSize = tileXSize*b.getWidth();
        	int ySize = tileYSize*b.getHeight();
        	int x = b.getxPos()*tileXSize;
        	int y = b.getyPos()*tileYSize;
        	Color color = b.getColor();
        	if(!b.isFinished())
        		color = color.brighter();
        	if(b.isOwnable())
        		if(((OwnableBuilding)(b)).isOwned())
        			color = color.darker();
        	g.setColor(color);
        	g.fillRect(x, y, xSize, ySize);
        	g.setColor(color.brighter());
        	g.drawRect(x, y, xSize, ySize);
        	g.setFont(new Font("Consolas", Font.BOLD, tileYSize));
        	g.setColor(Color.WHITE);
        	if(b.getType()==Type.FARM)
        		g.drawString("F",x+1,y+tileYSize);
        	if(b.getType()==Type.HOUSE)
        		g.drawString("H",x+1,y+tileYSize);
        	if(b.getType()==Type.MARKET)
        		g.drawString("M",x+1,y+tileYSize);
        }
        
        for(Map.Entry<Integer, Unit> e : map.getUnits().entrySet())
        {
        	Unit u = e.getValue();
        	if(!u.isAlive())
        		continue;
        	double unitSize = u.getSize();
        	if(u.isHuman())
        		if(((Human)u).getAge()<16)
        			unitSize /= 2;
        	double xSize = tileXSize*unitSize;
        	double ySize = tileYSize*unitSize;
        	int x = (int) (u.getxPos()*tileXSize+tileXSize/2.0-xSize/2.0);
        	int y = (int) (u.getyPos()*tileYSize+tileYSize/2.0-ySize/2.0);
        	Color unitColor = u.getColor();
        	g.setColor(unitColor);
        	g.fillOval(x, y, (int)xSize, (int)ySize);
        	g.setColor(unitColor.darker());
        	g.drawOval(x, y, (int)xSize, (int)ySize);
        }
        
    }
	
}
