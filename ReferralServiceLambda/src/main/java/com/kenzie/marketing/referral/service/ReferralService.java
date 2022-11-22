package com.kenzie.marketing.referral.service;

import com.kenzie.marketing.referral.model.CustomerReferrals;
import com.kenzie.marketing.referral.model.LeaderboardEntry;
import com.kenzie.marketing.referral.model.Referral;
import com.kenzie.marketing.referral.model.ReferralRequest;
import com.kenzie.marketing.referral.model.ReferralResponse;
import com.kenzie.marketing.referral.service.comparator.ReferralComparator;
import com.kenzie.marketing.referral.service.converter.ReferralConverter;
import com.kenzie.marketing.referral.service.converter.ZonedDateTimeConverter;
import com.kenzie.marketing.referral.service.dao.ReferralDao;
import com.kenzie.marketing.referral.service.exceptions.InvalidDataException;
import com.kenzie.marketing.referral.service.model.ReferralRecord;
import com.kenzie.marketing.referral.service.task.ReferralTask;

import javax.inject.Inject;

import java.sql.Ref;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class ReferralService {

    private ReferralDao referralDao;
    private ExecutorService executor;

    @Inject
    public ReferralService(ReferralDao referralDao) {
        this.referralDao = referralDao;
        this.executor = Executors.newCachedThreadPool();
    }

    // Necessary for testing, do not delete
    public ReferralService(ReferralDao referralDao, ExecutorService executor) {
        this.referralDao = referralDao;
        this.executor = executor;
    }

    public List<LeaderboardEntry> getReferralLeaderboard() {

        List<ReferralRecord> nodes = this.referralDao.findUsersWithoutReferrerId();
        List<Future<LeaderboardEntry>> threadFutures = new ArrayList<>();
        for(ReferralRecord node : nodes) {
            ReferralTask task = new ReferralTask(node, referralDao);
            threadFutures.add(executor.submit(task));
        }
        executor.shutdown();
        try {
            executor.awaitTermination(20, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException("Executor was interrupted " + e);
        }
        return condenseList(threadFutures);
    }
    private List<LeaderboardEntry> condenseList (List<Future<LeaderboardEntry>> threadFutures) {
        List<LeaderboardEntry> entries = new ArrayList<>();
        for(Future<LeaderboardEntry> future : threadFutures) {
            try {
                entries.add(future.get());
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        return entries.stream()
                .sorted(new ReferralComparator().reversed()).limit(5)
                .collect(Collectors.toList());
    }

    public CustomerReferrals getCustomerReferralSummary(String customerId) {
        // Task 2 Code Here

        CustomerReferrals referrals = new CustomerReferrals();
        List<ReferralRecord> referralRecords = referralDao.findByReferrerId(customerId);
        int firstLevelReferral = 0;
        int secondLevelReferral = 0;
        int thirdLevelReferral = 0;
        for(ReferralRecord referralRecord : referralRecords) {
            firstLevelReferral++;
            for(ReferralRecord nextReferral : referralDao.findByReferrerId(referralRecord.getCustomerId())){
                secondLevelReferral++;
               thirdLevelReferral += referralDao.findByReferrerId(nextReferral.getCustomerId()).size();
            }
        }
        referrals.setNumFirstLevelReferrals(firstLevelReferral);
        referrals.setNumSecondLevelReferrals(secondLevelReferral);
        referrals.setNumThirdLevelReferrals(thirdLevelReferral);
        return referrals;
    }


    public List<Referral> getDirectReferrals(String customerId) {

        return referralDao.findByReferrerId(customerId)
                .stream()
                .map(ReferralConverter::fromRecordToReferral)
                .collect(Collectors.toList());
    }


    public ReferralResponse addReferral(ReferralRequest referral) {
        if (referral == null || referral.getCustomerId() == null || referral.getCustomerId().length() == 0) {
            throw new InvalidDataException("Request must contain a valid Customer ID");
        }
        ReferralRecord record = ReferralConverter.fromRequestToRecord(referral);
        referralDao.addReferral(record);
        return ReferralConverter.fromRecordToResponse(record);
    }

}
