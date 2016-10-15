package com.org.gandhim.pso;

/* author: gandhi - gandhi.mtm [at] gmail [dot] com - Depok, Indonesia */

// this is an interface to keep the configuration for the PSO
// you can modify the value depends on your needs

public interface PSOConstants {
    int SWARM_SIZE = 50;
    int MAX_ITERATION = 20000;
    int PROBLEM_DIMENSION = 6;
    double C1 = 0.196;//1.496
    double C2 = 0.196;//1.496
    double W_UPPERBOUND = 2.0;
    double W_LOWERBOUND = 0.8;
}
