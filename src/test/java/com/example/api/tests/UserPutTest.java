package com.example.api.tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

class UserPutTest {

    private static final String BASE_URL = "https://petstore.swagger.io/v2";

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = BASE_URL;
    }

    @Test
    void putUser_400_InvalidUserData() {
        Map<String, Object> incompleteUser = new HashMap<>();
        incompleteUser.put("id", 123);

        given()
                .pathParam("username", "anyuser")
                .contentType(ContentType.JSON)
                .body(incompleteUser)
                .when()
                .put("/user/{username}")
                .then()
                .statusCode(400);

        Map<String, Object> wrongTypeUser = new HashMap<>();
        wrongTypeUser.put("id", "not_a_number"); // Должно быть число
        wrongTypeUser.put("username", "testuser");
        wrongTypeUser.put("firstName", "John");
        wrongTypeUser.put("lastName", "Doe");
        wrongTypeUser.put("email", "test@example.com");
        wrongTypeUser.put("password", "pass");
        wrongTypeUser.put("phone", "123456");
        wrongTypeUser.put("userStatus", "active"); // Должно быть число

        given()
                .pathParam("username", "testuser")
                .contentType(ContentType.JSON)
                .body(wrongTypeUser)
                .when()
                .put("/user/{username}")
                .then()
                .statusCode(400);
    }

    @Test
    void putUser_404_UserDoesNotExist() {
        Map<String, Object> validUser = new HashMap<>();
        validUser.put("id", 99999);
        validUser.put("username", "ghostuser");
        validUser.put("firstName", "Ghost");
        validUser.put("lastName", "User");
        validUser.put("email", "ghost@example.com");
        validUser.put("password", "password");
        validUser.put("phone", "0000000000");
        validUser.put("userStatus", 0);

        given()
                .pathParam("username", "ghostuser")
                .contentType(ContentType.JSON)
                .body(validUser)
                .when()
                .put("/user/{username}")
                .then()
                .statusCode(404);
    }

    @Test
    void putUser_Success_ThenVerify() {
        String username = "updatableuser_" + System.currentTimeMillis();

        Map<String, Object> user = new HashMap<>();
        user.put("id", 1001);
        user.put("username", username);
        user.put("firstName", "Original");
        user.put("lastName", "Name");
        user.put("email", "original@example.com");
        user.put("password", "pass123");
        user.put("phone", "1111111111");
        user.put("userStatus", 1);

        given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post("/user")
                .then()
                .statusCode(200);

        user.put("firstName", "Updated");
        user.put("email", "updated@example.com");

        given()
                .pathParam("username", username)
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .put("/user/{username}")
                .then()
                .statusCode(200);

        given()
                .pathParam("username", username)
                .when()
                .get("/user/{username}")
                .then()
                .statusCode(200)
                .body("firstName", equalTo("Updated"))
                .body("email", equalTo("updated@example.com"));

        given()
                .pathParam("username", username)
                .when()
                .delete("/user/{username}")
                .then()
                .statusCode(200);
    }
}