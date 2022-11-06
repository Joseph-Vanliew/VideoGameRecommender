package com.kenzie.marketing.application.service;

import com.kenzie.marketing.application.controller.model.CreateCustomerRequest;
import com.kenzie.marketing.application.controller.model.CustomerResponse;
import com.kenzie.marketing.application.repositories.CustomerRepository;
import com.kenzie.marketing.application.repositories.model.CustomerRecord;

import com.kenzie.marketing.referral.model.client.ReferralServiceClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.exceptions.base.MockitoAssertionError;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.UUID.randomUUID;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CustomerServiceTest {
    private CustomerRepository customerRepository;
    private CustomerService customerService;
    private ReferralServiceClient referralServiceClient;

    @BeforeEach
    void setup() {
        customerRepository = mock(CustomerRepository.class);
        referralServiceClient = mock(ReferralServiceClient.class);
        customerService = new CustomerService(customerRepository, referralServiceClient);
    }

    /** ------------------------------------------------------------------------
     *  customerService.findAllCustomers
     *  ------------------------------------------------------------------------ **/

    @Test
    void findAllCustomers_two_customers() {
        // GIVEN
        CustomerRecord record1 = new CustomerRecord();
        record1.setId(randomUUID().toString());
        record1.setName("customername1");
        record1.setDateCreated("recorddate2");

        CustomerRecord record2 = new CustomerRecord();
        record2.setId(randomUUID().toString());
        record2.setName("customername2");
        record2.setDateCreated("recorddate2");

        List<CustomerRecord> recordList = new ArrayList<>();
        recordList.add(record1);
        recordList.add(record2);
        when(customerRepository.findAll()).thenReturn(recordList);

        // WHEN
        List<CustomerResponse> customers = customerService.findAllCustomers();

        // THEN
        Assertions.assertNotNull(customers, "The customer list is returned");
        Assertions.assertEquals(2, customers.size(), "There are two customers");

        for (CustomerResponse customer : customers) {
            if (customer.getId().equals(record1.getId())) {
                Assertions.assertEquals(record1.getId(), customer.getId(), "The customer id matches");
                Assertions.assertEquals(record1.getName(), customer.getName(), "The customer name matches");
                Assertions.assertEquals(record1.getDateCreated(), customer.getDateJoined(), "The customer date matches");
            } else if (customer.getId().equals(record2.getId())) {
                Assertions.assertEquals(record2.getId(), customer.getId(), "The customer id matches");
                Assertions.assertEquals(record2.getName(), customer.getName(), "The customer name matches");
                Assertions.assertEquals(record2.getDateCreated(), customer.getDateJoined(), "The customer date matches");
            } else {
                Assertions.assertTrue(false, "Customer returned that was not in the records!");
            }
        }
    }

    /** ------------------------------------------------------------------------
     *  customerService.findByCustomerId
     *  ------------------------------------------------------------------------ **/

    @Test
    void getCustomer() {
        // GIVEN
        String customerId = randomUUID().toString();

        CustomerRecord record = new CustomerRecord();
        record.setId(customerId);
        record.setName("customername");
        record.setDateCreated("datecreated");

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(record));
        // WHEN
        CustomerResponse customer = customerService.getCustomer(customerId);

        // THEN
        Assertions.assertNotNull(customer, "The customer is returned");
        Assertions.assertEquals(record.getId(), customer.getId(), "The customer id matches");
        Assertions.assertEquals(record.getName(), customer.getName(), "The customer name matches");
        Assertions.assertEquals(record.getDateCreated(), customer.getDateJoined(), "The customer date matches");
    }

    @Test
    void findByCustomerId_invalid_customer() {
        // GIVEN
        String customerId = randomUUID().toString();

        // WHEN
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());
        CustomerResponse customer = customerService.getCustomer(customerId);

        // THEN
        Assertions.assertNull(customer, "The customer is null when not found");
    }

    /** ------------------------------------------------------------------------
     *  customerService.addNewCustomer
     *  ------------------------------------------------------------------------ **/
    @Test
    void addNewCustomer() {
        // GIVEN
        String customerName = "customerName";

        CreateCustomerRequest request = new CreateCustomerRequest();
        request.setName(customerName);
        request.setReferrerId(Optional.empty());

        ArgumentCaptor<CustomerRecord> customerRecordCaptor = ArgumentCaptor.forClass(CustomerRecord.class);

        // WHEN
        CustomerResponse returnedCustomer = customerService.addNewCustomer(request);

        // THEN
        Assertions.assertNotNull(returnedCustomer);

        verify(customerRepository).save(customerRecordCaptor.capture());

        CustomerRecord record = customerRecordCaptor.getValue();

        Assertions.assertNotNull(record, "The customer record is returned");
        Assertions.assertNotNull(record.getId(), "The customer id exists");
        Assertions.assertEquals(record.getName(), customerName, "The customer name matches");
        Assertions.assertNotNull(record.getDateCreated(), "The customer date exists");
        Assertions.assertNull(record.getReferrerId(), "The referrerId is null");
    }

    /** ------------------------------------------------------------------------
     *  customerService.updateCustomer
     *  ------------------------------------------------------------------------ **/

    @Test
    void updateCustomer() {
        // GIVEN
        String customerId = randomUUID().toString();

        CustomerRecord oldCustomerRecord = new CustomerRecord();
        oldCustomerRecord.setId(customerId);
        oldCustomerRecord.setName("oldcustomername");
        oldCustomerRecord.setDateCreated("olddatecreated");

        String newCustomerName = "newName";

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(oldCustomerRecord));

        ArgumentCaptor<CustomerRecord> customerRecordCaptor = ArgumentCaptor.forClass(CustomerRecord.class);

        // WHEN
        customerService.updateCustomer(customerId, newCustomerName);

        // THEN
        verify(customerRepository).save(customerRecordCaptor.capture());

        CustomerRecord record = customerRecordCaptor.getValue();

        Assertions.assertNotNull(record, "The customer record is returned");
        Assertions.assertEquals(record.getId(), customerId, "The customer id matches");
        Assertions.assertEquals(record.getName(), newCustomerName, "The customer name matches");
        Assertions.assertEquals(record.getDateCreated(), oldCustomerRecord.getDateCreated(), "The customer date has not changed");
    }

    @Test
    void updateCustomer_does_not_exist() {
        // GIVEN
        String customerId = randomUUID().toString();

        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        // WHEN
        Assertions.assertThrows(ResponseStatusException.class, () -> customerService.updateCustomer(customerId, "newName"));

        // THEN
        try {
            verify(customerRepository, never()).save(Matchers.any());
        } catch(MockitoAssertionError error) {
            throw new MockitoAssertionError("There should not be a call to .save() if the customer is not found in the database. - " + error);
        }

    }

    /** ------------------------------------------------------------------------
     *  customerService.deleteCustomer
     *  ------------------------------------------------------------------------ **/

    // Write additional tests here

}
