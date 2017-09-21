package net.etfbl.pj2.TouristInfo.user;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import net.etfbl.pj2.TouristInfo.attractions.*;
import net.etfbl.pj2.TouristInfo.enums.Movement;
import net.etfbl.pj2.TouristInfo.enums.Name;
import sample.adminAppController;
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
    private boolean done;
    private boolean movementFlag;
    private char leftOrRightFlag;
    private boolean restoreImageFlag = false;
    private Location oldLocation = new Location();

    public Tourist(int m, int col, int row) {
        name = Name.randomVal().toString();
        money = m > 0 ? m : (-1) * m;
        movement = Movement.randomVal();
        location = new Location(col, row);
        timeOfMovement = new Random().nextInt(1000) + 1000; // Treba 6000 kao bound
        movementFlag = true;
        generateFolders();
    }

    //TODO: realizovati run() metodu !!!
    @Override
    public void run() {
        while (!done)
            try {
                sleep(timeOfMovement);
                updateLocation();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        System.out.println("DONE !!!");
    }

    private void updateLocation() {
        oldLocation.col = location.col;
        oldLocation.row = location.row;
        if (movement.equals(Movement.ONE_ROW))
            moveInRow();
        else if (movement.equals(Movement.DIAGONAL))
            moveDiagonal();
        else if (movement.equals(Movement.WHOLE_MATRIX))
            moveThroughMatrix();
    }

    private void moveInRow() {
        ++location.col;
        if (location.col == userAppController.columnNumber) {
            done = true;
            Platform.runLater(() -> updateOldLocation(userAppController.grid));
        } else if (!userAppController.attractionsInMatrix.containsKey(location)) {
            Platform.runLater(() -> {
                Platform.runLater(() -> updateOldLocation(userAppController.grid));
                ((ImageView) adminAppController.getNode(userAppController.grid, location.col, location.row)).setImage(userAppController.tourist);
            });
        } else
            Platform.runLater(() -> {
                updateOldLocation(userAppController.grid);
                handleAttraction(userAppController.grid, location.col, location.row);
            });
    }

    private void moveDiagonal() {
        if (movementFlag) {
            leftOrRightFlag = location.col > (userAppController.rowNumber / 2) ? 'L' : 'R';
            movementFlag = false;
        }
        if (leftOrRightFlag == 'L')
            --location.col;
        else
            ++location.col;
        ++location.row;
        if (location.row == userAppController.rowNumber || location.col < 0 || location.col == userAppController.columnNumber) {
            done = true;
            updateOldLocation(userAppController.grid);
        } else if (!userAppController.attractionsInMatrix.containsKey(location)) { // Ako se ne nalazi atrakcija na location, azuriraj pozicije
            Platform.runLater(() -> {
                updateOldLocation(userAppController.grid);
                ((ImageView) adminAppController.getNode(userAppController.grid, location.col, location.row)).setImage(userAppController.tourist);
            });
        } else {
            Platform.runLater(() -> {
                updateOldLocation(userAppController.grid);
                handleAttraction(userAppController.grid, location.col, location.row);
            });
        }
    }

    private void moveThroughMatrix() {
        location.col = (location.col + 1) % userAppController.columnNumber;
        location.row = location.col == 0 ? location.row + 1 : location.row;          // Ako je location.col == 0 treba preci u novi red
        if (location.row == userAppController.rowNumber) {
            done = true;
            updateOldLocation(userAppController.grid);
        } else if (!userAppController.attractionsInMatrix.containsKey(location)) {
            updateOldLocation(userAppController.grid);
            Platform.runLater(() -> ((ImageView) adminAppController.getNode(userAppController.grid, location.col, location.row)).setImage(userAppController.tourist));
        } else {
            Platform.runLater(() -> updateOldLocation(userAppController.grid));
            handleAttraction(userAppController.grid, location.col, location.row);
        }
    }

    private void updateOldLocation(GridPane grid) {
        if (!restoreImageFlag) {
            ImageView iv = (ImageView) adminAppController.getNode(grid, oldLocation.col, oldLocation.row);
            try {
                iv.setImage(new Image("res/icons/touristIcon30pxWhite.png"));
            } catch (NullPointerException e) {
                System.out.println("EROR @ " + oldLocation + " SETIMAGE NULLPOINTEREXC");
            }
        } else {
            restoreImage(grid, oldLocation.col, oldLocation.row);
            restoreImageFlag = false;
        }
    }

    private void handleAttraction(GridPane grid, int col, int row) {
        TouristAttraction ta = userAppController.attractionsInMatrix.get(location);
        try {
            userAppController.commentator.appendText(name + " @ " + ta.getName() + ", location: " + location + "\n");
            if (ta instanceof HistoricalMonument) {
                restoreImageFlag = true;
                Platform.runLater(() -> {
                    ImageView iv = (ImageView) adminAppController.getNode(grid, col, row);
                    if (iv != null)
                        iv.setImage(new Image("res/icons/shootingAttractionIcon.png"));
                    Stage stage = new Stage();
                    Group group = new Group();
                    group.getChildren().add(new ImageView(((HistoricalMonument) ta).getImage()));
                    stage.setScene(new Scene(group));
                    stage.setX(50);
                    stage.setY(50);
                    stage.getIcons().add(new Image("res/icons/imageIcon.png"));
                    stage.show();
                    PauseTransition delay = new PauseTransition(Duration.seconds(3));
                    delay.setOnFinished(e -> stage.close());
                    delay.play();
                });
            } else if (ta instanceof Museum) {
                restoreImageFlag = true;
                //TODO: nastavi
            } else if (ta instanceof AmusementPark) {
                restoreImageFlag = true;
            } else if (ta instanceof Church) {
                restoreImageFlag = true;
            }
        } catch (NullPointerException e) {
            System.out.println("NULLPOINTER EXCEPTION");
        }
    }

    private void restoreImage(GridPane grid, int col, int row) {
        ImageView iv = (ImageView) adminAppController.getNode(grid, col, row);
        if (iv != null)
            Platform.runLater(() -> iv.setImage(new Image("res/icons/attractionIcon30px.png")));
    }

    private void generateFolders() {
        // Pravi listu imena sa datim imenom
        List<String> list = Arrays.asList(new File("Names").list()).stream().filter(s -> s.contains(name)).collect(Collectors.toList());
        if (list.isEmpty())
            touristFolder = new File("Names/" + name);
        else {
            // Pravi novi file sa nazivom: "ImeTuriste (br.foldera)"
            name += " (" + (list.size() + 1) + ")";
            touristFolder = new File("Names/" + name);
        }
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
