package hkr.controllers;

import hkr.model.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.*;
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
            FileInputStream input = new FileInputStream("qrcode.jpg");
            Image image = new Image(input);
            qrCodeImageView.setImage(image);
        }catch (IOException e){
            e.printStackTrace();
        }


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

}
