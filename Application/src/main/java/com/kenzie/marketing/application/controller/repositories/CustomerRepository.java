package com.kenzie.marketing.application.controller.repositories;

import com.kenzie.marketing.application.controller.repositories.model.CustomerRecord;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

@EnableScan
public interface CustomerRepository extends CrudRepository<CustomerRecord, String> {
}
