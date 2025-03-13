package com.example.meetime_test_app.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ListContactsResponse {
    @JsonProperty("results")
    private List<Contact> results;

    @JsonProperty("paging")
    private Paging paging;

    @Setter
    @Getter
    public static class Contact {

        @JsonProperty("id")
        private String id;

        @JsonProperty("properties")
        private ContactProperties properties;

        @JsonProperty("createdAt")
        private String createdAt;

        @JsonProperty("updatedAt")
        private String updatedAt;

        @JsonProperty("archived")
        private boolean archived;

        @Setter
        @Getter
        public static class ContactProperties {

            @JsonProperty("createdate")
            private String createdDate;

            @JsonProperty("email")
            private String email;

            @JsonProperty("firstname")
            private String firstName;

            @JsonProperty("hs_object_id")
            private String hsObjectId;

            @JsonProperty("lastmodifieddate")
            private String lastModifiedDate;

            @JsonProperty("lastname")
            private String lastName;

        }
    }

    @Setter
    @Getter
    public static class Paging {

        @JsonProperty("next")
        private Next next;

        @Setter
        @Getter
        public static class Next {
            @JsonProperty("after")
            private String after;

            @JsonProperty("link")
            private String link;
        }
    }
}