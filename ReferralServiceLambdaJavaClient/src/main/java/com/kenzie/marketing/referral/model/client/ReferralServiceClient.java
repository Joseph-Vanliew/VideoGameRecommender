package com.kenzie.marketing.referral.model.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kenzie.marketing.referral.model.CustomerReferrals;
import com.kenzie.marketing.referral.model.LeaderboardEntry;
import com.kenzie.marketing.referral.model.Referral;
import com.kenzie.marketing.referral.model.ReferralRequest;
import com.kenzie.marketing.referral.model.ReferralResponse;

import java.util.List;

public class ReferralServiceClient {

    private static final String ADD_REFERRAL_ENDPOINT = "referral/add";
    private static final String GET_REFERRAL_SUMMARY_ENDPOINT = "referral/{customerId}";
    private static final String GET_DIRECT_REFERRALS_ENDPOINT = "referral/list/{customerId}";
    private static final String GET_LEADERBOARD_ENDPOINT = "referral/leaderboard";

    private ObjectMapper mapper;

    public ReferralServiceClient() {
        this.mapper = new ObjectMapper();
    }

    public ReferralResponse addReferral(ReferralRequest referralRequest) {
        EndpointUtility endpointUtility = new EndpointUtility();
        String request;
        try {
            request = mapper.writeValueAsString(referralRequest);
        } catch(JsonProcessingException e) {
            throw new ApiGatewayException("Unable to serialize request: " + e);
        }
        String response = endpointUtility.postEndpoint(ADD_REFERRAL_ENDPOINT, request);
        ReferralResponse referralResponse;
        try {
            referralResponse = mapper.readValue(response, ReferralResponse.class);
        } catch (Exception e) {
            throw new ApiGatewayException("Unable to map deserialize JSON: " + e);
        }
        return referralResponse;
    }

    public CustomerReferrals getReferralSummary(String customerId) {
        EndpointUtility endpointUtility = new EndpointUtility();
        String response = endpointUtility.getEndpoint(GET_REFERRAL_SUMMARY_ENDPOINT.replace("{customerId}", customerId));
        CustomerReferrals referrals;
        try {
            referrals = mapper.readValue(response, CustomerReferrals.class);
        } catch (Exception e) {
            throw new ApiGatewayException("Unable to map deserialize JSON: " + e);
        }
        return referrals;
    }

    public List<Referral> getDirectReferrals(String customerId) {
        EndpointUtility endpointUtility = new EndpointUtility();
        String response = endpointUtility.getEndpoint(GET_DIRECT_REFERRALS_ENDPOINT.replace("{customerId}", customerId));
        List<Referral> referrals;
        try {
            referrals = mapper.readValue(response, new TypeReference<>(){});
        } catch (Exception e) {
            throw new ApiGatewayException("Unable to map deserialize JSON: " + e);
        }
        return referrals;
    }

    public List<LeaderboardEntry> getLeaderboard() {
        EndpointUtility endpointUtility = new EndpointUtility();
        String response = endpointUtility.getEndpoint(GET_LEADERBOARD_ENDPOINT);
        List<LeaderboardEntry> leaderboard;
        try {
            leaderboard = mapper.readValue(response, new TypeReference<>(){});
        } catch (Exception e) {
            throw new ApiGatewayException("Unable to map deserialize JSON: " + e);
        }
        return leaderboard;
    }
}
