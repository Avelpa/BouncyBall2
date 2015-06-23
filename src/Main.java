
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import javax.swing.JComponent;
import javax.swing.JFrame;
import blocks.*;
// make sure you rename this class if you are doing a copy/paste
public class Main extends JComponent implements MouseListener, KeyListener, MouseMotionListener, MouseWheelListener{

    // Height and Width of our game
    static final int WIDTH = 1000;
    static final int HEIGHT = 600;
    
    // sets the framerate and delay for our game
    // you just need to select an approproate framerate
    long desiredFPS = 60;
    long desiredTime = (1000)/desiredFPS;
    
    int sideLength = WIDTH/25;
    
    static int playerWidth = blockWidth/2;
    static double playerX, playerY;
    double playerSpeedX = 0;
    double playerSpeedY = 0;
    
    double flyingSpeed = 0;
    
    boolean initialLoad = true;
    
    static Block[][] map = new Block[HEIGHT/blockWidth][WIDTH/blockWidth];
    /*
    BufferedImage blockNormal = ImageHelper.resize(ImageHelper.loadImage("images\\block_normal.png"), blockWidth, blockWidth);
    BufferedImage ballNormal = ImageHelper.resize(ImageHelper.loadImage("images\\ball_normal.png"), playerWidth, playerWidth);
    BufferedImage starNormal = ImageHelper.resize(ImageHelper.loadImage("images\\star_normal.png"), blockWidth, blockWidth);
    BufferedImage blockBreakable = ImageHelper.resize(ImageHelper.loadImage("images\\block_breakable.png"), blockWidth, blockWidth);
    BufferedImage blockHighjump = ImageHelper.resize(ImageHelper.loadImage("images\\block_highjump.png"), blockWidth, blockWidth);
    BufferedImage spikesHalf = ImageHelper.resize(ImageHelper.loadImage("images\\spikes_half.png"), blockWidth, blockWidth);
    BufferedImage flyingBlockLeft = ImageHelper.resize(ImageHelper.loadImage("images\\flying_block.png"), blockWidth, blockWidth);
    BufferedImage flyingBlockRight = ImageHelper.horizontalflip(ImageHelper.resize(ImageHelper.loadImage("images\\flying_block.png"), blockWidth, blockWidth));
    BufferedImage blockBarrier = ImageHelper.horizontalflip(ImageHelper.resize(ImageHelper.loadImage("images\\block_barrier.png"), blockWidth, blockWidth));
    */
    int numStars = 0;
    
    int[] allBlocks = {1,2,3,4,5,6,7,-1, 100};
    /*
        1 = blockNormal
        2 = breakBlock
        3 = highjump
        4 = spikes
        5 = flying block left
        6 = flying block right
        7 = barrier
        -1 = player
        100 = star
    */
    
    
    int selectedBlock = 0;
    
    int currentLevel = 15;
    
    int selectRegionX, selectRegionY;
    
    //ArrayList<Block> blocks = new ArrayList();
    
    
   
    double bounceTime = Math.sqrt((800/25)*2*2/0.2);
    final double grav = 2*blockWidth*2/bounceTime/bounceTime; // mimick real game's gravity -- comparison is 0.2 grav / 800/25 block width
    //static final double grav = 0.2/(25.0/800)*(1.0/blockWidth);
    
    //final double jumpSpeed = -(4.5/0.2)*grav;
    final double jumpSpeed = -Math.sqrt(2*grav*((double)blockWidth*2-(double)playerWidth/1.2)); // bounce 2 blocks high
    
    final double moveSpeed = (blockWidth*3+playerWidth/2)/bounceTime/2; // bounce three blocks max
    
    boolean mouse1Pressed = false;
    boolean mouse2Pressed = false;
    boolean shift = false;
    int mx, my;
    int mwheel;
    boolean save;
    boolean run = true;
    boolean levelLoaded = false;
    boolean select;
    boolean selectDrag;
    boolean selectRelease;
    
    int[] selectRegionPoints;
    
    boolean w, a, s, d;
    
