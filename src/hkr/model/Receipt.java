package hkr.model;

import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Creator: matej
 * Date: 2019-05-07
 * Time: 21:18
 */

public class Receipt {
    private String store;
    private String location;
    private String country;
    private int receiptNumber;
    private int terminalID;
    private int cashierID;
    private List<Product> products;
    private float subTotal;
    private float saleTax;
    private float total;
    private float cashTotal;
    private float change;

    private float TAX_PERCENTAGE = 0.3f;

    public Receipt(String store, String location, String country, int receiptNumber, int terminalID, int cashierID, List<Product> products, float cashTotal) {
        this.store = store;
        this.location = location;
        this.country = country;
        this.receiptNumber = receiptNumber;
        this.terminalID = terminalID;
        this.cashierID = cashierID;
        this.products = products;
        this.cashTotal = cashTotal;
        calculateTotal();
        calculateSaleTax();
        calculateSubTotal();
        calculateChange();
    }

    private String getCurrentDate(){
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    private void calculateTotal(){
        for(Product p : products){
            total += p.getPrice();
        }
    }

    private void calculateSubTotal(){
        subTotal = total * TAX_PERCENTAGE;
    }

    private void calculateSaleTax(){
        saleTax = total - subTotal;
    }

    private void calculateChange(){

        if(total <= cashTotal){
            change = cashTotal - total;
        }else {
            // error not enough

        }
    }

    private String formatProducts(){
        SecureRandom sr = new SecureRandom();
        int quanity = sr.nextInt(10)+1;
        StringBuilder body = new StringBuilder();
        body.append("Name     Qnty    Price\n");

        for (Product p: products) {
            body.append(p.getName() + "     ");
            body.append(quanity + "     ");
            body.append(p.getPrice());
            body.append("\n");
        }

        return body.toString();
    }

    public String formatReceipt(){
        String header = String.format("%s%n%s%n%s%n Receipt number %04d  Cashier ID %04d %nTerminal %d %s %n------------------------------------ %n" +
                        "%s %n ------------------------------------ %nSUBTOTAL %f%nSALE TAX %f%nTOTAL %f%nCASH SALE %f%n%nCHANGE %f%n"
                ,store ,location, country, receiptNumber, cashierID, terminalID,
                getCurrentDate(), formatProducts(), subTotal, saleTax, total, cashTotal, change);
        return header;

    }
}