package com.kenzie.marketing.completiontests;

import com.kenzie.test.infrastructure.reflect.ClassQuery;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class Task4CompletionTest {

    @Test
    void checkForDaoInterfaces() {
        Class intrfce = Assertions.assertDoesNotThrow(()->
                        ClassQuery
                                .inContainingPackage("com.kenzie.marketing.referral.service")
                                .withExactSimpleName("ReferralDao")
                                .findClassOrFail(),
                "There must exist a ReferralDao class");

        Assertions.assertTrue(intrfce.isInterface(), "The ReferralDao is an Interface");

        Class nonCachingReferralDao = Assertions.assertDoesNotThrow(()->
                        ClassQuery
                                .inContainingPackage("com.kenzie.marketing.referral.service")
                                .withExactSimpleName("NonCachingReferralDao")
                                .findClassOrFail(),
                "There must exist a NonCachingReferralDao class");

        Assertions.assertTrue(intrfce.isAssignableFrom(nonCachingReferralDao),
                "The NonCachingReferralDao must implement the ReferralDao Interface");

        Class cachingReferralDao = Assertions.assertDoesNotThrow(()->
                        ClassQuery
                                .inContainingPackage("com.kenzie.marketing.referral.service")
                                .withExactSimpleName("CachingReferralDao")
                                .findClassOrFail(),
                "There must exist a CachingReferralDao class");

        Assertions.assertTrue(intrfce.isAssignableFrom(cachingReferralDao),
                "The CachingReferralDao must implement the ReferralDao Interface");
    }
}