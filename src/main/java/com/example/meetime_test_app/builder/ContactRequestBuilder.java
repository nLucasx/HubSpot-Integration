package com.example.meetime_test_app.builder;

import com.example.meetime_test_app.dto.request.CreateContactRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContactRequestBuilder {

    //@todo Implement contact association logic
    public static Map<String, Object> buildCreateContact(CreateContactRequest createContactRequest) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("associations", List.of());
        requestBody.put("properties", Map.of(
            "email", createContactRequest.getEmail(),
            "firstname", createContactRequest.getFirstName(),
            "lastname", createContactRequest.getLastName()
        ));
        return requestBody;
    }
}
