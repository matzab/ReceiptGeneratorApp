package hkr.model;

/* * * * * * * * * * * * * * * * * * * * * * * *
 *
 * Created by: matej
 * Time of the creation: 6:38 PM
 * Day of the creation: 4/30/2019
 *
 * * * * * * * * * * * * * * * * * * * * * * * *
 */

import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleStringProperty;

public class Product {
    private SimpleStringProperty name;
    private SimpleFloatProperty price;


    public Product(String name, float price) {
        this.name = new SimpleStringProperty(name);
        this.price = new SimpleFloatProperty(price);
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public float getPrice() {
        return price.get();
    }

    public SimpleFloatProperty priceProperty() {
        return price;
    }

    public void setPrice(float price) {
        this.price.set(price);
    }

    @Override
    public String toString() {
        return getName() + ", " + getPrice();
    }
}
