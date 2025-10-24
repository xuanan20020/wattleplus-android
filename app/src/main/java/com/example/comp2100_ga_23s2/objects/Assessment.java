package com.example.comp2100_ga_23s2.objects;

public class Assessment {
    private String name;  // name of the assessment
    private int weight;  // how much does the assessment worth over the total grade

    public Assessment(String name, int weight) {
        this.name = name;
        this.weight = weight;
    }

    public String getName() {return this.name;}

    public int getWeight() {return this.weight;}
}
