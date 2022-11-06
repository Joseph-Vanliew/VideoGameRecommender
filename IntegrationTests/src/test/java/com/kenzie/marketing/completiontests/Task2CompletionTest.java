package com.kenzie.marketing.completiontests;

import com.kenzie.marketing.referral.model.CustomerReferrals;
import com.kenzie.marketing.referral.model.LeaderboardEntry;
import com.kenzie.marketing.referral.model.client.ReferralServiceClient;
import com.kenzie.marketing.referral.service.model.client.ReferralServiceClientUtil;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Task2CompletionTest {

    @Test
    void referralServiceClient_getReferralSummary() {
        // GIVEN
        ReferralServiceClient client = new ReferralServiceClient();
        String customerId = ReferralServiceClientUtil.generate321ReferralTree(client);
        // WHEN
        CustomerReferrals referrals = client.getReferralSummary(customerId);
        // THEN
        assertNotNull(referrals, "The Referrals exist");
        assertEquals(3, referrals.getNumFirstLevelReferrals());
        assertEquals(2, referrals.getNumSecondLevelReferrals());
        assertEquals(1, referrals.getNumThirdLevelReferrals());
    }

    @Test
    void referralServiceClient_getLeaderboard() {
        // GIVEN
        ReferralServiceClient client = new ReferralServiceClient();
        String customerId = ReferralServiceClientUtil.generate321ReferralTree(client);
        // WHEN
        List<LeaderboardEntry> leaderboard = client.getLeaderboard();
        // THEN
        assertNotNull(leaderboard, "The leaderboard exist");
        assertTrue(leaderboard.size() > 0, "The leaderboard has entries");
        int highestEntry = Integer.MAX_VALUE;
        for (LeaderboardEntry entry : leaderboard) {
            assertNotNull(entry.getCustomerId(), "Each entry has a customerId");
            assertTrue(entry.getNumReferrals() <= highestEntry, "Entries are in descending order");
            highestEntry = entry.getNumReferrals();
        }
    }
}
