package com.cthulhu.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class BladeRunner {
    @Id
    private String name;
    @ManyToOne
    @JoinColumn(name = "account_name")
    @JsonIgnore
    private Account account;

    private boolean isHuman;
    private boolean secretReplicant;
    private String archetype;
    private int yearsOnTheForce;

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

    private int promotionPoints;
    private int humanityPoints;
    private int chinyenPoints;
}
