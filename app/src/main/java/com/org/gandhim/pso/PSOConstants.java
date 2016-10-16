package com.org.gandhim.pso;

/* author: gandhi - gandhi.mtm [at] gmail [dot] com - Depok, Indonesia */

// this is an interface to keep the configuration for the PSO
// you can modify the value depends on your needs

public interface PSOConstants {
    int SWARM_SIZE = 100;
    int MAX_ITERATION = 3000;
    int PROBLEM_DIMENSION = 4;
    double C1 = 0.496;//1.496
    double C2 = 0.496;//1.496
    double W_UPPERBOUND = 0.8;
    double W_LOWERBOUND = 0.2;
}
