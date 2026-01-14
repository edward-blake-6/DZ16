package com.example.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    @JsonProperty("code")
    private Integer code;

    @JsonProperty("type")
    private String type;

    @JsonProperty("message")
    private String message;

    @JsonProperty("token")
    private String token;
}