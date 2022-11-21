package com.kenzie.marketing.referral.service.dependency;


import com.kenzie.marketing.referral.service.caching.CacheClient;
import com.kenzie.marketing.referral.service.caching.CachingReferralDao;
import com.kenzie.marketing.referral.service.dao.NonCachingReferralDao;
import com.kenzie.marketing.referral.service.dao.ReferralDao;
import com.kenzie.marketing.referral.service.util.DynamoDbClientProvider;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import dagger.Module;
import dagger.Provides;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Provides DynamoDBMapper instance to DAO classes.
 */
@Module(
    includes = CachingModule.class
)
public class DaoModule {

    @Singleton
    @Provides
    @Named("DynamoDBMapper")
    public DynamoDBMapper provideDynamoDBMapper() {
        return new DynamoDBMapper(DynamoDbClientProvider.getDynamoDBClient());
    }

    @Singleton
    @Provides
    @Named("ReferralDao")
    @Inject
    public ReferralDao provideReferralDao(
        @Named("CacheClient") CacheClient cacheClient,
        @Named("NonCachingReferralDao") NonCachingReferralDao nonCachingReferralDao) {
            return new CachingReferralDao(cacheClient, nonCachingReferralDao);
    }

    @Singleton
    @Provides
    @Named("NonCachingReferralDao")
    @Inject
    public NonCachingReferralDao provideNonCachingReferralDao(@Named("DynamoDBMapper") DynamoDBMapper mapper) {
        return new NonCachingReferralDao(mapper);
    }

}
