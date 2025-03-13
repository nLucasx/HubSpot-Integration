package com.example.meetime_test_app.controller;

import com.example.meetime_test_app.annotation.RateLimited;
import com.example.meetime_test_app.dto.request.ContactWebHookRequest;
import com.example.meetime_test_app.dto.response.CreateContactResponse;
import com.example.meetime_test_app.dto.response.ListContactsResponse;
import com.example.meetime_test_app.service.ContactService;
import com.example.meetime_test_app.dto.request.CreateContactRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/contact")
public class ContactController {
    @Autowired
    private ContactService contactService;

    @GetMapping
    public Flux<ListContactsResponse> listContacts() {
        return this.contactService.listContacts();
    }

    @RateLimited
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Mono<CreateContactResponse> createContact(@Valid @RequestBody CreateContactRequest createContactRequest) {
        return this.contactService.createContact(createContactRequest);
    }

    @PostMapping("/webhook")
    public void webHook(@RequestBody List<ContactWebHookRequest> body) {
        this.contactService.showWebhookBody(body);
    }
}
