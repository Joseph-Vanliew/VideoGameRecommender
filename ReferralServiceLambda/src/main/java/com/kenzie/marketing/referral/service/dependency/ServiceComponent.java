package com.kenzie.marketing.referral.service.dependency;

import com.kenzie.marketing.referral.service.ReferralService;

import dagger.Component;
import redis.clients.jedis.Jedis;

import javax.inject.Singleton;

/**
 * Declares the dependency roots that Dagger will provide.
 */
@Singleton
@Component(modules = {DaoModule.class, CachingModule.class, ServiceModule.class})
public interface ServiceComponent {
    ReferralService provideReferralService();
    Jedis provideJedis();
}
