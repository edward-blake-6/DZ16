package com.example.api.tests;

import com.example.api.models.User;
import com.example.config.ApiConfig;
import io.qameta.allure.*;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@Epic("User API Tests")
@Feature("PATCH Operations")
@Story("Тестирование частичного обновления пользователя")
@Tag("patch")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserPatchTest {

    private String testUsername;

    @BeforeEach
    @Step("Создание тестового пользователя")
    public void setupTestUser() {
        Allure.label("layer", "api");
        testUsername = "patchuser_" + UUID.randomUUID().toString().substring(0, 8);
        User user = User.builder()
                .id(9999)
                .username(testUsername)
                .firstName("InitialFirstName")
                .lastName("InitialLastName")
                .email("initial@example.com")
                .password("initialPass")
                .phone("1111111111")
                .userStatus(1)
                .build();

        given(ApiConfig.getRequestSpec())
                .body(user)
                .when()
                .post("/user")
                .then()
                .statusCode(200);

        Allure.parameter("Test Username", testUsername);
    }

    @AfterEach
    @Step("Удаление тестового пользователя")
    public void cleanupTestUser() {
        if (testUsername != null) {
            given(ApiConfig.getRequestSpec())
                    .pathParam("username", testUsername)
                    .when()
                    .delete("/user/{username}");
        }
    }

    @Test
    @Story("Частичное обновление пользователя")
    @Description("Тест проверяет обновление отдельных полей пользователя через PATCH запрос")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("PATCH: Частичное обновление данных пользователя")
    public void testPartialUpdateUserWithPatch() {
        Map<String, Object> patchData = new HashMap<>();
        patchData.put("firstName", "PatchedFirstName");
        patchData.put("email", "patched.email@example.com");
        patchData.put("phone", "9998887777");

        Allure.addAttachment("Данные для PATCH", "application/json",
                patchData.toString());
        Response patchResponse = given(ApiConfig.getRequestSpec())
                .pathParam("username", testUsername)
                .body(patchData)
                .when()
                .patch("/user/{username}")
                .then()
                .log().all()
                .extract()
                .response();
        patchResponse.then()
                .statusCode(200)
                .body("code", equalTo(200))
                .body("type", equalTo("unknown"));
        Response getResponse = given(ApiConfig.getRequestSpec())
                .pathParam("username", testUsername)
                .when()
                .get("/user/{username}")
                .then()
                .log().all()
                .extract()
                .response();
        JsonPath jsonPath = getResponse.jsonPath();

        assertEquals("PatchedFirstName", jsonPath.getString("firstName"),
                "Имя должно быть обновлено");
        assertEquals("patched.email@example.com", jsonPath.getString("email"),
                "Email должен быть обновлен");
        assertEquals("9998887777", jsonPath.getString("phone"),
                "Телефон должен быть обновлен");
        assertEquals(testUsername, jsonPath.getString("username"),
                "Username не должен измениться");
        assertEquals("InitialLastName", jsonPath.getString("lastName"),
                "Фамилия не должна измениться");

        Allure.addAttachment("Результаты PATCH", "text/plain",
                "Имя обновлено: InitialFirstName → PatchedFirstName\n" +
                        "Email обновлен: initial@example.com → patched.email@example.com\n" +
                        "Телефон обновлен: 1111111111 → 9998887777\n" +
                        "Username остался прежним: " + testUsername + "\n" +
                        "Фамилия осталась прежней: InitialLastName");

        Allure.step("PATCH запрос выполнен успешно");
    }
}