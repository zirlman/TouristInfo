package net.etfbl.pj2.TouristInfo.user;

import net.etfbl.pj2.TouristInfo.enums.Movement;
import net.etfbl.pj2.TouristInfo.enums.Name;
import sample.userAppController;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Tourist extends Thread {
    private String name;
    private int money;
    private Movement movement;
    private File fliersFolder;
    private int timeOfMovement;
    private int visitedAttractions;
    private File touristFolder;
    private Location location;

    public Tourist(int m, int col, int row) {
        name = Name.randomVal().toString();
        money = m > 0 ? m : (-1) * m;
        movement = Movement.randomVal();
        visitedAttractions = 0;
        location = new Location(col, row);
        timeOfMovement = new Random().nextInt(6001) + 1000;
        generateFolders();
    }

    @Override
    public void run() {
        //TODO: realizovati run() metodu !!!
        System.out.println(location);
        userAppController.commentator.appendText(location + "\n");
    }

    private void generateFolders() {
        // Pravi listu imena sa datim imenom
        List<String> list = Arrays.asList(new File("Names").list()).stream().filter(s -> s.contains(name)).collect(Collectors.toList());
        if (list.isEmpty())
            touristFolder = new File("Names/" + name);
        else
            // Pravi novi file sa nazivom: "ImeTuriste (br.foldera)"
            touristFolder = new File("Names/"+name + " (" + (list.size() + 1) + ")");
        if (touristFolder.mkdir()) {
            // Pravi poddirektorijum za letke
            fliersFolder = new File("Names/" + touristFolder.getName() + "/Leci");
            fliersFolder.mkdir();
        }
    }

    public String getTouristName() {
        return name;
    }

    public int getMoney() {
        return money;
    }

    public int getTimeOfMovement() {
        return timeOfMovement;
    }

    public int getVisitedAttractions() {
        return visitedAttractions;
    }

    public Location getLocation() {
        return location;

    }
}
