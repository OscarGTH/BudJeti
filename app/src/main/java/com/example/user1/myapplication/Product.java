package com.example.user1.myapplication;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Product {
    private String category;
    private double price;
    private static int productID;
    private String purchaseDate;

    public Product(int id){
        price = 0;
        productID = id;
    }

    // Creates a product
    public Product(double newPrice,String newCategory, int id) throws NumberFormatException {
        if(newPrice > 0 && newCategory != null){
            category = newCategory;
            price = newPrice;
            productID = id;
            purchaseDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        } else {
            throw new NumberFormatException();
        }
    }

    /*
    Getters for product attributes
     */
    public double getPrice() {
        return price;
    }

    public int getProductID() {
        return productID;
    }

    public String getCategory() {
        return category;
    }
    public String getPurchaseDate(){
        return purchaseDate;
    }
}
