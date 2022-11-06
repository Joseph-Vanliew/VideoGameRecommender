package com.kenzie.marketing.application.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.kenzie.marketing.application.IntegrationTest;
import com.kenzie.marketing.application.controller.model.CreateCustomerRequest;
import com.kenzie.marketing.application.controller.model.CustomerResponse;
import com.kenzie.marketing.application.controller.model.ReferralBonusResponse;
import com.kenzie.marketing.application.service.CustomerService;
import net.andreinc.mockneat.MockNeat;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
public class Task2Part1ControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    CustomerService customerService;

    private static final MockNeat mockNeat = MockNeat.threadLocal();
    private static final ObjectMapper mapper = new ObjectMapper();

    @BeforeAll
    public static void setup() {
        mapper.registerModule(new Jdk8Module());
    }

    /** ------------------------------------------------------------------------
     *  Get Referral Bonus
     *  ------------------------------------------------------------------------ **/

    @Test
    public void getReferralBonus() throws Exception {
        // GIVEN
        CreateCustomerRequest referrerRequest = new CreateCustomerRequest();
        referrerRequest.setName(mockNeat.names().get());
        referrerRequest.setReferrerId(Optional.empty());

        CustomerResponse referrer = customerService.addNewCustomer(referrerRequest);

        CreateCustomerRequest customerRequest1 = new CreateCustomerRequest();
        customerRequest1.setName(mockNeat.names().get());
        customerRequest1.setReferrerId(Optional.of(referrer.getId()));
        CustomerResponse customerResponse1 = customerService.addNewCustomer(customerRequest1);

        CreateCustomerRequest customerRequest1_1 = new CreateCustomerRequest();
        customerRequest1_1.setName(mockNeat.names().get());
        customerRequest1_1.setReferrerId(Optional.of(customerResponse1.getId()));
        CustomerResponse customerResponse1_1 = customerService.addNewCustomer(customerRequest1_1);

        CreateCustomerRequest customerRequest1_2 = new CreateCustomerRequest();
        customerRequest1_2.setName(mockNeat.names().get());
        customerRequest1_2.setReferrerId(Optional.of(customerResponse1.getId()));
        CustomerResponse customerResponse1_2 = customerService.addNewCustomer(customerRequest1_2);

        CreateCustomerRequest customerRequest1_1_1 = new CreateCustomerRequest();
        customerRequest1_1_1.setName(mockNeat.names().get());
        customerRequest1_1_1.setReferrerId(Optional.of(customerResponse1_1.getId()));
        CustomerResponse customerResponse1_1_1 = customerService.addNewCustomer(customerRequest1_1_1);

        CreateCustomerRequest customerRequest2 = new CreateCustomerRequest();
        customerRequest2.setName(mockNeat.names().get());
        customerRequest2.setReferrerId(Optional.of(referrer.getId()));
        CustomerResponse customerResponse2 = customerService.addNewCustomer(customerRequest2);

        CreateCustomerRequest customerRequest3 = new CreateCustomerRequest();
        customerRequest3.setName(mockNeat.names().get());
        customerRequest3.setReferrerId(Optional.of(referrer.getId()));
        CustomerResponse customerResponse3 = customerService.addNewCustomer(customerRequest3);

        // WHEN
        ResultActions actions = mvc.perform(get("/customers/{customerId}/bonus", referrer.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());

        // THEN
        String responseBody = actions.andReturn().getResponse().getContentAsString();

        ReferralBonusResponse response = mapper.readValue(responseBody, new TypeReference<>() {});

        assertThat(response.getBonus()).isEqualTo(37.0).as("There referral bonus should be 37.0 for a 3 2 1 referral tree.");
    }
}
