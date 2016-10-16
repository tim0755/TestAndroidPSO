package com.org.gandhim.pso;

/* author: gandhi - gandhi.mtm [at] gmail [dot] com - Depok, Indonesia */

// this is the heart of the PSO program
// the code is for 2-dimensional space problem
// but you can easily modify it to solve higher dimensional space problem

import java.util.Random;
import java.util.Vector;

public class PSOProcess implements PSOConstants {
    Random generator = new Random();
    private Vector<Particle> swarm = new Vector<Particle>();
    private double[] pBest = new double[SWARM_SIZE];
    private Vector<Location> pBestLocation = new Vector<Location>();
    private double gBest;
    private Location gBestLocation;
    private double[] fitnessValueList = new double[SWARM_SIZE];

    private ProblemSet mProblemSet;

    public PSOProcess(Point[] points1, Point[] points2) {
        System.out.println("ProblemSet points1:" + points1.length + "   points2:" + points2.length);
        mProblemSet = new ProblemSet(points1, points2);
    }

    public double[] execute() {
        System.out.println("ProblemSet execute!");
        initializeSwarm();
        updateFitnessList();

        for (int i = 0; i < SWARM_SIZE; i++) {
            pBest[i] = fitnessValueList[i];
            pBestLocation.add(swarm.get(i).getLocation());
        }

        int t = 0;
        double w;
        double err = 9999;
        boolean prnitFlag = false;

        while (t < MAX_ITERATION && err > ProblemSet.ERR_TOLERANCE) {
            prnitFlag = true;
            // step 1 - update pBest
            for (int i = 0; i < SWARM_SIZE; i++) {
                if (fitnessValueList[i] < pBest[i]) {
                    pBest[i] = fitnessValueList[i];
                    pBestLocation.set(i, swarm.get(i).getLocation());
                }
            }

            // step 2 - update gBest
            int bestParticleIndex = PSOUtility.getMinPos(fitnessValueList);
            if (t == 0 || fitnessValueList[bestParticleIndex] < gBest) {
                gBest = fitnessValueList[bestParticleIndex];
                gBestLocation = swarm.get(bestParticleIndex).getLocation();

                System.out.print("hit:(");
                for (int i = 0; i < PSOConstants.PROBLEM_DIMENSION; i++) {
                    System.out.print("" + gBestLocation.getLoc()[i] + " , ");
                }
                System.out.println(") err:" + +mProblemSet.evaluate(gBestLocation));
            }

            w = W_UPPERBOUND - (((double) t) / MAX_ITERATION) * (W_UPPERBOUND - W_LOWERBOUND);

            for (int i = 0; i < SWARM_SIZE; i++) {
                double r1 = generator.nextDouble();
                double r2 = generator.nextDouble();

                Particle p = swarm.get(i);

                // step 3 - update velocity
                double[] newVel = new double[PROBLEM_DIMENSION];
                for (int j = 0; j < PROBLEM_DIMENSION; j++) {
                    newVel[j] = (w * p.getVelocity().getPos()[j]) +
                            (r1 * C1) * (pBestLocation.get(i).getLoc()[j] - p.getLocation().getLoc()[j]) +
                            (r2 * C2) * (gBestLocation.getLoc()[j] - p.getLocation().getLoc()[j]);
                    if (newVel[j] > ProblemSet.VEL_HIGH) {
                        newVel[j] = ProblemSet.VEL_HIGH;
                    } else if (newVel[j] < ProblemSet.VEL_LOW) {
                        newVel[j] = ProblemSet.VEL_LOW;
                    }
                }

                if (prnitFlag && t % 1000 == 0) {
                    prnitFlag = false;

                    System.out.print("newVel:(");
                    for (int k = 0; k < PSOConstants.PROBLEM_DIMENSION; k++) {
                        System.out.print("" + newVel[k] + " , ");
                    }
                    System.out.println(") times:" + t);
                }
                Velocity vel = new Velocity(newVel);
                p.setVelocity(vel);

                // step 4 - update location
                double[] newLoc = new double[PROBLEM_DIMENSION];
                for (int j = 0; j < PROBLEM_DIMENSION; j++) {
                    newLoc[j] = p.getLocation().getLoc()[j] + newVel[j];
                }

                if (newLoc[0] > ProblemSet.LOC_X_HIGH) {
                    newLoc[0] = ProblemSet.LOC_X_HIGH;
                } else if (newLoc[0] < ProblemSet.LOC_X_LOW) {
                    newLoc[0] = ProblemSet.LOC_X_LOW;
                }


                if (newLoc[1] > ProblemSet.LOC_Y_HIGH) {
                    newLoc[1] = ProblemSet.LOC_Y_HIGH;
                } else if (newLoc[1] < ProblemSet.LOC_Y_LOW) {
                    newLoc[1] = ProblemSet.LOC_Y_LOW;
                }


                if (newLoc[2] > ProblemSet.LOC_Z_HIGH) {
                    newLoc[2] = ProblemSet.LOC_Z_HIGH;
                } else if (newLoc[2] < ProblemSet.LOC_Z_LOW) {
                    newLoc[2] = ProblemSet.LOC_Z_LOW;
                }


                if (newLoc[3] > ProblemSet.LOC_Z_HIGH2) {
                    newLoc[3] = ProblemSet.LOC_Z_HIGH2;
                } else if (newLoc[3] < ProblemSet.LOC_Z_LOW2) {
                    newLoc[3] = ProblemSet.LOC_Z_LOW2;
                }

                Location loc = new Location(newLoc);
                p.setLocation(loc);
            }
            err = mProblemSet.evaluate(gBestLocation) - 0; // minimizing the functions means it's getting closer to 0
            t++;
            updateFitnessList();
        }
        //System.out.println("(azimuth,elevation,rotate)=(" + gBestLocation.getLoc()[0] + "," + gBestLocation.getLoc()[1] + "," + gBestLocation.getLoc()[2] + "), (" + gBestLocation.getLoc()[3] + "," + gBestLocation.getLoc()[4] + "," + gBestLocation.getLoc()[5] + ") err:" + err + " time:" + t);

        System.out.print("gBestLocation:(");
        for (int k = 0; k < PSOConstants.PROBLEM_DIMENSION; k++) {
            System.out.print("" + gBestLocation.getLoc()[k] + " , ");
        }
        System.out.println(") err:" + mProblemSet.evaluate(gBestLocation));

        mProblemSet.show();
        return gBestLocation.getLoc();
    }

