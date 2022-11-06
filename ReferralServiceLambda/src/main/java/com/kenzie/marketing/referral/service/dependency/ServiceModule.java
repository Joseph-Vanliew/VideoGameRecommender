package com.kenzie.marketing.referral.service.dependency;

import com.kenzie.marketing.referral.service.ReferralService;
import com.kenzie.marketing.referral.service.dao.ReferralDao;

import dagger.Module;
import dagger.Provides;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Module(
    includes = DaoModule.class
)
public class ServiceModule {

    @Singleton
    @Provides
    @Inject
    public ReferralService provideReferralService(@Named("ReferralDao") ReferralDao referralDao) {
        return new ReferralService(referralDao);
    }
}

