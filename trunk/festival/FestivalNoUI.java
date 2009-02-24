/*
Copyright 2006 by Sean Luke and George Mason University
Licensed under the Academic Free License version 3.0
See the file "LICENSE" for more information
 *
 * Adapted by Peter Lewis 2009
 */
package festival;

import sim.field.continuous.*;
import sim.engine.*;
import sim.util.*;

public class FestivalNoUI extends SimState {

    // Dimensions of environment - 600??
    public static final double XMIN = 0;
    public static final double XMAX = 400;
    public static final double YMIN = 0;
    public static final double YMAX = 400;

    // Diameter of the agents in the environment
    public static final double DIAMETER = 8;

    // Number of agents etc
    // Maybe replace with MAX_AGENTS or something similar?
    //public static final int NUM_AGENTS = 40;

    public AgentPopulation agentPopulation;


    Double2D[] agentPos;
    Double2D[] targetPos;
    public Continuous2D environment = null;
    

    /** Creates a FestivalNoUI simulation with the given random number seed. */
    public FestivalNoUI(long seed) {
        super(seed);
    }

    boolean conflict(final Object agent1, final Double2D a, final Object agent2, final Double2D b) {
        if (((a.x > b.x && a.x < b.x + DIAMETER) ||
                (a.x + DIAMETER > b.x && a.x + DIAMETER < b.x + DIAMETER)) &&
                ((a.y > b.y && a.y < b.y + DIAMETER) ||
                (a.y + DIAMETER > b.y && a.y + DIAMETER < b.y + DIAMETER))) {
            return true;
        }
        return false;
    }

    boolean acceptablePosition(final Object agent, final Double2D location) {
        if (location.x < DIAMETER / 2 || location.x > (XMAX - XMIN)/*environment.getXSize()*/ - DIAMETER / 2 ||
                location.y < DIAMETER / 2 || location.y > (YMAX - YMIN)/*environment.getYSize()*/ - DIAMETER / 2) {
            return false;
        }
        Bag misteriousObjects = environment.getObjectsWithinDistance(location, /*Strict*/ Math.max(2 * DIAMETER, 2 * DIAMETER));
        if (misteriousObjects != null) {
            for (int i = 0; i < misteriousObjects.numObjs; i++) {
                if (misteriousObjects.objs[i] != null && misteriousObjects.objs[i] != agent) {
                    Object ta = (FestivalAgent) (misteriousObjects.objs[i]);
                    if (conflict(agent, location, ta, environment.getObjectLocation(ta))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void start() {
        super.start();  // clear out the schedule

//        agentPos = new Double2D[NUM_AGENTS];
//        for (int i = 0; i < NUM_AGENTS; i++) {
//            agentPos[i] = new Double2D();
//        }

    

        environment = new Continuous2D(8.0, XMAX - XMIN, YMAX - YMIN);

        agentPopulation = new AgentPopulation(environment, schedule, this);

        

        // Generate agents at regular intervals
        schedule.scheduleRepeating(agentPopulation);
    }

    public static void main(String[] args) {
        doLoop(FestivalNoUI.class, args);
        System.exit(0);
    }
}
