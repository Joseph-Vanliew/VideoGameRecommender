package com.kenzie.marketing.application.controller.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotEmpty;
import java.util.Optional;

public class CreateCustomerRequest {

    @NotEmpty
    @JsonProperty("name")
    private String name;

    @JsonProperty("referrerId")
    private Optional<String> referrerId;

    public CreateCustomerRequest() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Optional<String> getReferrerId() {
        return referrerId;
    }

    public void setReferrerId(Optional<String> referrerId) {
        this.referrerId = referrerId;
    }
}
