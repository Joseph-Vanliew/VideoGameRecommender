package com.kenzie.marketing.referral.service.lambda;

import com.kenzie.marketing.referral.service.ReferralService;
import com.kenzie.marketing.referral.service.dependency.DaggerServiceComponent;
import com.kenzie.marketing.referral.service.dependency.ServiceComponent;
import com.kenzie.marketing.referral.service.exceptions.InvalidDataException;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class GetDirectReferrals implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    static final Logger log = LogManager.getLogger();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        // Logging the request json to make debugging easier.
        log.info(gson.toJson(input));

        ServiceComponent serviceComponent = DaggerServiceComponent.create();
        ReferralService referralService = serviceComponent.provideReferralService();

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers);

        String customerId = input.getPathParameters().get("customerId");

        if (customerId == null || customerId.length() == 0) {
            return response
                    .withStatusCode(400)
                    .withBody("Customer Id is invalid");
        }

        try {
            String output = gson.toJson(referralService.getDirectReferrals(customerId));
            return response
                    .withStatusCode(200)
                    .withBody(output);
        } catch (InvalidDataException e) {
            return response
                    .withStatusCode(400)
                    .withBody(gson.toJson(e.errorPayload()));
        }
    }
}
