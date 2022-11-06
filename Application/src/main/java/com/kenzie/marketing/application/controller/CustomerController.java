package com.kenzie.marketing.application.controller;

import com.kenzie.marketing.application.controller.model.CreateCustomerRequest;
import com.kenzie.marketing.application.controller.model.CustomerResponse;
import com.kenzie.marketing.application.controller.model.CustomerUpdateRequest;
import com.kenzie.marketing.application.controller.model.LeaderboardUiEntry;
import com.kenzie.marketing.application.controller.model.ReferralBonusResponse;
import com.kenzie.marketing.application.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private CustomerService customerService;

    CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> addNewCustomer(@RequestBody CreateCustomerRequest createCustomerRequest) {

        if (createCustomerRequest.getName() == null || createCustomerRequest.getName().length() == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Customer Name");
        }

        // If the referrerId is an empty string, then treat is as not present.
        if (createCustomerRequest.getReferrerId().isPresent() && createCustomerRequest.getReferrerId().get().length() == 0) {
            createCustomerRequest.setReferrerId(Optional.empty());
        }

        CustomerResponse response = customerService.addNewCustomer(createCustomerRequest);

        return ResponseEntity.created(URI.create("/customers/" + response.getId())).body(response);
    }

    @PostMapping("/{customerId}")
    public ResponseEntity<CustomerResponse> updateCustomer(@RequestBody CustomerUpdateRequest customerRequest) {
        CustomerResponse response = customerService.updateCustomer(customerRequest.getId(), customerRequest.getName());

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponse>> getAllCustomers() {
        List<CustomerResponse> customers = customerService.findAllCustomers();
        if (customers == null || customers.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(customers);
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<List<LeaderboardUiEntry>> getReferralsLeaders() {
        List<LeaderboardUiEntry> leaderboard = customerService.getLeaderboard();
        return ResponseEntity.ok(leaderboard);
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerResponse> searchCustomerById(@PathVariable("customerId") String customerId) {
        CustomerResponse customerResponse = customerService.getCustomer(customerId);
        if (customerResponse == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(customerResponse);
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity deleteCustomerById(@PathVariable("customerId") String customerId) {
        customerService.deleteCustomer(customerId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{customerId}/referrals")
    public ResponseEntity<List<CustomerResponse>> getReferrals(@PathVariable("customerId") String customerId) {
        List<CustomerResponse> referrals = customerService.getReferrals(customerId);
        return ResponseEntity.ok(referrals);
    }

    @GetMapping("/{customerId}/bonus")
    public ResponseEntity<ReferralBonusResponse> getCustomerBonus(@PathVariable("customerId") String customerId) {
        Double bonus = customerService.calculateBonus(customerId);
        ReferralBonusResponse referralBonusResponse = new ReferralBonusResponse();
        referralBonusResponse.setBonus(bonus);
        return ResponseEntity.ok(referralBonusResponse);
    }
}
