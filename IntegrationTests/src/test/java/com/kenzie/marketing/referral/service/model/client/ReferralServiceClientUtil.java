package com.kenzie.marketing.referral.service.model.client;

import com.kenzie.marketing.referral.model.CustomerReferrals;
import com.kenzie.marketing.referral.model.LeaderboardEntry;
import com.kenzie.marketing.referral.model.Referral;
import com.kenzie.marketing.referral.model.ReferralRequest;
import com.kenzie.marketing.referral.model.ReferralResponse;
import com.kenzie.marketing.referral.model.client.ReferralServiceClient;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReferralServiceClientUtil {

    public static String generate321ReferralTree(ReferralServiceClient client) {
        String rootCustomerId = UUID.randomUUID().toString();
        String aCustomerId = UUID.randomUUID().toString();
        String bCustomerId = UUID.randomUUID().toString();
        String cCustomerId = UUID.randomUUID().toString();

        String a1CustomerId = UUID.randomUUID().toString();
        String a2CustomerId = UUID.randomUUID().toString();

        String a11CustomerId = UUID.randomUUID().toString();

        client.addReferral(new ReferralRequest(rootCustomerId, ""));
        client.addReferral(new ReferralRequest(aCustomerId, rootCustomerId));
        client.addReferral(new ReferralRequest(bCustomerId, rootCustomerId));
        client.addReferral(new ReferralRequest(cCustomerId, rootCustomerId));

        client.addReferral(new ReferralRequest(a1CustomerId, aCustomerId));
        client.addReferral(new ReferralRequest(a2CustomerId, aCustomerId));

        client.addReferral(new ReferralRequest(a11CustomerId, a1CustomerId));

        return rootCustomerId;
    }

}