    // drawing of the game happens in here
    // we use the Graphics object, g, to perform the drawing
    // NOTE: This is already double buffered!(helps with framerate/speed)
    @Override
    public void paintComponent(Graphics g)
    {
        // always clear the screen first!
        g.clearRect(0, 0, WIDTH, HEIGHT);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        // GAME DRAWING GOES HERE 
        if (!run)
        {
            g.setColor(Color.BLACK);
            for (int i = 0; i < WIDTH; i += blockWidth)
            {
                g.drawLine(i, 0, i, HEIGHT);
            }
            for (int i = 0; i < HEIGHT; i += blockWidth)
            {
                g.drawLine(0, i, WIDTH, i);
            }
        }
        for (int y = 0; y < map.length; y ++) // draw map
        {
            for (int x = 0; x < map[y].length; x ++)
            {
                int bY = y*blockWidth;
                int bX = x*blockWidth;
                switch(map[y][x])
                {
                    case 1:  // block
                        g.drawImage(blockNormal, bX, bY, null);
                        break;
                    case -1: // player
                        if (!run) // draw at default spot
                        {
                            //g.fillOval(bX+playerWidth-playerWidth/2, bY + playerWidth-playerWidth/2, playerWidth, playerWidth);
                            g.drawImage(ballNormal, bX+playerWidth-playerWidth/2, bY+playerWidth-playerWidth/2, null);
                        }
                        else // draw at played spot
                        {
                            //g.fillOval((int)playerX, (int)playerY, playerWidth, playerWidth);
                            g.drawImage(ballNormal, (int)playerX, (int)playerY, null);
                        }
                        break;
                    case 2: // green block
                        g.drawImage(blockBreakable, bX, bY, null);
                        break;
                    case 3: // blue block (high jump)
                        g.drawImage(blockHighjump, bX, bY, null);
                        break;
                    case 4: // spikes normal
                        g.drawImage(spikesHalf, bX, bY, null);
                        break;
                    case 5: // flyingblock left
                        g.drawImage(flyingBlockLeft, bX, bY, null);
                        break;
                    case 6:
                        g.drawImage(flyingBlockRight, bX, bY, null);
                        break;
                    case 7:
                        g.drawImage(blockBarrier, bX, bY, null);
                        break;
                    case 100: // star
                        g.drawImage(starNormal, bX, bY, null);
                        break;
                }
            }
        }
        if (!run && !select) // draw current selected block
        {
            switch(allBlocks[selectedBlock])
            {
                case 1:  // black block
                    //g.setColor(Color.GRAY);
                    //g.fillRect(mx-blockWidth/2, my-blockWidth/2, blockWidth, blockWidth);
                    g.drawImage(blockNormal, mx-blockWidth/2, my-blockWidth/2, null);
                    break;
                case -1: // player
                    //g.setColor(Color.YELLOW);
                    //g.fillOval(mx+playerWidth-playerWidth/2-playerWidth, my + playerWidth-playerWidth/2-playerWidth, playerWidth, playerWidth);
                    g.drawImage(ballNormal, mx+playerWidth-playerWidth/2-playerWidth, my+playerWidth-playerWidth/2-playerWidth, null);
                    break;
                case 2: // green block
                    g.drawImage(blockBreakable, mx-blockWidth/2, my-blockWidth/2, null);
                    break;
                case 3: // blue block
                    g.drawImage(blockHighjump, mx-blockWidth/2, my-blockWidth/2, null);
                    break;
                case 4: // blue block
                    g.drawImage(spikesHalf, mx-blockWidth/2, my-blockWidth/2, null);
                    break;
                case 5: // flying block left
                    g.drawImage(flyingBlockLeft, mx-blockWidth/2, my-blockWidth/2, null);
                    break;
                case 6: // flying block right
                    g.drawImage(flyingBlockRight, mx-blockWidth/2, my-blockWidth/2, null);
                    break;
                case 7:
                    g.drawImage(blockBarrier, mx-blockWidth/2, my-blockWidth/2, null);
                    break;
                case 100: // star
                    g.drawImage(starNormal, mx-blockWidth/2, my-blockWidth/2, null);
                    break;
            }
        }
        else if (!run && selectDrag && select)
        {
            int startX, startY, endX, endY;
            if (mx < selectRegionX)
            {
                startX = mx/blockWidth*blockWidth;
                endX = selectRegionX;
            }
            else
            {
                startX = selectRegionX;
                endX = mx/blockWidth*blockWidth;
            }
            if (my < selectRegionY)
            {
                startY = my/blockWidth*blockWidth;
                endY = selectRegionY;
            }
            else
            {
                startY = selectRegionY;
                endY = my/blockWidth*blockWidth;
            }
            g.setColor(Color.GREEN);
            g.drawRect(startX, startY, endX-startX, endY-startY);
        }
        
        g.drawString("LEVEL: " + currentLevel, WIDTH/2, HEIGHT/4);
        // GAME DRAWING ENDS HERE
    }
    
    
    // The main game loop
    // In here is where all the logic for my game will go
    public void run() throws FileNotFoundException, UnsupportedEncodingException, IOException
    {
        // Used to keep track of time used to draw and update the game
        // This is used to limit the framerate later on
        long startTime;
        long deltaTime;
        
        //loadMap("level" + currentLevel);
        // the main game loop section
        // game will end if you set done = false;
        boolean done = false; 
        while(!done)
        {
            // determines when we started so we can keep a framerate
            startTime = System.currentTimeMillis();
            
            // all your game rules and move is done in here
            // GAME LOGIC STARTS HERE 
            if (!levelLoaded)
            {
                levelLoaded = true;
                loadMap("level" + currentLevel);
            }
            
            if (!run) // editing
            {
                
                if (mouse1Pressed)
                {
                    if (mx <= WIDTH && mx >= 0 && my <= HEIGHT && my >= 0)
                    {
                        if (!select) // normal edit (not select region)
                        {
                            if (map[my/blockWidth][mx/blockWidth] == 0)
                            {
                                map[my/blockWidth][mx/blockWidth] = allBlocks[selectedBlock];
                                if (allBlocks[selectedBlock] == -1) // player
                                {
                                    playerX = mx/blockWidth*blockWidth+playerWidth-playerWidth/2;
                                    playerY = my/blockWidth*blockWidth+playerWidth-playerWidth/2;
                                    //bX+playerWidth-playerWidth/2, bY + playerWidth-playerWidth/2, playerWidth, playerWidth);
                                }
                            }
                            if (!shift)
                            {
                                mouse1Pressed = false;
                            }
                        }
                        else if (!selectDrag)
                        {
                            selectRegionPoints = null;
                            selectRegionX = mx/blockWidth*blockWidth; // select starting point
                            selectRegionY = my/blockWidth*blockWidth; 
                            selectDrag = true;
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
                
                if (mwheel != 0)
                {
                    selectedBlock += mwheel;
                    if (selectedBlock < 0)
                    {
                        selectedBlock = allBlocks.length-1;
                    }
                    else if (selectedBlock >= allBlocks.length)
                    {
                        selectedBlock = 0;
                    }
                }
                
                else if (save)
                {
                    saveMap();
                }
                if (selectRelease) // finished select
                {
                    
                    selectCurrentRegion();
                    
                    selectDrag = false;
                    selectRelease = false;
                }
                
                
            }  //actual run
            else
            {
                int collideBlock1, collideBlock2;
                
                if (a || d)
                {
                    flyingSpeed = 0;
                }
                // move horizontally
                if (a)
                {
                    playerSpeedX = -moveSpeed;
                } 
                if (d)
                {
                    playerSpeedX = moveSpeed;
                }
                if (!d && !a || d && a)
                {
                    playerSpeedX = 0;
                }
                if (flyingSpeed == 0)
                {
                    playerX += playerSpeedX;
                }
                else
                {
                    playerX += flyingSpeed;
                }
                
                if (playerX+playerWidth >= WIDTH || playerX+playerWidth <= 0) // out of bounds
                {
                    levelLoaded = false;
                    continue;
                }
                
                // left top
                if ((collideBlock1=map[((int)playerY)/blockWidth][((int)playerX)/blockWidth]) > 0)
                {
                    collideLeft (collideBlock1, ((int)playerY)/blockWidth,((int)playerX)/blockWidth);
                }
                // left bottom
                if ((collideBlock1=map[((int)playerY+playerWidth)/blockWidth][((int)playerX)/blockWidth]) > 0)
                {
                    collideLeft (collideBlock1, ((int)playerY+playerWidth)/blockWidth, ((int)playerX)/blockWidth);
                }
                // right top
                if ((collideBlock1=map[((int)playerY)/blockWidth][((int)playerX+playerWidth)/blockWidth]) > 0)
                {
                    collideRight (collideBlock1, ((int)playerY)/blockWidth,((int)playerX+playerWidth)/blockWidth);
                }
                // right bottom
                if ((collideBlock1=map[((int)playerY+playerWidth)/blockWidth][((int)playerX+playerWidth)/blockWidth]) > 0)
                {
                    collideRight (collideBlock1, ((int)playerY+playerWidth)/blockWidth, ((int)playerX+playerWidth)/blockWidth);
                }
                
                if (flyingSpeed == 0)
                {
                    if (playerY != 0)
                    {
                        playerSpeedY += grav;
                        playerY += playerSpeedY+0.5*grav;
                        if (playerY+playerWidth >= HEIGHT || playerY <= 0) // out of bounds
                        {
                            levelLoaded = false;
                            continue;
                        }
                    }
                }
                
                
                if (playerSpeedY > 0)
                {
                    // collide bottom LEFT and RIGHT (for breakable blocks -- break 2 at a time)
                    if ((collideBlock1=map[((int)playerY+playerWidth)/blockWidth][(int)playerX/blockWidth]) == 2 && (map[((int)playerY+playerWidth)/blockWidth][((int)playerX+playerWidth)/blockWidth]) == 2)
                    {
                        map[((int)playerY+playerWidth)/blockWidth][((int)playerX+playerWidth)/blockWidth] = 0; // sets one on right to 0 immediately
                        collideDown(collideBlock1, ((int)playerY+playerWidth)/blockWidth,((int)playerX)/blockWidth); // collides only with left one
                    }
                    // bottom one at a time
                    else 
                    {
                        // bottom left
                        if ((collideBlock1=map[((int)playerY+playerWidth)/blockWidth][(int)playerX/blockWidth]) > 0)
                        {
                            collideDown (collideBlock1, ((int)playerY+playerWidth)/blockWidth,((int)playerX)/blockWidth);
                        }
                        // bottom right
                        if ((collideBlock1=map[((int)playerY+playerWidth)/blockWidth][((int)playerX+playerWidth)/blockWidth]) > 0)
                        {
                            collideDown (collideBlock1, ((int)playerY+playerWidth)/blockWidth, ((int)playerX+playerWidth)/blockWidth);
                        }
                    }
                }
                else if (playerSpeedY < 0)
                {
                    
                    // top left
                    if ((collideBlock1=map[((int)playerY)/blockWidth][((int)playerX)/blockWidth]) > 0)
                    {
                        collideUp (collideBlock1, ((int)playerY)/blockWidth,((int)playerX)/blockWidth);
                    }
                    // top right
                    if ((collideBlock1=map[((int)playerY)/blockWidth][((int)playerX+playerWidth)/blockWidth]) > 0)
                    {
                        collideUp (collideBlock1, ((int)playerY)/blockWidth, ((int)playerX+playerWidth)/blockWidth);
                    }

                    //check numStars 0, switch level
                    if (numStars == 0) // inside run loop cause don't want to switch levels in editor if no stars placed
                    {
                        levelLoaded = false;
                        currentLevel ++;
                        continue;
                    }
                }
            }
            mwheel = 0;
            
            
            
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
    
    public void loadMap(String mapName) throws FileNotFoundException, IOException
    {
        BlockBuilder.setBlockSideLength(sideLength);
        playerX = 0;
        playerY = 0;
        playerSpeedX = 0;
        playerSpeedY = 0;
        numStars = 0;
        flyingSpeed = 0;
        
        String info;
        BufferedReader br = new BufferedReader(new FileReader("levels\\" + mapName + ".txt"));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            info = sb.toString();
        } finally {
            br.close();
        }
        processInfo(info);
        
        
    }
    
    public void saveMap() throws FileNotFoundException, UnsupportedEncodingException
    {
        try (PrintWriter writer = new PrintWriter("levels\\level" + currentLevel + ".txt", "UTF-8")) {
            for (int y = 0; y < map.length; y ++)
            {
                for (int x = 0; x < map[y].length; x ++)
                {
                    writer.print(map[y][x] + ":");
                }
            }
        }
        save = false;
    }
    
    public void processInfo(String info)
    {
        String[] points = info.split(":");
        for (int y = 0; y < map.length; y ++)
        {
            for (int x = 0; x < map[y].length; x ++)
            {
                switch (Integer.parseInt(points[y*map[y].length+x]))
                {
                    case 1:
                        map[y][x] = new NormalBlock(x*sideLength, y*sideLength, "block_normal");
                        break;
                    case 2:
                        map[y][x] = new BreakableBlock(x*sideLength, y*sideLength, "block_breakable");
                        break;
                    case 3:
                        map[y][x] = new BreakableBlock(x*sideLength, y*sideLength, "block_highjump");
                        break;
                    case 4:
                        map[y][x] = new BreakableBlock(x*sideLength, y*sideLength, "spikes_half");
                        break;
                }
                /*
                map[y][x] = Integer.parseInt(points[y*map[y].length+x]);
                
                if (map[y][x] == -1)
                {
                    playerX = x*blockWidth+playerWidth-playerWidth/2;
                    playerY = y*blockWidth+playerWidth-playerWidth/2;
                }
                else if (map[y][x] == 100)
                {
                    numStars ++;
                }*/
            }
        }
    }
    
    public static void selectCurrentRegion()
    {
        
    }
    
    public void collideLeft(int collideBlock, int bYIndex, int bXIndex)
    {
        switch (collideBlock)
        {
            case 2: // break block
            case 3: // high jump
            case 4: // spikes half
            case 5: // flying block left
            case 6: // flying block right
            case 1: // blockNormal
                playerX = (int)playerX/blockWidth*blockWidth+blockWidth;
                playerSpeedX = 0;
                flyingSpeed = 0;
                break;
            case 100: // star
                collideAnySide(collideBlock, bYIndex, bXIndex);
                break;
        }
        
    }
    
    public void collideRight(int collideBlock, int bYIndex, int bXIndex)
    {
        switch (collideBlock)
        {
            case 2: // break block
            case 3: // high jump
            case 4: // spikes half
            case 5: // flying block left
            case 6: // flying block right
            case 1: // blockNormal
                playerX = ((int)playerX+playerWidth)/blockWidth*blockWidth-playerWidth-1;
                playerSpeedX = 0;
                flyingSpeed = 0;
                break;
            case 100: // star
                collideAnySide(collideBlock, bYIndex, bXIndex);
                break;
        }
        
    }
    
    public void collideUp(int collideBlock, int bYIndex, int bXIndex)
    {
        switch (collideBlock)
        {
            case 2: // break block
            case 3: // high jump
            case 4: // spikes half
            case 5: // flying block left
            case 6: // flying block right
            case 1: // blockNormal
                playerY = (int)playerY/blockWidth*blockWidth+blockWidth;
                playerSpeedY = 0;
                break;
            case 100: // star
                collideAnySide(collideBlock, bYIndex, bXIndex);
                break;
        }
    }
    
    public void collideDown(int collideBlock, int bYIndex, int bXIndex)
    {
        switch (collideBlock)
        {
            case 2: // break block
                map[bYIndex][bXIndex] = 0;
            case 1: // normalBlock
                
                playerY = ((int)playerY+playerWidth)/blockWidth*blockWidth-playerWidth-1; // offset by one because dumb grid
                playerSpeedY = jumpSpeed;
                break;
            case 3: // high jump
                playerY = ((int)playerY+playerWidth)/blockWidth*blockWidth-playerWidth-1; // offset by one because dumb grid
                playerSpeedY = jumpSpeed*1.6;
                break;
            case 4: // spikes half
                levelLoaded = false;
                break;
            case 5: // flying block left
                playerY = bYIndex*blockWidth+blockWidth/2-playerWidth/2;
                playerX = bXIndex*blockWidth-playerWidth;
                flyingSpeed = -10;
                playerSpeedY = 0;
                break;
            case 6: // flying block right
                playerY = bYIndex*blockWidth+blockWidth/2-playerWidth/2;
                playerX = bXIndex*blockWidth+blockWidth;
                flyingSpeed = 10;
                playerSpeedY = 0;
                break;
            case 100: // star
                collideAnySide(collideBlock, bYIndex, bXIndex);
                break;
        }
    }
    
    public void collideAnySide(int collideBlock, int bYIndex, int bXIndex)
    {
        switch (collideBlock)
        {
            case 100:
                numStars --;
                map[bYIndex][bXIndex] = 0;
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException, IOException {
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
        frame.addMouseWheelListener(game);
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
            selectRelease = true;
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
        if (e.getKeyCode() == KeyEvent.VK_R)
        {
            run = run?false:true;
            levelLoaded = false;
            initialLoad = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_CONTROL)
        {
            select = true;
        }
        
        if (e.getKeyCode() == KeyEvent.VK_W)
        {
            w = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_A)
        {
            a = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_S)
        {
            s = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_D)
        {
            d = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SHIFT)
        {
            shift = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_CONTROL)
        {
            select = false;
        }
        
        if (e.getKeyCode() == KeyEvent.VK_W)
        {
            w = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_A)
        {
            a = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_S)
        {
            s = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_D)
        {
            d = false;
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

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        mwheel = e.getWheelRotation();
    }
}

