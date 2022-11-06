package com.kenzie.marketing.referral.service.dao;

import com.kenzie.marketing.referral.service.exceptions.InvalidDataException;
import com.kenzie.marketing.referral.service.model.ReferralRecord;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.google.common.collect.ImmutableMap;

import java.util.List;

public class ReferralDao {
    private DynamoDBMapper mapper;

    /**
     * Allows access to and manipulation of Match objects from the data store.
     * @param mapper Access to DynamoDB
     */
    public ReferralDao(DynamoDBMapper mapper) {
        this.mapper = mapper;
    }

    public ReferralRecord addReferral(ReferralRecord referral) {
        try {
            mapper.save(referral, new DynamoDBSaveExpression()
                    .withExpected(ImmutableMap.of(
                            "CustomerId",
                            new ExpectedAttributeValue().withExists(false)
                    )));
        } catch (ConditionalCheckFailedException e) {
            throw new InvalidDataException("Customer has already been referred");
        }

        return referral;
    }

    public List<ReferralRecord> findByReferrerId(String referrerId) {
        ReferralRecord referralRecord = new ReferralRecord();
        referralRecord.setReferrerId(referrerId);

        DynamoDBQueryExpression<ReferralRecord> queryExpression = new DynamoDBQueryExpression<ReferralRecord>()
                .withHashKeyValues(referralRecord)
                .withIndexName("ReferrerIdIndex")
                .withConsistentRead(false);

        return mapper.query(ReferralRecord.class, queryExpression);
    }

    public List<ReferralRecord> findUsersWithoutReferrerId() {
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("attribute_not_exists(ReferrerId)");

        return mapper.scan(ReferralRecord.class, scanExpression);
    }
}
