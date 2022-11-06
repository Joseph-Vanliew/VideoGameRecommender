package com.kenzie.marketing.referral.service.converter;

import com.kenzie.marketing.referral.model.ReferralRequest;
import com.kenzie.marketing.referral.service.exceptions.InvalidDataException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonStringToReferralConverter {

    public ReferralRequest convert(String body) {
        try {
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            ReferralRequest referralRequest = gson.fromJson(body, ReferralRequest.class);
            return referralRequest;
        } catch (Exception e) {
            throw new InvalidDataException("Referral could not be deserialized");
        }
    }
}
