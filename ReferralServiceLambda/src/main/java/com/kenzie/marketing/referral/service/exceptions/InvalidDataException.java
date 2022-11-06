package com.kenzie.marketing.referral.service.exceptions;

import java.util.HashMap;
import java.util.Map;

public class InvalidDataException extends RuntimeException {

    public InvalidDataException(String msg) {
        super(msg);
    }

    public Map<String, Object> errorPayload() {
        Map<String, Object> errorPayload = new HashMap();
        errorPayload.put("errorType", "invalid_data");
        errorPayload.put("message", this.getMessage());
        return errorPayload;
    }
}
