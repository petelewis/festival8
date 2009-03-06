/**
 * 
 */
package festival;

import java.awt.Color;
//import java.awt.Font;
import java.awt.Graphics2D;

import sim.portrayal.DrawInfo2D;
import sim.portrayal.FieldPortrayal2D;

/**
 * @author Paul
 *
 */
public class BarPortrayal extends FieldPortrayal2D {
	private int x, y, width, height;
	private String foodstallColor;
	
	//private Font font = new Font("SansSerif", 0, 18);  // keep it around for efficiency
	
	public BarPortrayal(int x, int y, int width, int height, String color){
		super();
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.foodstallColor = color;		
	}
	
		
	public void draw(Object object, Graphics2D graphics, DrawInfo2D info){
		graphics.setColor(Color.pink);
		//graphics.drawRect(x, y, width, height);		
		graphics.fillRoundRect(x, y, width, height, 5, 5);
		graphics.setColor(Color.black);
		graphics.drawString("BEER", x+20, y+20);
	}
	
}
