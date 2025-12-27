package com.example.api.tests;

import com.example.api.ApiTestBase;
import com.example.api.endpoints.UserApi;
import com.example.api.models.User;
import io.qameta.allure.*;
import io.qameta.allure.model.Status;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Story("CRUD операции для пользователей")
@Tag("API")
@Tag("User")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserPostTest extends ApiTestBase {

    private UserApi userApi;
    private List<User> testUsers;

    @BeforeEach
    @Step("Подготовка тестовых данных")
    @Description("Создание тестовых пользователей для тестов")
    public void setupTestData() {
        Allure.label("layer", "api");
        Allure.label("component", "user-management");

        userApi = new UserApi();
        testUsers = new ArrayList<>();
        User user1 = createTestUser(1, "testuser1", "John", "Doe",
                "john.doe@example.com", "password123", "1234567890", 1);

        User user2 = createTestUser(2, "testuser2", "Jane", "Smith",
                "jane.smith@example.com", "password456", "0987654321", 0);
        testUsers.add(user1);
        testUsers.add(user2);
        Allure.addAttachment("Тестовые пользователи", "application/json",
                user1.toString() + "\n" + user2.toString());
    }

    @AfterEach
    @Step("Очистка тестовых данных")
    @Description("Удаление созданных пользователей после тестов")
    public void cleanupTestData() {
        if (testUsers != null) {
            for (User user : testUsers) {
                try {
                    userApi.deleteUser(user.getUsername());
                    Allure.step("Удален пользователь: " + user.getUsername());
                } catch (Exception e) {
                    Allure.step("Ошибка при удалении пользователя " + user.getUsername() + ": " + e.getMessage(),
                            Status.FAILED);
                }
            }
        }
    }

    @Test
    @Story("Создание пользователей списком")
    @Description("Тест проверяет создание нескольких пользователей через createWithList эндпоинт")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("API: Создание списка пользователей")
    @Tag("smoke")
    @Tag("regression")
    public void testCreateUsersWithList() {
        Allure.parameter("Количество пользователей", testUsers.size());
        Response response = createUsersStep(testUsers);
        validateCreateResponseStep(response);
        for (User user : testUsers) {
            validateUserDataStep(user);
        }
    }

    @Test
    @Story("Создание одного пользователя")
    @Description("Тест проверяет создание одного пользователя через стандартный эндпоинт")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("API: Создание одного пользователя")
    @Tag("regression")
    public void testCreateSingleUser() {
        User singleUser = testUsers.get(0);
        Allure.parameter("Пользователь", singleUser.getUsername());
        Response response = createSingleUserStep(singleUser);
        validateSingleUserResponseStep(response, singleUser);
    }

    @Test
    @Story("Проверка полей пользователя")
    @Description("Тест проверяет что все поля пользователя сохраняются корректно")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("API: Полная проверка полей пользователя")
    @Tag("smoke")
    @Tag("validation")
    public void testUserFieldsValidation() {

        User detailedUser = createDetailedTestUser();
        List<User> users = new ArrayList<>();
        users.add(detailedUser);

        Allure.parameter("Детальный пользователь", detailedUser.getUsername());
        Allure.addAttachment("Детали пользователя", "application/json", detailedUser.toString());
        Response createResponse = createUsersStep(users);
        validateCreateResponseStep(createResponse);
        validateDetailedUserStep(detailedUser);
    }

    @Test
    @Story("Обновление пользователя")
    @Description("Тест проверяет обновление данных пользователя")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("API: Обновление данных пользователя")
    @Tag("regression")
    @Tag("update")
    public void testUpdateUser() {
        User originalUser = testUsers.get(0);
        createUsersStep(List.of(originalUser));
        User updatedUser = createUpdatedUser(originalUser);

        Allure.addAttachment("Оригинальный пользователь", "application/json", originalUser.toString());
        Allure.addAttachment("Обновленный пользователь", "application/json", updatedUser.toString());
        Response updateResponse = updateUserStep(originalUser.getUsername(), updatedUser);
        validateUpdateResponseStep(updateResponse, originalUser);
        validateUpdatedUserStep(originalUser.getUsername(), updatedUser);
    }

    @Test
    @Story("Удаление пользователя")
    @Description("Тест проверяет удаление пользователя и последующую проверку 404")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("API: Удаление пользователя")
    @Tag("smoke")
    @Tag("delete")
    public void testDeleteUser() {
        User userToDelete = testUsers.get(0);
        createUsersStep(List.of(userToDelete));

        Allure.parameter("Пользователь для удаления", userToDelete.getUsername());
        Response deleteResponse = deleteUserStep(userToDelete.getUsername());
        validateDeleteResponseStep(deleteResponse, userToDelete);
        Response getResponse = getUserStep(userToDelete.getUsername());
        validateUserNotFoundStep(getResponse, userToDelete);
    }

    @Step("Создание тестового пользователя")
    private User createTestUser(Integer id, String username, String firstName, String lastName,
                                String email, String password, String phone, Integer userStatus) {
        return User.builder()
                .id(id)
                .username(username)
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .password(password)
                .phone(phone)
                .userStatus(userStatus)
                .build();
    }

    @Step("Создание детального тестового пользователя")
    private User createDetailedTestUser() {
        return User.builder()
                .id(100)
                .username("detaileduser")
                .firstName("Alex")
                .lastName("Johnson")
                .email("alex.johnson@company.com")
                .password("securePass123!")
                .phone("+1-234-567-8900")
                .userStatus(2)
                .build();
    }

    @Step("Создание обновленного пользователя")
    private User createUpdatedUser(User originalUser) {
        return User.builder()
                .id(originalUser.getId())
                .username(originalUser.getUsername())
                .firstName("UpdatedFirstName")
                .lastName("UpdatedLastName")
                .email("updated@example.com")
                .password("newpassword")
                .phone("9999999999")
                .userStatus(0)
                .build();
    }

    @Step("Создание пользователей через API")
    private Response createUsersStep(List<User> users) {
        Allure.addAttachment("Создаваемые пользователи", "application/json",
                users.toString());
        return userApi.createUsersWithList(users);
    }

    @Step("Валидация ответа создания пользователей")
    private void validateCreateResponseStep(Response response) {
        response.then()
                .statusCode(200)
                .body("code", equalTo(200))
                .body("type", equalTo("unknown"))
                .body("message", equalTo("ok"));

        Allure.addAttachment("Ответ создания", "application/json",
                response.getBody().asString());
    }

    @Step("Проверка данных пользователя: {user.username}")
    private void validateUserDataStep(User user) {
        Response getUserResponse = userApi.getUserByUsername(user.getUsername());

        getUserResponse.then()
                .statusCode(200)
                .body("id", equalTo(user.getId()))
                .body("username", equalTo(user.getUsername()))
                .body("firstName", equalTo(user.getFirstName()))
                .body("lastName", equalTo(user.getLastName()))
                .body("email", equalTo(user.getEmail()))
                .body("phone", equalTo(user.getPhone()))
                .body("userStatus", equalTo(user.getUserStatus()));

        Allure.addAttachment("Ответ получения пользователя " + user.getUsername(),
                "application/json", getUserResponse.getBody().asString());
    }

    @Step("Создание одного пользователя")
    private Response createSingleUserStep(User user) {
        return given()
                .body(user)
                .when()
                .post("/user")
                .then()
                .log().all()
                .extract()
                .response();
    }

    @Step("Валидация ответа создания одного пользователя")
    private void validateSingleUserResponseStep(Response response, User user) {
        response.then()
                .statusCode(200)
                .body("code", equalTo(200))
                .body("type", equalTo("unknown"))
                .body("message", equalTo(String.valueOf(user.getId())));
    }

    @Step("Проверка детального пользователя")
    private void validateDetailedUserStep(User detailedUser) {
        Response getResponse = userApi.getUserByUsername(detailedUser.getUsername());

        getResponse.then()
                .statusCode(200)
                .body("id", equalTo(detailedUser.getId()),
                        "username", equalTo(detailedUser.getUsername()),
                        "firstName", equalTo(detailedUser.getFirstName()),
                        "lastName", equalTo(detailedUser.getLastName()),
                        "email", equalTo(detailedUser.getEmail()),
                        "phone", equalTo(detailedUser.getPhone()),
                        "userStatus", equalTo(detailedUser.getUserStatus()));

        Allure.addAttachment("Детальный ответ пользователя", "application/json",
                getResponse.getBody().asString());
    }

    @Step("Обновление пользователя {username}")
    private Response updateUserStep(String username, User user) {
        return userApi.updateUser(username, user);
    }

    @Step("Валидация ответа обновления")
    private void validateUpdateResponseStep(Response response, User originalUser) {
        response.then()
                .statusCode(200)
                .body("code", equalTo(200))
                .body("message", equalTo(String.valueOf(originalUser.getId())));
    }

    @Step("Проверка обновленного пользователя {username}")
    private void validateUpdatedUserStep(String username, User expectedUser) {
        Response getResponse = userApi.getUserByUsername(username);

        getResponse.then()
                .statusCode(200)
                .body("firstName", equalTo(expectedUser.getFirstName()))
                .body("lastName", equalTo(expectedUser.getLastName()))
                .body("email", equalTo(expectedUser.getEmail()))
                .body("phone", equalTo(expectedUser.getPhone()))
                .body("userStatus", equalTo(expectedUser.getUserStatus()));
    }

    @Step("Удаление пользователя {username}")
    private Response deleteUserStep(String username) {
        return userApi.deleteUser(username);
    }

    @Step("Валидация ответа удаления")
    private void validateDeleteResponseStep(Response response, User user) {
        response.then()
                .statusCode(200)
                .body("code", equalTo(200))
                .body("message", equalTo(user.getUsername()));
    }

    @Step("Получение пользователя {username}")
    private Response getUserStep(String username) {
        return userApi.getUserByUsername(username);
    }

    @Step("Проверка что пользователь {user.username} не найден")
    private void validateUserNotFoundStep(Response response, User user) {
        response.then()
                .statusCode(404)
                .body("code", equalTo(1))
                .body("type", equalTo("error"))
                .body("message", equalTo("User not found"));

        Allure.addAttachment("Ответ 404", "application/json",
                response.getBody().asString());
    }
}