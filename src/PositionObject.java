
import java.awt.image.BufferedImage;

public class PositionObject
{
	// NOTE:
	// while it is normally recommended to have getters and setters instead of public variables, we decided to forego this 
	// practice here to make the code cleaner and more readable.
	// As an example, note that the following 2 lines do the same thing.
	// data.getAlienBullets.get(i).setYPos(data.getAlienBullets.get(i).getYPos() + 4);
	// data.alienbullets.get(i).ypos += 4;
	
	public PositionObject(BufferedImage pic)
	{
		image = pic;
	}
	
	public PositionObject(int x, int y, BufferedImage pic)
	{
		xpos = x;
		ypos = y;
		image = pic;
	}
	
	public BufferedImage image;
	public int xpos;
	public int ypos;
	/*
	public void setXPos(int x)
	{
		xpos = x;
	}
	public int getXPos()
	{
		return xpos;
	}
	public void setYPos(int y)
	{
		ypos = y;
	}
	public int getYPos()
	{
		return ypos;
	}*/
}
