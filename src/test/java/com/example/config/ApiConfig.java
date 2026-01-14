package com.example.config;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class ApiConfig {

    private static final String BASE_URL = "https://petstore.swagger.io/v2";

    private static final String API_TOKEN = "special-key";

    private ApiConfig() {
    }

    public static String getBaseUrl() {
        return BASE_URL;
    }

    public static String getApiToken() {
        return API_TOKEN;
    }

    public static RequestSpecification getRequestSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(BASE_URL)
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .build();
    }

    public static class Environment {
        public static final String DEV = "https://dev-petstore.swagger.io/v2";
        public static final String STAGING = "https://staging-petstore.swagger.io/v2";
        public static final String PROD = "https://petstore.swagger.io/v2";
    }

    public static RequestSpecification getMultipartRequestSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(BASE_URL)
                .setContentType(ContentType.MULTIPART)
                .setAccept(ContentType.JSON)
                .build();
    }

    public static RequestSpecification getRequestSpecWithToken(String token) {
        return new RequestSpecBuilder()
                .setBaseUri(BASE_URL)
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addHeader("Authorization", "Bearer " + token)
                .build();
    }

    public static RequestSpecification getRequestSpecWithApiKey(String apiKey) {
        return new RequestSpecBuilder()
                .setBaseUri(BASE_URL)
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addHeader("api_key", apiKey)
                .build();
    }
}