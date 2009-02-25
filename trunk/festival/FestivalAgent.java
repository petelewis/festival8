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

public class FestivalAgent extends sim.portrayal.simple.OvalPortrayal2D implements Steppable, Obstacle {
    // Move this into the environment

    private static boolean stageSwitched = false;
    // The agent's ID
    public int id = -1;

    // The agent's color
    private Color agentColor;

    // The agent's present location
    public Double2D agentLocation = null;

    // Is the agent currently in the environment
    public boolean inEnvironment = false;

    // The environment the agent is situated within
    private Continuous2D environment;

    // The population of which the agent is a member
    private AgentPopulation agents;

    // Constants for social forces
    private static final double AP = 6.0;
    private static final double BP = 0.3;
    private static final double AA = 1.5;
    private static final double BA = 9.0;
    private static final double DISTANCE_SCALE = 10.0;
    private static final double TIMESTEP = 0.1;
    private static final double ACCELERATION = 3.0;
    private static final double DESIRED_VELOCITY = 800.0;
    private static final double NOISE = 20.0;
    private double lastDX = 0.0;
    private double lastDY = 0.0;

    // The agent's properties / state
    private int preferredStage;
    public static double HORIZON = 42.0;

    /**
     * The agent's constuctor
     * @param location
     * @param state
     * @param i
     * @param env
     * @param a
     */
    public FestivalAgent(final Double2D location, final int state, int i, Continuous2D env, AgentPopulation a) {
        super(FestivalNoUI.DIAMETER);
        //System.out.println("New agent created with id " + i);


        this.agentLocation = location;

        this.setState(state);
        environment = env;
        agents = a;

        id = i;

        // Half the agents prefer each stage
        if (Math.random() < 0.5) {
            preferredStage = 1;

        } else {
            preferredStage = 2;

        }

    }
    Double2D desiredLocation = null;
    Double2D suggestedLocation = null;
    int steps = 0;

    public Double2D getLocation() {
        return this.agentLocation;
    }

    /**
     * Calculate the agent's goal position
     * @return
     */
    public Double2D getGoalPosition() {
        if (!stageSwitched) {
            if (preferredStage == 1) {
                agentColor = new Color(255, 255, 255);
                return new Double2D(800, 400);

            } else {
                agentColor = new Color(0, 0, 0);
                return new Double2D(10, 400);
            }
        } else {
            if (preferredStage == 2) {
                agentColor = new Color(255, 255, 255);
                return new Double2D(800, 400);

            } else {
                agentColor = new Color(0, 0, 0);
                return new Double2D(10, 400);
            }
        }
    }

    /**
     * Agent's update per step
     * @param state
     */
    public void step(final SimState state) {

//        System.out.println("Agent " + id + " stepping:");
//        System.out.println("    x="+agentLocation.x+", y="+agentLocation.y);

        FestivalNoUI hb = (FestivalNoUI) state;

        Double2D location = agentLocation;//hb.environment.getObjectLocation(this);

        // Randomly every now and then switch stages - this should go in the stage / environment class
        if (Math.random() < 0.00001) {
            stageSwitched = !stageSwitched;
        }

        if (inEnvironment) {
            // Get the agent's goal location
            //suggestedLocation = hb.kMeansEngine.getGoalPosition(intID);
            desiredLocation = getGoalPosition();

//            if (id == 0) {
//                System.out.println("Current Position: "+location.x+","+location.y+", Desired Position: "+desiredLocation.x+","+desiredLocation.y);
//            }

            // Calculate the next move towards the goal.

            // Difference between where I am and where I want to be
            // dx and dy are my desired vector of travel
            double dx = desiredLocation.x - location.x;
            double dy = desiredLocation.y - location.y;

            // Normalise the vector
            double normalisation = Math.pow(dx, 2.0) + Math.pow(dy, 2.0);
            if (normalisation > 0) {
                normalisation = Math.sqrt(normalisation);
                dx /= normalisation;
                dy /= normalisation;
            }

            // Discretise
            dx /= DISTANCE_SCALE;
            dy /= DISTANCE_SCALE;

            dx *= DESIRED_VELOCITY;
            dy *= DESIRED_VELOCITY;

            dx -= (lastDX / TIMESTEP);
            dy -= (lastDY / TIMESTEP);

            dx /= ACCELERATION;
            dy /= ACCELERATION;

            // Social forces
            double sx = 0.0;
            double sy = 0.0;

            Bag obstacles = environment.getObjectsWithinDistance(this.getLocation(), HORIZON);

            // iterate through other agents, determining social forces
            for (Object o : obstacles) {
                Obstacle a = (Obstacle) o;

                if (a instanceof FestivalAgent) {
                    if ((((FestivalAgent) a).inEnvironment) && (!a.equals(this))) {
                        double displacementX = this.getLocation().getX() - a.getLocation().getX();
                        double displacementY = this.getLocation().getY() - a.getLocation().getY();

                        displacementX /= DISTANCE_SCALE;
                        displacementY /= DISTANCE_SCALE;

                        double displacement = Math.sqrt(Math.pow(displacementX, 2.0) + Math.pow(displacementY, 2.0));

                        double forceX = (AP * displacementX / displacement) * Math.exp(-displacement / BP);
                        forceX += (AA * displacementX / displacement) * Math.exp(-displacement / BA);

                        double forceY = (AP * displacementY / displacement) * Math.exp(-displacement / BP);
                        forceY += (AA * displacementY / displacement) * Math.exp(-displacement / BA);

                        sx += forceX;
                        sy += forceY;
                    }
                }
            }

            // make a step dependent on the force
            dx += sx;
            dy += sy;

            // add noise
            dx += (Math.random() * 2.0 - 1.0) * NOISE;
            dy += (Math.random() * 2.0 - 1.0) * NOISE;

            // How much this time step
            dx *= TIMESTEP;
            dy *= TIMESTEP;

            // Actually move the agent, if it's a position it can occupy, otherwise stay still.
            if (hb.acceptablePosition(this, new Double2D(location.x + dx, location.y + dy))) {
                agentLocation = new Double2D(location.x + dx, location.y + dy);
                hb.environment.setObjectLocation(this, agentLocation);

            }

            // Remember
            lastDX = dx;
            lastDY = dy;
        } else {
            // If not yet in the environment (because the entrance is blocked), try to put me in it
            if (hb.acceptablePosition(this, location)) {
                environment.setObjectLocation(this, location);
                inEnvironment = true;
            }
        }


        // Update the agent's colour
        paint = agentColor;


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
