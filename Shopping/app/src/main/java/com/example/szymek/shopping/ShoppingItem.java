package com.example.szymek.shopping;

/**
 * Created by Szymek on 10-Mar-17.
 */

public class ShoppingItem {
    private String title;
    private Double quantity;

    public ShoppingItem(String title, Double quantity) {
        this.title = title;
        this.quantity = quantity;
    }

    public String getTitle() {
        return title;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }
}
