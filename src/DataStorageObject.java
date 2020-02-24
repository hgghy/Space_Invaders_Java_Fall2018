
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class DataStorageObject extends Canvas
{
	// this class holds various data structures and variables, such as aliens, bunkers, the score, and an empty list of bullets
	
	// NOTE:
	// while it is normally recommended to have getters and setters instead of public variables, we decided to forego this 
	// practice here to make the code cleaner and more readable.
	// As an example, note that the following 2 lines do the same thing.
	// data.alienbullets.get(i).ypos += 4;
	// data.getAlienBullets.get(i).setYPos(data.getAlienBullets.get(i).getYPos() + 4);

	// PositionObjects are just x and y positions, that is all we need to display them on the screen
	// and we can figure out the starting bullet position from that alone

	
	String directory = "src\\Images\\";
    BufferStrategy strategy;
    BufferedImage thevoid = null;
	BufferedImage alien01Bimage = null;
	BufferedImage alien02Bimage = null;
	BufferedImage alien03Bimage = null;
	BufferedImage alien01Aimage = null;
	BufferedImage alien02Aimage = null;
	BufferedImage alien03Aimage = null;
	BufferedImage alienredimage = null;
	BufferedImage alienexplosion = null;
	BufferedImage bulletimage = null;
	BufferedImage usershipimage = null;
	BufferedImage scoreimage = null;
	BufferedImage hi_image = null;
	BufferedImage livesimage = null;
	BufferedImage pausedimage = null;
	BufferedImage[] intcharimages = new BufferedImage[10];
	BufferedImage barverticalimage = null;
	public final int WINDOWHEIGHT = 520;
	public final int INVADERSWIDTH = 650;
	public final int HIGHSCORESWIDTH = 200;
	int[] highscores = new int[5];
	
	JFrame container;
	public JFrame getContainer() {return container;}
	JPanel panel; 
	public JPanel getPanel() {return panel;}
	
	
	boolean img;
	
	boolean gameover;
	
	Object dummyimage = new Object();
	
	// aliens
	PositionObject[][] aliens;
	PositionObject redalien;
	boolean redalienside;
	PositionObject redaliendeath;
	String redalienpoints;
	int redaliendeathtimer;
	// where to put death animations
	ArrayList<PositionObject> aliendeathanimations;
	ArrayList<Integer> aliendeathanimationstimers;
	// alien speed/direction
	int aliendirectionAndSpeed;
	// number of aliens alive
	int numAliens;
	// alien spawn position distance from the top
	int alienSpawnPosition;
	// bunkers TODO much later after demo if ever
	// score
	int score;
	// bullets
	ArrayList<PositionObject> alienbullets;
	ArrayList<PositionObject> userbullets;
	// user ship
	PositionObject usership;
	// user bullet cooldown
	int cooldown;
	// user respawns
	int respawns;
	// game paused
	boolean paused;
	
	public DataStorageObject() throws URISyntaxException
	{
		container = new JFrame("Space Invaders");
		panel = (JPanel) container.getContentPane();
		panel.setPreferredSize(new Dimension(INVADERSWIDTH+HIGHSCORESWIDTH,WINDOWHEIGHT));
		panel.setLayout(null);
		setBounds(0,0,INVADERSWIDTH+HIGHSCORESWIDTH,WINDOWHEIGHT);
		panel.add(this);
		setIgnoreRepaint(true);
		container.pack();
		container.setResizable(false);
		container.setVisible(true);
		
		container.addWindowListener(new WindowAdapter()
		{ public void windowClosing(WindowEvent e)
			{
				writescores();
				System.exit(0);
			}
		});
		
		createBufferStrategy(2);
		strategy = getBufferStrategy();
		
		try
		{
			thevoid = ImageIO.read(new File(directory+"TheVoid.jpg"));
			alien01Bimage = ImageIO.read(new File(directory+"Alien01B.jpg"));
		    alien02Bimage = ImageIO.read(new File(directory+"Alien02B.jpg"));
		    alien03Bimage = ImageIO.read(new File(directory+"Alien03B.jpg"));
		    alien01Aimage = ImageIO.read(new File(directory+"Alien01A.jpg"));
		    alien02Aimage = ImageIO.read(new File(directory+"Alien02A.jpg"));
		    alien03Aimage = ImageIO.read(new File(directory+"Alien03A.jpg"));
		    alienredimage = ImageIO.read(new File(directory+"RedAlien.jpg"));
		    alienexplosion = ImageIO.read(new File(directory+"Aliendeath.jpg")); 
		    bulletimage = ImageIO.read(new File(directory+"Bullet.jpg"));
		    usershipimage = ImageIO.read(new File(directory+"Usership.jpg"));
		    scoreimage = ImageIO.read(new File(directory+"Score.jpg"));
		    hi_image = ImageIO.read(new File(directory+"Hi_.jpg"));
		    livesimage = ImageIO.read(new File(directory+"Lives.jpg"));
		    pausedimage = ImageIO.read(new File(directory+"Paused.jpg"));
		    barverticalimage = ImageIO.read(new File(directory+"BarVertical.jpg"));
		    for(int i = 0; i < 10; i++)
		    {
		    	intcharimages[i] = ImageIO.read(new File(directory+"Char"+i+".jpg"));
		    }
		    
		} catch (IOException e)
		{
			e.printStackTrace();
			System.exit(0);
		}
		
		readscores();
		clear();
	}
	
	public void clear()
	{
		img = true;
		gameover = false;
		aliendeathanimations = new ArrayList<PositionObject>();
		aliendeathanimationstimers = new ArrayList<Integer>();
		aliendirectionAndSpeed = 5; // TODO figure out the right number to start with
		alienSpawnPosition = 0;
		score = 0;
		alienbullets = new ArrayList<PositionObject>();
		userbullets = new ArrayList<PositionObject>();
		cooldown = 0;
		respawns = 2;
		paused = true;
		redaliendeath = null;
		redalien = null;
		redalienside = true;
		redaliendeathtimer = 0;
		
		spawnUser();
		spawnAliens();
	}
	
	public void spawnUser()
	{
		usership = new PositionObject(usershipimage);
		usership.xpos = 0; // TODO figure out the right number to start with
		usership.ypos = WINDOWHEIGHT - usershipimage.getHeight(); // TODO figure out the right number to start with
	}
	
	public void spawnAliens()
	{
		numAliens = 50;
		aliens = new PositionObject[10][5];
		
		for(int i = 0; i < aliens.length; i++)
		{
			for(int j = 0; j < aliens[i].length; j++)
			{
				aliens[i][j] = new PositionObject(alienexplosion);
				aliens[i][j].xpos = (i + 2) * (10 + alien01Bimage.getWidth()); // TODO figure out the right number to start with
				aliens[i][j].ypos = 50 + (j + 1)* 5 + (alienSpawnPosition + j) * alien01Bimage.getHeight(); // TODO figure out the right number to start with
			}
		}
		
		if(aliendirectionAndSpeed < 0)
			aliendirectionAndSpeed *= -1;
	}
	public void writescores()
	{
		try {
			PrintWriter writer = new PrintWriter(directory+"HighscoresFile.txt");
			for(int i = 0; i < highscores.length; i++)
			{
				writer.write(highscores[i]+"\n");
			}
			writer.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	public void readscores()
	{
		try
		{
			FileReader fileReader = new FileReader(directory+"HighscoresFile.txt");
		
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			
			for(int i = 0; i < highscores.length; i++)
				highscores[i] = Integer.parseInt(bufferedReader.readLine());
			
			bufferedReader.close();
		} catch (Exception e1)
		{
			e1.printStackTrace();
		}
	}
}
