package net.etfbl.pj2.TouristInfo.enums;


import java.util.Random;

public enum Name {
    Nikola, Srdjan, Jovan, Marko, Dejan, Dragan, Goran, Zoran, Milan, Petar, Bojan;

    public static Name randomVal() {
        return values()[new Random().nextInt(values().length)];
    }
}
