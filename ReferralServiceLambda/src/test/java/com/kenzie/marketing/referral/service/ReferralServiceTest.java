package com.kenzie.marketing.referral.service;


import com.kenzie.marketing.referral.model.*;
import com.kenzie.marketing.referral.service.converter.ZonedDateTimeConverter;
import com.kenzie.marketing.referral.service.dao.ReferralDao;
import com.kenzie.marketing.referral.service.exceptions.InvalidDataException;
import com.kenzie.marketing.referral.service.model.ReferralRecord;
import net.andreinc.mockneat.MockNeat;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.ArgumentCaptor;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ReferralServiceTest {

    /** ------------------------------------------------------------------------
     *  expenseService.getExpenseById
     *  ------------------------------------------------------------------------ **/

    private ReferralDao referralDao;
    private ReferralService referralService;

    @BeforeAll
    void setup() {
        this.referralDao = mock(ReferralDao.class);
        this.referralService = new ReferralService(referralDao);
    }

    @Test
    void addReferralTest() {
        ArgumentCaptor<ReferralRecord> referralCaptor = ArgumentCaptor.forClass(ReferralRecord.class);

        // GIVEN
        String customerId = "fakecustomerid";
        String referrerId = "fakereferralid";
        ReferralRequest request = new ReferralRequest();
        request.setCustomerId(customerId);
        request.setReferrerId(referrerId);

        // WHEN
        ReferralResponse response = this.referralService.addReferral(request);

        // THEN
        verify(referralDao, times(1)).addReferral(referralCaptor.capture());
        ReferralRecord record = referralCaptor.getValue();

        assertNotNull(record, "The record is valid");
        assertEquals(customerId, record.getCustomerId(), "The record customerId should match");
        assertEquals(referrerId, record.getReferrerId(), "The record referrerId should match");
        assertNotNull(record.getDateReferred(), "The record referral date exists");

        assertNotNull(response, "A response is returned");
        assertEquals(customerId, response.getCustomerId(), "The response customerId should match");
        assertEquals(referrerId, response.getReferrerId(), "The response referrerId should match");
        assertNotNull(response.getReferralDate(), "The response referral date exists");
    }

    @Test
    void addReferralTest_no_customer_id() {
        // GIVEN
        String customerId = "";
        String referrerId = "";
        ReferralRequest request = new ReferralRequest();
        request.setCustomerId(customerId);
        request.setReferrerId(referrerId);

        // WHEN / THEN
        assertThrows(InvalidDataException.class, ()->this.referralService.addReferral(request));
    }

    @Test
    void getDirectReferralsTest() {
        // GIVEN
        String customerId = "fakecustomerid";
        List<ReferralRecord> recordList = new ArrayList<>();

        ReferralRecord record1 = new ReferralRecord();
        record1.setCustomerId("customer1");
        record1.setReferrerId(customerId);
        record1.setDateReferred(ZonedDateTime.now());
        recordList.add(record1);

        ReferralRecord record2 = new ReferralRecord();
        record2.setCustomerId("customer2");
        record2.setReferrerId(customerId);
        record2.setDateReferred(ZonedDateTime.now());
        recordList.add(record2);

        when(referralDao.findByReferrerId(customerId)).thenReturn(recordList);

        // WHEN
        List<Referral> referrals = this.referralService.getDirectReferrals(customerId);

        // THEN
        verify(referralDao, times(1)).findByReferrerId(customerId);

        assertNotNull(referrals, "The returned referral list is valid");
        assertEquals(2, referrals.size(), "The referral list has 2 items");
        for (Referral referral : referrals) {
            if (record1.getCustomerId().equals(referral.getCustomerId())) {
                assertEquals(record1.getReferrerId(), customerId);
                assertEquals(new ZonedDateTimeConverter().convert(record1.getDateReferred()), referral.getReferralDate());
            } else if (record2.getCustomerId().equals(referral.getCustomerId())) {
                assertEquals(record2.getReferrerId(), customerId);
                assertEquals(new ZonedDateTimeConverter().convert(record2.getDateReferred()), referral.getReferralDate());
            } else {
                fail("A Referral was returned that does not match record 1 or 2.");
            }
        }
    }

    // Write additional tests here
    @Test
    void getCustomerReferralSummary() {
        //GIVEN
        String originalCustomerId = "This is the first ID";
        String firstLevelReferralId = "firstLevel";
        ReferralRecord firstLevelRecord = new ReferralRecord();
        firstLevelRecord.setCustomerId(firstLevelReferralId);
        firstLevelRecord.setReferrerId(originalCustomerId);
        String secondLevelReferralId = "secondLevel";
        ReferralRecord secondLevelRecord = new ReferralRecord();
        secondLevelRecord.setCustomerId(secondLevelReferralId);
        secondLevelRecord.setReferrerId(firstLevelReferralId);
        String thirdLevelReferralId = "thirdLevel";
        ReferralRecord thirdLevelRecord = new ReferralRecord();
        thirdLevelRecord.setCustomerId(thirdLevelReferralId);
        thirdLevelRecord.setReferrerId(secondLevelReferralId);

        List<ReferralRecord> firstLevelReferralList = new ArrayList<>();
        firstLevelReferralList.add(firstLevelRecord);
        List<ReferralRecord> secondLevelReferralList = new ArrayList<>();
        secondLevelReferralList.add(secondLevelRecord);
        List<ReferralRecord> thirdLevelReferralList = new ArrayList<>();
        thirdLevelReferralList.add(thirdLevelRecord);

        when(referralDao.findByReferrerId(originalCustomerId)).thenReturn(firstLevelReferralList);
        when(referralDao.findByReferrerId(firstLevelReferralId)).thenReturn(secondLevelReferralList);
        when(referralDao.findByReferrerId(secondLevelReferralId)).thenReturn(thirdLevelReferralList);

        //WHEN
        CustomerReferrals referrals = referralService.getCustomerReferralSummary(originalCustomerId);

        //THEN
        assertEquals(referrals.getNumFirstLevelReferrals(), 1, "Expected One First Level Referral");
        assertEquals(referrals.getNumSecondLevelReferrals(), 1, "Expected One Second Level Referral");
        assertEquals(referrals.getNumThirdLevelReferrals(), 1, "Expected One Third Level Referral");
    }

    /** ------------------------------------------------------------------------
     *  ReferralService.getReferralLeaderboard()
     *  ------------------------------------------------------------------------ **/

    @Test
    void getReferralLeaderboard() {
        //GIVEN
        String originalCustomerId = "This is the first ID";
        ReferralRecord record = new ReferralRecord();
        record.setCustomerId(originalCustomerId);
        String firstLevelReferralId = "firstLevel";
        ReferralRecord firstLevelRecord = new ReferralRecord();
        firstLevelRecord.setCustomerId(firstLevelReferralId);
        firstLevelRecord.setReferrerId(originalCustomerId);
        firstLevelRecord.setDateReferred(ZonedDateTime.now());

        List<ReferralRecord> userWithoutReference = new ArrayList<>();
        userWithoutReference.add(record);
        List<ReferralRecord> directReferrals = new ArrayList<>();
        directReferrals.add(firstLevelRecord);

        when(referralDao.findUsersWithoutReferrerId()).thenReturn(userWithoutReference);
        when(referralDao.findByReferrerId(originalCustomerId)).thenReturn(directReferrals);

        //WHEN
        List<LeaderboardEntry> leaderboard = referralService.getReferralLeaderboard();

        //THEN
        assertEquals(leaderboard.size(), 1);
        LeaderboardEntry entry = leaderboard.get(0);
        assertEquals(entry.getCustomerId(), originalCustomerId);
        assertEquals(entry.getNumReferrals(), 1);
    }

}