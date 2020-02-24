
import java.util.ArrayList;
import java.util.Random;

public class AliensThread extends Thread
{
	private DataStorageObject data;
	
	public AliensThread(DataStorageObject data)
	{
		this.data = data;
	}
	
	@Override
	public void run()
	{
		Random rand = new Random();
		
		/* Pseudo code:
		 * 
		 * while
		 * {
		 * 		while paused
		 * 			wait
		 * 
		 * 		sleep for a short time
		 * 		
		 * 		while paused
		 * 			wait
		 * 		
		 * 		if aliens have reached the side
		 * 		{
		 * 			change direction and increase speed
		 * 			move each alien down
		 * 		}
		 * 		
		 * 		for each alien
		 * 		{
		 * 			move it
		 * 			if alien is in bottom row && random chance
		 * 				alien fires
		 * 		}
		 * }
		 * 
		 */
		boolean sidereached = false, bottom = true;
		int i, j, overshoot = 0;
		
		ArrayList<PositionObject> bullets = new ArrayList<PositionObject>();

		synchronized(data.dummyimage)
		{
			// for each alien
			for(i = 0; i < data.aliens.length; i++)
			{
				bottom = true;
				for(j = data.aliens[i].length-1; j >= 0; j--)
				{
					if(data.aliens[i][j] != null)
					{
						// move it
						data.aliens[i][j].xpos += data.aliendirectionAndSpeed;
					
					
						// if alien is on bottom of it's stack && random chance
						if(bottom && rand.nextInt(10) < 1)
						{
							// alien fires
							PositionObject bullet = new PositionObject(data.bulletimage);
							bullet.xpos = data.aliens[i][j].xpos + (data.alien01Bimage.getWidth() / 2) - (data.bulletimage.getWidth() / 2);// TODO figure out the right number
							bullet.ypos = data.aliens[i][j].ypos + data.alien01Bimage.getHeight();// TODO figure out the right number
							bullets.add(bullet);
						}

						bottom = false;
					}
				}
			}
			
			// if aliens have reached the side
			for(i = data.aliens.length-1; !sidereached && i >= 0; i--)
			{
				for(j = data.aliens[i].length-1; !sidereached && j >= 0; j--)
				{
					if(data.aliens[i][j] != null && data.aliens[i][j].xpos + data.alien01Bimage.getWidth() >= data.INVADERSWIDTH)
					{
						sidereached = true;
						overshoot = (data.aliens[i][j].xpos + data.alien01Bimage.getWidth() - data.INVADERSWIDTH);
					}
					else if(data.aliens[i][j] != null && data.aliens[i][j].xpos <= 0)
					{
						sidereached = true;
						overshoot = data.aliens[i][j].xpos;
					}
				}
			}
			if(sidereached)
			{
				for(i = 0; i < bullets.size(); i++)
				{
					bullets.get(i).ypos += data.alien01Bimage.getHeight();
					bullets.get(i).xpos -= 2 * overshoot;
				}
				
				// change direction and increase speed
				data.aliendirectionAndSpeed *= -1;
				data.aliendirectionAndSpeed += data.aliendirectionAndSpeed > 0 ? 2 : -2; // TODO figure out the right number
				
				// for each alien
				for(i = 0; i < data.aliens.length; i++)
				{
					for(j = 0; j < data.aliens[i].length; j++)
					{
						if(data.aliens[i][j] != null)
						{
							// move it down
							data.aliens[i][j].ypos += data.alien01Bimage.getHeight();// TODO figure out the right number
						
							// if it is past the edge, move it back
							data.aliens[i][j].xpos -= 2 * overshoot;
						}
					}
				}
				// next wave spawns lower
				data.alienSpawnPosition++;
				sidereached = false;
			}
			
			data.alienbullets.addAll(bullets);
		}
			
		data.img = !data.img;
	}
}
