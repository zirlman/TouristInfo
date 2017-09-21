package sample;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.etfbl.pj2.TouristInfo.attractions.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class adminAppController {
    @FXML
    private JFXButton addButton;
    @FXML
    private JFXButton viewButton;
    @FXML
    private JFXButton startButton;
    @FXML
    private JFXComboBox<String> taComboBox;
    /*private ObservableList<TouristAttraction> tourristAttractions
            = FXCollections.observableArrayList(
            new HistoricalMonument("Cathedral of Christ the Saviour", "Banja Luka"),
            new Museum("Museum of Modern Art of Republika Srpska", "Banja Luka"),
            new AmusementPark("Aquana", "Banja Luka"),
            new Church("Holy Trinity Church", "Banja Luka"));*/
    private ObservableList<String> tourristAttractions = FXCollections.observableArrayList("Historical Monument", "Museum", "Amusement Park", "Church");
    static ObservableList<TouristAttraction> attractions = FXCollections.observableArrayList();
    private TableView table;
    private Stage tableStage;
    private static int tableFlag;
    private Stage attractionStage;
    private Group attractionGroup;
    private static int attractionFlag;
    private int attractionIndex; // Pri editovanju atrakcije, indeks atrakcije u tabeli = indeks atrakcije u ObservableList
    private String file;

    @FXML
    private void initialize() {
        makeTable();
        deserialize();
        taComboBox.getItems().addAll(tourristAttractions);
        addButton.setOnAction(e -> {
            String newAttraction = taComboBox.getValue();
            if (newAttraction != null) {
                newAttraction = newAttraction.replaceAll("\\s", "");
                if (attractionFlag == 0)
                    attractionStage.initOwner(addButton.getScene().getWindow());
                attractionForm(newAttraction, "create");
            }
        });
        addButton.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                String newAttraction = taComboBox.getValue();
                if (newAttraction != null) {
                    newAttraction = newAttraction.replaceAll("\\s", "");
                    if (attractionFlag == 0)
                        attractionStage.initOwner(addButton.getScene().getWindow());
                    attractionForm(newAttraction, "create");
                }
            }

        });
        viewButton.setOnAction(e -> {
            if (attractions.isEmpty())
                makeAlertWindow("Table is empty!\nPlease add attractions to it.", "Empty table", "ALERT");
            else {
                if (tableFlag == 0) {
                    tableStage.initOwner(viewButton.getScene().getWindow());
                }
                tableStage.showAndWait();
            }
        });
        viewButton.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER)
                if (attractions.isEmpty())
                    makeAlertWindow("Table is empty!\nPlease add attractions to it.", "Empty table", "ALERT");
                else {
                    if (tableFlag == 0) {
                        tableStage.initOwner(viewButton.getScene().getWindow());
                    }
                    tableStage.showAndWait();
                }
        });
        startButton.setOnAction(e -> {
            serialize();
            switchToUser();
        });
        startButton.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                serialize();
                switchToUser();
            }
        });
        ++tableFlag;                // Flagovi koji se koriste da bi se izbjegao NullPointerException prilikom
        ++attractionFlag;           // poziva initOwner()-a  u addButton i viewButton
    }

    private void makeTable() {
        table = new TableView();

        TableColumn<TouristAttraction, String> nameCol = new TableColumn<>("Name");
        nameCol.setMinWidth(200);
        nameCol.setMaxWidth(200);
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));            // uzima vrijednost reference name i smjesta je u kolonu
        nameCol.setSortable(false);

        TableColumn<TouristAttraction, String> locationCol = new TableColumn<>("Location");
        locationCol.setMinWidth(200);
        locationCol.setMaxWidth(200);
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));    // uzima vrijendost reference location i smjesta je u kolonu
        locationCol.setSortable(false);
