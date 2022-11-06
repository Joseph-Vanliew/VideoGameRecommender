package com.kenzie.marketing.completiontests;


import com.kenzie.marketing.referral.model.Referral;
import com.kenzie.marketing.referral.model.ReferralRequest;
import com.kenzie.marketing.referral.model.ReferralResponse;
import com.kenzie.marketing.referral.model.client.ReferralServiceClient;
import com.kenzie.marketing.referral.service.model.client.ReferralServiceClientUtil;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Task1CompletionTest {

    @Test
    void referralServiceClient_addReferral_no_referrer() {
        // GIVEN
        String customerId = UUID.randomUUID().toString();
        ReferralServiceClient client = new ReferralServiceClient();
        ReferralRequest request = new ReferralRequest(customerId, "");

        // WHEN
        ReferralResponse response = client.addReferral(request);

        // THEN
        assertTrue(response.getCustomerId().equals(customerId), "The customerId matches");
        assertNotNull(response.getReferralDate(), "The date is not null");
        assertEquals("", response.getReferrerId(), "The referrerId is empty");
    }

    @Test
    void referralServiceClient_addReferral_with_valid_referrer() {
        // GIVEN
        ReferralServiceClient client = new ReferralServiceClient();

        String customerId = UUID.randomUUID().toString();
        String referrerId = UUID.randomUUID().toString();
        client.addReferral(new ReferralRequest(referrerId, ""));

        // WHEN
        ReferralResponse response = client.addReferral(new ReferralRequest(customerId, referrerId));

        // THEN
        assertEquals(customerId, response.getCustomerId(), "The customerId matches");
        assertNotNull(response.getReferralDate(), "The date is not null");
        assertEquals(referrerId, response.getReferrerId(), "The referrerId matches");
    }

    @Test
    void referralServiceClient_getReferrals_multiple() {
        // GIVEN
        ReferralServiceClient client = new ReferralServiceClient();
        String customerId = ReferralServiceClientUtil.generate321ReferralTree(client);
        // WHEN
        List<Referral> referrals = client.getDirectReferrals(customerId);
        // THEN
        assertNotNull(referrals, "The Referrals exist");
        assertEquals(3, referrals.size(), "There are three direct referrals");
        for (Referral referral : referrals) {
            assertEquals(customerId, referral.getReferrerId(), "The referral was referred by the customer");
            assertNotNull(referral.getCustomerId(), "The referral has a customerId");
            assertNotNull(referral.getReferralDate(), "The referral has a date");
        }
    }
}
