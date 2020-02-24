
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Random;

public class SpaceInvadersMain
{
	
	
	public static void main(String[] args) throws InterruptedException, URISyntaxException, IOException
	{
		Random rand = new Random();
		
		// create a window
		DataStorageObject data = new DataStorageObject();

		new DisplayThread(data).start();
		
		int control = 1;
		int highscoreposition = -1; // if this is -1, that indicates that we are not on the list. else, shows our position in the high scores.
		
		while(true)
		{
			try
			{
				while(data.paused)
					Thread.sleep(100); // we are paused, so we wait // TODO figure out the right number
				
				Thread.sleep(10); // TODO figure out the right number
				
				while(data.paused)
					Thread.sleep(100); // we are paused, so we wait // TODO figure out the right number
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			new BulletsThread(data).start();
			
			for(int i = 0; i < data.aliendeathanimations.size(); i++)
			{
				data.aliendeathanimationstimers.set(i, data.aliendeathanimationstimers.get(i)-1);
				if(data.aliendeathanimationstimers.get(i) == 0)
				{
					data.aliendeathanimationstimers.remove(i);
					data.aliendeathanimations.remove(i);
				}
			}
			if(data.redaliendeathtimer != 0)
			{
				data.redaliendeathtimer--;
				if(data.redaliendeathtimer == 0)
				{
					data.redalienpoints = null;
					data.redaliendeath = null;
				}
			}
			
			if(control == 75)
			{
				new AliensThread(data).start();
				control = 1;
			}
			else
				control++;
			
			if(data.redalien == null)// TODO figure out the right number
			{
				if(rand.nextInt(1000) == 0)
				{
					data.redalien = new PositionObject(data.alienredimage);
					data.redalien.xpos = data.redalienside ? -data.alienredimage.getWidth() : data.INVADERSWIDTH;
					data.redalien.ypos = 20;
					data.redalienside = !data.redalienside;
				}
			}
			else
			{
				data.redalien.xpos += data.redalienside ? -2 : 2; // TODO figure out the right number
				if(data.redalien.xpos < -data.alienredimage.getWidth() || data.redalien.xpos > data.INVADERSWIDTH)
					data.redalien = null;
			}
			
			if(highscoreposition >= 0 || data.score > data.highscores[data.highscores.length - 1])
			{
				if(highscoreposition < 0)
				{
					data.highscores[data.highscores.length - 1] = data.score;
					highscoreposition = data.highscores.length - 1;
				}
				else
					data.highscores[highscoreposition] = data.score;
				
				for(int i = highscoreposition - 1; i >= 0; i--)
				{
					if(data.highscores[i] < data.score)
					{
						data.highscores[i+1] = data.highscores[i];
						data.highscores[i] = data.score;
						highscoreposition--;
					}
					else
						break;
				}
			}
			
			// if game over
			// 		end loop
			if(data.gameover)
			{
				data.writescores();
				data.clear();
				highscoreposition = -1;
			}
		}
	}
}
