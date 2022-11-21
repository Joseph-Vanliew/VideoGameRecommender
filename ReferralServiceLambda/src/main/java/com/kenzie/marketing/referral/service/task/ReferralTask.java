package com.kenzie.marketing.referral.service.task;

import com.kenzie.marketing.referral.model.LeaderboardEntry;
import com.kenzie.marketing.referral.service.ReferralService;
import com.kenzie.marketing.referral.service.dao.ReferralDao;
import com.kenzie.marketing.referral.service.model.ReferralRecord;

import java.util.concurrent.Callable;

public class ReferralTask implements Callable<LeaderboardEntry> {
    private final ReferralRecord referralRecord;
    private final ReferralService referralService;

    public ReferralTask(ReferralRecord referralRecord, ReferralDao referralDao) {
        this.referralRecord = referralRecord;
        this.referralService = new ReferralService(referralDao);
    }
    @Override
    public LeaderboardEntry call() throws Exception {
        return new LeaderboardEntry(referralService.getDirectReferrals(referralRecord.getCustomerId()).size(), referralRecord.getCustomerId());
    }
}