    public void initializeSwarm() {
        Particle p;
        for (int i = 0; i < SWARM_SIZE; i++) {
            p = new Particle(mProblemSet);

            // randomize location inside a space defined in Problem Set
            double[] loc = new double[PROBLEM_DIMENSION];
            loc[0] = ProblemSet.LOC_X_LOW + generator.nextDouble() * (ProblemSet.LOC_X_HIGH - ProblemSet.LOC_X_LOW);
            loc[1] = ProblemSet.LOC_Y_LOW + generator.nextDouble() * (ProblemSet.LOC_Y_HIGH - ProblemSet.LOC_Y_LOW);
            loc[2] = ProblemSet.LOC_Z_LOW + generator.nextDouble() * (ProblemSet.LOC_Z_HIGH - ProblemSet.LOC_Z_LOW);
            loc[3] = ProblemSet.LOC_Z_LOW2 + generator.nextDouble() * (ProblemSet.LOC_Z_HIGH2 - ProblemSet.LOC_Z_LOW2);
//            loc[4] = ProblemSet.LOC_Y_LOW2 + generator.nextDouble() * (ProblemSet.LOC_Y_HIGH2 - ProblemSet.LOC_Y_LOW2);
//            loc[5] = ProblemSet.LOC_Z_LOW2 + generator.nextDouble() * (ProblemSet.LOC_Z_HIGH2 - ProblemSet.LOC_Z_LOW2);

            /*System.out.print("initializeSwarm loc:(");
            for (int k = 0; k < PSOConstants.PROBLEM_DIMENSION; k++) {
                System.out.print("" + loc[k] + " , ");
            }
            System.out.println(")");*/

            Location location = new Location(loc);

            // randomize velocity in the range defined in Problem Set
            double[] vel = new double[PROBLEM_DIMENSION];
            vel[0] = 0.5;//ProblemSet.VEL_LOW + generator.nextDouble() * (ProblemSet.LOC_X_HIGH - ProblemSet.LOC_X_LOW) / 8.0;
            vel[1] = 0.5;//ProblemSet.VEL_LOW + generator.nextDouble() * (ProblemSet.LOC_Y_HIGH - ProblemSet.LOC_Y_LOW) / 8.0;
            vel[2] = 0.5;//ProblemSet.VEL_LOW + generator.nextDouble() * (ProblemSet.LOC_Z_HIGH - ProblemSet.LOC_Z_LOW) / 8.0;
            vel[3] = 0.5;//ProblemSet.VEL_LOW + generator.nextDouble() * (ProblemSet.LOC_Z_HIGH2 - ProblemSet.LOC_Z_LOW2) / 8.0;
//            vel[4] = /*ProblemSet.VEL_LOW + generator.nextDouble() * */(ProblemSet.LOC_Y_HIGH2 - ProblemSet.LOC_Y_LOW2) / 8.0;
//            vel[5] = /*ProblemSet.VEL_LOW + generator.nextDouble() * */(ProblemSet.LOC_Z_HIGH2 - ProblemSet.LOC_Z_LOW2) / 8.0;

            /*System.out.print("initializeSwarm vel:(");
            for (int k = 0; k < PSOConstants.PROBLEM_DIMENSION; k++) {
                System.out.print("" + vel[k] + " , ");
            }
            System.out.println(")");*/

            Velocity velocity = new Velocity(vel);

            p.setLocation(location);
            p.setVelocity(velocity);
            swarm.add(p);
        }
    }

    public void updateFitnessList() {
        for (int i = 0; i < SWARM_SIZE; i++) {
            fitnessValueList[i] = swarm.get(i).getFitnessValue();
        }
    }
}
