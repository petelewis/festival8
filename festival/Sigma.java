package festival;


/**
 *
 * @author pete
 */
public class Sigma {
    // The sigma value to use
    private double p;
    
    // The fitness of this sigma value, as found in the environment
    private double fitness;
    
    // Whether or not this has in fact been tested yet (i.e. does the fitness value have any meaning?)
    boolean tested;
    
    // The mutation rate -  need to calibrate this
    //private static final double mu = 0.01;
    private static final double mu = 0.5;
    
    
    
    /** Creates a new instance of Sigma */
    public Sigma(double mu, double sigma) {
        // Randomly within the constraints. Min is assumed to be zero.
        //p = (Math.random() * (double)(max - min)) + (double)min;
        p = gaussian_rand(mu, sigma);
        tested = false;
    }
    
    /** Create a new offer by mutation from a previous one */
    public Sigma(Sigma o) {
        p = mutate(o.getSigma(), mu);
        
        tested = false;
    }

    public Sigma(double price) {
        p = price;

        tested = false;
    }

    /** Create a new offer by crossover and mutation from previous ones */
    public Sigma(Sigma o1, Sigma o2) {
        if (o1.getSigma() > o2.getSigma())
            p = mutate( ((o1.getSigma() - o2.getSigma()) / 2.0) + o2.getSigma(), mu);
        else
            p = mutate( ((o2.getSigma() - o1.getSigma()) / 2.0) + o1.getSigma(), mu);
        
        tested = false;
    }
    
    public double getSigma() {
           return p;
    }

    public double getFitness() {
        if (!tested)
            System.out.println("WARNING! Requesting untested fitness value.");
        
        return fitness;
    }
    
    public void setFitness(double f) {
        fitness = f;
        tested = true;
    }
    
    private static double mutate(double d, double mu) {
        //return (d + ((Math.random() * 2 * mu) - mu));
        return (d + (gaussian_rand(0.0, mu)));
        
    }
    
    // Random number generators
    private static double uniform_rand(double min, double max) {
        return min+(max-min)*(Math.random());
    }
    
    private static double gaussian_rand(double mu, double sigma) {
        double p, p1, p2;
        do {
            p1=uniform_rand(-1.0, 1.0);
            p2=uniform_rand(-1.0, 1.0);
            p=p1*p1+p2*p2;
        } while (p>=1.0);
        return mu+sigma*p1*Math.sqrt(-2.0*Math.log(p)/p);
    }
    

}
