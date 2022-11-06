package com.kenzie.marketing.referral.service.model;

import com.kenzie.marketing.referral.service.converter.ZonedDateTimeConverter;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;

import java.time.ZonedDateTime;
import java.util.Objects;

@DynamoDBTable(tableName = "Referral")
public class ReferralRecord {

    private String customerId;
    private String referrerId;
    private ZonedDateTime dateReferred;

    @DynamoDBHashKey(attributeName = "CustomerId")
    public String getCustomerId() {
        return customerId;
    }

    @DynamoDBAttribute(attributeName = "ReferrerId")
    @DynamoDBIndexHashKey(globalSecondaryIndexName = "ReferrerIdIndex", attributeName = "ReferrerId")
    public String getReferrerId() {
        return referrerId;
    }

    @DynamoDBAttribute(attributeName = "DateReferred")
    @DynamoDBTypeConverted(converter = ZonedDateTimeConverter.class)
    public ZonedDateTime getDateReferred() {
        return dateReferred;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public void setReferrerId(String referrerId) {
        this.referrerId = referrerId;
    }

    public void setDateReferred(ZonedDateTime dateReferred) {
        this.dateReferred = dateReferred;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ReferralRecord that = (ReferralRecord) o;
        return Objects.equals(customerId, that.customerId) && Objects.equals(referrerId, that.referrerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId, referrerId);
    }
}
