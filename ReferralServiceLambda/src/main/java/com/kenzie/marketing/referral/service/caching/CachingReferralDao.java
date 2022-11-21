package com.kenzie.marketing.referral.service.caching;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.kenzie.marketing.referral.service.dao.NonCachingReferralDao;
import com.kenzie.marketing.referral.service.dao.ReferralDao;
import com.kenzie.marketing.referral.service.model.ReferralRecord;

import javax.inject.Inject;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class CachingReferralDao implements ReferralDao {
    private static final int REFERRAL_READ_TTL = 60 * 60;
    private static final String REFERRAL_KEY = "ReferralKey::%s";
    private final CacheClient cacheClient;
    private final NonCachingReferralDao referralDao;

    private final Gson gson;

    @Inject
    public CachingReferralDao(CacheClient cacheClient, NonCachingReferralDao referralDao) {
        this.cacheClient = cacheClient;
        this.referralDao = referralDao;
        this.gson = builder.create();
    }
    @Override
    public ReferralRecord addReferral(ReferralRecord referral) {
        cacheClient.invalidate(referral.getReferrerId());
        return referralDao.addReferral(referral);
    }

    @Override
    public List<ReferralRecord> findByReferrerId(String referrerId) {
        List<ReferralRecord> referralRecordList = new ArrayList<>();
        cacheClient.getValue(String.format(REFERRAL_KEY, referrerId)).ifPresentOrElse(string -> referralRecordList.addAll(fromJson(string)),
                () -> referralRecordList.addAll(addToCache(referralDao.findByReferrerId(referrerId), referrerId)));
        return referralRecordList;
    }

    @Override
    public List<ReferralRecord> findUsersWithoutReferrerId() {
        return referralDao.findUsersWithoutReferrerId();
    }

    GsonBuilder builder = new GsonBuilder().registerTypeAdapter(
            ZonedDateTime.class,
            new TypeAdapter<ZonedDateTime>() {
                @Override
                public void write(JsonWriter out, ZonedDateTime value) throws IOException {
                    out.value(value.toString());
                }
                @Override
                public ZonedDateTime read(JsonReader in) throws IOException {
                    return ZonedDateTime.parse(in.nextString());
                }
            }
    ).enableComplexMapKeySerialization();

    private List<ReferralRecord> fromJson(String json) {
        return gson.fromJson(json, new TypeToken<ArrayList<ReferralRecord>>() { }.getType());
    }

    private List<ReferralRecord> addToCache(List<ReferralRecord> records, String referrerId) {
        cacheClient.setValue(
                String.format(REFERRAL_KEY, referrerId),
                REFERRAL_READ_TTL,
                gson.toJson(records)
        );
        return  records;
    }
}
