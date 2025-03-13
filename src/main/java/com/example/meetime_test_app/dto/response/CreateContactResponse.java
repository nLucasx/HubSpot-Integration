package com.example.meetime_test_app.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateContactResponse {

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

    @Getter
    @Setter
    public static class ContactProperties {

        @JsonProperty("createdate")
        private String createDate;

        @JsonProperty("email")
        private String email;

        @JsonProperty("firstname")
        private String firstName;

        @JsonProperty("hs_all_contact_vids")
        private String hsAllContactVids;

        @JsonProperty("hs_associated_target_accounts")
        private String hsAssociatedTargetAccounts;

        @JsonProperty("hs_currently_enrolled_in_prospecting_agent")
        private String hsCurrentlyEnrolledInProspectingAgent;

        @JsonProperty("hs_email_domain")
        private String hsEmailDomain;

        @JsonProperty("hs_full_name_or_email")
        private String hsFullNameOrEmail;

        @JsonProperty("hs_is_contact")
        private String hsIsContact;

        @JsonProperty("hs_is_unworked")
        private String hsIsUnworked;

        @JsonProperty("hs_lifecyclestage_lead_date")
        private String hsLifecyclestageLeadDate;

        @JsonProperty("hs_membership_has_accessed_private_content")
        private String hsMembershipHasAccessedPrivateContent;

        @JsonProperty("hs_object_id")
        private String hsObjectId;

        @JsonProperty("hs_object_source")
        private String hsObjectSource;

        @JsonProperty("hs_object_source_id")
        private String hsObjectSourceId;

        @JsonProperty("hs_object_source_label")
        private String hsObjectSourceLabel;

        @JsonProperty("hs_pipeline")
        private String hsPipeline;

        @JsonProperty("hs_prospecting_agent_actively_enrolled_count")
        private String hsProspectingAgentActivelyEnrolledCount;

        @JsonProperty("hs_registered_member")
        private String hsRegisteredMember;

        @JsonProperty("hs_sequences_actively_enrolled_count")
        private String hsSequencesActivelyEnrolledCount;

        @JsonProperty("lastmodifieddate")
        private String lastModifiedDate;

        @JsonProperty("lastname")
        private String lastName;

        @JsonProperty("lifecyclestage")
        private String lifecycleStage;

        @JsonProperty("num_notes")
        private String numNotes;
    }
}
