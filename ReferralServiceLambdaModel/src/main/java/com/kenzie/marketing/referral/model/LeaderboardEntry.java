package com.kenzie.marketing.referral.model;

public class LeaderboardEntry {
    private int numReferrals;
    private String customerId;

    public LeaderboardEntry(int numReferrals, String customerId) {
        this.numReferrals = numReferrals;
        this.customerId = customerId;
    }

    public LeaderboardEntry() {
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
}
