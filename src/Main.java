import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import javax.swing.JComponent;
import javax.swing.JFrame;


// make sure you rename this class if you are doing a copy/paste
public class Main extends JComponent implements MouseListener, KeyListener, MouseMotionListener{

    // Height and Width of our game
    static final int WIDTH = 800;
    static final int HEIGHT = 600;
    
    // sets the framerate and delay for our game
    // you just need to select an approproate framerate
    long desiredFPS = 60;
    long desiredTime = (1000)/desiredFPS;
    
    int blockWidth = 20;
    
    int[][] map = new int[HEIGHT/blockWidth][WIDTH/blockWidth];
    
    int selectedBlock = 1;
    
    //ArrayList<Block> blocks = new ArrayList();
    
    boolean mouse1Pressed = false;
    boolean mouse2Pressed = false;
    boolean shift = false;
    int mx, my;
    boolean save;
    
    // drawing of the game happens in here
    // we use the Graphics object, g, to perform the drawing
    // NOTE: This is already double buffered!(helps with framerate/speed)
    @Override
    public void paintComponent(Graphics g)
    {
        // always clear the screen first!
        g.clearRect(0, 0, WIDTH, HEIGHT);
        
        // GAME DRAWING GOES HERE 
        for (int i = 0; i < WIDTH; i += blockWidth)
        {
            g.drawLine(i, 0, i, HEIGHT);
        }
        for (int i = 0; i < HEIGHT; i += blockWidth)
        {
            g.drawLine(0, i, WIDTH, i);
        }
        for (int y = 0; y < map.length; y ++)
        {
            for (int x = 0; x < map[y].length; x ++)
            {
                int bY = y*blockWidth;
                int bX = x*blockWidth;
                switch(map[y][x])
                {
                    case 1: 
                        g.fillRect(bX, bY, blockWidth, blockWidth);
                        break;
                }
            }
        }
        
        /*for (Block b: blocks)
        {
            b.draw(g);
        }*/
        
        // GAME DRAWING ENDS HERE
    }
    
    
    // The main game loop
    // In here is where all the logic for my game will go
    public void run() throws FileNotFoundException, UnsupportedEncodingException
    {
        // Used to keep track of time used to draw and update the game
        // This is used to limit the framerate later on
        long startTime;
        long deltaTime;
        
        // the main game loop section
        // game will end if you set done = false;
        boolean done = false; 
        while(!done)
        {
            // determines when we started so we can keep a framerate
            startTime = System.currentTimeMillis();
            
            // all your game rules and move is done in here
            // GAME LOGIC STARTS HERE 
            if (mouse1Pressed)
            {
                if (mx <= WIDTH && mx >= 0 && my <= HEIGHT && my >= 0)
                {
                    if (map[my/blockWidth][mx/blockWidth] == 0)
                    {
                        map[my/blockWidth][mx/blockWidth] = 1;
                    }
                    if (!shift)
                    {
                        mouse1Pressed = false;
                    }
                }
            }
            else if (mouse2Pressed)
            {
                if (mx <= WIDTH && mx >= 0 && my <= HEIGHT && my >= 0)
                {
                    map[my/blockWidth][mx/blockWidth] = 0;
                    if (!shift)
                    {
                        mouse2Pressed = false;
                    }
                }
            }
            else if (save)
            {
                PrintWriter writer = new PrintWriter("levels\\level1.txt", "UTF-8");
                for (int y = 0; y < map.length; y ++)
                {
                    for (int x = 0; x < map[y].length; x ++)
                    {
                        writer.print(map[y][x]);
                    }
                }
                writer.close();
                save = false;
            }

            
            
            // GAME LOGIC ENDS HERE 
            
            // update the drawing (calls paintComponent)
            repaint();
            
            
            
            // SLOWS DOWN THE GAME BASED ON THE FRAMERATE ABOVE
            // USING SOME SIMPLE MATH
            deltaTime = System.currentTimeMillis() - startTime;
            if(deltaTime > desiredTime)
            {
                //took too much time, don't wait
            }else
            {
                try
                {
                    Thread.sleep(desiredTime - deltaTime);
                }catch(Exception e){};
            }
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        // creates a windows to show my game
        JFrame frame = new JFrame("My Game");
       
        // creates an instance of my game
        Main game = new Main();
        // sets the size of my game
        game.setPreferredSize(new Dimension(WIDTH,HEIGHT));
        // adds the game to the window
        frame.add(game);
         
        // sets some options and size of the window automatically
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        // shows the window to the user
        frame.setVisible(true);
        game.addMouseListener(game);
        game.addMouseMotionListener(game);
        frame.addKeyListener(game);
        // starts my game loop
        game.run();
        
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == 1)
        {
            mouse1Pressed = true;
        }
        else if (e.getButton() == 3)
        {
            mouse2Pressed = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == 1)
        {
            mouse1Pressed = false;
        }
        else if (e.getButton() == 3)
        {
            mouse2Pressed = false;
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SHIFT)
        {
            shift = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_S)
        {
            save = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SHIFT)
        {
            shift = false;
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mx = e.getX();
        my = e.getY();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mx = e.getX();
        my = e.getY();
    }
}