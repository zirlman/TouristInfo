package sample;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.etfbl.pj2.TouristInfo.attractions.TouristAttraction;
import net.etfbl.pj2.TouristInfo.user.Location;
import net.etfbl.pj2.TouristInfo.user.Tourist;

import java.io.*;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;

import static javafx.beans.binding.Bindings.format;

public class userAppController {
    @FXML
    private JFXButton touristNumberButton;
    @FXML
    private JFXButton matrixDimensionsButton;
    @FXML
    private JFXButton attractionsNumberButton;
    @FXML
    private JFXButton startButton;
    @FXML
    public JFXButton showResultsButton;
    @FXML
    private Label infoLabel;

    private Stage inputStage;
    private JFXButton submit;
    private JFXTextField inputTextField;
    private Label inputLabel;
    private Tooltip tt;
    private int touristNumber;
    private int attractionsNumber;
    // Niz ikona iz kojeg se uzima ikona za inputStage u zavisnosti koje je dugme pritisnuto
    private Image imageArr[] =
            {new Image("res/icons/touristNumberIcon.png"),
                    new Image("res/icons/matrixIcon.png"), new Image("res/icons/attractionNumberIcon.png")};
    // Staticke metode kojima se pristupa u klasi Tourist
    public static int columnNumber;
    public static int rowNumber;
    public static GridPane grid;
    public static TextArea commentator = new TextArea();
    public static Image tourist = new Image("res/icons/touristIcon30px.png");
    public static Image attraction = new Image("res/icons/attractionIcon30px.png");
    public static HashMap<Location, TouristAttraction> attractionsInMatrix = new HashMap<>();
    public static ObservableList<Tourist> touristsArr = FXCollections.observableArrayList();
    public static boolean doneSimulation;
    public static boolean forcedClose;
    private static Stage simulationStage;
    private static Stage tableStage;
    private static TableView<Tourist> table;

