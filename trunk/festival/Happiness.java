package festival;
import sim.util.*;
import java.util.*;


public class Happiness {
	private static List<Double2D> setupStages(){
		List<Double2D> stages = new ArrayList<Double2D>();
		stages.add(new Double2D(790, 400));
		stages.add(new Double2D(10, 400));
		return stages;
	}
	
	// positions
	public static List<Double2D> STAGES = setupStages();
	public static Double2D TOILET = new Double2D(790,100);
	public static Double2D BAR = new Double2D(400, 400);
	public static Double2D BURGERKING = new Double2D(10,100);
		
    // toilet threshold
    public double toiletThresh;
    
    // toilet state
    public double toiletState;

    // toilet threshold
    public double foodThresh;
    
    // toilet state
    public double foodState;

    // toilet threshold
    public double drinkThresh;
    
    // toilet state
    public double drinkState;
    
    public Happiness(){
    	toiletThresh = 50 - 50 * Math.random();
    	foodThresh =  50 * (Math.random() -0.5);
    	drinkThresh =  50 * (Math.random() -0.5);
    	
    	toiletState = 50 * Math.random();
    	foodState = 50 + 50 * Math.random();
    	drinkState = 50 + 50 * Math.random();
    	
    	
    }
    
    public Double2D getNewGoal(int preferredStage){
    	if(toiletState > toiletThresh){
    		return TOILET;
    	} else if(foodState < foodThresh){
    		return BURGERKING;
    	} else if(drinkState < drinkThresh){
    		return BAR;
    	} else {
    		return STAGES.get(preferredStage - 1 );
    	}
    }
    
    public void updateStep(){
    	foodState -= Math.random();
    	drinkState -= Math.random();
    	toiletState += Math.random();
    	
    }
    
    public static void exchangeStages(){
    	// dirty little bitch
    	Double2D t = STAGES.get(0);
    	STAGES.set(0, STAGES.get(1));
    	STAGES.set(1, t);
    }

}
