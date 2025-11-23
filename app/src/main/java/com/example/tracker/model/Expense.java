package com.example.tracker.model;

import com.google.gson.annotations.SerializedName;

public class Expense {
    @SerializedName("id")
    private String id;

    @SerializedName("userId")
    private String userId;

    @SerializedName("amount")
    private double amount;

    @SerializedName("description")
    private String description;

    @SerializedName("category")
    private String category;

    @SerializedName("date")
    private long date;

    public Expense(String userId, double amount, String description, String category, long date) {
        this.userId = userId;
        this.amount = amount;
        this.description = description;
        this.category = category;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
