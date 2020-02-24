
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.ImageObserver;
import java.io.BufferedReader;
import java.io.FileReader;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

public class DisplayThread extends Thread
{
	static Graphics2D g;
	static DataStorageObject data;
	
	//booleans to replace movement activation with  boolean changes to 
	//allow for simultaneous move and shoot in game loop and eliminate lag
	static boolean rightkey = false;
	static boolean leftkey = false;
	static boolean spacekey = false;
	
	public DisplayThread(DataStorageObject data)
	{
		this.data = data;
	}
	
	@Override
	public void run()
	{
		//Keybinding code
		Action leftAction = new AbstractAction() 
			{
			@Override
			public void actionPerformed(ActionEvent arg0) 
			 {
			     leftkey = true;
			 }
		};
		
		Action shootAction = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
		     spacekey = true;
		   }
		};
		
		Action rightAction = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
		      rightkey = true;
		  }
		};
	
		Action leftActionSTOP = new AbstractAction() 
		{
		@Override
		public void actionPerformed(ActionEvent arg0) 
			{
		     leftkey = false;
		    
			}
		};
		
		Action shootActionSTOP = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
		     spacekey = false;
			}
		};
		
		Action rightActionSTOP = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
		      rightkey = false;
		 }
		};
	
		//keep as is, don't need to change
		Action pauseAction = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			data.paused = !data.paused;
		}};
		
		KeyStroke space = KeyStroke.getKeyStroke("SPACE");
		KeyStroke left = KeyStroke.getKeyStroke("LEFT");
		KeyStroke right = KeyStroke.getKeyStroke("RIGHT");
		KeyStroke spaceOFF = KeyStroke.getKeyStroke("released SPACE");
		KeyStroke leftOFF = KeyStroke.getKeyStroke("released LEFT");
		KeyStroke rightOFF = KeyStroke.getKeyStroke("released RIGHT");
		KeyStroke p = KeyStroke.getKeyStroke(KeyEvent.VK_P,0);
		
		
		
		data.requestFocus();

		int mapName = JComponent.WHEN_IN_FOCUSED_WINDOW;
		
		InputMap inputMap = data.getPanel().getInputMap(mapName);
		
		inputMap.put(space, "shoot");
		inputMap.put(left, "left");
		inputMap.put(right, "right");
		inputMap.put(spaceOFF, "shootstop");
		inputMap.put(leftOFF, "leftstop");
		inputMap.put(rightOFF, "rightstop");
		inputMap.put(p, "pause");
		
		ActionMap amap = data.getPanel().getActionMap();
		
		amap.put("shoot", shootAction);
		amap.put("left", leftAction);
		amap.put("right", rightAction);
		amap.put("shootstop", shootActionSTOP);
		amap.put("leftstop", leftActionSTOP);
		amap.put("rightstop", rightActionSTOP);
		amap.put("pause", pauseAction);
			
		final int USERSHIPRIGHTLIMIT = data.INVADERSWIDTH - data.usershipimage.getWidth(); // far right limit for the user ship
		
		while(true)// loop
		{
			// sleep for minimal amount of time
			try {Thread.sleep(10); } catch (Exception e) {}
			
//////////////////////////////////////////////////////////////////////////////////////////controls////////////////////////			
			if(!data.paused)
			{
				if(leftkey)//left movement
				{
					int locationtomoveto = data.usership.xpos - 3;
					data.usership.xpos = locationtomoveto <= 0 ? 0 : locationtomoveto;
				}
				
				if(rightkey)//right movement
				{
					int locationtomoveto = data.usership.xpos + 3;
					data.usership.xpos = locationtomoveto >= USERSHIPRIGHTLIMIT ? USERSHIPRIGHTLIMIT : locationtomoveto;
				}
				
				//shoot action//
				if(spacekey) {
					if(data.cooldown == 0) {
						// fire a bullet
						PositionObject bullet = new PositionObject(data.bulletimage);
						bullet.xpos = data.usership.xpos + data.usershipimage.getWidth() / 2;// TODO figure out the right number
						bullet.ypos = data.usership.ypos;// TODO figure out the right number
						data.userbullets.add(bullet);
						
						// start cooldown
						data.cooldown = 75; // TODO figure out the right number
					}
				}
			}

			
			// display things
			g = (Graphics2D) data.strategy.getDrawGraphics();
			g.setColor(Color.black);
			g.fillRect(0,0,data.INVADERSWIDTH+data.HIGHSCORESWIDTH,data.WINDOWHEIGHT);
			
			synchronized(data.dummyimage)
			{
				if(data.redalien != null)
				{
					drawPositionObject(data.redalien);
					//g.drawImage(data.alienredimage, data.redalien.xpos , data.redalien.ypos, null);
					g.drawImage(data.thevoid, data.INVADERSWIDTH , 0, null);
				}
				
				if(data.redalienpoints != null)
					drawInt(data.redalienpoints, data.redaliendeath.xpos, data.redaliendeath.ypos, null);
				
				for(int i = 0; i < data.WINDOWHEIGHT; i += 100)
				{
					g.drawImage(data.barverticalimage, data.INVADERSWIDTH, i, null);
				}
				
				for(int i = 0; i < data.aliens.length; i++)
				{
					for(int j = 0; j < data.aliens[i].length; j++)
					{
						if(data.aliens[i][j] != null)
						{
							if(j == 0)
								g.drawImage(data.img ? data.alien01Bimage : data.alien01Aimage, data.aliens[i][j].xpos , data.aliens[i][j].ypos, null);
							else if(j == 1 || j == 2)
								g.drawImage(data.img ? data.alien02Bimage : data.alien02Aimage, data.aliens[i][j].xpos , data.aliens[i][j].ypos, null);
							else
								g.drawImage(data.img ? data.alien03Bimage : data.alien03Aimage, data.aliens[i][j].xpos , data.aliens[i][j].ypos, null);
						}
					}
				}
				
				for(int i = 0; i < data.alienbullets.size(); i++)
				{
					drawPositionObject(data.alienbullets.get(i));
					//g.drawImage(data.bulletimage, data.alienbullets.get(i).xpos , data.alienbullets.get(i).ypos, null);
				}
				
				for(int i = 0; i < data.userbullets.size(); i++)
				{
					drawPositionObject(data.userbullets.get(i));
					//g.drawImage(data.bulletimage, data.userbullets.get(i).xpos , data.userbullets.get(i).ypos, null);
				}
				
				drawPositionObject(data.usership);
				//g.drawImage(data.usershipimage, data.usership.xpos , data.usership.ypos, null);
				
				for(int i = data.aliendeathanimations.size() - 1; i >= 0; i--)
				{
					drawPositionObject(data.aliendeathanimations.get(i));
					//g.drawImage(data.alienexplosion, data.aliendeathanimations.get(i).xpos , data.aliendeathanimations.get(i).ypos, null);
				}
				
				g.drawImage(data.scoreimage, 100, 0, null);
				drawInt(Integer.toString(data.score), 100 + data.scoreimage.getWidth(), 0, null);
				
				g.drawImage(data.livesimage, 400, 0, null);
				drawInt(Integer.toString(data.respawns), 400 + data.livesimage.getWidth(), 0, null);
				
				g.drawImage(data.hi_image, 5 + data.INVADERSWIDTH, 0, null);
				for(int i = 0; i < data.highscores.length; i++)
				{
					drawInt(Integer.toString(data.highscores[i]), 5 + data.INVADERSWIDTH, data.hi_image.getHeight() + i * 30, null);
				}
			}
			
			if(data.paused)
			{
				g.drawImage(data.pausedimage, (data.INVADERSWIDTH - data.pausedimage.getWidth()) / 2, (data.WINDOWHEIGHT - data.pausedimage.getHeight()) / 2, null);
			}
			
			g.dispose();
			data.strategy.show();
		}
	}
	private static void drawPositionObject(PositionObject obj)
	{
		g.drawImage(obj.image, obj.xpos , obj.ypos, null);
	}

	private static void drawInt(String number, int x, int y, ImageObserver observer)
	{
		try
		{
			int offset = 0;
			for(int i = 0; i < number.length(); i++)
			{
				g.drawImage(data.intcharimages[(int) number.charAt(i) - 48], x + offset, y, observer);
				offset += data.intcharimages[(int) number.charAt(i) - 48].getWidth();
			}
		}
		catch (Exception e)
		{
			// when the game ends, (int) number.charAt(i) - 48 somehow winds up as -3.
			// this tells us that number.charAt(i) is a '-' (minus sign)
			// how this is possible when we only call this mwethod on an int is for only god to know.
			// this catch block lets us move on without restarting the program.
		}
	}
}
