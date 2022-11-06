package com.kenzie.marketing.application.controller.model;

public class LeaderboardUiEntry {
    private String customerId;
    private String customerName;
    private int numReferrals;

    public LeaderboardUiEntry(String customerId, String customerName, int numReferrals) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.numReferrals = numReferrals;
    }

    public LeaderboardUiEntry() {
    }

    public int getNumReferrals() {
        return numReferrals;
    }

    public void setNumReferrals(int numReferrals) {
        this.numReferrals = numReferrals;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
}
