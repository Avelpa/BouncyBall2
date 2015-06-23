package blocks;


import imagehelper.ImageHelper;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author kobed6328
 */
public class Block extends Rectangle{

    protected BufferedImage sprite;
    static int sideLength;
    
    public Block (int x, int y, String imagePath)
    {
        super(x, y, sideLength, sideLength);
        try 
        {
            sprite = ImageHelper.resize(ImageHelper.loadImage("images\\" + imagePath + ".png"), sideLength, sideLength);
        }
        catch (Exception e)
        {
            System.err.println("Error loading image: " + e);
            System.exit(1);
        }
    }
    
    public void drawOnGrid(Graphics g) 
    {
        g.drawImage(sprite, x/sideLength*sideLength, y/sideLength*sideLength, null);
    }
    
}
