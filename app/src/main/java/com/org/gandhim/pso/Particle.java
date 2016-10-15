package com.org.gandhim.pso;

public class Particle {
    private double fitnessValue;
    private Velocity velocity;
    private Location location;

    private ProblemSet mProblemSet;

    public Particle(ProblemSet problemSet) {
        super();
        mProblemSet = problemSet;
    }

    public Particle(double fitnessValue, Velocity velocity, Location location) {
        super();
        this.fitnessValue = fitnessValue;
        this.velocity = velocity;
        this.location = location;
    }

    public Velocity getVelocity() {
        return velocity;
    }

    public void setVelocity(Velocity velocity) {
        this.velocity = velocity;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public double getFitnessValue() {
        fitnessValue = mProblemSet.evaluate(location);
        return fitnessValue;
    }
}
