package com.example.tests;

import com.example.driver.DriverFactory;
import com.example.listeners.TestListener;
import com.example.manager.PageManager;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(TestListener.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Epic("UI Тесты")
@Feature("Авторизация и формы")
@Story("Page Object тесты с Allure отчетами")
@Tag("UI")
@Tag("smoke")
public class PageObjectTestsWithScreenshots {

    private PageManager pageManager;

    @BeforeEach
    @Step("Инициализация WebDriver и PageManager")
    @Description("Настройка окружения для UI тестов")
    public void setup() {
        Allure.label("layer", "ui");
        Allure.label("component", "web-ui");

        pageManager = new PageManager(DriverFactory.createDefaultDriver());
        Allure.addAttachment("WebDriver инициализирован", "text/plain",
                "Браузер: Chrome\nОкно: максимизировано");
    }

    @AfterEach
    @Step("Завершение теста")
    @Description("Закрытие браузера и очистка ресурсов")
    public void teardown() {
        if (pageManager != null) {
            pageManager.quit();
            Allure.step("Браузер закрыт");
        }
    }

    @Test
    @Story("Логин с неверными данными")
    @Description("Тест проверяет отображение ошибки при неверном логине")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("UI: Логин с неверным именем пользователя")
    @Tag("login")
    @Tag("negative")
    public void testLoginPage_WrongUsername() throws InterruptedException {
        Allure.parameter("URL", "https://bonigarcia.dev/selenium-webdriver-java/login-form.html");
        Allure.parameter("Логин", "wronguser");

        openLoginPageStep();
        enterUsernameStep("wronguser");
        submitLoginFormStep();

        waitForResponseStep();
        validateErrorMessageStep("Invalid credentials");
    }

    @Test
    @Story("Валидация веб-формы")
    @Description("Тест проверяет заполнение обязательных полей формы")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("UI: Валидация текстового поля формы")
    @Tag("form")
    @Tag("positive")
    public void testWebForm_Validation() {
        Allure.parameter("URL", "https://bonigarcia.dev/selenium-webdriver-java/web-form.html");
        Allure.parameter("Тестовые данные", "Test Data");

        openWebFormPageStep();
        enterTextInputStep("Test Data");
        validateTextInputStep("Test Data");
    }

    @Test
    @Story("Создание пользователя через UI")
    @Description("Тест проверяет создание нового пользователя через веб-интерфейс")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("UI: Создание нового пользователя")
    @Tag("registration")
    @Tag("smoke")
    public void testCreateNewUser() throws InterruptedException {
        openWebFormPageStep();
        fillAllFormFieldsStep();
        validateFormSubmissionStep();
    }

    @Step("Открытие страницы логина")
    private void openLoginPageStep() {
        pageManager.openLoginPage("https://bonigarcia.dev/selenium-webdriver-java/login-form.html");
        Allure.addAttachment("Страница открыта", "text/plain",
                "Страница логина загружена успешно");
    }

    @Step("Открытие страницы веб-формы")
    private void openWebFormPageStep() {
        pageManager.openWebFormPage("https://bonigarcia.dev/selenium-webdriver-java/web-form.html");
        Allure.addAttachment("Страница открыта", "text/plain",
                "Страница веб-формы загружена успешно");
    }

    @Step("Ввод имени пользователя: {username}")
    private void enterUsernameStep(String username) {
        pageManager.getLoginPage().enterUsername(username);
        Allure.addAttachment("Введен логин", "text/plain", username);
    }

    @Step("Отправка формы логина")
    private void submitLoginFormStep() {
        pageManager.getLoginPage().submitForm();
        Allure.step("Форма отправлена");
    }

    @Step("Ожидание ответа (500ms)")
    private void waitForResponseStep() throws InterruptedException {
        Thread.sleep(500);
        Allure.step("Ожидание завершено");
    }

    @Step("Валидация сообщения об ошибке. Ожидается: {expectedError}")
    private void validateErrorMessageStep(String expectedError) {
        String actualError = pageManager.getLoginPage().getErrorMessage();
        Allure.addAttachment("Фактическая ошибка", "text/plain", actualError);
        Allure.addAttachment("Ожидаемая ошибка", "text/plain", expectedError);

        assertTrue(actualError.contains(expectedError),
                String.format("Ожидалась ошибка содержащая '%s', но получено: '%s'",
                        expectedError, actualError));

        Allure.step("Ошибка валидирована успешно");
    }

    @Step("Ввод текста в поле: {text}")
    private void enterTextInputStep(String text) {
        pageManager.getWebFormPage().setTextInput(text);
        Allure.addAttachment("Введенный текст", "text/plain", text);
    }

    @Step("Проверка текстового поля. Ожидается: {expectedText}")
    private void validateTextInputStep(String expectedText) {
        String actualText = pageManager.getWebFormPage().getTextInputValue();
        Allure.addAttachment("Фактический текст", "text/plain", actualText);

        assertTrue(actualText.equals(expectedText),
                String.format("Ожидался текст '%s', но получено: '%s'",
                        expectedText, actualText));

        Allure.step("Текст валидирован успешно");
    }

    @Step("Заполнение всех полей формы")
    private void fillAllFormFieldsStep() {
        pageManager.getWebFormPage().setTextInput("John Doe");
        pageManager.getWebFormPage().setPassword("Secret123!");
        pageManager.getWebFormPage().setTextarea("Комментарий для теста");
        pageManager.getWebFormPage().selectDropdownOptionByValue("2");
        pageManager.getWebFormPage().setDatalistValue("New York");
        pageManager.getWebFormPage().selectRadioButton();

        Allure.addAttachment("Заполненные данные", "text/plain",
                "Текст: John Doe\nПароль: *******\nВыпадающий список: 2\nГород: New York\nРадио: выбран");
    }

    @Step("Валидация отправки формы")
    private void validateFormSubmissionStep() throws InterruptedException {
        pageManager.getWebFormPage().submitForm();
        Thread.sleep(2000);

        String pageSource = pageManager.getWebFormPage().driver.getPageSource();
        boolean isSubmitted = pageSource.contains("Submitted") ||
                pageManager.getWebFormPage().driver.getCurrentUrl().contains("submitted");

        Allure.addAttachment("Результат отправки", "text/plain",
                isSubmitted ? "Форма успешно отправлена" : "Ошибка при отправке формы");

        assertTrue(isSubmitted, "Форма не была успешно отправлена");
        Allure.step("Отправка формы валидирована успешно");
    }
}