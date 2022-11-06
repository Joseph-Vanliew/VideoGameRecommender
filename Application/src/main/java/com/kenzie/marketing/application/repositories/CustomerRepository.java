package com.kenzie.marketing.application.repositories;

import com.kenzie.marketing.application.repositories.model.CustomerRecord;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

@EnableScan
public interface CustomerRepository extends CrudRepository<CustomerRecord, String> {
}
