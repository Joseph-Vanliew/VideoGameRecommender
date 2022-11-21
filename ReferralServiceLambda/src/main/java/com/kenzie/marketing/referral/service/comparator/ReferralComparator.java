package com.kenzie.marketing.referral.service.comparator;

import com.kenzie.marketing.referral.model.LeaderboardEntry;

import java.util.Comparator;

public class ReferralComparator implements Comparator<LeaderboardEntry> {

    @Override
    public int compare(LeaderboardEntry o1, LeaderboardEntry o2) {
        return Integer.compare(o1.getNumReferrals(), o2.getNumReferrals());
    }
}
