package com.kenzie.marketing.application.config;

import com.kenzie.marketing.referral.model.client.ReferralServiceClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReferralServiceClientConfiguration {

    @Bean
    public ReferralServiceClient referralServiceClient() {
        return new ReferralServiceClient();
    }
}
