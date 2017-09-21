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
    private boolean firstUpdateFlag = true;
    private boolean restoreImageFlag = false;

    public Tourist(int m, int col, int row) {
        name = Name.randomVal().toString();
        money = m > 0 ? m : (-1) * m;
        movement = Movement.ONE_ROW;//Movement.randomVal();
        location = new Location(col, row);
        timeOfMovement = new Random().nextInt(6000) + 1000; // Treba 6000 kao bound
        movementFlag = true;
        generateFolders();
    }

    //TODO: realizovati run() metodu !!!
    @Override
    public void run() {
        while (!done)
            try {
                sleep(timeOfMovement);
                updatePosition();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        System.out.println("DONE !!!");
    }

    private void updatePosition() {
        if (movement.equals(Movement.ONE_ROW)) {
            ++location.col;
            if (location.col == userAppController.columnNumber) {
                done = true;
                Platform.runLater(() -> updateOldPosition(userAppController.grid, location.col - 1, location.row));
            } else if (!userAppController.attractionsInMatrix.containsKey(location)) {
                Platform.runLater(() -> {
                    updateOldPosition(userAppController.grid, location.col - 1, location.row);
                    ((ImageView) adminAppController.getNode(userAppController.grid, location.col, location.row)).setImage(userAppController.tourist);
                });
            } else {
                Platform.runLater(() -> {
                    updateOldPosition(userAppController.grid, location.col - 1, location.row);
                    handleAction(userAppController.grid, location.col, location.row);
                });
            }
//        } else if (movement.equals(Movement.DIAGONAL)) {
//            if (movementFlag) {
//                leftOrRightFlag = location.row > (userAppController.rowNumber / 2) ? 'L' : 'R';
//                movementFlag = false;
//            }
//            if (leftOrRightFlag == 'L')
//                --location.col;
//            else
//                ++location.col;
//            ++location.row;
//            if (location.row == userAppController.rowNumber || location.col < 0 || location.col == userAppController.columnNumber) {
//                done = true;
//                if (!restoreImageFlag)
//                    updatePostionForDiagonalMovement(userAppController.grid);
//            } else if (!userAppController.attractionsInMatrix.containsKey(location)) { // Ako se ne nalazi atrakcija na location, azuriraj pozicije
//                Platform.runLater(() -> userAppController.grid.add(new ImageView(userAppController.tourist), location.col, location.row));
//                updatePostionForDiagonalMovement(userAppController.grid);
//            } else {
//                updatePostionForDiagonalMovement(userAppController.grid);
//                handleAction(userAppController.grid, location.col, location.row);
//            }
//        } else if (movement.equals(Movement.WHOLE_MATRIX)) {
//            boolean tempFlag = false;           // Ukoliko je tempFlag == true onda je Node vec obrisan
//            ++location.col;
//            if (location.col > userAppController.columnNumber) {
//                Platform.runLater(() -> updateOldPosition(userAppController.grid, location.col - 1, location.row));
//                location.col = 0;
//                ++location.row;
//                tempFlag = true;
//            }
//            if (location.row == userAppController.rowNumber) {
//                done = true;
//                if (!restoreImageFlag && !tempFlag)
//                    Platform.runLater(() -> updateOldPosition(userAppController.grid, location.col - 1, location.row - 1));
//            } else if (!userAppController.attractionsInMatrix.containsKey(location)) {
//                Platform.runLater(() -> userAppController.grid.add(new ImageView(userAppController.tourist), location.col, location.row));
//                if (!tempFlag)
//                    Platform.runLater(() -> updateOldPosition(userAppController.grid, location.col - 1, location.row - 1));
//            } else {
//                handleAction(userAppController.grid, location.col, location.row);
//                if (!tempFlag)
//                    Platform.runLater(() -> updateOldPosition(userAppController.grid, location.col - 1, location.row - 1));
//            }
        }
    }

    private void updateOldPosition(GridPane grid, int col, int row) {
        if (!restoreImageFlag) {
            ImageView iv = (ImageView) adminAppController.getNode(grid, col, row);
            if (iv.getImage() != null)
                iv.setImage(new Image("res/icons/touristIcon30pxWhite.png"));
        } else {
            restoreImage(grid, col, row);
            restoreImageFlag = false;
        }
    }

    private void handleAction(GridPane grid, int col, int row) {
        TouristAttraction ta = userAppController.attractionsInMatrix.get(location);
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
    }

    private void updatePostionForDiagonalMovement(GridPane grid) {
        if (leftOrRightFlag == 'L')
            Platform.runLater(() -> updateOldPosition(grid, location.col + 1, location.row - 1));
        else
            Platform.runLater(() -> updateOldPosition(grid, location.col - 1, location.row - 1));
    }

    private void restoreImage(GridPane grid, int col, int row) {
        ImageView iv = (ImageView) adminAppController.getNode(grid, col, row);
        if (iv != null)
            iv.setImage(new Image("res/icons/attractionIcon30px.png"));
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
