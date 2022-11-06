package com.kenzie.marketing.application.controller;

import com.kenzie.marketing.application.IntegrationTest;
import com.kenzie.marketing.application.controller.model.CreateCustomerRequest;
import com.kenzie.marketing.application.controller.model.CustomerResponse;
import com.kenzie.marketing.application.controller.model.CustomerUpdateRequest;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
public class Task1Part1ControllerTest {

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
     *  Add Customer
     *  ------------------------------------------------------------------------ **/

    @Test
    public void addCustomer_noReferrer() throws Exception {

        CreateCustomerRequest customerRequest = new CreateCustomerRequest();
        customerRequest.setName(mockNeat.names().get());
        customerRequest.setReferrerId(Optional.empty());

        ResultActions actions = mvc.perform(post("/customers/")
                        .content(mapper.writeValueAsString(customerRequest))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());

        String responseBody = actions.andReturn().getResponse().getContentAsString();
        CustomerResponse response = mapper.readValue(responseBody, CustomerResponse.class);
        assertThat(response.getId()).isNotEmpty().as("The ID is populated");
        assertThat(response.getName()).isEqualTo(customerRequest.getName()).as("The name is correct");
        assertThat(response.getDateJoined()).isNotEmpty().as("The date is populated");
        assertThat(response.getReferrerId()).isNullOrEmpty();
        assertThat(response.getReferrerName()).isNullOrEmpty();
    }

