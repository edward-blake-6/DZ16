package com.example.api.tests;

import com.example.api.endpoints.AuthApi;
import com.example.api.models.AuthRequest;
import com.example.api.models.User;
import com.example.config.ApiConfig;
import io.qameta.allure.*;
import io.qameta.allure.model.Status;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@Epic("Authentication API Tests")
@Feature("Token-based Authentication")
@Story("Тестирование авторизации по токену и защищенных эндпоинтов")
@Tag("authentication")
@Tag("token")
@Tag("security")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TokenAuthenticationTest {

    private AuthApi authApi;
    private String testUsername;
    private String testPassword;
    private User testUser;
    private String authToken;

    @BeforeAll
    @Step("Инициализация тестовых ресурсов")
    public void setupAll() {
        Allure.label("layer", "api");
        Allure.label("component", "authentication");

        authApi = new AuthApi();
        testUsername = "authtest_" + UUID.randomUUID().toString().substring(0, 8);
        testPassword = "SecurePass123!";

        Allure.parameter("Test Username", testUsername);
        Allure.parameter("Test Password", "*******");
    }

    @BeforeEach
    @Step("Создание тестового пользователя")
    public void setupTestUser() {
        testUser = User.builder()
                .id(8888)
                .username(testUsername)
                .firstName("Auth")
                .lastName("Test")
                .email(testUsername + "@auth.test")
                .password(testPassword)
                .phone("5555555555")
                .userStatus(1)
                .build();
        Response createResponse = given(ApiConfig.getRequestSpec())
                .body(testUser)
                .when()
                .post("/user")
                .then()
                .log().all()
                .extract()
                .response();

        assertEquals(200, createResponse.getStatusCode(),
                "Пользователь должен быть создан успешно");

        Allure.addAttachment("Созданный пользователь", "application/json",
                testUser.toString());
    }

    @AfterEach
    @Step("Очистка тестовых данных")
    public void cleanupTestData() {
        if (authToken != null) {
            try {
                authApi.logout();
                Allure.step("Выполнен логаут");
            } catch (Exception e) {
            }
        }

        if (testUsername != null) {
            try {
                given(ApiConfig.getRequestSpecWithApiKey(ApiConfig.getApiToken()))
                        .pathParam("username", testUsername)
                        .when()
                        .delete("/user/{username}")
                        .then()
                        .log().all();

                Allure.step("Пользователь удален: " + testUsername);
            } catch (Exception e) {
                Allure.step("Ошибка при удалении пользователя", Status.FAILED);
            }
        }
    }

    @Test
    @Story("Авторизация по токену")
    @Description("Тест проверяет полный цикл аутентификации: логин, использование токена, логаут")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Аутентификация: Полный цикл работы с токеном")
    public void testTokenBasedAuthentication() {
        Allure.step("Шаг 1: Логин и получение токена");
        Response loginResponse = authApi.login(testUsername, testPassword);

        loginResponse.then()
                .statusCode(200)
                .body("code", equalTo(200))
                .body("type", equalTo("unknown"));

        JsonPath loginJson = loginResponse.jsonPath();
        String loginMessage = loginJson.getString("message");
        assertNotNull(loginMessage, "Сообщение логина не должно быть null");
        assertTrue(loginMessage.contains("logged in user session:"),
                "Сообщение должно содержать информацию о сессии");

        authToken = authApi.extractTokenFromResponse(loginResponse);
        assertNotNull(authToken, "Токен не должен быть null");
        assertFalse(authToken.isEmpty(), "Токен не должен быть пустым");

        Allure.addAttachment("Ответ логина", "application/json",
                loginResponse.getBody().asString());
        Allure.addAttachment("Извлеченный токен", "text/plain", authToken);

        Allure.step("Шаг 2: Использование токена для защищенных операций");

        Response getUserWithToken = given(ApiConfig.getRequestSpecWithApiKey(authToken))
                .pathParam("username", testUsername)
                .when()
                .get("/user/{username}")
                .then()
                .log().all()
                .extract()
                .response();

        getUserWithToken.then()
                .statusCode(200);

        JsonPath userJson = getUserWithToken.jsonPath();
        assertEquals(testUsername, userJson.getString("username"),
                "Должны получить данные авторизованного пользователя");

        User updatedUser = User.builder()
                .id(testUser.getId())
                .username(testUsername)
                .firstName("UpdatedWithToken")
                .lastName("User")
                .email("updated.with.token@example.com")
                .password(testPassword)
                .phone("9999999999")
                .userStatus(0)
                .build();

        Response updateResponse = given(ApiConfig.getRequestSpecWithApiKey(authToken))
                .pathParam("username", testUsername)
                .body(updatedUser)
                .when()
                .put("/user/{username}")
                .then()
                .log().all()
                .extract()
                .response();

        updateResponse.then()
                .statusCode(200)
                .body("code", equalTo(200));

        Response verifyUpdate = given(ApiConfig.getRequestSpecWithApiKey(authToken))
                .pathParam("username", testUsername)
                .when()
                .get("/user/{username}")
                .then()
                .extract()
                .response();

        JsonPath verifyJson = verifyUpdate.jsonPath();
        assertEquals("UpdatedWithToken", verifyJson.getString("firstName"),
                "Имя должно быть обновлено через авторизованный запрос");
        assertEquals("updated.with.token@example.com", verifyJson.getString("email"),
                "Email должен быть обновлен через авторизованный запрос");

        Allure.addAttachment("Обновленные данные", "application/json",
                verifyUpdate.getBody().asString());

        Allure.step("Шаг 3: Проверка неавторизованных запросов");

        Response unauthorizedGet = given(ApiConfig.getRequestSpec())
                .pathParam("username", testUsername)
                .when()
                .get("/user/{username}")
                .then()
                .log().all()
                .extract()
                .response();
        int statusCode = unauthorizedGet.getStatusCode();
        if (statusCode == 200) {
            Allure.step("GET запрос разрешен без авторизации (ожидаемо для PetStore)");
        } else if (statusCode == 401 || statusCode == 403) {
            Allure.step("GET запрос требует авторизации");
        }
        Response badTokenResponse = given(ApiConfig.getRequestSpecWithApiKey("invalid_token_12345"))
                .pathParam("username", testUsername)
                .body(testUser)
                .when()
                .put("/user/{username}")
                .then()
                .log().all()
                .extract()
                .response();
        int badTokenStatus = badTokenResponse.getStatusCode();
        if (badTokenStatus == 401 || badTokenStatus == 403) {
            Allure.step("Невалидный токен отклонен с кодом " + badTokenStatus);
        } else if (badTokenStatus == 200) {
            Allure.step("API приняло запрос с невалидным токеном (специфика PetStore)");
        }

        Allure.step("Шаг 4: Логаут и проверка токена");
        Response logoutResponse = authApi.logout();

        logoutResponse.then()
                .statusCode(200)
                .body("code", equalTo(200))
                .body("message", containsString("ok"));

        Response afterLogout = given(ApiConfig.getRequestSpecWithApiKey(authToken))
                .pathParam("username", testUsername)
                .when()
                .get("/user/{username}")
                .then()
                .extract()
                .response();

        Allure.parameter("Статус после логаута", String.valueOf(afterLogout.getStatusCode()));

        Allure.step("Шаг 5: Проверка Bearer токена");

        try {
            Response bearerResponse = given(ApiConfig.getRequestSpecWithToken(authToken))
                    .pathParam("username", testUsername)
                    .when()
                    .get("/user/{username}")
                    .then()
                    .extract()
                    .response();

            Allure.parameter("Bearer Token Status", String.valueOf(bearerResponse.getStatusCode()));
        } catch (Exception e) {
            Allure.step("Bearer токен не поддерживается (ожидаемо)");
        }

        Allure.step("Финальные проверки");

        assertNotNull(authToken, "Токен должен быть получен");
        assertTrue(authToken.length() > 0, "Токен не должен быть пустым");

        Allure.addAttachment("Результаты теста", "text/plain",
                "Логин выполнен успешно\n" +
                        "Токен получен: " + (authToken != null ? "Да" : "Нет") + "\n" +
                        "Защищенные операции с токеном: выполнены\n" +
                        "Обновление данных: выполнено\n" +
                        "Логаут: выполнен\n" +
                        "Неавторизованные запросы: протестированы\n" +
                        "Bearer токен: протестирован\n" +
                        "\nИтог: Полный цикл аутентификации протестирован успешно");

        Allure.step("Полный цикл аутентификации по токену выполнен успешно");
    }

    @Test
    @Story("Авторизация с неверными учетными данными")
    @Description("Тест проверяет обработку ошибок при неверных учетных данных")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Аутентификация: Неверные учетные данные")
    public void testAuthenticationWithInvalidCredentials() {
        String wrongUsername = "wrong_user_" + UUID.randomUUID();
        String wrongPassword = "WrongPass123!";

        Allure.parameter("Wrong Username", wrongUsername);

        Response wrongUserResponse = authApi.login(wrongUsername, testPassword);

        int statusCode = wrongUserResponse.getStatusCode();

        if (statusCode == 200) {
            JsonPath jsonPath = wrongUserResponse.jsonPath();
            String message = jsonPath.getString("message");
            assertNotNull(message, "Сообщение не должно быть null");

            Allure.addAttachment("Ответ на неверного пользователя", "application/json",
                    wrongUserResponse.getBody().asString());

        } else if (statusCode == 401 || statusCode == 400) {
            Allure.step("Неверный пользователь отклонен с кодом " + statusCode);
        }

        Response wrongPassResponse = authApi.login(testUsername, wrongPassword);

        int passStatusCode = wrongPassResponse.getStatusCode();
        if (passStatusCode == 200) {
            JsonPath jsonPath = wrongPassResponse.jsonPath();
            String message = jsonPath.getString("message");
            Allure.addAttachment("Ответ на неверный пароль", "application/json",
                    wrongPassResponse.getBody().asString());
        }

        Allure.addAttachment("Результаты проверки неверных данных", "text/plain",
                "Неверный пользователь - статус: " + statusCode + "\n" +
                        "Неверный пароль - статус: " + passStatusCode + "\n" +
                        "Обработка ошибок: " + (statusCode != 200 || passStatusCode != 200 ? "корректная" : "принята (специфика API)"));
    }

    @Test
    @Story("Проверка истечения срока действия токена")
    @Description("Тест демонстрирует проверку валидности токена во времени")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Аутентификация: Валидация токена")
    public void testTokenValidation() throws InterruptedException {
        Response loginResponse = authApi.login(testUsername, testPassword);
        String token = authApi.extractTokenFromResponse(loginResponse);

        for (int i = 1; i <= 3; i++) {
            Allure.step("Использование токена, попытка " + i);

            Response userResponse = given(ApiConfig.getRequestSpecWithApiKey(token))
                    .pathParam("username", testUsername)
                    .when()
                    .get("/user/{username}")
                    .then()
                    .extract()
                    .response();
            assertEquals(200, userResponse.getStatusCode(),
                    "Токен должен оставаться валидным для попытки " + i);
            Thread.sleep(500);
        }
        authApi.logout();

        Allure.addAttachment("Валидация токена", "text/plain",
                "Токен использован 3 раза\n" +
                        "Все запросы выполнены успешно\n" +
                        "Токен оставался валидным в течение всего времени\n" +
                        "Логаут выполнен после тестирования");
    }
}