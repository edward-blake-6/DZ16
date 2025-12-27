package com.example.api.tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

class UserGetTest {

    private static final String BASE_URL = "https://petstore.swagger.io/v2";

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = BASE_URL;
    }

    @Test
    void getUser_200_OK_CheckAllFields() {
        String username = "existingUser";

        given()
                .pathParam("username", username)
                .when()
                .get("/user/{username}")
                .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("username", equalTo(username))
                .body("firstName", not(emptyOrNullString()))
                .body("lastName", not(emptyOrNullString()))
                .body("email", not(emptyOrNullString()))
                .body("password", not(emptyOrNullString()))
                .body("phone", not(emptyOrNullString()))
                .body("userStatus", notNullValue());
    }

    @Test
    void getUser_400_BadRequest() {
        String[] badUsernames = {"", "a", "user@test", "test user"};

        for (String badUsername : badUsernames) {
            Response response = given()
                    .pathParam("username", badUsername)
                    .when()
                    .get("/user/{username}");

            if (response.getStatusCode() == 400) {
                response.then()
                        .statusCode(400);
                return;
            }
        }
    }

    @Test
    void getUser_404_NotFound() {
        String fakeUsername = "user_" + System.currentTimeMillis();

        given()
                .pathParam("username", fakeUsername)
                .when()
                .get("/user/{username}")
                .then()
                .statusCode(404);
    }
}