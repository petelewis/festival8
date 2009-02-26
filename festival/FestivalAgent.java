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
import java.util.ArrayList;

public class FestivalAgent extends sim.portrayal.simple.OvalPortrayal2D implements Steppable, Obstacle {
    // Move this into the environment

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
    private static final double DESIRED_VELOCITY = 80.0;
    private static final double NOISE = 30.0;
    private static final double ANGLE_MAX = Math.PI / 4;
    private static final double STAGE_RADIUS = 3.0;
    private double lastDX = 0.0;
    private double lastDY = 0.0;
    //private double threshold;

    // The agent's properties / state
    private int intention;
    private int preferredStage;
    private Double2D currentGoal;
    private Double2D startLocation;
    private boolean measuring;
    private int timeMeasured;
    public static double HORIZON = 42.0;

    // The agent's sigma stuff
    private ArrayList<Sigma> sigmas = new ArrayList<Sigma>();
    private Sigma currentSigma;
    private static final int POPULATION_SIZE = 20;
    private static final double MIN_SIGMA = -5.0;
    private static final double MAX_SIGMA = 5.0;
    Double2D desiredLocation = null;
    Double2D suggestedLocation = null;
    int steps = 0;

    /**
     * The agent's constuctor
     * @param location
     * @param state
     * @param i
     * @param env
     * @param a
     */
    public FestivalAgent(final Double2D location, final int state, int myid, Continuous2D env, AgentPopulation a) {
        super(FestivalNoUI.DIAMETER);
        //System.out.println("New agent created with id " + myid);
        measuring = false;

        this.agentLocation = location;

        this.setState(state);
        environment = env;
        agents = a;

        id = myid;

        // Band agents
        if (id < 5) {
            agentColor = new Color(0, 0, 255);
            preferredStage = 1;
        } else if (id < 10) {
            agentColor = new Color(0, 0, 255);
            preferredStage = 2;
        } else if (Math.random() < 0.5) {    // Half the agents prefer each stage
            agentColor = new Color(255, 255, 255);
            preferredStage = 1;
        } else {
            agentColor = new Color(0, 0, 0);
            preferredStage = 2;
        }
        
		if (!agents.stagePreferencesSwitched) {
			setNewIntention(preferredStage);
		} else {
			if (preferredStage == 1) {
				setNewIntention(2);
			}
			if (preferredStage == 2) {
				setNewIntention(1);
			}
		}


        // Sigma stuff
        for (int i = 0; i < POPULATION_SIZE; i++) {
            sigmas.add(new Sigma(MIN_SIGMA, MAX_SIGMA));
        }

        // Set the initial goal
        setNewGoal(getStageLocation(intention));


        System.out.println("Agent " + id + ": intention = " + intention + ", goal = " + getCurrentGoal().x + ", " + getCurrentGoal().y);
    }

    public Double2D getLocation() {
        return this.agentLocation;
    }

    public Double2D getCurrentGoal() {
        return currentGoal;
    }

    public int getCurrentIntention() {
        return intention;
    }

    public void setNewIntention(int x) {
        intention = x;

        setNewGoal(getStageLocation(x));
    }

    public void setNewGoal(Double2D d) {
        // Get a new sigma value to try this time
        for (Sigma s : sigmas) {
            if (!s.tested) {
                currentSigma = s;
            }
        }

        currentGoal = d;
        startMeasuring();
    }

    public void startMeasuring() {
        if (id >= 10) {

            // Record the location of where we were when we started measuring
            startLocation = agentLocation;
            timeMeasured = 0;
            measuring = true;

            if (preferredStage == 1) {
                agentColor = new Color(255, 255, 255);
            } else {
                agentColor = new Color(0, 0, 0);
            }

            if (id == 10) {
                System.out.println("Starting measuring... ");
            }
        }
    }

