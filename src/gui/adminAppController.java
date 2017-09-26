package gui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
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
        clearDirectory(new File("Names"));      // Obrisi stari sadrzaj direktorijum kao i sam direktorijum
        new File("Names").mkdir();              // Napravi novi direktorijum
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
                makeAlertWindow("Table is empty!\nPlease add attractions to it.", "Empty table", "WARNING");
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
                    makeAlertWindow("Table is empty!\nPlease add attractions to it.", "Empty table", "WARNING");
                else {
                    if (tableFlag == 0) {
                        tableStage.initOwner(viewButton.getScene().getWindow());
                    }
                    tableStage.showAndWait();
                }
        });
        startButton.setOnAction(e -> {
            if (attractions.isEmpty())
                makeAlertWindow("Table is empty!\nPlease add attractions to start the simulation.", "Empty table", "WARNING");
            else {
                serialize();
                switchToUser();
            }
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
            if (event.getButton() == MouseButton.SECONDARY)
                table.getContextMenu().show(table, event.getScreenX(), event.getScreenY());     // Prikazi contextMenu za edit ili remove ukoliko je desni klik pritisnut
        });
        table.getColumns().addListener(new ListChangeListener() {
            public boolean suspended;

            @Override
            public void onChanged(ListChangeListener.Change change) {
                change.next();
                if (change.wasReplaced() && !suspended) {
                    this.suspended = true;
                    table.getColumns().setAll(attractionCol, nameCol, locationCol);
                    this.suspended = false;
                }
            }
        });     // Onemogucuje promjenu redoslijeda kolona

        ScrollPane scrollPane = new ScrollPane(table);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        tableStage = new Stage();
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
            switch (title) {
                case "WARNING":
                    makeAlert(new Alert(Alert.AlertType.WARNING), text, header, title);
                    break;
                case "INFORMATION":
                    makeAlert(new Alert(Alert.AlertType.INFORMATION), text, header, title);
                    break;
                case "ERROR":
                    makeAlert(new Alert(Alert.AlertType.ERROR), text, header, title);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void makeAlert(Alert alert, String text, String header, String title) {
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(text);
        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(new Image("res/icons/alertIcon.png"));
        alert.showAndWait();
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

        if (createOrEditFlag.equals("edit")) {
            ((JFXTextField) getNode(gridPane, 1, 0)).setText(attractions.get(attractionIndex).getName());
            ((JFXTextField) getNode(gridPane, 1, 1)).setText(attractions.get(attractionIndex).getLocation());
        }
        switch (newAttraction) {
            case "HistoricalMonument":
                historicalMonumentForm(gridPane, hBox, createOrEditFlag);
                break;
            case "Museum":
                museumForm(gridPane, hBox, createOrEditFlag);
                break;
            case "AmusementPark":
                amusementParkForm(gridPane, hBox, createOrEditFlag);
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
                    makeAlertWindow("Press ESC/ENTER to exit the window.", "Submition successful", "INFORMATION");
                    viewImage(file);
                    file = null;
                    serialize();
                } else
                    makeAlertWindow("Please fill the empty fields", "Empty field(s)", "WARNING");
                break;
            case "Museum":
                if (!name.isEmpty() && !location.isEmpty() && file != null) {
                    attractions.add(new Museum(name, location, file));
                    attractionStage.close();
                    makeAlertWindow("Press ESC/ENTER to exit the window.", "Submition successful", "INFORMATION");
                    file = null;
                    serialize();
                } else
                    makeAlertWindow("Please fill the empty fields", "Empty field(s)", "WARNING");
                break;
            case "AmusementPark":
                if (!name.isEmpty() && !location.isEmpty()) {
                    try {
                        String price = ((JFXTextField) getNode(grid, 1, 2)).getText();
                        if (price != null && !price.isEmpty())
                            try {
                                Integer tmp = Integer.parseInt(price);
                                tmp = tmp > 0 ? tmp : tmp == 0 ? 10 : (-1) * tmp;
                                attractions.add(new AmusementPark(name, location, tmp));
                                attractionStage.close();
                                makeAlertWindow("Press ESC/ENTER to exit the window.", "Submition successful", "INFORMATION");
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
                            makeAlertWindow("Press ESC/ENTER to exit the window.", "Submition successful", "INFORMATION");
                            serialize();
                        }
                    } catch (NullPointerException e) {
                        System.out.println("NULLPOINTEREXC");
                    }
                } else
                    makeAlertWindow("Enter name & location of the attraction", "Empty field(s)", "WARNING");
                break;
            case "Church":
                if (!name.isEmpty() && !location.isEmpty()) {
                    attractions.add(new Church(name, location));
                    attractionStage.close();
                    makeAlertWindow("Press ESC/ENTER to exit the window.", "Submition successful", "INFORMATION");
                    serialize();
                } else
                    makeAlertWindow("Please fill the empty fields", "Empty field(s)", "WARNING");
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
                } else
                    makeAlertWindow("Please fill the empty fields", "Empty field(s)", "WARNING");
                break;
            case "Museum":
                if (!name.isEmpty() && !location.isEmpty() && file != null) {
                    Museum mm = (Museum) attractions.get(attractionIndex);
                    mm.setName(name);
                    mm.setLocation(location);
                    mm.setFlyer(file);
                    file = null;
                } else
                    makeAlertWindow("Please fill the empty fields", "Empty field(s)", "WARNING");
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
                                tmp = tmp > 0 ? tmp : tmp == 0 ? 10 : (-1) * tmp;
                                ap.setPrice(tmp);
                            } catch (NumberFormatException e) {
                                attractionStage.close();
                                makeAttractionWindow();
                                attractionForm(attractionToEdit, "create");
                            }
                    } catch (NullPointerException e) {
                        System.out.println("NULLPOINTEREXCEPTION IN EDITATTRACTION !!!");
                    }
                } else
                    makeAlertWindow("Enter name & location of the attraction", "Empty field(s)", "WARNING");
                break;
            case "Church":
                if (!name.isEmpty() && !location.isEmpty()) {
                    Church c = (Church) attractions.get(attractionIndex);
                    c.setName(name);
                    c.setLocation(location);
                } else
                    makeAlertWindow("Please fill the empty fields", "Empty field(s)", "WARNING");
                break;
        }
        attractionStage.close();
        table.refresh();
        serialize();
    }

    private void historicalMonumentForm(GridPane gridPane, HBox hBox, String createOrEditFlag) {
        Label descriptionLabel = new Label("Description:");
        descriptionLabel.setPadding(new Insets(5, 5, 5, 5));

        Label imageLabel = new Label("Image upload:");
        imageLabel.setPadding(new Insets(5, 5, 5, 5));

        Label imageName = new Label();
        imageName.setPadding(new Insets(5, 5, 5, 5));
        gridPane.add(imageName, 2, 3, 2, 1);
        if (createOrEditFlag.equals("edit")) {
            File f = new File(((HistoricalMonument) attractions.get(attractionIndex)).getImage().replace("file:/", ""));
            imageName.setText(f.getName());
        }

        JFXTextArea descriptionTextArea = new JFXTextArea();
        descriptionTextArea.setPadding(new Insets(5, 0, 5, 0));
        if (createOrEditFlag.equals("edit"))
            descriptionTextArea.setText(((HistoricalMonument) attractions.get(attractionIndex)).getDescription());
        else
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

    private void museumForm(GridPane gridPane, HBox hBox, String createOrEditFlag) {
        Label flyerLabel = new Label("Flyer upload:");
        flyerLabel.setPadding(new Insets(5, 5, 5, 5));

        Label flyerName = new Label();
        flyerName.setPadding(new Insets(5, 5, 5, 5));
        gridPane.add(flyerName, 1, 3, 3, 1);

        JFXButton flyerButton = new JFXButton();
        setButtonStyle(flyerButton, "Upload flyer");
        flyerButton.setOnAction(e -> addFile(flyerName));

        if (createOrEditFlag.equals("edit")) {
            File f = new File(((Museum) attractions.get(attractionIndex)).getFlyer().replace("file:/", ""));
            flyerName.setText(f.getName());
        }

        gridPane.add(flyerLabel, 0, 2);
        gridPane.add(flyerButton, 1, 2);
        gridPane.add(hBox, 1, 4);
    }

    private void amusementParkForm(GridPane gridPane, HBox hBox, String createOrEditFlag) {
        Label priceLabel = new Label("Entry price:");
        priceLabel.setPadding(new Insets(5, 5, 5, 5));

        JFXTextField priceTextField = new JFXTextField();
        if (createOrEditFlag.equals("edit"))
            priceTextField.setText(((AmusementPark) attractions.get(attractionIndex)).getPrice() + "");
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

    public static Node getNode(GridPane grid, int col, int row) {
        for (Node node : grid.getChildren())
            if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row)
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
                makeAlertWindow("Table is empty!\nPlease add attractions to it.", "Empty table", "WARNING");
            }
        });

        menu.getItems().addAll(edit, remove);
        table.setContextMenu(menu);
    }

    static void serialize() {
        try (FileOutputStream fos = new FileOutputStream("turisticka-mapa.ser"); ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(new ArrayList<>(attractions));
        } catch (FileNotFoundException e) {
//            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("IOEXCEPTION IN SERIALIZE !!!");
//            e.printStackTrace();
        }
    }

    static void deserialize() {
        try (FileInputStream fis = new FileInputStream("turisticka-mapa.ser"); ObjectInputStream ois = new ObjectInputStream(fis)) {
            attractions.setAll(FXCollections.observableList((List<TouristAttraction>) ois.readObject()));
        } catch (FileNotFoundException | ClassNotFoundException e) {
            //e.printStackTrace();
        } catch (IOException e) {
            //System.out.println("IOEXCEPTION IN DESERIALIZE !!!");
            File f = new File("turisticka-mapa.ser"); // Brisanje turisticke mape jer postoji mogucnost da neki objekat nije ispravno deserijalizovan
            if (f.exists())
                f.delete();
            //e.printStackTrace();
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

    private void clearDirectory(File folder) {
        if (folder.exists()) {
            File files[] = folder.listFiles();
            if (files != null)
                for (File f : files)
                    if (f.isDirectory())
                        clearDirectory(f);
                    else
                        f.delete();
            folder.delete();
        }
    }
}