    @Test
    public void addCustomer_withInvalidReferrer() throws Exception {

        CreateCustomerRequest customerRequest = new CreateCustomerRequest();
        customerRequest.setName(mockNeat.names().get());
        customerRequest.setReferrerId(Optional.of(UUID.randomUUID().toString()));

        mvc.perform(post("/customers/")
                        .content(mapper.writeValueAsString(customerRequest))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void addCustomer_withValidReferrer() throws Exception {
        // GIVEN
        CreateCustomerRequest referrerRequest = new CreateCustomerRequest();
        referrerRequest.setName(mockNeat.names().get());
        referrerRequest.setReferrerId(Optional.empty());

        CustomerResponse referrer = customerService.addNewCustomer(referrerRequest);

        CreateCustomerRequest customerRequest = new CreateCustomerRequest();
        customerRequest.setName(mockNeat.names().get());
        customerRequest.setReferrerId(Optional.of(referrer.getId()));

        // WHEN
        ResultActions actions = mvc.perform(post("/customers/")
                        .content(mapper.writeValueAsString(customerRequest))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                // THEN
                .andExpect(status().is2xxSuccessful());

        String responseBody = actions.andReturn().getResponse().getContentAsString();
        CustomerResponse response = mapper.readValue(responseBody, CustomerResponse.class);
        assertThat(response.getId()).isNotEmpty().as("The ID is populated");
        assertThat(response.getName()).isEqualTo(customerRequest.getName()).as("The name is correct");
        assertThat(response.getReferrerId()).isEqualTo(referrer.getId()).as("The referrer id is correct");
        assertThat(response.getReferrerName()).isEqualTo(referrerRequest.getName()).as("The referrer name is populated and correct");
        assertThat(response.getDateJoined()).isNotEmpty().as("The date is populated");
    }

    /** ------------------------------------------------------------------------
     *  Update Customer
     *  ------------------------------------------------------------------------ **/

    @Test
    public void updateCustomer_success() throws Exception {

        // GIVEN
        CreateCustomerRequest customerRequest = new CreateCustomerRequest();
        customerRequest.setName(mockNeat.names().get());
        customerRequest.setReferrerId(Optional.empty());

        CustomerResponse customerResponse = customerService.addNewCustomer(customerRequest);

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest();
        updateRequest.setId(customerResponse.getId());
        updateRequest.setName(mockNeat.names().get());

        // WHEN
        ResultActions actions = mvc.perform(post("/customers/{customerId}", customerResponse.getId())
                        .content(mapper.writeValueAsString(updateRequest))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());

        // THEN
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        CustomerResponse response = mapper.readValue(responseBody, CustomerResponse.class);
        assertThat(response.getId()).isNotEmpty().as("The ID is populated");
        assertThat(response.getName()).isEqualTo(updateRequest.getName()).as("The name is correct");
        assertThat(response.getDateJoined()).isNotEmpty().as("The date is populated");
        assertThat(response.getReferrerId()).isNullOrEmpty();
        assertThat(response.getReferrerName()).isNullOrEmpty();
    }

    @Test
    public void updateCustomer_invalidCustomer() throws Exception {

        // GIVEN
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest();
        updateRequest.setId(UUID.randomUUID().toString());
        updateRequest.setName(mockNeat.names().get());

        // WHEN
        ResultActions actions = mvc.perform(put("/customers/{customerId}", updateRequest.getId())
                        .content(mapper.writeValueAsString(updateRequest))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                // THEN
                .andExpect(status().is4xxClientError());
    }

    /** ------------------------------------------------------------------------
     *  Get All Customers
     *  ------------------------------------------------------------------------ **/

    @Test
    public void getAllCustomers_success() throws Exception {

        // GIVEN
        CreateCustomerRequest customerRequest = new CreateCustomerRequest();
        customerRequest.setName(mockNeat.names().get());
        customerRequest.setReferrerId(Optional.empty());

        CustomerResponse customerResponse = customerService.addNewCustomer(customerRequest);

        // WHEN
        ResultActions actions = mvc.perform(get("/customers")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());

        // THEN
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        List<CustomerResponse> responses = mapper.readValue(responseBody, new TypeReference<List<CustomerResponse>>() {});
        assertThat(responses.size()).isGreaterThan(0).as("There are responses");
        for (CustomerResponse response : responses) {
            assertThat(response.getId()).isNotEmpty().as("The ID is populated");
            assertThat(response.getName()).isNotEmpty().as("The name is populated");
            assertThat(response.getDateJoined()).isNotEmpty().as("The date is populated");
        }
    }


    /** ------------------------------------------------------------------------
     *  Get Customer by ID
     *  ------------------------------------------------------------------------ **/

    @Test
    public void getCustomerById_success() throws Exception {
        // GIVEN
        CreateCustomerRequest customerRequest = new CreateCustomerRequest();
        customerRequest.setName(mockNeat.names().get());
        customerRequest.setReferrerId(Optional.empty());

        CustomerResponse customerResponse = customerService.addNewCustomer(customerRequest);

        // WHEN
        ResultActions actions = mvc.perform(get("/customers/{customerId}", customerResponse.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // THEN
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        CustomerResponse response = mapper.readValue(responseBody, CustomerResponse.class);

        assertThat(response.getId()).isNotEmpty().as("The ID is populated");
        assertThat(response.getName()).isNotEmpty().as("The name is populated");
        assertThat(response.getDateJoined()).isNotEmpty().as("The date is populated");
        assertThat(response.getReferrerId()).isNullOrEmpty();
        assertThat(response.getReferrerName()).isNullOrEmpty();
    }

    @Test
    public void getCustomerById_withReferrer() throws Exception {
        // GIVEN
        CreateCustomerRequest referrerRequest = new CreateCustomerRequest();
        referrerRequest.setName(mockNeat.names().get());
        referrerRequest.setReferrerId(Optional.empty());

        CustomerResponse referrer = customerService.addNewCustomer(referrerRequest);

        CreateCustomerRequest customerRequest = new CreateCustomerRequest();
        customerRequest.setName(mockNeat.names().get());
        customerRequest.setReferrerId(Optional.of(referrer.getId()));

        CustomerResponse customerResponse = customerService.addNewCustomer(customerRequest);

        // WHEN
        ResultActions actions = mvc.perform(get("/customers/{customerId}", customerResponse.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // THEN
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        CustomerResponse response = mapper.readValue(responseBody, CustomerResponse.class);

        assertThat(response.getId()).isNotEmpty().as("The ID is populated");
        assertThat(response.getName()).isNotEmpty().as("The name is populated");
        assertThat(response.getDateJoined()).isNotEmpty().as("The date is populated");
        assertThat(response.getReferrerId()).isEqualTo(referrer.getId()).as("The referrer id is correct");
        assertThat(response.getReferrerName()).isEqualTo(referrerRequest.getName()).as("The referrer name is populated and correct");
    }

    /** ------------------------------------------------------------------------
     *  Delete Customer
     *  ------------------------------------------------------------------------ **/

    @Test
    public void deleteCustomer_success() throws Exception {
        // GIVEN
        CreateCustomerRequest customerRequest = new CreateCustomerRequest();
        customerRequest.setName(mockNeat.names().get());
        customerRequest.setReferrerId(Optional.empty());

        CustomerResponse customerResponse = customerService.addNewCustomer(customerRequest);

        // WHEN
        mvc.perform(delete("/customers/{customerId}", customerResponse.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());

        // THEN
        mvc.perform(get("/customers/{customerId}", customerResponse.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
