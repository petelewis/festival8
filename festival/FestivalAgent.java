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
    private AgentPopulation agents;
    private static final double AP = 2.0;
    private static final double BP = 0.1;
    private static final double AA = 0.5;
    private static final double BA = 3.0;
    private double lastDX = 0.0;
    private double lastDY = 0.0;

    public FestivalAgent(final Double2D location, final int state, int i, Continuous2D env, AgentPopulation a) {
        super(FestivalNoUI.DIAMETER);

        this.agentLocation = location;

        this.setState(state);
        environment = env;
        agents = a;

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
            double timestep = 0.1;
            double acceleration = 3.0;
            double desiredVelocity = 2.0;

            dx *= desiredVelocity;
            dy *= desiredVelocity;

            dx -= (lastDX / timestep);
            dy -= (lastDY / timestep);


            dx /= acceleration;
            dy /= acceleration;


            double sx = 0.0;
            double sy = 0.0;
            // iterate through other agents, determining social forces

            for (FestivalAgent a : agents) {
                if ((a.inEnvironment) && (!a.equals(this))) {
                    double displacementX = a.agentLocation.x - agentLocation.getX();
                    double displacementY = a.agentLocation.y - agentLocation.getY();

                    double displacement = Math.sqrt(Math.pow(displacementX, 2.0) + Math.pow(displacementY, 2.0));
                    displacement /= 100;

                    double forceX = (AP * displacementX / displacement) * Math.exp(-displacement / BP);
                    forceX += (AA * displacementX / displacement) * Math.exp(-displacement / BA);

                    double forceY = (AP * displacementY / displacement) * Math.exp(-displacement / BP);
                    forceY += (AA * displacementY / displacement) * Math.exp(-displacement / BA);

                    sx += forceX;
                    sy += forceY;
                }
            }


            // make a step dependent on the force
            dx += sx;
            dy += sy;


            //System.out.println("dx = " + dx + ", dy = " + dy + "     sx = "+sx + ", sy = " + sy);
            // add noise


            dx *= timestep;
            dy *= timestep;


            

            //System.out.println(dx + ", " + dy);

            // Actually move the agent, if it's a position it can occupy, otherwise stay still.
            if (hb.acceptablePosition(this, new Double2D(location.x + dx, location.y + dy))) {
                agentLocation = new Double2D(location.x + dx, location.y + dy);
                hb.environment.setObjectLocation(this, agentLocation);
            }

            lastDX = dx;
            lastDY = dy;
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
