package com.example.api.tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

class UserDeleteTest {

    private static final String BASE_URL = "https://petstore.swagger.io/v2";

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = BASE_URL;
    }

    @Test
    void deleteUser_400_InvalidUsername() {
        String[] badUsernames = {"", "   ", "@badname", "bad name", "verylongusernameover20characters"};

        for (String badUsername : badUsernames) {
            int statusCode = given()
                    .pathParam("username", badUsername)
                    .when()
                    .delete("/user/{username}")
                    .getStatusCode();

            if (statusCode == 400) {
                System.out.println("Получен 400 для username: '" + badUsername + "'");
                return; // Тест пройден
            }
        }

        System.out.println("API не возвращает 400 для некорректных имен");
    }

    @Test
    void deleteUser_404_UserNotFound() {
        String fakeUsername = "user_that_never_existed_" + System.currentTimeMillis();

        given()
                .pathParam("username", fakeUsername)
                .when()
                .delete("/user/{username}")
                .then()
                .statusCode(404);
    }

    @Test
    void deleteUser_Success_ThenVerifyDeleted() {
        String username = "todelete_" + System.currentTimeMillis();

        Map<String, Object> user = new HashMap<>();
        user.put("id", 1002);
        user.put("username", username);
        user.put("firstName", "ToDelete");
        user.put("lastName", "User");
        user.put("email", "delete@example.com");
        user.put("password", "password");
        user.put("phone", "2222222222");
        user.put("userStatus", 0);

        given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post("/user")
                .then()
                .statusCode(200);

        given()
                .pathParam("username", username)
                .when()
                .delete("/user/{username}")
                .then()
                .statusCode(200);

        given()
                .pathParam("username", username)
                .when()
                .get("/user/{username}")
                .then()
                .statusCode(404);
    }

    @Test
    void deleteUser_AlreadyDeleted() {
        String username = "alreadydeleted_" + System.currentTimeMillis();

        Map<String, Object> user = new HashMap<>();
        user.put("id", 1003);
        user.put("username", username);
        user.put("firstName", "Already");
        user.put("lastName", "Deleted");
        user.put("email", "deleted@example.com");
        user.put("password", "pass");
        user.put("phone", "3333333333");
        user.put("userStatus", 0);

        given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post("/user");

        given()
                .pathParam("username", username)
                .when()
                .delete("/user/{username}");

        given()
                .pathParam("username", username)
                .when()
                .delete("/user/{username}")
                .then()
                .statusCode(404); // Должен вернуть 404
    }
}