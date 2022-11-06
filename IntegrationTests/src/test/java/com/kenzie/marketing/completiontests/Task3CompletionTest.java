package com.kenzie.marketing.completiontests;

import com.kenzie.marketing.referral.model.LeaderboardEntry;
import com.kenzie.marketing.referral.service.ReferralService;
import com.kenzie.marketing.referral.service.dao.ReferralDao;
import com.kenzie.marketing.referral.service.model.ReferralRecord;
import com.kenzie.test.infrastructure.reflect.ClassQuery;

import org.apache.commons.lang3.concurrent.ConcurrentUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class Task3CompletionTest {

    @Test
    void checkForRunnableReferralTask() {
        Class clazz = Assertions.assertDoesNotThrow(()->
                ClassQuery
                        .inContainingPackage("com.kenzie.marketing.referral.service")
                        .withExactSimpleName("ReferralTask")
                        .findClassOrFail(),
                "There must exist a ReferralTask class");

        Assertions.assertTrue(Callable.class.isAssignableFrom(clazz), "The class must implement Callable");
    }

    @Test
    void getReferralLeaderboard_verifyCallsExecutor() {
        // GIVEN

        Class referralTask = Assertions.assertDoesNotThrow(()->
                        ClassQuery
                                .inContainingPackage("com.kenzie.marketing.referral.service")
                                .withExactSimpleName("ReferralTask")
                                .findClassOrFail(),
                "There must exist a ReferralTask class");

        ReferralDao referralDao = mock(ReferralDao.class);
        ExecutorService executorService = mock(ExecutorService.class);
        ReferralService referralService = new ReferralService(referralDao, executorService);

        List<ReferralRecord> rootReferrers = new ArrayList<>();
        ReferralRecord rootReferrer1 = new ReferralRecord();
        rootReferrer1.setCustomerId("customerid1");
        rootReferrer1.setDateReferred(ZonedDateTime.now());
        rootReferrers.add(rootReferrer1);

        ReferralRecord rootReferrer2 = new ReferralRecord();
        rootReferrer2.setCustomerId("customerid2");
        rootReferrer2.setDateReferred(ZonedDateTime.now());
        rootReferrers.add(rootReferrer2);

        when(referralDao.findUsersWithoutReferrerId()).thenReturn(rootReferrers);
        when(executorService.submit(any(Callable.class))).thenReturn(ConcurrentUtils.constantFuture(null));

        // WHEN
        try {
            List<LeaderboardEntry> entries = referralService.getReferralLeaderboard();
        } catch (Exception e) {
            // There will probably be an exception since we're mocking the executor service.
            // But the calls to the executor service should have gone through...
        }

        // THEN
        verify(executorService,
                atLeast(2)
                    .description("There should be at least two callables submitted")
            ).submit(any(Callable.class));

        verify(executorService,
                times(1)
                    .description("Shutdown should be called at least once")
            ).shutdown();
    }
}
