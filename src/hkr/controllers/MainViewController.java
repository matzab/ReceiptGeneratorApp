package hkr.controllers;

import com.github.sarxos.webcam.Webcam;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import hkr.model.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;

public class MainViewController implements Initializable {
    private final String FILE_PATH = "products.txt";
    private final File file = new File(FILE_PATH);
    private List<Product> products = new ArrayList<>();
    private Webcam webcam;
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


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        try {
            if (file.exists()) {
                readFile();
            } else {
                // file does not exist
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        displaySavedProducts();


    }

    private void writeToFile(List<Product> products) throws IOException {
        PrintWriter pw = new PrintWriter(new FileWriter("products.txt"));

        for (Product p : products) {
            //  System.out.println("Number written: " +p.toString() );
            pw.write(p.toString() + "\n");
        }
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

    @FXML
    public void pressButton(ActionEvent event) {
        String value = ((Button) event.getSource()).getText();

        switch (value) {
            case "+":
                String name = nameTextField.getText();
                String price = priceTextField.getText();

                if (priceFormat(price) || !name.equals("")) {
                    products.add(new Product(name, Float.valueOf(price)));
                    try {
                        writeToFile(products);
                        displaySavedProducts();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Incorrect input", ButtonType.OK);
                    alert.showAndWait();
                }
                break;

            case "start":
                webcam = Webcam.getDefault();
                webcam.open();
                if (!isRunning) {
                    isRunning = true;
                    new CaptureThread().start();
                } else {

                }
                break;
            case "stop":
                isRunning = false;
                webcam.close();
                break;
        }

    }


    private boolean priceFormat(String price) {
        return price.matches("[-+]?[0-9]*\\.?[0-9]+");
    }

    private void displaySavedProducts() {
        ObservableList<Product> productList = FXCollections.observableArrayList(products);
        productTableView.setItems(productList);
        nameTableCol.setCellValueFactory(name -> name.getValue().nameProperty());
        priceTableCol.setCellValueFactory(price -> price.getValue().priceProperty());
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

                    //  ImageIO.write(webcam.getImage(), "PNG", new File("hello-world.png"));

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
                    System.out.println("QR code data is: " + result.getText());
                }

                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                }
            }
        }
    }

}
