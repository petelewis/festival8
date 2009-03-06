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
public class StagePortrayal extends FieldPortrayal2D {
	private int x, y, width, height;
	private String stageColor;
	
	public StagePortrayal(int x, int y, int width, int height, String color){
		super();
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;	
		this.stageColor = "red";
	}
	
		
	public void draw(Object object, Graphics2D graphics, DrawInfo2D info){		
		graphics.setColor(Color.red);
		//graphics.drawRect(x, y, width, height);		
		graphics.fillRect(x, y, width, height);
	}
	
}
