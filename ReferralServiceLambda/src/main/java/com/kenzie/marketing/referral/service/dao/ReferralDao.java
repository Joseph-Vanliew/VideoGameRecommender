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

public interface ReferralDao {
    ReferralRecord addReferral(ReferralRecord referral);
    List<ReferralRecord> findByReferrerId(String referrerId);
    List<ReferralRecord> findUsersWithoutReferrerId();

}
