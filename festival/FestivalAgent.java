/*
Copyright 2006 by Sean Luke and George Mason University
Licensed under the Academic Free License version 3.0
See the file "LICENSE" for more information

Adapted by Peter Lewis 2009
 */
package festival;

import sim.util.*;
import sim.engine.*;
import sim.field.continuous.*;
import java.awt.*;

public class FestivalAgent extends sim.portrayal.simple.OvalPortrayal2D implements Steppable {

    public int id = -1;
    protected Color agentColor = new Color(0, 0, 0);
    public Double2D agentLocation = null;
    public boolean inEnvironment = false;
    private Continuous2D environment;

    public FestivalAgent(final Double2D location, final int state, int i, Continuous2D env) {
        super(FestivalNoUI.DIAMETER);

        this.agentLocation = location;
        this.setState(state);
        environment = env;

        id = i;
    }

    Double2D desiredLocation = null;
    Double2D suggestedLocation = null;
    int steps = 0;

    /**
     * Calculate the agent's goal position
     * @return
     */
    public Double2D getGoalPosition() {

        // Explore!
        if (id % 2 == 0) {
            return new Double2D(600, 300);
        } else {
            return new Double2D(300, 600);
        }
    }

    /**
     * Agent's update per step
     * @param state
     */
    public void step(final SimState state) {

        //System.out.println("Agent " + id + " stepping.");

        FestivalNoUI hb = (FestivalNoUI) state;

        Double2D location = agentLocation;//hb.environment.getObjectLocation(this);



        if (inEnvironment) {
            // Get the agent's goal location
            //suggestedLocation = hb.kMeansEngine.getGoalPosition(intID);
            desiredLocation = getGoalPosition();

//            if (id == 0) {
//                System.out.println("Current Position: "+location.x+","+location.y+", Desired Position: "+desiredLocation.x+","+desiredLocation.y);
//            }

            // Calculate the next move towards the goal.

            // Difference between where I am and where I want to be
            // dx and dy become my next step
            double dx = desiredLocation.x - location.x;
            double dy = desiredLocation.y - location.y;

            // make a small step
            dx *= 0.01;
            dy *= 0.01;

            // Actually move the agent, if it's a position it can occupy, otherwise stay still.
            if (hb.acceptablePosition(this, new Double2D(location.x + dx, location.y + dy))) {
                agentLocation = new Double2D(location.x + dx, location.y + dy);
                hb.environment.setObjectLocation(this, agentLocation);
            }
        } else {
            // Not yet in the environment - try to put me in it
            if (hb.acceptablePosition(this, location)) {
                environment.setObjectLocation(this, location);
                inEnvironment = true;
            }

        }
    }

// application specific variables
    public static final int AGENT = 0;
    protected int agentState;

    public int getState() {
        return agentState;
    }

    void setState(final int agentState) {
        // set the oval's color
        paint = agentColor;
    }
}
