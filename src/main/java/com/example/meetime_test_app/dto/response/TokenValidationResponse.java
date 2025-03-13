package com.example.meetime_test_app.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class TokenValidationResponse {
    private String token;

    private String user;

    @JsonProperty("hub_domain")
    private String hubDomain;

    private List<String> scopes;

    @JsonProperty("hub_id")
    private String hubId;

    @JsonProperty("app_id")
    private String appId;

    @JsonProperty("expires_in")
    private String expiresIn;

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("token_type")
    private String tokenType;
}
