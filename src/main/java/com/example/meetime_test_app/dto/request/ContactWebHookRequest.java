package com.example.meetime_test_app.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContactWebHookRequest {
    private long eventId;

    private long subscriptionId;

    private long portalId;

    private long appId;

    private long occurredAt;

    private String subscriptionType;

    private long attemptNumber;

    private long objectId;

    private String changeFlag;

    private String changeSource;

    private long sourceId;

    @Override
    public String toString() {
        return "ContactWebHookRequest{" +
                "eventId=" + eventId +
                ", subscriptionId=" + subscriptionId +
                ", portalId=" + portalId +
                ", appId=" + appId +
                ", occurredAt=" + occurredAt +
                ", subscriptionType='" + subscriptionType + '\'' +
                ", attemptNumber=" + attemptNumber +
                ", objectId=" + objectId +
                ", changeFlag='" + changeFlag + '\'' +
                ", changeSource='" + changeSource + '\'' +
                ", sourceId=" + sourceId +
                '}';
    }
}
