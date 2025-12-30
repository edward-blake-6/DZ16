package com.example.api.endpoints;

import com.example.api.models.User;
import com.example.config.ApiConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class UserApi {

    private static final String USER_PATH = "/user";
    private static final String CREATE_WITH_LIST_PATH = USER_PATH + "/createWithList";
    private static final String USER_BY_USERNAME_PATH = USER_PATH + "/{username}";

    private final RequestSpecification requestSpec;

    public UserApi() {
        this.requestSpec = given(ApiConfig.getRequestSpec());
    }

    public Response createUsersWithList(List<User> users) {
        return requestSpec
                .body(users)
                .when()
                .post(CREATE_WITH_LIST_PATH)
                .then()
                .log().all()
                .body(matchesJsonSchemaInClasspath("schemas/create-users-response.json"))
                .extract()
                .response();
    }

    public Response getUserByUsername(String username) {
        return requestSpec
                .pathParam("username", username)
                .when()
                .get(USER_BY_USERNAME_PATH)
                .then()
                .log().all()
                .body(matchesJsonSchemaInClasspath("schemas/user-response.json"))
                .extract()
                .response();
    }

    public Response createUser(User user) {
        return requestSpec
                .body(user)
                .when()
                .post(USER_PATH)
                .then()
                .log().all()
                .extract()
                .response();
    }

    public Response updateUser(String username, User user) {
        return requestSpec
                .pathParam("username", username)
                .body(user)
                .when()
                .put(USER_BY_USERNAME_PATH)
                .then()
                .log().all()
                .extract()
                .response();
    }

    public Response deleteUser(String username) {
        return requestSpec
                .pathParam("username", username)
                .when()
                .delete(USER_BY_USERNAME_PATH)
                .then()
                .log().all()
                .extract()
                .response();
    }

    public Response loginUser(String username, String password) {
        return requestSpec
                .queryParam("username", username)
                .queryParam("password", password)
                .when()
                .get(USER_PATH + "/login")
                .then()
                .log().all()
                .extract()
                .response();
    }

    public Response logoutUser() {
        return requestSpec
                .when()
                .get(USER_PATH + "/logout")
                .then()
                .log().all()
                .extract()
                .response();
    }
}