    public void completeMeasuring() {
        if (id >= 10) {
            measuring = false;

            agentColor = new Color(255, 0, 0);

            // Fill in the current individual's payoff information
            currentSigma.setFitness(-timeMeasured);

            if (id == 10) {
                System.out.println("Completed measuring.  Sigma was " + currentSigma.getSigma() + ".  Time taken = " + timeMeasured);
            }


            // Only do the evostep if the whole population's fitness information is full
            boolean doGAstep = true;
            for (Sigma s : sigmas) {
                if (!s.tested) {
                    doGAstep = false;
                }
            }

            if (doGAstep) {
                // Then do some tournament selection...

                // Select four at random from the population
                Sigma s1 = sigmas.get((int) (Math.random() * (double) sigmas.size()));
                Sigma s2 = sigmas.get((int) (Math.random() * (double) sigmas.size()));
                Sigma s3 = sigmas.get((int) (Math.random() * (double) sigmas.size()));
                Sigma s4 = sigmas.get((int) (Math.random() * (double) sigmas.size()));


                // Pointers for convenience
                Sigma champ1;
                Sigma champ2;
                Sigma toBeReplaced;

                // Simple probabilistic tournament selection
                boolean goWithBest = true;

                // The probabilistic bit
                // This bit is needed to stop premature (inefficient) convergence.
                if (Math.random() < 0.1) {
                    goWithBest = false;
                }


                if ((s1.getFitness() <= s2.getFitness()) && (goWithBest)) {
                    champ1 = s2;
                } else {
                    champ1 = s1;
                }

                if ((s3.getFitness() <= s4.getFitness()) && (goWithBest)) {
                    champ2 = s4;
                } else {
                    champ2 = s3;
                }


                // Find who to replace
                toBeReplaced = s1;


                // In tests, the absence of this probabilistic bit made no difference.
                // See results/simcoma/mar08/elitism
                //if (Math.random() < 0.1)
                //    goWithBest = false;

                if ((s2.getFitness() <= toBeReplaced.getFitness()) && (goWithBest)) {
                    toBeReplaced = s2;
                }

                if ((s3.getFitness() <= toBeReplaced.getFitness()) && (goWithBest)) {
                    toBeReplaced = s3;
                }

                if ((s4.getFitness() <= toBeReplaced.getFitness()) && (goWithBest)) {
                    toBeReplaced = s4;
                }


                // Do the replacement, by crossover and mutation, from our two champions.
                sigmas.set(sigmas.indexOf(toBeReplaced), new Sigma(champ1, champ2));
            }

        //System.out.println(thisRoundPayoff);

        }



    }

    public Double2D getStageLocation(int x) {
        if (x == 1) {
            return new Double2D(790, 400);
        } else if (x == 2) {
            return new Double2D(10, 400);
        } else {
            System.out.println("Error");
            return new Double2D(500, 500);
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

        if (inEnvironment) {
            // Get the agent's goal location
            //suggestedLocation = hb.kMeansEngine.getGoalPosition(intID);
            desiredLocation = getCurrentGoal();


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
                //Calculate maximum divergence angle
                //double omega = ANGLE_MAX * Math.tanh(threshold);
                double omega = ANGLE_MAX * Math.tanh(currentSigma.getSigma() / 1.0);
                //Adjust according to distance
                //omega *= Math.pow(normalisation / (normalisation + 1), 10);
                omega *= normalisation / (normalisation + 150);
                double tempx = dx * Math.cos(omega) - dy * Math.sin(omega);
                double tempy = dx * Math.sin(omega) + dy * Math.cos(omega);
                dx = tempx;
                dy = tempy;
            }

            // Discretise;

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

            // Repel from stage barriers using getStageLocation(1) etc...
            double stageX, stageY, stageDisplacement, stageForceX, stageForceY;

            // Start high!
            double goalStageDisplacement = 1000;

            // Don't repel band members
            if (id >= 10) {
                for (int i = 1; i < 3; i++) {
                    stageX = agentLocation.x - getStageLocation(i).x;
                    stageY = agentLocation.y - getStageLocation(i).y;

                    stageX /= DISTANCE_SCALE;
                    stageY /= DISTANCE_SCALE;

                    stageDisplacement = Math.sqrt(Math.pow(stageX, 2.0) + Math.pow(stageY, 2.0));
                    if (intention == i) {
                        goalStageDisplacement = stageDisplacement;
                    }

                    stageForceX = (100 * AP * stageX / stageDisplacement) * Math.exp(-Math.abs(stageDisplacement - STAGE_RADIUS) / BP);
                    stageForceY = (100 * AP * stageY / stageDisplacement) * Math.exp(-Math.abs(stageDisplacement - STAGE_RADIUS) / BP);

                    dx += stageForceX;
                    dy += stageForceY;
                }
            }


            // How much this time step
            dx *= TIMESTEP;
            dy *= TIMESTEP;

            // Actually move the agent, if it's a position it can occupy, otherwise stay still.
            if (hb.acceptablePosition(this, new Double2D(location.x + dx, location.y + dy))) {
                agentLocation = new Double2D(location.x + dx, location.y + dy);
                hb.environment.setObjectLocation(this, agentLocation);

            }

            // Update time measuring if we are
            if (measuring) {
                timeMeasured++;
            }

            // Check to see if we've reached our goal.
            if ((goalStageDisplacement < 10) && measuring) {
                completeMeasuring();
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

    // Random number generators
    private static double uniform_rand(double min, double max) {
        return min + (max - min) * (Math.random());
    }

    private static double gaussian_rand(double mu, double sigma) {
        double p, p1, p2;
        do {
            p1 = uniform_rand(-1.0, 1.0);
            p2 = uniform_rand(-1.0, 1.0);
            p = p1 * p1 + p2 * p2;
        } while (p >= 1.0);
        return mu + sigma * p1 * Math.sqrt(-2.0 * Math.log(p) / p);
    }
}
