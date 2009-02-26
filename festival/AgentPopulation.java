package festival;

import java.util.*;

import sim.field.continuous.*;
import sim.util.*;
import sim.engine.*;

/**
 * A population of agents.
 * Can generate new ones and keep track of those so far.
 * @author pete
 */
public class AgentPopulation extends ArrayDeque<FestivalAgent> implements Steppable {

    private Continuous2D environment;
    private Schedule schedule;
    private FestivalNoUI sim;
    private static final double STARTX = 300.0;
    private static final double STARTY = 10.0;
    private static final int MIN_TIME_BETWEEN_NEW_AGENTS = 2;
    private static final int MAX_NUMBER_OF_AGENTS = 700;
    private static int timeBetweenAgents = 0;

    // Count the number of agents generated, to give agents an ID number
    private static int count = 0;

    /**
     * Constructor.
     * @param env The environment the agent will be placed in.
     * @param sch The scheduler which will fire the agent's step method.
     */
    public AgentPopulation(Continuous2D env, Schedule sch, FestivalNoUI s) {
        environment = env;
        schedule = sch;
        sim = s;
    }

    public void step(SimState state) {
        if (count < MAX_NUMBER_OF_AGENTS) {
            timeBetweenAgents++;
            if ((Math.random() < 0.05) && (timeBetweenAgents > MIN_TIME_BETWEEN_NEW_AGENTS)) {
                generateNew();
                timeBetweenAgents = 0;
            }
        }
    }

    /**
     *  Here we generate a new agent
     */
    public void generateNew() {
        // The agent's location
        Double2D loc = null;

        // A reference for the agent
        FestivalAgent agent = null;

        // The starting location
        loc = new Double2D(STARTX, STARTY);

        // Go ahead and generate it
        agent = new FestivalAgent(loc, FestivalAgent.AGENT, count, environment, this);
        count++;

        // If the location is acceptable, place it in the environment
        if (sim.acceptablePosition(agent, loc)) {
            environment.setObjectLocation(agent, loc);
            agent.inEnvironment = true;
        }

        schedule.scheduleRepeating(agent);

        // And finally add it to the population
        add(agent);
    }
}
