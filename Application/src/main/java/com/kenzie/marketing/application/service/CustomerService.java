package com.kenzie.marketing.application.service;

import com.kenzie.marketing.application.controller.model.CreateCustomerRequest;
import com.kenzie.marketing.application.controller.model.CustomerResponse;
import com.kenzie.marketing.application.controller.model.LeaderboardUiEntry;
import com.kenzie.marketing.application.controller.repositories.CustomerRepository;
import com.kenzie.marketing.application.controller.repositories.model.CustomerRecord;
import com.kenzie.marketing.referral.model.CustomerReferrals;
import com.kenzie.marketing.referral.model.ReferralRequest;
import com.kenzie.marketing.referral.model.client.ReferralServiceClient;
import org.joda.time.DateTime;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.util.UUID.randomUUID;


@Service
public class CustomerService {
    private static final Double REFERRAL_BONUS_FIRST_LEVEL = 10.0;
    private static final Double REFERRAL_BONUS_SECOND_LEVEL = 3.0;
    private static final Double REFERRAL_BONUS_THIRD_LEVEL = 1.0;

    private CustomerRepository customerRepository;
    private ReferralServiceClient referralServiceClient;

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
                .map(customerRecord -> toCustomerResponse(customerRecord))
                .collect(Collectors.toList());
    }

    /**
     * findByCustomerId
     * @param customerId
     * @return The Customer with the given customerId
     */
    public CustomerResponse getCustomer(String customerId) {

        return Optional.of(customerRepository.findById(customerId))
                .orElse(Optional.empty())
                .stream()
                .map(customerRecord -> toCustomerResponse(customerRecord))
                .findFirst().orElse(null);
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

        return Optional.of(createCustomerRequest)
                .stream()
                .map(customerRequest -> {
                    if (createCustomerRequest.getReferrerId().isPresent() && !(customerRepository.existsById(createCustomerRequest.getReferrerId().toString()))) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid ReferrerID, nice try");
                        //TODO include in one of the tests to verify exception is thrown
                    }
                    return toCustomerRecord(customerRequest);
                })
                .map(customerRecord -> {
                    ReferralRequest referralRequest = new ReferralRequest(customerRecord.getId(), customerRecord.getReferrerId());
                    customerRepository.save(customerRecord);
                    referralServiceClient.addReferral(referralRequest);
                    return toCustomerResponse(customerRecord);
                })
                .findFirst().orElse(null);
    }

    /**
     * updateCustomer - This updates the customer name for the given customer id
     * @param customerId - The Id of the customer to update
     * @param customerName - The new name for the customer
     */
    public CustomerResponse updateCustomer(String customerId, String customerName) {

        return Optional.ofNullable(customerRepository.findById(customerId))
                .stream()
                .map(customerRecord -> {
                    if(customerRecord.isEmpty()) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer Not Found");
                    }
                    customerRecord.get().setName(customerName);
                    customerRepository.save(customerRecord.get());
                    return customerRecord;
                })
                .map(customerRecord -> toCustomerResponse(customerRecord.get()))
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
        List<CustomerRecord> records = StreamSupport.stream(customerRepository.findAll().spliterator(), true).collect(Collectors.toList());
        //TODO write unit test!
        return records.stream()
                .filter(customerRecord -> !(customerRecord.getReferrerId().equals(customerId)))
                .map(customerRecord -> toCustomerResponse(customerRecord))
                .collect(Collectors.toList());

    }

    /**
     * getLeaderboard - This calls the referral service to retrieve the current top 5 leaderboard of the most referrals
     * @return
     */
    public List<LeaderboardUiEntry> getLeaderboard() {

        // Task 2 - Add your code here //TODO Task 2 and write test when code is complete

        return null;
    }

    /* -----------------------------------------------------------------------------------------------------------
        Private Methods
       ----------------------------------------------------------------------------------------------------------- */

    // Add any private methods here
    private CustomerResponse toCustomerResponse(CustomerRecord record) {
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
        }

        return customerRecord;
    }
}
