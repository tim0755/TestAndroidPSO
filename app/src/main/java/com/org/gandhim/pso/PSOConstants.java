package com.org.gandhim.pso;

/* author: gandhi - gandhi.mtm [at] gmail [dot] com - Depok, Indonesia */

// this is an interface to keep the configuration for the PSO
// you can modify the value depends on your needs

public interface PSOConstants {
    int SWARM_SIZE = 200;
    int MAX_ITERATION = 2000;
    int PROBLEM_DIMENSION = 8;
    int initDivide = 8;
    double C1 = 0.396;//1.496
    double C2 = 0.396;//1.496
    double W_UPPERBOUND = 2.0;//0.8~1.2
    double W_LOWERBOUND = 1.2;
}
