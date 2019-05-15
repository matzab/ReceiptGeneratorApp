package hkr.model;

import org.junit.Assert;

import java.util.ArrayList;

import static org.junit.Assert.*;


public class ReceiptTest {
    private ArrayList<Product> products = new ArrayList<>();
    private Receipt receipt;

    @org.junit.Test
    public void calculateSubTotal() {
        testDataInit();
        System.out.println("Calculate sub total test ...");
        receipt.calculateSubTotal();
        float expectedSubTotal = 8.988f;
        Assert.assertEquals(expectedSubTotal, receipt.getSubTotal(), 0.00f);

    }

    @org.junit.Test
    public void calculateSaleTax() {
        testDataInit();
        System.out.println("Calculate total sale tax ... ");
        receipt.calculateSaleTax();
        float expectedTax = 3.852f;
        Assert.assertEquals(expectedTax, receipt.getSaleTax(), 0.00f);
    }

    @org.junit.Test
    public void calculateChange() {
        testDataInit();
        System.out.println("Calculate return change ... ");
        receipt.calculateChange();
        float expectedChange = 2.16f;
        Assert.assertEquals(expectedChange, receipt.getChange(),0.00f);
    }

    private void testDataInit(){
        products.add(new Product("potato", 1.39f));
        products.add(new Product("wax", 4.00f));
        products.add(new Product("brush", 7.23f));
        products.add(new Product("yeast", 0.22f));
        receipt = new Receipt("Jons' botique", "The Shire", "Middle Earth", 666,
                333, 111, products, 15.00f);
    }
}