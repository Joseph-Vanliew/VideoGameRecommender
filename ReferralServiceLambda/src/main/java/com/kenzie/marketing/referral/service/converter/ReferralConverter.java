package com.kenzie.marketing.referral.service.converter;

import com.kenzie.marketing.referral.model.Referral;
import com.kenzie.marketing.referral.model.ReferralRequest;
import com.kenzie.marketing.referral.model.ReferralResponse;
import com.kenzie.marketing.referral.service.model.ReferralRecord;

import java.time.ZonedDateTime;

public class ReferralConverter {

    private static ZonedDateTimeConverter converter = new ZonedDateTimeConverter();

    public static ReferralRecord fromRequestToRecord(ReferralRequest referral) {
        ReferralRecord record = new ReferralRecord();
        record.setCustomerId(referral.getCustomerId());
        record.setReferrerId(referral.getReferrerId());
        record.setDateReferred(ZonedDateTime.now());
        return record;
    }

    public static ReferralResponse fromRecordToResponse(ReferralRecord record) {
        ReferralResponse referral = new ReferralResponse();
        referral.setReferrerId(record.getReferrerId());
        referral.setCustomerId(record.getCustomerId());
        referral.setReferralDate(converter.convert(record.getDateReferred()));
        return referral;
    }

    public static Referral fromRecordToReferral(ReferralRecord record) {
        Referral referral = new Referral();
        referral.setCustomerId(record.getCustomerId());
        referral.setReferrerId(record.getReferrerId());
        referral.setReferralDate(new ZonedDateTimeConverter().convert(record.getDateReferred()));
        return referral;
    }
}
