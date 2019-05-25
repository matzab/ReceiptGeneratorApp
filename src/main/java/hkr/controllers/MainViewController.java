package hkr.controllers;

import com.github.sarxos.webcam.Webcam;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import hkr.model.Product;
import hkr.model.Receipt;
import hkr.utils.DatabaseHelper;
import hkr.utils.PDFFormater;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;

public class MainViewController implements Initializable {
    private final String FILE_PATH = "products.txt";
    private File file = new File(FILE_PATH);
    private File pdfFile;
    private List<Product> products = new ArrayList<>();
    private Webcam webcam;
    private DatabaseHelper databaseHelper;
    private boolean isRunning = false;

    @FXML
    private TextField nameTextField, priceTextField;
    @FXML
    private TableView<Product> productTableView;
    @FXML
    private TableColumn<Product, String> nameTableCol;
    @FXML
    private TableColumn<Product, Number> priceTableCol;
    @FXML
    private ImageView qrCodeImageView;
    @FXML
    private Button deleteProductButton;
    @FXML
    private AnchorPane mainAnchorPane;
    @FXML
    private StackPane stackPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        databaseHelper = new DatabaseHelper();
        setDefWebcImg();
        try {
            if (file.exists()) {
                readFile();
            } else {
                displayAlert("File does not exist");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        deleteProductButton.setOnAction(event -> {
            Product selectedItem = productTableView.getSelectionModel().getSelectedItem();
            productTableView.getItems().remove(selectedItem);
            products.remove(selectedItem);
            try {
                writeToFile(products);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        ObservableList<Product> productList = FXCollections.observableArrayList(products);
        productTableView.setItems(productList);
        nameTableCol.setCellValueFactory(name -> name.getValue().nameProperty());
        priceTableCol.setCellValueFactory(price -> price.getValue().priceProperty());
        setUpPopUp();
    }

    @FXML
    public void pressButton(ActionEvent event) {
        String value = ((Button) event.getSource()).getText();
        switch (value) {
            case "Add":
                String name = nameTextField.getText();
                String price = priceTextField.getText();

                if (name.equals("")) {
                    displayAlert("Name of the product cannot be empty");
                    return;
                }

                if (!priceFormat(price)) {
                    return;
                }

                Product product = new Product(name, Float.valueOf(price));
                products.add(product);
                productTableView.getItems().add(product);
                try {
                    appendProductToFile(product);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case "Start":
                // changeTop();


                if (pdfFile == null) {
                    displayAlert("Generate receipt before scanning the QR code!");
                    return;
                }

                if (!isRunning && openWebcam()) {
                    isRunning = true;
                    new CaptureThread().start();
                } else {
                    displayAlert("Camera could not be opened");
                }
                break;
            case "Stop":
                if (isRunning) {
                    isRunning = false;
                    webcam.close();
                }
                setDefWebcImg();
                break;
            case "Generate receipt":
                //TODO make more dynamic random receipts for testing purposes
                Receipt receipt = new Receipt("Jons' botique", "The Shire", "Middle Earth", 666,
                        333, 111, products, 1000.00f);
                System.out.println(receipt.formatReceipt());
                pdfFile = PDFFormater.returnRecipePdfFile(receipt.formatReceipt(), "tmp.pdf");
                break;
        }
    }

    private void setDefWebcImg(){
        qrCodeImageView.setImage(new Image(getClass().getResourceAsStream("/drawable/webcam-placeholder.png")));

    }

    private boolean openWebcam() {
        webcam = Webcam.getDefault();
        if (webcam != null) {
            return true;
        }
        return false;
    }


    private boolean priceFormat(String price) {
        try {
            Float.valueOf(price);
            return true;
        } catch (NumberFormatException e) {
            displayAlert("Incorrect price input");
            //e.printStackTrace();
        }
        return false;
    }

    private void displayAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.showAndWait();
    }

    private void writeToFile(List<Product> products) throws IOException {
        PrintWriter pw = new PrintWriter(new FileWriter("products.txt"));
        for (Product p : products) {
            pw.write(p.toString() + "\n");
        }
        pw.close();
    }

    private void appendProductToFile(Product product) throws IOException {
        PrintWriter pw = new PrintWriter(new FileWriter("products.txt", true));
        pw.write(product.toString() + "\n");
        pw.close();
    }

    private void readFile() throws IOException {
        Path p = Paths.get(FILE_PATH);
        Scanner sc = new Scanner(p);
        while (sc.hasNext()) {
            String s = sc.nextLine();
            String[] prod = s.split(", ");
            products.add(new Product(prod[0], Float.valueOf(prod[1])));
        }
    }

    private void changeTop() {
        ObservableList<Node> children = this.stackPane.getChildren();

        if (children.size() > 1) {
            //
            Node topNode = children.get(children.size()-1);
            topNode.toBack();
        }
    }

    private void setUpPopUp(){
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setPrefWidth(mainAnchorPane.getPrefWidth());
        anchorPane.setPrefHeight(mainAnchorPane.getPrefHeight());
        anchorPane.setStyle("-fx-background-color: rgba(255,255,255,0.25)");
        VBox box = new VBox();
        box.getStylesheets().add("/styles/mainSceneStyleSheet.css");
        box.setAlignment(Pos.BASELINE_CENTER);
        box.setPrefHeight(256);
        box.setPrefWidth(256);
        box.setLayoutX(172);
        box.setLayoutY(185);
        box.setStyle("-fx-background-color: white");
        Text text = new Text("Receipt successfully sent");
        Button button = new Button("OK");
        button.setOnAction(event -> {
            changeTop();
        });

        //TODO add animations


        box.getChildren().add(text);
        box.getChildren().add(button);
        anchorPane.getChildren().add(box);
        stackPane.getChildren().add(0,anchorPane);
    }


    class CaptureThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (isRunning) {

                Result result = null;
                BufferedImage image = null;

                if (webcam.isOpen()) {
                    if ((image = webcam.getImage()) == null) {
                        continue;
                    }

                    Image capture = SwingFXUtils.toFXImage(image, null);
                    qrCodeImageView.setImage(capture);

                    LuminanceSource source = new BufferedImageLuminanceSource(image);
                    BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
                    try {
                        result = new MultiFormatReader().decode(bitmap);
                    } catch (NotFoundException e) {
                        // skip
                    }
                }

                if (result != null) {
                    isRunning = false;
                    webcam.close();

                    if (pdfFile != null) {
                        try {
                            databaseHelper.uploadReceipt(result.getText(), Files.readAllBytes(pdfFile.toPath()));
                            if (pdfFile.delete()) {
                            } else {
                                displayAlert("File does not exist");
                            }
                            changeTop();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                }
            }
        }
    }
}
