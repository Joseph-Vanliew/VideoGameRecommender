package com.kenzie.marketing.referral.model;


public class ReferralRequest {
    private String customerId;
    private String referrerId;

    public ReferralRequest(String customerId, String referrerId) {
        this.customerId = customerId;
        this.referrerId = referrerId;
    }

    public ReferralRequest() {
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

    @Override
    public String toString() {
        return "ReferralRequest{" +
                "customerId='" + customerId + '\'' +
                ", referrerId='" + referrerId + '\'' +
                '}';
    }
}
