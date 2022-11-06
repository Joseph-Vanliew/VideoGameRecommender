package com.kenzie.marketing.application.controller.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReferralListResponse {

    @JsonProperty("customers")
    private Set<String> customers;

    public Set<String> getCustomers() {
        return customers;
    }

    public void setCustomers(Set<String> customers) {
        this.customers = customers;
    }
}
