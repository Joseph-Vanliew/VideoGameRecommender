package com.kenzie.marketing.referral.model;

import java.util.Objects;

public class Referral {
    private String customerId;
    private String referrerId;
    private String referralDate;

    public Referral(String customerId, String referrerId, String referralDate) {
        this.customerId = customerId;
        this.referrerId = referrerId;
        this.referralDate = referralDate;
    }

    public Referral() {}

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getReferrerId() {
        return referrerId;
    }

    public void setReferrerId(String referrerId) {
        this.referrerId = referrerId;
    }

    public String getReferralDate() {
        return referralDate;
    }

    public void setReferralDate(String referralDate) {
        this.referralDate = referralDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Referral referral = (Referral) o;
        return Objects.equals(customerId, referral.customerId) &&
                Objects.equals(referrerId, referral.referrerId) &&
                Objects.equals(referralDate, referral.referralDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId, referrerId, referralDate);
    }

    @Override
    public String toString() {
        return "Referral{" +
                "customerId='" + customerId + '\'' +
                ", referrerId='" + referrerId + '\'' +
                ", referralDate='" + referralDate + '\'' +
                '}';
    }
}
