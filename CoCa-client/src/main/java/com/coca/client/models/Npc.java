package com.coca.client.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Npc {
    private String name;
    private boolean isHuman;

    private int strength;
    private int agility;
    private int intelligence;
    private int empathy;

    private int health;
    private int resolve;

    private int force;
    private int handToHandCombat;
    private int stamina;
    private int firearms;
    private int mobility;
    private int stealth;
    private int medicalAid;
    private int observation;
    private int tech;
    private int connections;
    private int manipulation;
    private int insight;
}
