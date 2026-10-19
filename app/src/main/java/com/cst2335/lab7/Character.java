package com.cst2335.lab7;

import java.io.Serializable;

/**
 * Model class representing a Star Wars character fetched from SWAPI.
 */
public class Character implements Serializable {
    private String name;
    private String height;
    private String mass;

    public Character(String name, String height, String mass) {
        this.name = name;
        this.height = height;
        this.mass = mass;
    }

    public String getName() {
        return name;
    }

    public String getHeight() {
        return height;
    }

    public String getMass() {
        return mass;
    }
}
