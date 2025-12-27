package com.example.api.endpoints;

import com.example.api.models.User;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.List;

import static io.restassured.RestAssured.given;

public class UserApi {

    private static final String BASE_URL = "https://petstore.swagger.io/v2";
    private static final String USER_PATH = "/user";
    private static final String CREATE_WITH_LIST_PATH = USER_PATH + "/createWithList";
    private static final String USER_BY_USERNAME_PATH = USER_PATH + "/{username}";

    private RequestSpecification requestSpec;

    public UserApi() {
        this.requestSpec = given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .log().all();
    }

    public Response createUsersWithList(List<User> users) {
        return requestSpec
                .body(users)
                .when()
                .post(CREATE_WITH_LIST_PATH)
                .then()
                .log().all()
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
}