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
public class ToiletPortrayal extends FieldPortrayal2D {
	private int x, y, width, height;
	private String toiletColor;
	
	//private Font font = new Font("SansSerif", 0, 18);  // keep it around for efficiency
	
	public ToiletPortrayal(int x, int y, int width, int height, String color){
		super();
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.toiletColor = color;		
	}
	
		
	public void draw(Object object, Graphics2D graphics, DrawInfo2D info){
		//String s = "";
		//graphics.setColor(Color.getColor(toiletColor));
		graphics.setColor(Color.yellow);
		graphics.fillRect(x, y, width, height);
		//graphics.drawRect(x, y, width, height);		
	}
	
}
