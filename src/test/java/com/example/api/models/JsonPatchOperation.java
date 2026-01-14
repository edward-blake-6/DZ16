package com.example.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JsonPatchOperation {

    @JsonProperty("op")
    private String op; // "replace", "add", "remove"

    @JsonProperty("path")
    private String path;

    @JsonProperty("value")
    private Object value;
}