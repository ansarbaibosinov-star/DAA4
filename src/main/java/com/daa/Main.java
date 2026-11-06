package com.daa;

import com.daa.experiment.Benchmark;

public class Main {

    public static void main(String[] args) {

        System.out.println("DAA Assignment 4");
        System.out.println("Dynamic Programming and String Algorithms");
        System.out.println("------------------------------------------");

        Benchmark benchmark = new Benchmark();

        try {
            benchmark.runAll();
        } catch (Exception e) {
            System.err.println("Benchmark failed:");
            e.printStackTrace();
        }
    }
}