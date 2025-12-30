package com.example.api.tests;

import com.example.api.endpoints.UserApi;
import com.example.api.models.User;
import com.example.config.ApiConfig;
import io.qameta.allure.*;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@Epic("PetStore API Tests")
@Feature("User Management")
@Story("CRUD операции для пользователей с использованием JSONPath и JSON Schema")
@Tag("API")
@Tag("JSON")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserApiTests {

    private UserApi userApi;
    private List<User> testUsers;

    @BeforeAll
    @Step("Проверка доступности API")
    public static void checkApiAvailability() {
        given(ApiConfig.getRequestSpec())
                .when()
                .get("/user")
                .then()
                .statusCode(anyOf(is(200), is(404)));

        Allure.addAttachment("Base URL", "text/plain", ApiConfig.getBaseUrl());
    }

    @BeforeEach
    @Step("Подготовка тестовых данных")
    @Description("Создание тестовых пользователей")
    public void setupTestData() {
        Allure.label("layer", "api");
        Allure.label("component", "user-management");

        userApi = new UserApi();
        testUsers = new ArrayList<>();

        testUsers.add(User.builder()
                .id(1001)
                .username("testuser_jsonpath")
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("password123")
                .phone("1234567890")
                .userStatus(1)
                .build());

        testUsers.add(User.builder()
                .id(1002)
                .username("testuser2_jsonpath")
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .password("password456")
                .phone("0987654321")
                .userStatus(0)
                .build());

        Allure.addAttachment("Тестовые пользователи", "application/json",
                testUsers.toString());
    }

    @AfterEach
    @Step("Очистка тестовых данных")
    public void cleanupTestData() {
        if (testUsers != null) {
            testUsers.forEach(user -> {
                try {
                    userApi.deleteUser(user.getUsername());
                } catch (Exception e) {
                    // Игнорируем ошибки удаления
                }
            });
        }
    }

    @Test
    @Story("Создание пользователей с JSONPath проверками")
    @Description("Тест создает пользователей и проверяет ответ с использованием JSONPath")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("API: Создание пользователей с JSONPath валидацией")
    public void testCreateUsersWithJsonPathValidation() {
        Response response = userApi.createUsersWithList(testUsers);

        JsonPath jsonPath = response.jsonPath();

        assertEquals(200, jsonPath.getInt("code"), "Код ответа должен быть 200");
        assertEquals("unknown", jsonPath.getString("type"), "Тип должен быть 'unknown'");
        assertEquals("ok", jsonPath.getString("message"), "Сообщение должно быть 'ok'");

        Map<String, Object> responseMap = jsonPath.getMap("");
        assertTrue(responseMap.containsKey("code"), "Ответ должен содержать поле 'code'");
        assertTrue(responseMap.containsKey("type"), "Ответ должен содержать поле 'type'");
        assertTrue(responseMap.containsKey("message"), "Ответ должен содержать поле 'message'");

        testUsers.forEach(user -> {
            Response userResponse = userApi.getUserByUsername(user.getUsername());
            JsonPath userJson = userResponse.jsonPath();

            assertEquals(user.getId(), userJson.getInt("id"), "ID должен совпадать");
            assertEquals(user.getUsername(), userJson.getString("username"), "Username должен совпадать");
            assertEquals(user.getFirstName(), userJson.getString("firstName"), "FirstName должен совпадать");
            assertEquals(user.getLastName(), userJson.getString("lastName"), "LastName должен совпадать");
            assertEquals(user.getEmail(), userJson.getString("email"), "Email должен совпадать");
            assertEquals(user.getPhone(), userJson.getString("phone"), "Phone должен совпадать");
            assertEquals(user.getUserStatus(), userJson.getInt("userStatus"), "UserStatus должен совпадать");
        });
    }

    @Test
    @Story("Полная проверка с JSON Schema")
    @Description("Тест проверяет схему JSON ответа")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("API: Валидация JSON схемы ответов")
    public void testJsonSchemaValidation() {
        Response createResponse = userApi.createUser(testUsers.get(0));

        createResponse.then()
                .assertThat()
                .body(matchesJsonSchemaInClasspath("schemas/create-users-response.json"));

        Response getUserResponse = userApi.getUserByUsername(testUsers.get(0).getUsername());

        getUserResponse.then()
                .assertThat()
                .body(matchesJsonSchemaInClasspath("schemas/user-response.json"));

        Allure.addAttachment("JSON Schema Validation", "text/plain",
                "✓ Схема ответа создания валидна\n" +
                        "✓ Схема ответа пользователя валидна");
    }

    @Test
    @Story("Проверка коллекций с JSONPath")
    @Description("Тест демонстрирует работу с коллекциями через JSONPath")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("API: Работа с JSONPath выражениями")
    public void testJsonPathExpressions() {
        userApi.createUsersWithList(testUsers);

        testUsers.forEach(user -> {
            Response response = userApi.getUserByUsername(user.getUsername());
            JsonPath jsonPath = response.jsonPath();

            String email = jsonPath.getString("email");
            String phone = jsonPath.getString("phone");
            Integer userStatus = jsonPath.getInt("userStatus");
            assertNotNull(email, "Email не должен быть null");
            assertNotNull(phone, "Phone не должен быть null");
            assertNotNull(userStatus, "UserStatus не должен быть null");

            assertTrue(jsonPath.get("id") instanceof Integer, "ID должен быть Integer");
            assertTrue(jsonPath.get("username") instanceof String, "Username должен быть String");
            assertTrue(jsonPath.get("userStatus") instanceof Integer, "UserStatus должен быть Integer");

            Allure.addAttachment("JSONPath проверки для " + user.getUsername(), "text/plain",
                    "Email: " + email + "\n" +
                            "Phone: " + phone + "\n" +
                            "UserStatus: " + userStatus + "\n" +
                            "Типы данных валидны");
        });
    }

    @Test
    @Story("Проверка вложенных JSON структур")
    @Description("Тест проверяет работу с вложенными структурами через JSONPath")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("API: Вложенные JSONPath выражения")
    public void testNestedJsonPathExpressions() {

        User user = testUsers.get(0);
        userApi.createUser(user);
        Response response = userApi.getUserByUsername(user.getUsername());

        Map<String, Object> userMap = response.jsonPath().getMap("");

        assertTrue(userMap.containsKey("id"), "Должно содержать поле 'id'");
        assertTrue(userMap.containsKey("username"), "Должно содержать поле 'username'");
        assertTrue(userMap.containsKey("firstName"), "Должно содержать поле 'firstName'");
        assertTrue(userMap.containsKey("lastName"), "Должно содержать поле 'lastName'");
        assertTrue(userMap.containsKey("email"), "Должно содержать поле 'email'");
        assertTrue(userMap.containsKey("phone"), "Должно содержать поле 'phone'");
        assertTrue(userMap.containsKey("userStatus"), "Должно содержать поле 'userStatus'");

        assertEquals(user.getId(), userMap.get("id"), "ID должен совпадать");
        assertEquals(user.getUsername(), userMap.get("username"), "Username должен совпадать");

        Allure.addAttachment("Map представление пользователя", "application/json",
                userMap.toString());
    }

    @Test
    @Story("Обработка ошибок с JSONPath")
    @Description("Тест проверяет обработку ошибок через JSONPath")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("API: JSONPath при ошибках")
    public void testErrorHandlingWithJsonPath() {
        Response response = given(ApiConfig.getRequestSpec())
                .pathParam("username", "nonexistent_user_12345")
                .when()
                .get("/user/{username}")
                .then()
                .log().all()
                .extract()
                .response();

        JsonPath jsonPath = response.jsonPath();

        assertEquals(404, response.getStatusCode(), "Должен вернуть 404");
        assertEquals(1, jsonPath.getInt("code"), "Код ошибки должен быть 1");
        assertEquals("error", jsonPath.getString("type"), "Тип должен быть 'error'");
        assertEquals("User not found", jsonPath.getString("message"), "Сообщение должно быть 'User not found'");

        Allure.addAttachment("Ошибка 404", "application/json", response.getBody().asString());
    }

    @Test
    @Story("Проверка логина пользователя")
    @Description("Тест проверяет эндпоинт логина с JSONPath")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("API: Логин пользователя с JSONPath")
    public void testUserLoginWithJsonPath() {

        User user = testUsers.get(0);
        userApi.createUser(user);

        Response loginResponse = userApi.loginUser(user.getUsername(), user.getPassword());
        JsonPath jsonPath = loginResponse.jsonPath();

        assertEquals(200, loginResponse.getStatusCode(), "Логин должен быть успешным");
        assertTrue(jsonPath.getString("message").contains("logged in"),
                "Сообщение должно содержать 'logged in'");

        String message = jsonPath.getString("message");
        assertTrue(message.matches(".*sessionid:\\s*\\d+.*"),
                "Сообщение должно содержать sessionid");

        Allure.addAttachment("Логин ответ", "application/json", loginResponse.getBody().asString());
    }

    @Test
    @Story("Комплексная проверка CRUD операций")
    @Description("Тест проверяет полный цикл CRUD с JSONPath")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("API: Полный CRUD цикл с JSONPath")
    public void testFullCrudCycleWithJsonPath() {
        User user = testUsers.get(0);

        Response createResponse = userApi.createUser(user);
        JsonPath createJson = createResponse.jsonPath();
        assertEquals(200, createJson.getInt("code"), "Код создания должен быть 200");

        Response readResponse = userApi.getUserByUsername(user.getUsername());
        JsonPath readJson = readResponse.jsonPath();
        assertEquals(user.getUsername(), readJson.getString("username"), "Username должен совпадать");

        User updatedUser = User.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName("UpdatedFirstName")
                .lastName("UpdatedLastName")
                .email("updated@example.com")
                .password("newpassword123")
                .phone("9999999999")
                .userStatus(0)
                .build();

        Response updateResponse = userApi.updateUser(user.getUsername(), updatedUser);
        JsonPath updateJson = updateResponse.jsonPath();
        assertEquals(200, updateJson.getInt("code"), "Код обновления должен быть 200");

        Response afterUpdateResponse = userApi.getUserByUsername(user.getUsername());
        JsonPath afterUpdateJson = afterUpdateResponse.jsonPath();
        assertEquals("UpdatedFirstName", afterUpdateJson.getString("firstName"), "FirstName должен обновиться");
        assertEquals("UpdatedLastName", afterUpdateJson.getString("lastName"), "LastName должен обновиться");

        Response deleteResponse = userApi.deleteUser(user.getUsername());
        JsonPath deleteJson = deleteResponse.jsonPath();
        assertEquals(200, deleteJson.getInt("code"), "Код удаления должен быть 200");
        assertEquals(user.getUsername(), deleteJson.getString("message"), "Сообщение должно содержать username");

        Response verifyResponse = given(ApiConfig.getRequestSpec())
                .pathParam("username", user.getUsername())
                .when()
                .get("/user/{username}");

        assertEquals(404, verifyResponse.getStatusCode(), "Должен вернуть 404 после удаления");

        Allure.addAttachment("CRUD цикл завершен", "text/plain",
                "✓ CREATE - создание пользователя\n" +
                        "✓ READ - чтение пользователя\n" +
                        "✓ UPDATE - обновление пользователя\n" +
                        "✓ DELETE - удаление пользователя\n" +
                        "✓ VERIFY - проверка удаления");
    }

    @Test
    @Story("Проверка граничных значений")
    @Description("Тест проверяет обработку граничных значений через JSONPath")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("API: Граничные значения с JSONPath")
    public void testBoundaryValuesWithJsonPath() {
        User minimalUser = User.builder()
                .id(1)
                .username("minimal")
                .firstName("A") // Минимальная длина
                .lastName("B")
                .email("a@b.c") // Минимальный email
                .password("1") // Минимальный пароль
                .phone("1") // Минимальный телефон
                .userStatus(0) // Минимальный статус
                .build();

        Response response = userApi.createUser(minimalUser);
        JsonPath jsonPath = response.jsonPath();

        assertEquals(200, jsonPath.getInt("code"), "Должен создаться с минимальными данными");

        Response getUserResponse = userApi.getUserByUsername(minimalUser.getUsername());
        JsonPath getUserJson = getUserResponse.jsonPath();

        assertEquals(minimalUser.getFirstName(), getUserJson.getString("firstName"),
                "Минимальное имя должно сохраниться");
        assertEquals(minimalUser.getEmail(), getUserJson.getString("email"),
                "Минимальный email должен сохраниться");

        userApi.deleteUser(minimalUser.getUsername());
    }
}