package com.example.api.endpoints;

import com.example.api.models.AuthRequest;
import com.example.api.models.AuthResponse;
import com.example.config.ApiConfig;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class AuthApi {

    private static final String LOGIN_PATH = "/user/login";
    private static final String LOGOUT_PATH = "/user/logout";

    public Response login(String username, String password) {
        return given(ApiConfig.getRequestSpec())
                .queryParam("username", username)
                .queryParam("password", password)
                .when()
                .get(LOGIN_PATH)
                .then()
                .log().all()
                .extract()
                .response();
    }

    public Response loginWithBody(AuthRequest authRequest) {
        return given(ApiConfig.getRequestSpec())
                .body(authRequest)
                .when()
                .post(LOGIN_PATH)
                .then()
                .log().all()
                .extract()
                .response();
    }

    public Response logout() {
        return given(ApiConfig.getRequestSpec())
                .when()
                .get(LOGOUT_PATH)
                .then()
                .log().all()
                .extract()
                .response();
    }

    public String extractTokenFromResponse(Response loginResponse) {
        JsonPath jsonPath = loginResponse.jsonPath();

        String message = jsonPath.getString("message");

        if (message != null && message.contains("logged in user session:")) {
            return message.replace("logged in user session:", "").trim();
        }

        String token = jsonPath.getString("token");
        if (token != null) {
            return token;
        }

        return ApiConfig.getApiToken();
    }

    public boolean isAuthenticated(String token) {
        try {
            Response response = given(ApiConfig.getRequestSpecWithApiKey(token))
                    .when()
                    .get("/user/test") // Тестовый эндпоинт, если есть
                    .then()
                    .extract()
                    .response();

            return response.getStatusCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }
}