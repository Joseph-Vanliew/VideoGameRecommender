package com.kenzie.marketing.application.controller;

import com.kenzie.marketing.application.IntegrationTest;
import com.kenzie.marketing.application.controller.model.CreateCustomerRequest;
import com.kenzie.marketing.application.controller.model.CustomerResponse;
import com.kenzie.marketing.application.service.CustomerService;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import net.andreinc.mockneat.MockNeat;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
public class Task1Part2ControllerTest {

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
     *  Get Referrals
     *  ------------------------------------------------------------------------ **/

    @Test
    public void getReferrals_success() throws Exception {
        // GIVEN
        CreateCustomerRequest referrerRequest = new CreateCustomerRequest();
        referrerRequest.setName(mockNeat.names().get());
        referrerRequest.setReferrerId(Optional.empty());

        CustomerResponse referrer = customerService.addNewCustomer(referrerRequest);

        CreateCustomerRequest customerRequest1 = new CreateCustomerRequest();
        customerRequest1.setName(mockNeat.names().get());
        customerRequest1.setReferrerId(Optional.of(referrer.getId()));
        CustomerResponse customerResponse1 = customerService.addNewCustomer(customerRequest1);

        CreateCustomerRequest customerRequest2 = new CreateCustomerRequest();
        customerRequest2.setName(mockNeat.names().get());
        customerRequest2.setReferrerId(Optional.of(referrer.getId()));
        CustomerResponse customerResponse2 = customerService.addNewCustomer(customerRequest2);

        CreateCustomerRequest customerRequest3 = new CreateCustomerRequest();
        customerRequest3.setName(mockNeat.names().get());
        customerRequest3.setReferrerId(Optional.of(referrer.getId()));
        CustomerResponse customerResponse3 = customerService.addNewCustomer(customerRequest3);

        // WHEN
        ResultActions actions = mvc.perform(get("/customers/{customerId}/referrals", referrer.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // THEN
        String responseBody = actions.andReturn().getResponse().getContentAsString();

        List<CustomerResponse> responses = mapper.readValue(responseBody, new TypeReference<List<CustomerResponse>>() {});

        assertThat(responses.size()).isEqualTo(3).as("There are 3 referrals");
        for (CustomerResponse response : responses) {
            assertThat(response.getId()).isNotEmpty().as("The ID is populated");
            assertThat(response.getName()).isNotEmpty().as("The name is populated");
            assertThat(response.getDateJoined()).isNotEmpty().as("The date is populated");
            assertThat(response.getReferrerId()).isEqualTo(referrer.getId()).as("The referrerId matches");
            assertThat(response.getReferrerName()).isEqualTo(referrer.getName()).as("The referrerName matches");
        }
    }
}
