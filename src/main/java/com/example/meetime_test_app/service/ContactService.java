package com.example.meetime_test_app.service;

import com.example.meetime_test_app.builder.ContactRequestBuilder;
import com.example.meetime_test_app.dto.request.ContactWebHookRequest;
import com.example.meetime_test_app.dto.request.CreateContactRequest;
import com.example.meetime_test_app.dto.response.CreateContactResponse;
import com.example.meetime_test_app.dto.response.ListContactsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
public class ContactService {

    @Autowired
    private WebClient webClient;

    public String endpoint = "/crm/v3/objects/contacts";

    public Mono<CreateContactResponse> createContact(CreateContactRequest createContactRequest) {
        Map<String, Object> requestBody = ContactRequestBuilder.buildCreateContact(createContactRequest);

        return webClient.post()
                .uri(endpoint)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                        Mono.error(new ResponseStatusException(HttpStatus.CONFLICT, "Contact already exists")))
                .bodyToMono(CreateContactResponse.class);
    }

    public Flux<ListContactsResponse> listContacts() {
        return webClient.get()
                .uri(endpoint)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToFlux(ListContactsResponse.class);
    }

    public void showWebhookBody(List<ContactWebHookRequest> body) {
        System.out.println("Webhook called: contact.creation event received - event body:");
        System.out.println(body);
    }
}
