package com.kenzie.marketing.referral.service.lambda;

import com.kenzie.marketing.referral.model.ReferralRequest;
import com.kenzie.marketing.referral.model.ReferralResponse;
import com.kenzie.marketing.referral.service.ReferralService;
import com.kenzie.marketing.referral.service.converter.JsonStringToReferralConverter;
import com.kenzie.marketing.referral.service.dependency.ServiceComponent;
import com.kenzie.marketing.referral.service.dependency.DaggerServiceComponent;
import com.kenzie.marketing.referral.service.exceptions.InvalidDataException;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AddReferral implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    static final Logger log = LogManager.getLogger();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        JsonStringToReferralConverter jsonStringToReferralConverter = new JsonStringToReferralConverter();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        // Logging the request json to make debugging easier.
        log.info(gson.toJson(input));

        ServiceComponent serviceComponent = DaggerServiceComponent.create();
        ReferralService referralService = serviceComponent.provideReferralService();

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();

        try {
            ReferralRequest referralRequest = jsonStringToReferralConverter.convert(input.getBody());
            ReferralResponse referralResponse = referralService.addReferral(referralRequest);
            return response
                    .withStatusCode(200)
                    .withBody(gson.toJson(referralResponse));
        } catch (InvalidDataException e) {
            return response
                    .withStatusCode(400)
                    .withBody(gson.toJson(e.errorPayload()));
        }
    }
}
