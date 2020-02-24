import java.util.Random;

public class BulletsThread extends Thread
{
	private DataStorageObject data;
	
	public BulletsThread(DataStorageObject data)
	{
		this.data = data;
	}
	
	@Override
	public void run()
	{
		/* Pseudo code:
		 * 
		 * while
		 * {
		 * 		sleep for minimal amount of time
		 * 		
		 * 		while paused
		 * 			wait
		 * 		
		 * 		cooldown--
		 * 
		 * 		for each alien bullet
		 * 		{
		 * 			move it
		 * 			if it has hit
		 * 			{
		 * 				destroy the bullet
		 * 				respawns--
		 * 				respawn user
		 * 				if respawns < 0
		 * 						game over
		 * 
		 * 				update score
		 * 				destroy the thing it hit
		 * 				
		 * 				if there are no aliens left (i.e. this bullet killed the last alien)
		 * 				{
		 * 					create a new wave of aliens
		 * 					respawns++
		 * 				}
		 * 			}
		 * 		}
		 * 		for each user bullet
		 * 		{
		 * 			if it has hit
		 * 			{
		 * 				destroy the bullet
		 * 				update score
		 * 				destroy the thing it hit
		 * 				
		 * 				if there are no aliens left (i.e. this bullet killed the last alien)
		 * 				{
		 * 					create a new wave of aliens
		 * 					respawns++
		 * 				}
		 * 		}
		 * }
		 * 
		 */
		Random rand = new Random();
	
		if(data.cooldown > 0)
			data.cooldown--;
		
		synchronized(data.dummyimage)
		{
			for(int i = 0; i < data.alienbullets.size(); i++)
			{
				data.alienbullets.get(i).ypos += 4; // TODO figure out the right number
				
				if(data.alienbullets.get(i).ypos >= data.WINDOWHEIGHT)
					data.alienbullets.remove(i);
				else if(checkcollision(data.alienbullets.get(i), data.usership))
//						data.alienbullets.get(i).xpos > data.usership.xpos 
//						&& data.alienbullets.get(i).xpos + data.bulletimage.getWidth() < data.usership.xpos + data.usershipimage.getWidth()
//						&& data.alienbullets.get(i).ypos + data.bulletimage.getHeight() > data.usership.ypos) // this tells us that the bullet has collided with the usership
				{
					data.alienbullets.remove(i);
					data.respawns--;
					data.spawnUser();
					if(data.respawns < 0)
						data.gameover = true;
				}
			}
			
			for(int i = 0; i < data.userbullets.size(); i++)
			{
				data.userbullets.get(i).ypos -= 6; // TODO figure out the right number
				
				if(data.userbullets.get(i).ypos <= 0)
					data.userbullets.remove(i);
				else
				{
					if(data.redalien != null)
					{
						if(checkcollision(data.redalien, data.userbullets.get(i)))
//								data.redalien.xpos < data.userbullets.get(i).xpos + data.bulletimage.getWidth()
//							&& data.redalien.xpos + data.alien01Bimage.getWidth() > data.userbullets.get(i).xpos
//							&& data.redalien.ypos < data.userbullets.get(i).ypos + data.bulletimage.getHeight()
//							&& data.redalien.ypos + data.alien01Bimage.getHeight() > data.userbullets.get(i).ypos) // this tells us that the bullet has collided with the alien
						{
							data.redaliendeath = data.redalien;
							data.redaliendeathtimer = 25;
							data.redalienpoints = ""+(rand.nextInt(4) + 3) * 50;
							data.score += Integer.parseInt(data.redalienpoints);
							data.userbullets.remove(i);
							data.redalien = null;
						}
					}
					for(int j = 0; j < data.aliens.length && data.userbullets.size() > i; j++)
					{
						for(int k = 0; k < data.aliens[j].length && data.userbullets.size() > i; k++)
						{
							if(data.aliens[j][k] != null)
							{
								if(checkcollision(data.aliens[j][k], data.userbullets.get(i)))
//										data.aliens[j][k].xpos < data.userbullets.get(i).xpos + data.bulletimage.getWidth()
//									&& data.aliens[j][k].xpos + data.alien01Bimage.getWidth() > data.userbullets.get(i).xpos
//									&& data.aliens[j][k].ypos < data.userbullets.get(i).ypos + data.bulletimage.getHeight()
//									&& data.aliens[j][k].ypos + data.alien01Bimage.getHeight() > data.userbullets.get(i).ypos) // this tells us that the bullet has collided with the alien
								{
									data.score += k == 0 ? 40 : k == 1 || k == 2 ? 20 : 10;
									data.userbullets.remove(i);
									data.aliendeathanimations.add(data.aliens[j][k]);
									data.aliendeathanimationstimers.add(20);
									data.aliens[j][k] = null;
									data.numAliens--;
								}
							}
						}
					}
				}
			}
		}
		if(data.numAliens == 0)
		{
			data.spawnAliens();
			data.respawns++;
		}
		if(data.alienSpawnPosition > 5)
			data.gameover = true;
	}
	
	public boolean checkcollision(PositionObject a, PositionObject b)
	{
		return a.xpos < b.xpos + b.image.getWidth()
		&& a.xpos + a.image.getWidth() > b.xpos
		&& a.ypos < b.ypos + b.image.getHeight()
		&& a.ypos + a.image.getHeight() > b.ypos; // this tells us if the two have collided.
	}
}
