package com.kenzie.marketing.referral.model;

public class ReferralResponse {
    private String customerId;
    private String referrerId;
    private String referralDate;

    public ReferralResponse(String customerId, String referrerId, String referralDate) {
        this.customerId = customerId;
        this.referrerId = referrerId;
        this.referralDate = referralDate;
    }

    public ReferralResponse() {
    }

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
    public String toString() {
        return "ReferralResponse{" +
                "customerId='" + customerId + '\'' +
                ", referrerId='" + referrerId + '\'' +
                ", referralDate=" + referralDate +
                '}';
    }
}
