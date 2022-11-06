package com.kenzie.marketing.referral.model;

public class CustomerReferrals {
    private Integer numFirstLevelReferrals;
    private Integer numSecondLevelReferrals;
    private Integer numThirdLevelReferrals;

    public CustomerReferrals() {
        this.numFirstLevelReferrals = 0;
        this.numSecondLevelReferrals = 0;
        this.numThirdLevelReferrals = 0;
    }

    public Integer getNumFirstLevelReferrals() {
        return numFirstLevelReferrals;
    }

    public void setNumFirstLevelReferrals(Integer numFirstLevelReferrals) {
        this.numFirstLevelReferrals = numFirstLevelReferrals;
    }

    public Integer getNumSecondLevelReferrals() {
        return numSecondLevelReferrals;
    }

    public void setNumSecondLevelReferrals(Integer numSecondLevelReferrals) {
        this.numSecondLevelReferrals = numSecondLevelReferrals;
    }

    public Integer getNumThirdLevelReferrals() {
        return numThirdLevelReferrals;
    }

    public void setNumThirdLevelReferrals(Integer numThirdLevelReferrals) {
        this.numThirdLevelReferrals = numThirdLevelReferrals;
    }
}
