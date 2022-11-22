package com.kenzie.marketing.application.service;

import com.kenzie.marketing.application.controller.model.CreateCustomerRequest;
import com.kenzie.marketing.application.controller.model.CustomerResponse;
import com.kenzie.marketing.application.controller.model.LeaderboardUiEntry;
import com.kenzie.marketing.application.repositories.CustomerRepository;
import com.kenzie.marketing.application.repositories.model.CustomerRecord;
import com.kenzie.marketing.referral.model.CustomerReferrals;
import com.kenzie.marketing.referral.model.LeaderboardEntry;
import com.kenzie.marketing.referral.model.Referral;
import com.kenzie.marketing.referral.model.ReferralRequest;
import com.kenzie.marketing.referral.model.client.ReferralServiceClient;
import org.joda.time.DateTime;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.util.UUID.randomUUID;


@Service
public class CustomerService {
    private static final Double REFERRAL_BONUS_FIRST_LEVEL = 10.0;
    private static final Double REFERRAL_BONUS_SECOND_LEVEL = 3.0;
    private static final Double REFERRAL_BONUS_THIRD_LEVEL = 1.0;

    private final CustomerRepository customerRepository;
    private final ReferralServiceClient referralServiceClient;


    public CustomerService(CustomerRepository customerRepository, ReferralServiceClient referralServiceClient) {
        this.customerRepository = customerRepository;
        this.referralServiceClient = referralServiceClient;
    }

    /**
     * findAllCustomers
     * @return A list of Customers
     */
    public List<CustomerResponse> findAllCustomers() {
        List<CustomerRecord> records = StreamSupport.stream(customerRepository.findAll().spliterator(), true).collect(Collectors.toList());
//
        return records.stream()
                .map(this::toCustomerResponseFromRecord)
                .collect(Collectors.toList());
    }

    /**
     * findByCustomerId
     * @param customerId
     * @return The Customer with the given customerId
     */
    public CustomerResponse getCustomer(String customerId) {
        return toCustomerResponseFromRecord(customerRepository.findById(customerId)
                .orElse(null));
    }

    /**
     * addNewCustomer
     *
     * This creates a new customer.  If the referrerId is included, the referrerId must be valid and have a
     * corresponding customer in the DB.  This posts the referrals to the referral service
     * @param createCustomerRequest
     * @return A CustomerResponse describing the customer
     */
    public CustomerResponse addNewCustomer(CreateCustomerRequest createCustomerRequest) {
        CustomerRecord customerRecord = (toCustomerRecord(createCustomerRequest));

        if(customerRecord.getReferrerId() != null && !customerRecord.getReferrerId().equals("") && !customerRepository.existsById(customerRecord.getReferrerId())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "referralID was not found");
        }

        ReferralRequest referralRequest = new ReferralRequest(customerRecord.getId(), customerRecord.getReferrerId());
        referralServiceClient.addReferral(referralRequest);

