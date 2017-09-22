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

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Tourist extends Thread {
    private String touristName;
    private int money;
    private int donation;
    private Movement movement;
    private File flyersFolder;
    private int collectedFlyers;
    private int timeOfMovement;
    private int visitedAttractions;
    private static int totalAttractions;
    private File touristFolder;
    private Location location;
    private boolean done;
    private boolean movementFlag;
    private char leftOrRightFlag;
    private boolean restoreImageFlag = false;
    private Location oldLocation = new Location();
    private char imageSwitch;                   // Bira koja ce se ikona postaviti ako je turista posjetio atrakciju

    public Tourist(int m, int col, int row) {
        touristName = Name.randomVal().toString();
        money = m > 0 ? m : (-1) * m;
        movement = Movement.randomVal();
        location = new Location(col, row);
        timeOfMovement = new Random().nextInt(6000) + 1000; // Treba 6000 kao bound
        movementFlag = true;
        generateFolders();
    }

    @Override
    public void run() {
        while (!done)
            try {
                sleep(timeOfMovement);
                updateLocation();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        userAppController.commentator.appendText(touristName + " has finished his tour.\n");
        for (Tourist t : userAppController.touristsArr)
            if (!t.isDone())
                return;
        userAppController.doneSimulation = true;
        Platform.runLater(userAppController::finishSimulation);
    }

    private void updateLocation() {
        oldLocation.col = location.col;
        oldLocation.row = location.row;
        if (movement.equals(Movement.IN_ROW))
            moveInRow();
        else if (movement.equals(Movement.DIAGONAL))
            moveDiagonal();
        else if (movement.equals(Movement.WHOLE_MATRIX))
            moveThroughMatrix();
    }

    private void moveInRow() {
        ++location.col;
        if (location.col == userAppController.columnNumber)
            updateAndFinish();              // Azuriraj staru lokaciju i postavi flag za kraj obilaska
        else if (!userAppController.attractionsInMatrix.containsKey(location))
            updateAndAdd();                 // Azurira staru lokaciju i postavi turistu na novu lokaciju
        else
            updateAndHandle();              // Azurira staru lokaciju i obradi atrakciju
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
        if (location.row == userAppController.rowNumber || location.col < 0 || location.col == userAppController.columnNumber)
            updateAndFinish();              // Azuriraj staru lokaciju i postavi flag za kraj obilaska
        else if (!userAppController.attractionsInMatrix.containsKey(location))        // Ako se ne nalazi atrakcija na location, azuriraj pozicije
            updateAndAdd();                 // Azurira staru lokaciju i postavi turistu na novu lokaciju
        else
            updateAndHandle();              // Azurira staru lokaciju i obradi atrakciju
    }

    private void moveThroughMatrix() {
        location.col = (location.col + 1) % userAppController.columnNumber;
        location.row = location.col == 0 ? location.row + 1 : location.row;          // Ako je location.col == 0 treba preci u novi red
        if (location.row == userAppController.rowNumber)
            updateAndFinish();              // Azuriraj staru lokaciju i postavi flag za kraj obilaska
        else if (!userAppController.attractionsInMatrix.containsKey(location))
            updateAndAdd();                 // Azurira staru lokaciju i postavi turistu na novu lokaciju
        else
            updateAndHandle();              // Azurira staru lokaciju i obradi atrakciju
    }

    private void updateAndAdd() {
        Platform.runLater(() -> {
            try {
                updateOldLocation(userAppController.grid);
                ((ImageView) adminAppController.getNode(userAppController.grid, location.col, location.row)).setImage(userAppController.tourist);
            } catch (NullPointerException e) {
            }
        });
    }

    private void updateAndHandle() {
        Platform.runLater(() -> {
            updateOldLocation(userAppController.grid);
            handleAttraction(userAppController.grid, location.col, location.row);
        });
    }

    private void updateAndFinish() {
        Platform.runLater(() -> updateOldLocation(userAppController.grid));
        done = true;
    }

    private void updateOldLocation(GridPane grid) {
        if (!restoreImageFlag) {
            try {
                ImageView iv = (ImageView) adminAppController.getNode(grid, oldLocation.col, oldLocation.row);
                iv.setImage(new Image("res/icons/touristIcon30pxWhite.png"));
            } catch (NullPointerException e) {
                System.out.println("EROR @ " + oldLocation + " SETIMAGE NULLPOINTEREXC");
            }
            try {
                if (money <= 0) {     // Ako turisti nestane novca potrebno je azurirati lokaciju na kojoj se nalazio, a ne samo staru lokaciju !!!
                    ImageView iv = (ImageView) adminAppController.getNode(grid, location.col, location.row);
                    iv.setImage(new Image("res/icons/touristIcon30pxWhite.png"));
                }
            } catch (NullPointerException e) {
                System.out.println("EROR @ " + location + " SETIMAGE NULLPOINTEREXC");
            }
        } else {
            restoreImage(grid, oldLocation.col, oldLocation.row);
            if (money <= 0)          // Ako turisti nestane novca potrebno je azurirati lokaciju na kojoj se nalazio, a ne samo staru lokaciju !!!
                restoreImage(grid, location.col, location.row);
            restoreImageFlag = false;
        }
    }

    private void handleAttraction(GridPane grid, int col, int row) {
        TouristAttraction ta = userAppController.attractionsInMatrix.get(location);
        ++visitedAttractions;
        try {
            userAppController.commentator.appendText(touristName + " @ " + ta.getName() + ", location: " + location + "\n");
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
                imageSwitch = 'h';
            } else if (ta instanceof Museum) {
                restoreImageFlag = true;
                imageSwitch = 'm';
                storeflyer((Museum) ta);
                money -= ((Museum) ta).getPrice();
                if (money <= 0) {
                    done = true;
                    updateOldLocation(grid);
                }
            } else if (ta instanceof AmusementPark) {
                restoreImageFlag = true;
                imageSwitch = 'a';
                money -= ((AmusementPark) ta).getPrice();
                if (money <= 0) {
                    done = true;
                    updateOldLocation(grid);
                }
            } else if (ta instanceof Church) {
                restoreImageFlag = true;
                imageSwitch = 'c';
                donation = money - new Random().nextInt(80);
                donation = donation > 0 ? donation : donation * (-1);
                ((Church) ta).setCollectedMoney(donation);
                money -= donation;
                if (money <= 0) {
                    done = true;
                    updateOldLocation(grid);
                }
            }
        } catch (NullPointerException e) {
            System.out.println("NULLPOINTER EXCEPTION");
        }
    }

    private void storeflyer(Museum museum) {
        try {
            String path = museum.getFlyer().replace("file:/", "");
            File flyer = new File(path);
            List<String> list = Arrays.stream(flyersFolder.list()).filter(s -> s.contains(flyer.getName().split("\\.")[0])).collect(Collectors.toList());
            File f;
            if (list.size() > 0) {
                String tmp = (flyersFolder.getPath() + "/" + flyer.getName()).split("\\.")[0];    // Odbaci ekstenziju .txt
                f = new File(tmp + (list.size() + 1) + ".txt");
            } else
                f = new File(flyersFolder.getPath() + "/" + flyer.getName());
            try (BufferedReader br = new BufferedReader(new FileReader(flyer)); PrintWriter pw = new PrintWriter(f)) {
                String string;
                while ((string = br.readLine()) != null)
                    pw.append(string);
                ++collectedFlyers;
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("ERROR IN IO OPERATION");
        }
    }

    private void restoreImage(GridPane grid, int col, int row) {
        ImageView iv = (ImageView) adminAppController.getNode(grid, col, row);
        if (iv != null)
            switch (imageSwitch) {
                case 'h':
                    Platform.runLater(() -> iv.setImage(new Image("res/icons/historicalMonument30px.png")));
                    break;
                case 'm':
                    Platform.runLater(() -> iv.setImage(new Image("res/icons/museum30px.png")));
                    break;
                case 'a':
                    Platform.runLater(() -> iv.setImage(new Image("res/icons/amusementPark30px.png")));
                    break;
                case 'c':
                    Platform.runLater(() -> iv.setImage(new Image("res/icons/church30px.png")));
                    break;
            }
        imageSwitch = '.';
    }

    private void generateFolders() {
        // Pravi listu imena sa datim imenom
        List<String> list = Arrays.asList(new File("Names").list()).stream().filter(s -> s.contains(touristName)).collect(Collectors.toList());
        if (list.isEmpty())
            touristFolder = new File("Names/" + touristName);
        else {
            touristName += " (" + (list.size() + 1) + ")";                 // Pravi novi file sa nazivom: "ImeTuriste (br.foldera)"
            touristFolder = new File("Names/" + touristName);
        }
        if (touristFolder.mkdir()) {
            flyersFolder = new File("Names/" + touristFolder.getName() + "/Leci");
            flyersFolder.mkdir();    // Pravi poddirektorijum za letke
        }

    }

    public String getTouristName() {
        return touristName;
    }

    public File getFlyersFolder() {
        return flyersFolder;
    }

    public double getVisitedAttractions() {
        return ((double) visitedAttractions / totalAttractions) * 100;
    }

    public int getCollectedFlyers() {
        return collectedFlyers;
    }

    public boolean isDone() {
        return done;
    }


    public static void setTotalAttractions(int totalAttractions) {
        Tourist.totalAttractions = totalAttractions;
    }
}