//        locationCol.setCellFactory(TextFieldTableCell.forTableColumn());
//        locationCol.setOnEditCommit((
//                TableColumn.CellEditEvent<TouristAttraction, String> t) ->
//                (t.getTableView().getItems().get(t.getTableLocation().getRow())).setLocation(t.getNewValue()));
        TableColumn<TouristAttraction, String> attractionCol = new TableColumn<>("Tourist Attraction");
        attractionCol.setMinWidth(150);
        attractionCol.setMaxWidth(150);
        attractionCol.setCellValueFactory(new PropertyValueFactory<>("className"));
        attractionCol.setSortable(false);

        table.setEditable(true);
        table.setItems(attractions);
        table.getColumns().addAll(attractionCol, nameCol, locationCol);
        setTableContextMenu();
        table.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                // Prikazi contextMenu za edit ili remove ukoliko je desni klik pritisnut
                table.getContextMenu().show(table, event.getScreenX(), event.getScreenY());
            }
        });

        ScrollPane scrollPane = new ScrollPane(table);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);     // TODO: onemoguciti prikaz horizontalBar-a !!!
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        tableStage = new Stage();                                       // TODO: Onemoguciti promjenu redoslijeda kolona u table-u !!!
        tableStage.setTitle("Tourrist Attractions");
        tableStage.setScene(new Scene(scrollPane));
        tableStage.getIcons().add(new Image("res/icons/tableIcon.png"));
        tableStage.setMinWidth(550);
        tableStage.setMinHeight(200);
        tableStage.setMaxWidth(550);
        tableStage.setMaxHeight(200);
    }

    static void makeAlertWindow(String text, String header, String title) {
        try {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(text);
            Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
            alertStage.getIcons().add(new Image("res/icons/errorIcon.png"));
            alert.showAndWait();
            //TODO: Obrisati komentare
//            Label label = new Label();
//            label.setText(text);
//            System.out.println(label.getText());
//            label.setAlignment(Pos.CENTER);
//            label.setTextAlignment(TextAlignment.CENTER);
//            label.setPadding(new Insets(10, 10, 10, 30));
//
//            Pane alertPane = new Pane();
//            alertPane.getChildren().add(label);
//            alertStage = new Stage();
//            alertStage.setTitle(title);
//            Scene scene = new Scene(alertPane, 300, 100);         // Zatvori prozor preko ESC ili ENTER
//            scene.setOnKeyPressed(e -> {
//                if (e.getCode() == KeyCode.ESCAPE || e.getCode() == KeyCode.ENTER) alertStage.close();
//            });
//            alertStage.setScene(scene);
//            alertStage.setMinWidth(300);
//            alertStage.setMinHeight(100);
//            alertStage.setMaxWidth(300);
//            alertStage.setMaxHeight(100);
//            alertStage.initModality(Modality.APPLICATION_MODAL);                 // Blokiraj rad sa drugim prozorima sve dok se ovaj ne zatvori
//            alertStage.initOwner(viewButton.getScene().getWindow());
//            alertStage.getIcons().add(new Image("res/icons/errorIcon.png"));
//            alertStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void makeAttractionWindow() {
        Label nameLabel = new Label("Name:");
        nameLabel.setPadding(new Insets(5, 5, 0, 5));

        Label locationLabel = new Label("Location:");
        locationLabel.setPadding(new Insets(5, 5, 0, 5));

        JFXTextField nameTextField = new JFXTextField();
        nameTextField.setPromptText("Enter name");

        JFXTextField locationTextField = new JFXTextField();
        locationTextField.setPromptText("Enter location");

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.add(nameLabel, 0, 0);
        gridPane.add(nameTextField, 1, 0);
        gridPane.add(locationLabel, 0, 1);
        gridPane.add(locationTextField, 1, 1);

        attractionGroup = new Group();
        attractionGroup.getChildren().add(gridPane);
        attractionStage = new Stage();
        attractionStage.setTitle("Add attraction");
        attractionStage.setResizable(false);
        attractionStage.setScene(new Scene(attractionGroup));
        attractionStage.getIcons().add(new Image("res/icons/addAttractionIcon.png"));
        attractionStage.initModality(Modality.APPLICATION_MODAL);                 // Blokiraj rad sa drugim prozorima sve dok se ovaj ne zatvori
        attractionStage.setMinWidth(260);
        attractionStage.setOnCloseRequest(e -> {
            attractionStage.close();
            makeAttractionWindow();
        });
    }

    private void attractionForm(String newAttraction, String createOrEditFlag) {
        makeAttractionWindow();
        GridPane gridPane = (GridPane) attractionGroup.getChildren().get(0);

        JFXButton submit = new JFXButton();
        setButtonStyle(submit, "Submit");
        submit.setOnAction(e -> {
            if (createOrEditFlag.equals("create"))
                createAttraction(gridPane, newAttraction);
            if (createOrEditFlag.equals("edit"))
                editAttraction(gridPane, newAttraction);
        });
        submit.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                if (createOrEditFlag.equals("create"))
                    createAttraction(gridPane, newAttraction);
                if (createOrEditFlag.equals("edit"))
                    editAttraction(gridPane, newAttraction);
            }
        });
        JFXButton cancel = new JFXButton();
        setButtonStyle(cancel, "Cancel");
        cancel.setOnAction(e -> {
            attractionStage.close();
            makeAttractionWindow();
        });

        HBox hBox = new HBox();
        hBox.setPadding(new Insets(10, 10, 0, 0));
        hBox.setSpacing(10);
        hBox.getChildren().addAll(submit, cancel);
        switch (newAttraction) {
            case "HistoricalMonument":
                historicalMonumentForm(gridPane, hBox);
                break;
            case "Museum":
                museumForm(gridPane, hBox);
                break;
            case "AmusementPark":
                amusementParkForm(gridPane, hBox);
                break;
            case "Church":
                churchForm(gridPane, hBox);
                break;
        }
        attractionStage.showAndWait();
    }

    private void createAttraction(GridPane grid, String newAttraction) {
        String name = ((JFXTextField) getNode(grid, 1, 0)).getText();
        String location = ((JFXTextField) getNode(grid, 1, 1)).getText();
        switch (newAttraction) {
            case "HistoricalMonument":
                String description = ((JFXTextArea) getNode(grid, 1, 2)).getText();
                if (!name.isEmpty() && !location.isEmpty() && !description.isEmpty() && file != null) {
                    attractions.add(new HistoricalMonument(name, location, description, file));
                    attractionStage.close();
                    makeAlertWindow("Press ESC/ENTER to exit the window.", "Submition successful", "ALERT");
                    viewImage(file);
                    file = null;
                    serialize();
                }
                break;
            case "Museum":
                if (!name.isEmpty() && !location.isEmpty() && file != null) {
                    attractions.add(new Museum(name, location, file));
                    attractionStage.close();
                    makeAlertWindow("Press ESC/ENTER to exit the window.", "Submition successful", "ALERT");
                    file = null;
                    serialize();
                }
                break;
            case "AmusementPark":
                if (!name.isEmpty() && !location.isEmpty()) {
                    try {
                        String price = ((JFXTextField) getNode(grid, 1, 2)).getText();
                        if (price != null && !price.isEmpty())
                            try {
                                Integer tmp = Integer.parseInt(price);
                                attractions.add(new AmusementPark(name, location, tmp));
                                attractionStage.close();
                                makeAlertWindow("Press ESC/ENTER to exit the window.", "Submition successful", "ALERT");
                                serialize();
                            } catch (NumberFormatException e) {
                                attractionStage.close();
                                makeAlertWindow("Invalid number format.", "Invalid input", "ERROR");
                                makeAttractionWindow();
                                attractionForm(newAttraction, "create");
                            }
                        else {
                            attractions.add(new AmusementPark(name, location));
                            attractionStage.close();
                            makeAlertWindow("Press ESC/ENTER to exit the window.", "Submition successful", "ALERT");
                            serialize();
                        }
                    } catch (NullPointerException e) {
                        System.out.println("NULLPOINTEREXC");
                    }
                }
                break;
            case "Church":
                if (!name.isEmpty() && !location.isEmpty()) {
                    attractions.add(new Church(name, location));
                    attractionStage.close();
                    makeAlertWindow("Press ESC/ENTER to exit the window.", "Submition successful", "ALERT");
                    serialize();
                }
                break;
        }
    }

    private void editAttraction(GridPane grid, String attractionToEdit) {
        String name = ((JFXTextField) getNode(grid, 1, 0)).getText();
        String location = ((JFXTextField) getNode(grid, 1, 1)).getText();
        switch (attractionToEdit) {
            case "HistoricalMonument":
                String description = ((JFXTextArea) getNode(grid, 1, 2)).getText();
                if (!name.isEmpty() && !location.isEmpty() && !description.isEmpty() && file != null) {
                    HistoricalMonument hm = (HistoricalMonument) attractions.get(attractionIndex);
                    hm.setName(name);
                    hm.setLocation(location);
                    hm.setDescription(description);
                    hm.setImage(file);
                    viewImage(file);
                    file = null;
                }
                break;
            case "Museum":
                if (!name.isEmpty() && !location.isEmpty() && file != null) {
                    Museum mm = (Museum) attractions.get(attractionIndex);
                    mm.setName(name);
                    mm.setLocation(location);
                    mm.setFlier(file);
                    file = null;
                }
                break;
            case "AmusementPark":
                if (!name.isEmpty() && !location.isEmpty()) {
                    AmusementPark ap = (AmusementPark) attractions.get(attractionIndex);
                    ap.setName(name);
                    ap.setLocation(location);
                    try {
                        String price = ((JFXTextField) getNode(grid, 1, 2)).getText();
                        if (price != null && !price.isEmpty())
                            try {
                                Integer tmp = Integer.parseInt(price);
                                ap.setPrice(tmp);
                            } catch (NumberFormatException e) {
                                attractionStage.close();
                                makeAttractionWindow();
                                attractionForm(attractionToEdit, "create");
                            }
                    } catch (NullPointerException e) {
                        System.out.println("NULLPOINTEREXCEPTION IN EDITATTRACTION !!!");
                    }
                }
                break;
            case "Church":
                if (!name.isEmpty() && !location.isEmpty()) {
                    Church c = (Church) attractions.get(attractionIndex);
                    c.setName(name);
                    c.setLocation(location);
                }
                break;
        }
        attractionStage.close();
        table.refresh();
        serialize();
    }

    private void historicalMonumentForm(GridPane gridPane, HBox hBox) {
        Label descriptionLabel = new Label("Description:");
        descriptionLabel.setPadding(new Insets(5, 5, 5, 5));

        Label imageLabel = new Label("Image upload:");
        imageLabel.setPadding(new Insets(5, 5, 5, 5));

        Label imageName = new Label();
        imageName.setPadding(new Insets(5, 5, 5, 5));
        gridPane.add(imageName, 2, 3, 2, 1);

        JFXTextArea descriptionTextArea = new JFXTextArea();
        descriptionTextArea.setPadding(new Insets(5, 0, 5, 0));
        descriptionTextArea.setPromptText("Enter description");

        JFXButton imageButton = new JFXButton();
        setButtonStyle(imageButton, "Upload image");
        imageButton.setOnAction(e -> addFile(imageName));

        gridPane.add(descriptionLabel, 0, 2);
        gridPane.add(descriptionTextArea, 1, 2, 4, 1);
        GridPane.setValignment(descriptionLabel, VPos.TOP);             // Pozicionira labelu
        gridPane.add(imageLabel, 0, 3);
        gridPane.add(imageButton, 1, 3);
        gridPane.add(hBox, 2, 4);
    }

    private void museumForm(GridPane gridPane, HBox hBox) {
        Label flierLabel = new Label("Flier upload:");
        flierLabel.setPadding(new Insets(5, 5, 5, 5));

        Label flierName = new Label();
        flierName.setPadding(new Insets(5, 5, 5, 5));
        gridPane.add(flierName, 1, 3, 3, 1);

        JFXButton flierButton = new JFXButton();
        setButtonStyle(flierButton, "Upload flier");
        flierButton.setOnAction(e -> addFile(flierName));

        gridPane.add(flierLabel, 0, 2);
        gridPane.add(flierButton, 1, 2);
        gridPane.add(hBox, 1, 4);
    }

    private void amusementParkForm(GridPane gridPane, HBox hBox) {
        Label priceLabel = new Label("Entry price:");
        priceLabel.setPadding(new Insets(5, 5, 5, 5));

        JFXTextField priceTextField = new JFXTextField();
        priceTextField.setPromptText("Enter entry price");

        gridPane.add(priceLabel, 0, 2);
        gridPane.add(priceTextField, 1, 2);
        gridPane.add(hBox, 1, 3);
    }

    private void churchForm(GridPane gridPane, HBox hBox) {
        gridPane.add(hBox, 1, 2);
    }

    static void setButtonStyle(JFXButton button, String s) {
        button.setStyle("-fx-background-color:  #1E88E5");
        button.setTextFill(Color.WHITE);
        button.setRipplerFill(Color.WHITE);
        button.setText(s);
    }

    private void addFile(Label label) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("./src/res"));
        fileChooser.setTitle("Open resource File");
        File f = fileChooser.showOpenDialog(attractionStage);
        if (f != null) {
            label.setText(f.getName());
            file = f.toURI().toString();
        }
    }

    private void viewImage(String im) {
        ImageView iv = new ImageView(im);
        Group group = new Group();
        group.getChildren().add(iv);
        Scene scene = new Scene(group);
        Stage stage = new Stage();
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE || e.getCode() == KeyCode.ENTER)
                stage.close();  // Zatvori prozor preko ESC ili ENTER
        });
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);                 // Blokiraj rad sa drugim prozorima sve dok se ovaj ne zatvori
        stage.initOwner(viewButton.getScene().getWindow());
        stage.getIcons().add(new Image("res/icons/imageIcon.png"));
        stage.showAndWait();
    }

    public static synchronized Node getNode(GridPane grid, int col, int row) {
        for (Node node : grid.getChildren())
            if (grid.getColumnIndex(node) == col && grid.getRowIndex(node) == row)
                return node;
        return null;
    }

    private void setTableContextMenu() {
        ContextMenu menu = new ContextMenu();

        MenuItem edit = new MenuItem("Edit");
        edit.setOnAction(e -> {
            attractionIndex = table.getSelectionModel().selectedIndexProperty().get();
            attractionForm(table.getSelectionModel().getSelectedItem().getClass().getSimpleName(), "edit");
        });

        MenuItem remove = new MenuItem("Remove");
        remove.setOnAction(e -> {
            attractionIndex = table.getSelectionModel().selectedIndexProperty().get();
            attractions.remove(attractionIndex);
            table.refresh();
            serialize();
            if (attractions.isEmpty()) {
                tableStage.close();
                makeAlertWindow("Table is empty!\nPlease add attractions to it.", "Empty table", "ALERT");
            }
        });

        menu.getItems().addAll(edit, remove);
        table.setContextMenu(menu);
    }

    static void serialize() {
        try (FileOutputStream fos = new FileOutputStream("turisticka-mapa.ser"); ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(new ArrayList<>(attractions));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("IOEXCEPTION IN SERIALIZE !!!");
            e.printStackTrace();
        }
    }

    static void deserialize() {
        try (FileInputStream fis = new FileInputStream("turisticka-mapa.ser"); ObjectInputStream ois = new ObjectInputStream(fis)) {
            attractions.setAll(FXCollections.observableList((List<TouristAttraction>) ois.readObject()));
        } catch (FileNotFoundException e) {
            //e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("IOEXCEPTION IN DESERIALIZE !!!");
            e.printStackTrace();
        }
    }

    private void switchToUser() {
        FXMLLoader loader2 = new FXMLLoader(getClass().getResource("userApp.fxml"));
        try {
            Parent root = loader2.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root, 590, 393));
            stage.setTitle("Tourist");
            stage.setResizable(false);
            stage.getIcons().add(new Image("res/icons/touristIcon.png"));
            viewButton.getScene().getWindow().hide();
            stage.setOnCloseRequest(e -> userAppController.clearGrid());
            stage.show();
        } catch (IOException e) {
            System.out.println("USER SWITCH IOEXCEPTION");
            e.printStackTrace();
        }
    }
}