        customerRepository.save(customerRecord);
        return toCustomerResponseFromRecord(customerRecord);
    }

    /**
     * updateCustomer - This updates the customer name for the given customer id
     * @param customerId - The id of the customer to update
     * @param customerName - The new name for the customer
     */
    public CustomerResponse updateCustomer(String customerId, String customerName) {

        return Optional.of(customerRepository.findById(customerId))
                .stream()
                .peek(customerRecord -> {
                    if(customerRecord.isEmpty()) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer Not Found");
                    }
                    customerRecord.get().setName(customerName);
                    customerRepository.save(customerRecord.get());
                })
                .map(customerRecord -> toCustomerResponseFromRecord(customerRecord.get()))
                .findFirst().get();
    }

    /**
     * deleteCustomer - This deletes the customer record for the given customer id
     * @param customerId
     */
    public void deleteCustomer(String customerId) {
        customerRepository.deleteById(customerId); //TODO write test for this!
    }

    /**
     * calculateBonus - This calculates the referral bonus for the given customer according to the referral bonus
     * constants.
     * @param customerId
     * @return
     */
    public Double calculateBonus(String customerId) {
        CustomerReferrals referrals = referralServiceClient.getReferralSummary(customerId);

        Double calculationResult = REFERRAL_BONUS_FIRST_LEVEL * referrals.getNumFirstLevelReferrals() +
                REFERRAL_BONUS_SECOND_LEVEL * referrals.getNumSecondLevelReferrals() +
                REFERRAL_BONUS_THIRD_LEVEL * referrals.getNumThirdLevelReferrals();

        return calculationResult;
    }

    /**
     * getReferrals - This returns a list of referral entries for every customer directly referred by the given
     * customerId.
     * @param customerId
     * @return
     */
    public List<CustomerResponse> getReferrals(String customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new IllegalArgumentException("Customer does not exist");
        }
        //TODO write unit test!
        List <Referral> referrals = referralServiceClient.getDirectReferrals(customerId);
        return referrals.stream()
                .map(this::toCustomerResponseFromReferral)
                .collect(Collectors.toList());
    }

    /**
     * getLeaderboard - This calls the referral service to retrieve the current top 5 leaderboard of the most referrals
     * @return
     */

    public List<LeaderboardUiEntry> getLeaderboard() {
        // Task 2 - Add your code here
        List<LeaderboardEntry> leaderBoardList = referralServiceClient.getLeaderboard();

        return leaderBoardList
                .stream()
                .filter(referralCount -> referralCount.getNumReferrals()>0)
                .map(this::toLeaderboardUI)
                .collect(Collectors.toList());
    }

    /* -----------------------------------------------------------------------------------------------------------
        Private Methods
       ----------------------------------------------------------------------------------------------------------- */

    // Add any private methods here
    private CustomerResponse toCustomerResponseFromRecord(CustomerRecord record) {
        if(record == null) {
            return null;
        }
        CustomerResponse customerResponse = new CustomerResponse();

        customerResponse.setId(record.getId());
        customerResponse.setName(record.getName());
        customerResponse.setDateJoined(record.getDateCreated());
        customerResponse.setReferrerId(record.getReferrerId());

        CustomerRecord referrer;
        if (customerRepository.existsById(record.getReferrerId())){
            referrer = (customerRepository.findById(record.getReferrerId()).orElse(null));
            customerResponse.setReferrerName(referrer.getName()); //TODO write test for this check
        }
        return customerResponse;
    }
    private CustomerRecord toCustomerRecord(CreateCustomerRequest createCustomerRequest) {
        CustomerRecord customerRecord = new CustomerRecord();
        customerRecord.setName(createCustomerRequest.getName());
        customerRecord.setId(randomUUID().toString());
        customerRecord.setDateCreated(DateTime.now().toString());
        if(createCustomerRequest.getReferrerId().isPresent()) {
            customerRecord.setReferrerId(createCustomerRequest.getReferrerId().toString());
        } else {
            customerRecord.setReferrerId("");
        }
        return customerRecord;
    }
    private CustomerResponse toCustomerResponseFromReferral (Referral referral){
        CustomerResponse customerResponse = new CustomerResponse();
        customerResponse.setId(referral.getCustomerId());
        customerResponse.setReferrerId(referral.getReferrerId());
        customerResponse.setDateJoined(referral.getReferralDate());
        return customerResponse;
    }
    private LeaderboardUiEntry toLeaderboardUI(LeaderboardEntry entry) {
        LeaderboardUiEntry leaderboardUiEntry = new LeaderboardUiEntry();
        leaderboardUiEntry.setCustomerId(entry.getCustomerId());
        leaderboardUiEntry.setNumReferrals(entry.getNumReferrals());

        if (customerRepository.existsById(entry.getCustomerId())) {
            leaderboardUiEntry.setCustomerName(getCustomer(entry.getCustomerId()).getName());
        } else {
            leaderboardUiEntry.setCustomerName("No name present");
        }

        return leaderboardUiEntry;
    }
}