    @FXML
    private void initialize() {
        startButton.setDisable(true);

        showResultsButton.setOnAction(e -> {
            if (!forcedClose && doneSimulation)
                finishSimulation();
            else
                adminAppController.makeAlertWindow("Start simulation and wait for it to finish.\nDon't close the window !!!", "Invalid request", "ERROR");
        });
        showResultsButton.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER)
                if (doneSimulation)
                    finishSimulation();
                else
                    adminAppController.makeAlertWindow("Start/Wait for the simulation to finish", "Invalid request", "ERROR");
        });

        touristNumberButton.setDisable(true);
        touristNumberButton.setOnAction(e -> touristForm());
        touristNumberButton.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                touristForm();
            }
        });

        matrixDimensionsButton.setOnAction(e -> matrixForm());
        matrixDimensionsButton.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                matrixForm();
            }
        });

        attractionsNumberButton.setDisable(true);
        attractionsNumberButton.setOnAction(e -> attractionsForm());
        attractionsNumberButton.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER)
                attractionsForm();
        });

        infoLabel.setText("  Enter tourist number,matrix dimensions and attractions number to start the simulation...");
        makeForm();
    }

    private void makeForm() {
        inputStage = new Stage();
        inputLabel = new Label();
        inputTextField = new JFXTextField();
        tt = new Tooltip();
        submit = new JFXButton();
        adminAppController.setButtonStyle(submit, "Submit");

        inputTextField.setTooltip(tt);
        Label info = new Label("Hover over the text field for more information.");
        Group root = new Group();
        GridPane gridPane = new GridPane();
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        gridPane.setPadding(new Insets(10, 10, 0, 10));
        gridPane.add(inputLabel, 0, 0);
        gridPane.add(inputTextField, 1, 0, 2, 1);
        gridPane.add(submit, 1, 1, 2, 1);
        gridPane.add(info, 0, 3, 2, 1);
        root.getChildren().add(gridPane);
        inputStage.setScene(new Scene(root, 300, 100));
        inputStage.setResizable(false);
        inputStage.getIcons().addAll(imageArr);
    }

    private void touristForm() {
        inputStage.getIcons().removeAll(imageArr);
        inputStage.getIcons().add(imageArr[0]);
        inputStage.setTitle("Tourist number input");
        inputLabel.setText("Tourist number:");
        tt.setText("Enter the number of tourists");
        submit.setOnAction(e -> getInput('t'));
        submit.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER)
                getInput('t');
        });
        inputStage.show();
    }

    private void matrixForm() {
        inputStage.getIcons().removeAll(imageArr);
        inputStage.getIcons().add(imageArr[1]);
        inputStage.setTitle("Matrix dimensions input");
        inputLabel.setText("Matrix dimensions:");
        tt.setText("Format input: rowNumber,columnNumber");
        submit.setOnAction(e -> getInput('m'));
        submit.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER)
                getInput('m');
        });
        inputStage.show();
    }

    private void attractionsForm() {
        inputStage.getIcons().removeAll(imageArr);
        inputStage.getIcons().add(imageArr[2]);
        inputStage.setTitle("Attractions number input");
        inputLabel.setText("Attractions number:");
        tt.setText("Enter minimum number of tourist attractions");
        submit.setOnAction(e -> getInput('a'));
        submit.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER)
                getInput('a');
        });
        inputStage.show();
    }

    private void getInput(char flag) {
        String no = inputTextField.getText();
        if (no != null && !no.isEmpty()) {
            try {
                if (flag == 't') {
                    touristNumber = Integer.parseInt(no);
                    if (touristNumber < 1 || (touristNumber + attractionsNumber) > (rowNumber * columnNumber))
                        throw new NumberFormatException();
                } else if (flag == 'a') {
                    attractionsNumber = Integer.parseInt(no);
                    if (attractionsNumber < 0 || (touristNumber + attractionsNumber) > (rowNumber * columnNumber))
                        throw new NumberFormatException();
                } else if (flag == 'm') {
                    String coord[] = no.split(",");
                    if (coord.length == 2) {
                        rowNumber = Integer.parseInt(coord[0]) % 21;
                        columnNumber = Integer.parseInt(coord[1]) % 21;
                        if (rowNumber < 1 || columnNumber < 1 || (touristNumber + attractionsNumber) > (rowNumber * columnNumber))
                            throw new NumberFormatException();
                        touristNumberButton.setDisable(false);
                        attractionsNumberButton.setDisable(false);
                    } else
                        throw new NumberFormatException();
                }
                inputStage.close();
                adminAppController.makeAlertWindow("Press ESC/ENTER to exit the window.", "Submition successful", "INFORMATION");
                clearGrid();
                makeForm();
            } catch (NumberFormatException e) {
                inputStage.close();
                adminAppController.makeAlertWindow("Please enter a valid input.", "Invalid input", "ERROR");
                makeForm();
                if (flag == 't')
                    touristForm();
                else if (flag == 'a')
                    attractionsForm();
                else if (flag == 'm')
                    matrixForm();

            }
        }
        if (touristNumber != 0 && attractionsNumber >= 0 && rowNumber != 0 && columnNumber != 0) {
            startButton.setDisable(false);
            startButton.setOnAction(e -> startSimulation());
            startButton.setOnKeyPressed(e -> {
                if (e.getCode() == KeyCode.ENTER)
                    startSimulation();
            });
        }
    }

    private void startSimulation() {
        adminAppController.deserialize();
        Random LocationGen = new Random();
        int col, row;
        Tourist.setTotalAttractions(attractionsNumber);

        commentator.setPrefSize(400, 100);
        commentator.setStyle("-fx-faint-focus-color: transparent; -fx-focus-color: WHITE;");
        grid = new GridPane();
        grid.setHgap(3);
        grid.setVgap(3);
        grid.setStyle("-fx-background-color: #FFFFFFFF");

        HBox hBox = new HBox(5);
        hBox.getChildren().addAll(grid, commentator);

        for (TouristAttraction ta : adminAppController.attractions) {
            col = LocationGen.nextInt(columnNumber);
            row = LocationGen.nextInt(rowNumber);
            if (attractionsNumber > 0 && adminAppController.getNode(grid, col, row) == null) {
                grid.add(new ImageView(attraction), col, row);
                attractionsInMatrix.put(new Location(col, row), ta);
                --attractionsNumber;
            }
        }
        int tmp = 0;
        while (attractionsNumber > 0) {
            col = LocationGen.nextInt(columnNumber);
            row = LocationGen.nextInt(rowNumber);
            if (adminAppController.getNode(grid, col, row) == null) {
                grid.add(new ImageView(attraction), col, row);
                attractionsInMatrix.put(new Location(col, row), adminAppController.attractions.get(tmp++));
                --attractionsNumber;
                if (adminAppController.attractions.size() == tmp)
                    tmp = 0;
            }
        }
        while (touristNumber > 0) {
            col = LocationGen.nextInt(columnNumber);
            row = LocationGen.nextInt(rowNumber);
            if (adminAppController.getNode(grid, col, row) == null) {
                grid.add(new ImageView(tourist), col, row);
                touristsArr.add(new Tourist(500, col, row));
                touristsArr.get(touristsArr.size() - 1).start();
                --touristNumber;
            }
        }
        fillGrid();

        Group simulationGroup = new Group();
        simulationGroup.getChildren().add(hBox);
        simulationStage = new Stage();
        simulationStage.setResizable(false);
        simulationStage.setTitle("App simulation");
        simulationStage.setScene(new Scene(simulationGroup));
        simulationStage.getIcons().add(new Image("res/icons/simulationIcon.png"));
        simulationStage.initModality(Modality.APPLICATION_MODAL);
        simulationStage.show();
        simulationStage.setOnCloseRequest(e -> {
            forcedClose = true;
            commentator.clear();
            clearGrid();
        });
    }

    public static void finishSimulation() {
        if (doneSimulation) {
            TableColumn<Tourist, String> nameCol = new TableColumn<>("Name");
            nameCol.setMinWidth(200);
            nameCol.setMaxWidth(200);
            nameCol.setCellValueFactory(new PropertyValueFactory<>("touristName"));

            TableColumn<Tourist, Integer> collectedFlyersCol = new TableColumn<>("Collected flyers");
            collectedFlyersCol.setMinWidth(200);
            collectedFlyersCol.setMaxWidth(200);
            collectedFlyersCol.setCellValueFactory(new PropertyValueFactory<>("collectedFlyers"));

            TableColumn<Tourist, String> visitedAttractionsCol = new TableColumn<>("Visited attractions (%)");
            visitedAttractionsCol.setMinWidth(200);
            visitedAttractionsCol.setMaxWidth(200);
            visitedAttractionsCol.setCellValueFactory(cellData -> format("%.2f", cellData.getValue().getVisitedAttractions()));

            Label label = new Label();
            label.setPadding(new Insets(2, 0, 0, 0));

            JFXButton showFlyerButton = new JFXButton();
            showFlyerButton.setDisable(true);
            adminAppController.setButtonStyle(showFlyerButton, "Show flyer");
            showFlyerButton.setOnAction(e -> showFlyer());

            JFXButton saveResultsButton = new JFXButton();
            adminAppController.setButtonStyle(saveResultsButton, "Save results");
            saveResultsButton.setOnAction(e -> {
                saveResults();
                label.setText("Successful");
            });

            touristsArr.sort(Comparator.comparingInt(Tourist::getCollectedFlyers).reversed());
            table = new TableView<>();
            table.setEditable(true);
            table.setItems(touristsArr);
            table.getColumns().addAll(nameCol, collectedFlyersCol, visitedAttractionsCol);
            table.setOnMouseClicked(e -> showFlyerButton.setDisable(false));

            ScrollPane scrollPane = new ScrollPane(table);
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);     // TODO: onemoguciti prikaz horizontalBar-a !!!
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

            Region region = new Region();
            region.setPrefWidth(220);

            HBox hBox = new HBox();
            hBox.setPadding(new Insets(10, 10, 10, 10));
            hBox.setSpacing(10);
            hBox.getChildren().addAll(region, showFlyerButton, saveResultsButton, label);

            VBox vBox = new VBox();
            vBox.getChildren().addAll(scrollPane, hBox);

            tableStage = new Stage();                                 // TODO: Onemoguciti promjenu redoslijeda kolona u table-u !!!
            tableStage.setTitle("Results");
            tableStage.setScene(new Scene(vBox));
            tableStage.getIcons().add(new Image("res/icons/tableIcon.png"));
            tableStage.setResizable(false);
            tableStage.show();
        }
    }

    private static void showFlyer() {
        Tourist t = table.getSelectionModel().getSelectedItem();
        File file = t.getFlyersFolder();
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            try (BufferedReader br = new BufferedReader(new FileReader(files[new Random().nextInt(files.length)]))) {
                TextArea ta = new TextArea();
                String s;
                while ((s = br.readLine()) != null)
                    ta.appendText(s);
                Stage stage = new Stage();
                stage.setScene(new Scene(ta));
                stage.setTitle("Museum flyer");
                stage.getIcons().add(new Image("res/icons/flyerIcon.png"));
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.initOwner(tableStage);
                stage.showAndWait();
            } catch (IOException | NullPointerException e) {
            }
        }
    }

    private static void saveResults() {
        File file = new File("Results.csv");
        try (PrintWriter pw = new PrintWriter(file)) {
            for (Tourist t : table.getItems())
                pw.println(t.getTouristName() + "," + t.getCollectedFlyers() + "," + t.getVisitedAttractions());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void fillGrid() {
        for (int i = 0; i < rowNumber; ++i)
            for (int j = 0; j < columnNumber; ++j)
                if (adminAppController.getNode(grid, i, j) == null) {
                    ImageView imageView = new ImageView("res/icons/touristIcon30pxWhite.png");
                    grid.add(imageView, i, j);
                }
    }

    static void clearGrid() {
        if (grid != null) {
            for (int i = 0; i < rowNumber; ++i)
                for (int j = 0; j < columnNumber; ++j)
                    if (adminAppController.getNode(grid, i, j) != null)
                        grid.getChildren().remove(adminAppController.getNode(grid, i, j));
        }
    }
}
