
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author kobed6328
 */
public class Block extends Rectangle implements Drawable{

    private Color c = Color.BLACK;
    
    public Block (int x, int y, int width, int height)
    {
        super(x, y, width, height);
    }
    
    @Override
    public void draw(Graphics g) {
        g.setColor(c);
        g.fillRect(x, y, width, height);
    }
}
